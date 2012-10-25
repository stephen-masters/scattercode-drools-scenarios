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

import uk.co.scattercode.beans.BeanMatcher;
import uk.co.scattercode.beans.BeanPropertyFilter;
import uk.co.scattercode.beans.BeanPropertyStringMatcher;
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
	
	private static final BeanMatcher beanMatcher = new BeanPropertyStringMatcher(); 
	
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
    protected FactHandle insert(Object o) {
    	return knowledgeEnvironment.knowledgeSession.insert(o);
    }
    
    protected void update(FactHandle factHandle, Object o) {
    	knowledgeEnvironment.knowledgeSession.update(factHandle, o);
    }
    
    protected void retract(FactHandle handle) {
    	knowledgeEnvironment.knowledgeSession.retract(handle);
    }
    
    /**
	 * Fire all rules in the knowledge base. Generally you will be looking to
	 * insert a number of facts and then call this method to evaluate them.
	 */
    protected void fireAllRules() {
    	knowledgeEnvironment.knowledgeSession.fireAllRules();
    }

    protected void assertRuleFired(String... ruleNames) {
    	for (String ruleName : ruleNames) {
	        assertTrue("Rule [" + ruleName + "] should have fired.", 
	                DroolsUtil.ruleFired(
	                        this.knowledgeEnvironment.agendaEventListener.getActivationList(), 
	                        ruleName));
    	}
    }

    protected void assertRuleNotFired(String... ruleNames) {
    	for (String ruleName : ruleNames) {
	        assertFalse("Rule [" + ruleName + "] should not have fired.", 
	                DroolsUtil.ruleFired(
	                        this.knowledgeEnvironment.agendaEventListener.getActivationList(), 
	                        ruleName));
    	}
    }

    /**
	 * @param factClasses
	 *            The simple names of the classes of the facts expected to be in
	 *            working memory.
	 */
    protected void assertFactIsInWorkingMemory(final String... factClasses) {
    	for (final String factName : factClasses) {
	        ObjectFilter filter = new ObjectFilter() {
	            @Override
	            public boolean accept(Object object) {
	                return object.getClass().getSimpleName().equals(factName);
	            }
	        };
	        Collection<FactHandle> factHandles = this.knowledgeEnvironment.knowledgeSession.getFactHandles(filter);
	        assertTrue("There should have been at least one [" + factName + "] in working memory.", factHandles.size() > 0);
    	}
    }

    /**
	 * A more complex assertion that a fact of the expected class with specified
	 * properties is in working memory.
	 * 
	 * @param factName
	 *            The simple name of the class of the fact we're looking for.
	 * @param expectedProperties
	 *            A sequence of expected property name/value pairs.
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
    protected void assertFactIsInWorkingMemory(final String factClass, BeanPropertyFilter... expectedProperties) {

        ObjectFilter filter = new ObjectFilter() {
            @Override
            public boolean accept(Object object) {
                return object.getClass().getSimpleName().equals(factClass);
            }
        };

        Collection<FactHandle> factHandles = this.knowledgeEnvironment.knowledgeSession.getFactHandles(filter);
        assertTrue("There should have been at least one [" + factClass + "] in working memory.", factHandles.size() > 0);

        log.debug(factHandles.size() + " " + factClass + " found in working memory.");

        for (FactHandle handle : factHandles) {
            Object fact = this.knowledgeEnvironment.knowledgeSession.getObject(handle);
            if (beanMatcher.matches(fact, expectedProperties)) {
                log.debug("Fact in working memory has all the expected properties.");
                return;
            }
            log.debug("Fact didn't have all expected properties: " + DroolsUtil.objectDetails(fact));
        }
        fail("No facts found in working memory with the expected properties.");
    }

}
