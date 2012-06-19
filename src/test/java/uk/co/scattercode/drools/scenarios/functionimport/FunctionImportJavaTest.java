package uk.co.scattercode.drools.scenarios.functionimport;

import static org.drools.builder.ResourceType.*;
import org.junit.Before;
import org.junit.Test;

import uk.co.scattercode.drools.util.AbstractRulesTest;
import uk.co.scattercode.drools.util.DroolsResource;
import uk.co.scattercode.drools.util.FactMarshallingException;
import static uk.co.scattercode.drools.util.ResourcePathType.*;

/**
 * 
 * @author Stephen Masters
 */
public class FunctionImportJavaTest extends AbstractRulesTest {

    @Override
    public DroolsResource[] getResources() {
        return new DroolsResource[] { 
            new DroolsResource("rules/functionimport/MessageFact.drl", CLASSPATH, DRL),
            new DroolsResource("rules/functionimport/CloneMessage.drl", CLASSPATH, DRL),
            new DroolsResource("rules/functionimport/SayHelloRuleJava.drl", CLASSPATH, DRL)
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
