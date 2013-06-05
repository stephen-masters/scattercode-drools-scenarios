package diet;

import static org.junit.Assert.*;
import static org.drools.builder.ResourceType.*;
import static uk.co.scattercode.drools.util.ResourcePathType.*;

import org.junit.Test;

import uk.co.scattercode.drools.util.DroolsResource;
import uk.co.scattercode.drools.util.DroolsTestUtil;
import uk.co.scattercode.drools.util.KnowledgeEnvironment;

public class MealLimitTest {

    private KnowledgeEnvironment kenv = new KnowledgeEnvironment(
            new DroolsResource[] {
                    //new DroolsResource("rules/diet.drl", CLASSPATH, DRL),
                    new DroolsResource("rules/diet.dsl", CLASSPATH, DSL),
                    new DroolsResource("rules/diet.dslr", CLASSPATH, DSLR) });

    /**
     * Running this test will cause the DRL and DSLR to compile.
     */
    @Test
    public void shouldCompile() {
    }

    @Test
    public void shouldCheckLimits() throws InstantiationException, IllegalAccessException {
        Meal meal = new Meal(1500, 13);
        
        kenv.insert(meal);
        kenv.fireAllRules();
        DroolsTestUtil.assertRuleFired(kenv, "Warn when my meal is too large");
        DroolsTestUtil.assertRuleFired(kenv, "Warn when my meal has too many calories");
        DroolsTestUtil.assertRuleFired(kenv, "Warn when my meal has too many diet club points");
    }

}
