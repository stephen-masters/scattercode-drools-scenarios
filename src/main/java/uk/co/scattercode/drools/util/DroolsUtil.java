package uk.co.scattercode.drools.util;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.apache.commons.beanutils.BeanUtils;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.kiesession.rulebase.KnowledgeBaseFactory;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.rule.Rule;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.io.Resource;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.scattercode.beans.BeanMatcher;

/**
 *
 * @author Stephen Masters
 */
public class DroolsUtil {

    private static Logger log = LoggerFactory.getLogger(DroolsUtil.class);

    private static BeanMatcher matcher = new BeanMatcher();

    /**
     * This class provides static methods, so we prevent instantiation.
     */
    private DroolsUtil() {}

    public static KieBase createKnowledgeBase(DroolsResource[] resources) {
        return createKnowledgeBase(resources, EventProcessingOption.STREAM);
    }

    /**
     * Creates a new knowledge base using a collection of resources.
     *
     * @param resources
     *            An array of {@link DroolsResource} indicating where the
     *            various resources should be loaded from. These could be
     *            classpath, file or URL resources.
     * @return A new knowledge base.
     */
    public static KieBase createKnowledgeBase(DroolsResource[] resources, EventProcessingOption eventProcessingOption) {
        KnowledgeBuilder builder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        for (DroolsResource resource : resources) {
            log.info("Resource: " + resource.getType() + ", path type=" + resource.getPathType() + ", path="
                    + resource.getPath());
            switch (resource.getPathType()) {
                case CLASSPATH:
                    builder.add(ResourceFactory.newClassPathResource(resource.getPath()), resource.getType());
                    break;
                case FILE:
                    builder.add(ResourceFactory.newFileResource(resource.getPath()), resource.getType());
                    break;
                case URL:
                    builder.add(newUrlResource(resource), resource.getType());
                    break;
                default:
                    throw new IllegalArgumentException("Unable to build this resource path type.");
            }
        }

        if (builder.hasErrors()) {
            throw new RuntimeException(builder.getErrors().toString());
        }

        KieBaseConfiguration conf = KieServices.Factory.get().newKieBaseConfiguration();
        conf.setOption(eventProcessingOption);

        InternalKnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase(conf);
        knowledgeBase.addPackages(builder.getKnowledgePackages());

        log.info("Knowledge base built with packages: " + packageSummary(knowledgeBase.getKiePackages()));

        return knowledgeBase;
    }

    /**
     * Drools no longer reads resources from a URL itself, so the resource is
     * fetched here, with HTTP basic authentication when the resource has a
     * user name, and handed to the builder as bytes.
     */
    private static Resource newUrlResource(DroolsResource resource) {
        HttpRequest.Builder request =
                HttpRequest.newBuilder(URI.create(resource.getPath())).GET();
        if (resource.getUsername() != null) {
            log.info("Setting authentication for: " + resource.getUsername());
            String credentials = resource.getUsername() + ":" + resource.getPassword();
            request.header(
                    "Authorization",
                    "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8)));
        }
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpResponse<byte[]> response = client.send(request.build(), HttpResponse.BodyHandlers.ofByteArray());
            if (response.statusCode() != 200) {
                throw new IllegalStateException(
                        "Unable to fetch [" + resource.getPath() + "]: HTTP " + response.statusCode());
            }
            Resource fetched = ResourceFactory.newByteArrayResource(response.body());
            fetched.setSourcePath(URI.create(resource.getPath()).getPath());
            return fetched;
        } catch (IOException e) {
            throw new IllegalStateException("Unable to fetch [" + resource.getPath() + "]", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted fetching [" + resource.getPath() + "]", e);
        }
    }

    private static String packageSummary(Collection<KiePackage> packages) {
        StringBuilder sb = new StringBuilder();
        for (KiePackage p : packages) {
            sb.append("\n  Package : " + p.getName());
            for (Rule r : p.getRules()) {
                sb.append("\n    Rule: " + r.getName());
            }
        }
        return sb.toString();
    }

    /**
     * Return a string containing the packages used to build the knowledge base.
     */
    public static String knowledgeBaseDetails(KieBase kbase) {
        if (kbase == null) {
            return "Knowledge Base is null.";
        } else {
            StringBuilder sb = new StringBuilder("Knowledge base built from the following packages:");
            Collection<KiePackage> packages = kbase.getKiePackages();
            for (KiePackage kp : packages) {
                sb.append("\n    Package: [" + kp.getName() + "]");
                for (Rule rule : kp.getRules()) {
                    sb.append("\n        Rule: [" + rule.getName() + "]");
                }
            }
            return sb.toString();
        }
    }

    public static String objectDetails(Object o) {
        StringBuilder sb = new StringBuilder(o.getClass().getSimpleName());

        try {
            Map<String, String> objectProperties = BeanUtils.describe(o);
            for (String k : objectProperties.keySet()) {
                sb.append(", " + k + "=\"" + objectProperties.get(k) + "\"");
            }
        } catch (IllegalAccessException e) {
            return "IllegalAccessException attempting to parse object.";
        } catch (InvocationTargetException e) {
            return "InvocationTargetException attempting to parse object.";
        } catch (NoSuchMethodException e) {
            return "NoSuchMethodException attempting to parse object.";
        }

        return sb.toString();
    }

    public static Object findInsertedFact(List<ObjectInsertedEvent> insertions, String factType, String[] filters) {
        for (ObjectInsertedEvent event : insertions) {
            Object fact = event.getObject();

            if (factType.equals(fact.getClass().getSimpleName())) {
                if (matcher.matches(fact, filters)) {
                    return fact;
                }
            }
        }
        return null;
    }

    /**
     * Search for an activation by rule name. Note that when using decision
     * tables, the rule name is generated as <code>Row N Rule_Name</code>. This
     * means that we can't just search for exact matches. This method will
     * therefore return true if an activation ends with the ruleName argument.
     *
     * @param ruleName
     *            The name of the rule we're looking for.
     */
    public static boolean ruleFired(List<Activation> activations, String ruleName) {
        for (Activation activation : activations) {
            if (activation.getRuleName().toUpperCase().endsWith(ruleName.toUpperCase())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Given a list of rule activations and a list of rule names, did all of the
     * rules in the list provided get fired? This is a utility method designed
     * primarily to help with testing.
     *
     * @param ruleNames
     *            The list of rule names we're looking for.
     */
    public static boolean allRulesFired(List<Activation> activations, String[] ruleNames) {
        for (String ruleName : ruleNames) {
            if (!ruleFired(activations, ruleName)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns a <code>String</code> showing the rule names that were expected
     * to fire, and whether or not they fired, ready to be logged.
     *
     * @param ruleNames
     *            The list of rule names we're looking for.
     */
    public static String prettyRulesFired(List<Activation> activations, String[] ruleNames) {
        StringBuilder sb = new StringBuilder();
        for (String ruleName : ruleNames) {
            sb.append("\n    " + ruleName + " : " + (ruleFired(activations, ruleName) ? "Y" : "N"));
        }
        return sb.toString();
    }
}
