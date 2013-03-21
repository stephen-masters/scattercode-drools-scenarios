package rules.tradeorder;

import static org.drools.builder.ResourceType.*;

import java.util.Date;

import org.drools.definition.type.FactType;
import org.junit.Test;

import rules.tradeorder.facts.TradeOrder;

import uk.co.scattercode.drools.util.DroolsResource;
import uk.co.scattercode.drools.util.DroolsTestUtil;
import uk.co.scattercode.drools.util.KnowledgeEnvironment;
import static uk.co.scattercode.drools.util.ResourcePathType.*;

/**
 * Test to help out with query from "kurrent93" on Drools user list.
 * 
 * @author Stephen Masters
 */
public class TradeOrderRulesTest {

    private KnowledgeEnvironment kenv = new KnowledgeEnvironment(
            new DroolsResource[] {
                    new DroolsResource("rules/tradeorder/tradeorder-facts.drl", CLASSPATH, DRL),
                    new DroolsResource("rules/tradeorder/tradeorder.dsl", CLASSPATH, DSL),
                    new DroolsResource("rules/tradeorder/tradeorder-rules.dslr", CLASSPATH, DSLR) });

    /**
     * Running this test will cause the DRL and DSLR to compile.
     */
    @Test
    public void shouldCompile() {
    }

    @Test
    public void shouldInsertTradeOrder() throws InstantiationException, IllegalAccessException {
        Date newDate = new Date(new Date().getTime() - 10);
        Date oldDate = new Date(new Date().getTime() - 600000);

        TradeOrder newOrder = new TradeOrder(newDate, "OPEN");
        kenv.insert(newOrder);
        tick();
        kenv.fireAllRules();
        DroolsTestUtil.assertRuleNotFired(kenv, "Close old trades");

        TradeOrder oldOrder = new TradeOrder(oldDate, "OPEN");
        kenv.insert(oldOrder);
        tick();
        kenv.fireAllRules();
        DroolsTestUtil.assertRuleFired(kenv, "Close old trades");
    }

    private void tick() throws InstantiationException, IllegalAccessException {
        FactType tickType = kenv.getKnowledgeBase().getFactType("rules.tradeorder", "Tick");
        Object tick = tickType.newInstance();
        tickType.set(tick, "tickTime", new Date());
        kenv.insert(tick);
    }

}

