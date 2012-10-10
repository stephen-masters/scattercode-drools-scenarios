package uk.co.scattercode.drools.test;

import java.lang.reflect.InvocationTargetException;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.beanutils.BeanUtils;
import org.drools.runtime.ObjectFilter;
import org.drools.runtime.rule.FactHandle;
import org.junit.After;
import org.junit.Before;
import static org.junit.Assert.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.scattercode.drools.util.DroolsResource;
import uk.co.scattercode.drools.util.DroolsUtil;
import uk.co.scattercode.drools.util.KnowledgeEnvironment;

/**
 * Extend this class within your unit tests so that you can gain simplified
 * access to Drools rules evaluations.
 * 
 * @author Stephen Masters
 */
public abstract class AbstractStatefulSessionRulesTest {

	private static Logger log = LoggerFactory.getLogger(AbstractStatefulSessionRulesTest.class);
	
	@Resource
	protected KnowledgeEnvironment knowledgeEnvironment 
			= new KnowledgeEnvironment(getResources());

	/**
	 * The test knowledge base will be initialised using these resources.
	 * Extending classes need to implement this method to return an array of
	 * {@link DroolsResource} references. These can be anything from DRL files
	 * on the class path to Guvnor packages.
	 * 
	 * @return An array of {@link DroolsResource} references 
	 */
	protected abstract DroolsResource[] getResources();

	/**
	 * Prior to each test, a new stateful knowledge session will be initialised,
	 * and various working memory listeners will be attached to help track your
	 * rules evaluations.
	 */
	@Before
	public void startKnowledgeSession() {
		knowledgeEnvironment.initialiseSession();
	}

	/**
	 * Nothing is currently done after each test, as the knowledge base is fully
	 * initialised prior to each test.
	 */
	@After
	public void endKnowledgeSession() {
	}

	/**
	 * Insert a POJO fact into working memory.
	 * 
	 * @param o A POJO fact.
	 */
    protected void insert(Object o) {
    	knowledgeEnvironment.knowledgeSession.insert(o);
    }
    
    /**
	 * Fire all rules in the knowledge base. Generally you will be looking to
	 * insert a number of facts and then call this method to evaluate them.
	 */
    protected void fireAllRules() {
    	knowledgeEnvironment.knowledgeSession.fireAllRules();
    }

    protected void assertRuleFired(String ruleName) {
        assertTrue("Rule [" + ruleName + "] should have fired.", 
                DroolsUtil.ruleFired(
                        this.knowledgeEnvironment.agendaEventListener.getActivationList(), 
                        ruleName));
    }

    protected void assertRuleNotFired(String ruleName) {
        assertFalse("Rule [" + ruleName + "] should not have fired.", 
                DroolsUtil.ruleFired(
                        this.knowledgeEnvironment.agendaEventListener.getActivationList(), 
                        ruleName));
    }

    protected void assertFactIsInWorkingMemory(final String factName) {
        ObjectFilter filter = new ObjectFilter() {
            @Override
            public boolean accept(Object object) {
                return object.getClass().getSimpleName().equals(factName);
            }
        };
        Collection<FactHandle> factHandles = this.knowledgeEnvironment.knowledgeSession.getFactHandles(filter);
        assertTrue("There should have been at least one [" + factName + "] in working memory.", factHandles.size() > 0);
    }

    /**
     * 
     * @param factName The 
     * @param expectedProperties A sequence of expected property name/value pairs.
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     */
    protected void assertFactIsInWorkingMemory(final String factName, AbstractMap.SimpleEntry<String, Object>... expectedProperties) 
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        ObjectFilter filter = new ObjectFilter() {
            @Override
            public boolean accept(Object object) {
                return object.getClass().getSimpleName().equals(factName);
            }
        };

        Collection<FactHandle> factHandles = this.knowledgeEnvironment.knowledgeSession.getFactHandles(filter);
        assertTrue("There should have been at least one [" + factName + "] in working memory.", factHandles.size() > 0);

        log.debug(factHandles.size() + " " + factName + " found in working memory.");

        for (FactHandle handle : factHandles) {
            Object fact = this.knowledgeEnvironment.knowledgeSession.getObject(handle);
            if (factHasAllProperties(fact, expectedProperties)) {
                log.debug("Fact in working memory has all the expected properties.");
                return;
            }
            log.debug("Fact didn't have all expected properties: " + DroolsUtil.objectDetails(fact));
        }
        fail("No facts found in working memory with the expected properties.");
    }

    private boolean factHasAllProperties(Object fact, AbstractMap.SimpleEntry<String, Object>... expectedProperties) 
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        @SuppressWarnings("unchecked")
        Map<String, Object> factProperties = BeanUtils.describe(fact);

        for (AbstractMap.SimpleEntry<String, Object> expectedProperty : expectedProperties) {
            if (!factProperties.containsKey(expectedProperty.getKey())) {
                // The fact doesn't even have this property.
                return false;
            }
            Object actualValue = factProperties.get(expectedProperty.getKey());
            Object expectedValue = expectedProperty.getValue();
            log.debug("Comparing [" + expectedProperty.getKey() + "] actual [" + actualValue + "] to expected [" + expectedValue + "].");
            if (!equivalent(actualValue, expectedValue)) {
                return false;
            }
        }

        // If there was an expected property missing or of incorrect value then we would have
        // returned false by now. The actual fact must match have all of the expected properties.
        return true;
    }
    
    private boolean equivalent(Object o1, Object o2) {
        if (o1 == null && o2 == null) return true;
        if (o1 == o2) return true;
        if (o1.equals(o2)) return true;
        return false;
    }

}
