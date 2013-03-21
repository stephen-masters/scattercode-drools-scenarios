package rulesusers.tradeorder;

import static org.drools.builder.ResourceType.*;

import java.util.Date;

import org.junit.Test;

import rulesusers.tradeorder.facts.TradeOrder;

import uk.co.scattercode.drools.util.DroolsResource;
import uk.co.scattercode.drools.util.KnowledgeEnvironment;
import static uk.co.scattercode.drools.util.ResourcePathType.*;

/**
 * Test to help out with query from "kurrent93" on Drools user list.
 * 
 * @author Stephen Masters
 */
public class TradeOrderRulesTest {

    private KnowledgeEnvironment kenv = new KnowledgeEnvironment(new DroolsResource[] { 
            new DroolsResource("rulesusers/tradeorder/tradeorder-facts.drl", CLASSPATH, DRL),
            new DroolsResource("rulesusers/tradeorder/tradeorder.dsl", CLASSPATH, DSL),
            new DroolsResource("rulesusers/tradeorder/tradeorder-rules.dslr", CLASSPATH, DSLR)
            });

	/**
	 * Running this test will cause the DRL and DSLR to compile.
	 */
	@Test
	public void shouldCompile() {
	}
	
	@Test
	public void shouldInsertTradeOrder() {
	    TradeOrder order = new TradeOrder(new Date(), "OPEN");
	    kenv.insert(order);
	    kenv.fireAllRules();
	    
	}

}
