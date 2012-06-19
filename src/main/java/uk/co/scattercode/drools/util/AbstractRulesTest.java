package uk.co.scattercode.drools.util;

/**
 * 
 * @author Stephen Masters 
 */
public abstract class AbstractRulesTest {

    protected KnowledgeEnvironment knowledgeEnvironment;
    
    public AbstractRulesTest() {
        initialize();
    }

    /**
     * Call this in your @Before methods to ensure that you have a clean
     * knowledge base for each test.
     */
    public void initialize() {
        // The getResources method is provided by the child class.
        setResources(getResources());
    }
    
    /**
     * This method must be overridden, to provide the various Drools resources
     * that will be used in the knowledge base.
     */
    public abstract DroolsResource[] getResources();

    /**
     * This method sets up the knowledge base using the array of resources
     * provided.
     */
    public final void setResources(DroolsResource[] resources) {
        knowledgeEnvironment = new KnowledgeEnvironment(resources);
    }
    
}
