package uk.co.scattercode.drools.scenarios.functionimport;

import static org.drools.builder.ResourceType.*;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.scattercode.drools.util.AbstractStatefulSessionRulesTest;
import uk.co.scattercode.drools.util.DroolsResource;
import uk.co.scattercode.drools.util.FactMarshallingException;
import static uk.co.scattercode.drools.util.ResourcePathType.*;

/**
 * 
 * @author Stephen Masters
 */
public class FunctionImportMvelTest extends AbstractStatefulSessionRulesTest {

    private static Logger log = LoggerFactory.getLogger(FunctionImportMvelTest.class);
    
    @Override
    public DroolsResource[] getResources() {
        return new DroolsResource[] { 
            new DroolsResource("rules/functionimport/MessageFact.drl", CLASSPATH, DRL),
            new DroolsResource("rules/functionimport/CloneMessage.drl", CLASSPATH, DRL),
            new DroolsResource("rules/functionimport/SayHelloRuleMvel.drl", CLASSPATH, DRL)
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
