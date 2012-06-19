package uk.co.scattercode.drools.util;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.drools.KnowledgeBase;
import org.drools.definition.KnowledgePackage;
import org.drools.definition.rule.Rule;
import org.drools.event.rule.ObjectInsertedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Stephen Masters
 */
public class DroolsUtil {

    private static Logger log = LoggerFactory.getLogger(DroolsUtil.class);
    
    /** Return a string containing the packages used to build the knowledge base. */
	public static String knowledgeBaseDetails(KnowledgeBase kbase) {
		if (kbase == null) {
			return "Knowledge Base is null.";
		} else {
			StringBuilder sb = new StringBuilder("Knowledge base built from the following packages:");
			Collection<KnowledgePackage> packages = kbase.getKnowledgePackages();
			for (KnowledgePackage kp : packages) {
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
			@SuppressWarnings("unchecked")
			Map<String, Object> objectProperties = BeanUtils.describe(o);
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
	
	/**
     * Search for an activation by rule name. Note that when using decision
     * tables, the rule name is generated as <code>Row N Rule_Name</code>. This
     * means that we can't just search for exact matches. This method will
     * therefore return true if an activation ends with the ruleName argument.
     * 
     * @param ruleName The name of the rule we're looking for.
     * @return
     */
    public static boolean ruleFired(List<Activation> activations, String ruleName) {
        for (Activation activation : activations) {
            if (activation.getRuleName().toUpperCase().endsWith(ruleName.toUpperCase())) {
                return true;
            }
        }
        return false;
    }
    
    public static Object findInsertedFact(List<ObjectInsertedEvent> insertions, String factType, String[] filters) {
        for (ObjectInsertedEvent event : insertions) {
            Object fact = event.getObject();
            
            if (factType.equals(fact.getClass().getSimpleName())) {
                if (BeanMatcher.matches(fact, filters)) {
                    return fact;
                }
            }
        }
        return null;
    }
    
}
