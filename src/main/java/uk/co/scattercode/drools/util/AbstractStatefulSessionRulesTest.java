package uk.co.scattercode.drools.util;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;

/**
 * 
 * @author Stephen Masters
 */
public abstract class AbstractStatefulSessionRulesTest {

	@Resource
	protected KnowledgeEnvironment knowledgeEnvironment 
			= new KnowledgeEnvironment(getResources());

	protected abstract DroolsResource[] getResources();

	@Before
	public void startKnowledgeSession() {
		knowledgeEnvironment.initialiseSession();
	}

	@After
	public void endKnowledgeSession() {
	}

    protected void insert(Object o) {
    	knowledgeEnvironment.knowledgeSession.insert(o);
    }
    
    protected void fireAllRules() {
    	knowledgeEnvironment.knowledgeSession.fireAllRules();
    }

}
