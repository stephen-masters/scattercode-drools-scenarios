package uk.co.scattercode.drools.scenarios.functionimport;

import static org.drools.builder.ResourceType.*;
import org.junit.Test;

import uk.co.scattercode.drools.util.AbstractStatefulSessionRulesTest;
import uk.co.scattercode.drools.util.DroolsResource;
import uk.co.scattercode.drools.util.FactMarshallingException;
import static uk.co.scattercode.drools.util.ResourcePathType.*;

/**
 * 
 * @author Stephen Masters
 */
public class FunctionImportJavaTest extends AbstractStatefulSessionRulesTest {

    @Override
    protected DroolsResource[] getResources() {
        return new DroolsResource[] { 
            new DroolsResource("scenarios/functionimport/MessageFact.drl", CLASSPATH, DRL),
            new DroolsResource("scenarios/functionimport/CloneMessage.drl", CLASSPATH, DRL),
            new DroolsResource("scenarios/functionimport/SayHelloRuleJava.drl", CLASSPATH, DRL)
        };
    }
    
    /**
     * 
     */
    @Test
    public void shouldCloneMessage() throws FactMarshallingException {

        knowledgeEnvironment.knowledgeSession.fireAllRules();
       
    }
    
}
