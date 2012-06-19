package uk.co.scattercode.drools.scenarios.functionimport;

import org.drools.builder.ResourceType;
import org.junit.Before;
import org.junit.Test;

import uk.co.scattercode.drools.util.AbstractRulesTest;
import uk.co.scattercode.drools.util.DroolsResource;
import uk.co.scattercode.drools.util.FactMarshallingException;
import uk.co.scattercode.drools.util.ResourcePathType;

/**
 * 
 * @author Stephen Masters
 */
public class FunctionImportJavaTest extends AbstractRulesTest {

    @Override
    public DroolsResource[] getResources() {
        return new DroolsResource[] { 
                new DroolsResource("rules/functionimport/MessageFact.drl", 
                        ResourcePathType.CLASSPATH, 
                        ResourceType.DRL),
                new DroolsResource("rules/functionimport/CloneMessage.drl", 
                        ResourcePathType.CLASSPATH, 
                        ResourceType.DRL),
                new DroolsResource("rules/functionimport/SayHelloRuleJava.drl", 
                        ResourcePathType.CLASSPATH, 
                        ResourceType.DRL)
        };
    }
    
    @Before
    public void initialize() {
        super.initialize();
    }
    
    /**
     * 
     */
    @Test
    public void shouldCloneMessage() throws FactMarshallingException {

        knowledgeEnvironment.knowledgeSession.fireAllRules();
       
    }
    
}
