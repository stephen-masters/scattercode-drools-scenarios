package uk.co.scattercode.drools.util;

import java.util.Collection;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.conf.EventProcessingOption;
import org.drools.definition.KnowledgePackage;
import org.drools.definition.rule.Rule;
import org.drools.event.rule.WorkingMemoryEventListener;
import org.drools.io.ResourceFactory;
import org.drools.io.impl.UrlResource;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Initialises and encapsulates the various components required for the rules engine.
 * Includes initialising the knowledge base, creating a stateful session and attaching 
 * listeners for the default events. 
 * 
 * Note that the Spring context will inject this with a URL for the package in Guvnor.
 * 
 * @author Stephen Masters
 */
public class KnowledgeEnvironment {

    private static Logger log = LoggerFactory.getLogger(KnowledgeEnvironment.class);

    public DroolsResource[] resources;

    public KnowledgeBase knowledgeBase;
    public StatefulKnowledgeSession knowledgeSession;
    public TrackingAgendaEventListener agendaEventListener;
    public TrackingWorkingMemoryEventListener workingMemoryEventListener;

    /**
     * Constructor supporting setting up a knowledge environment using just a
     * list of resources, which may be local. Particularly useful when testing
     * DRL files.
     * 
     * @param resources
     */
    public KnowledgeEnvironment(DroolsResource[] resources) {
        initialise(resources);
    }

    /**
     * Constructor.
     * 
     * @param url The URL of the package via the Guvnor REST API.
     */
    public KnowledgeEnvironment(String url) {
        initialise(url);
    }

    public KnowledgeEnvironment(String url, String username, String password) {
        initialise(url, username, password);
    }

    public void initialise(String url) {
        this.resources = new DroolsResource[] { 
            new DroolsResource(url,
                    ResourcePathType.URL, 
                    ResourceType.PKG
        )};
        initialise();
    }

    public void initialise(String url, String username, String password) {
        this.resources = new DroolsResource[] { 
                new DroolsResource(url, 
                        ResourcePathType.URL, 
                        ResourceType.PKG, 
                        username, 
                        password
        )};
        initialise();
    }

    public void initialise(DroolsResource[] resources) {
        this.resources = resources;
        initialise();
    }

    public void initialise() {
        log.info("Initialising KnowledgeEnvironment with resources: " + this.resources);
        this.knowledgeBase = createKnowledgeBase(this.resources);
        initialiseSession();
    }
    
    public void initialiseSession() {
        log.info("Initialising session...");
        if (this.knowledgeSession == null) {
            this.knowledgeSession = knowledgeBase.newStatefulKnowledgeSession();
            this.agendaEventListener = new TrackingAgendaEventListener();
            this.knowledgeSession.addEventListener(this.agendaEventListener);
            this.workingMemoryEventListener = new TrackingWorkingMemoryEventListener();
            this.knowledgeSession.addEventListener(this.workingMemoryEventListener);
        } else {
            retractAll();
            clearListeners();
        }
    }
    
    public void clearListeners() {
        this.knowledgeSession.removeEventListener(this.agendaEventListener);
        this.knowledgeSession.removeEventListener(this.workingMemoryEventListener);

        this.agendaEventListener = new TrackingAgendaEventListener();
        this.workingMemoryEventListener = new TrackingWorkingMemoryEventListener();

        this.knowledgeSession.addEventListener(this.agendaEventListener);
        this.knowledgeSession.addEventListener(this.workingMemoryEventListener);
    }
    
    public void retractAll() {
        log.info("Retracting all fact handles...");
        for (FactHandle handle : this.knowledgeSession.getFactHandles()) {
            this.knowledgeSession.retract(handle);
        }
    }
    
    public void addEventListener(WorkingMemoryEventListener listener) {
        knowledgeSession.addEventListener(listener);
    }
    public void removeEventListener(WorkingMemoryEventListener listener) {
        knowledgeSession.removeEventListener(listener);
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
    public KnowledgeBase createKnowledgeBase(DroolsResource[] resources) {
        KnowledgeBuilder builder = KnowledgeBuilderFactory
                .newKnowledgeBuilder();

        for (DroolsResource resource : resources) {
            log.info("Resource: " + resource.getType() + ", path type="
                    + resource.getPathType() + ", path=" + resource.getPath());
            switch (resource.getPathType()) {
            case CLASSPATH:
                builder.add(ResourceFactory.newClassPathResource(resource
                        .getPath()), resource.getType());
                break;
            case FILE:
                builder.add(
                        ResourceFactory.newFileResource(resource.getPath()),
                        resource.getType());
                break;
            case URL:
                UrlResource urlResource = (UrlResource) ResourceFactory
                        .newUrlResource(resource.getPath());
                
                if (resource.getUsername() != null) {
                    log.info("Setting authentication for: " + resource.getUsername());
                    urlResource.setBasicAuthentication("enabled");
                    urlResource.setUsername(resource.getUsername());
                    urlResource.setPassword(resource.getPassword());
                }
                
                builder.add(urlResource, resource.getType());
                
                break;
            default:
                throw new IllegalArgumentException(
                        "Unable to build this resource path type.");
            }
        }

        if (builder.hasErrors()) {
            throw new RuntimeException(builder.getErrors().toString());
        }

        KnowledgeBaseConfiguration conf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        conf.setOption(EventProcessingOption.STREAM);

        KnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase(conf);
        knowledgeBase.addKnowledgePackages(builder.getKnowledgePackages());

        // Output the packages in this knowledge base.
        Collection<KnowledgePackage> packages = knowledgeBase.getKnowledgePackages();

        StringBuilder sb = new StringBuilder();
        for (KnowledgePackage p : packages) {
            sb.append("\n  Package : " + p.getName());
            for (Rule r : p.getRules()) {
                sb.append("\n    Rule: " + r.getName());
            }
        }
        log.info("Knowledge base built with packages: " + sb.toString());

        return knowledgeBase;
    }

    public void printFacts(StatefulKnowledgeSession session) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n************************************************************");
        sb.append("\nThe following facts are currently in the system...");
        for (Object fact : session.getObjects()) {
            sb.append("\n\nFact: " + DroolsUtil.objectDetails(fact));
        }
        sb.append("\n************************************************************\n");
        log.info(sb.toString());
    }

}
