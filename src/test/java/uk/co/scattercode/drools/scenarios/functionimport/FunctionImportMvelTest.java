package uk.co.scattercode.drools.scenarios.functionimport;

import org.drools.builder.ResourceType;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.scattercode.drools.util.AbstractRulesTest;
import uk.co.scattercode.drools.util.DroolsResource;
import uk.co.scattercode.drools.util.FactMarshallingException;
import uk.co.scattercode.drools.util.ResourcePathType;

/**
 * 
 * @author Stephen Masters
 */
public class FunctionImportMvelTest extends AbstractRulesTest {

    private static Logger log = LoggerFactory.getLogger(FunctionImportMvelTest.class);
    
    @Override
    public DroolsResource[] getResources() {
        return new DroolsResource[] { 
                new DroolsResource("rules/functionimport/MessageFact.drl", 
                        ResourcePathType.CLASSPATH, 
                        ResourceType.DRL),
                new DroolsResource("rules/functionimport/CloneMessage.drl", 
                        ResourcePathType.CLASSPATH, 
                        ResourceType.DRL),
                new DroolsResource("rules/functionimport/SayHelloRuleMvel.drl", 
                        ResourcePathType.CLASSPATH, 
                        ResourceType.DRL)
        };
    }
    
    @Before
    public void initialize() {
        try {
            super.initialize();
        } catch (Throwable t) {
            log.error("When using MVEL dialect, the function import fails. " 
                    + "Or perhaps the function fails to compile.", t);
        }
    }
    
    /**
     * 
     */
    @Test
    public void shouldCloneMessage() throws FactMarshallingException {

        knowledgeEnvironment.knowledgeSession.fireAllRules();
        
    }
    
}
