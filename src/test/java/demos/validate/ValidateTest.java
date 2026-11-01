package demos.validate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.kie.api.io.ResourceType.DRL;
import static uk.co.scattercode.drools.util.ResourcePathType.CLASSPATH;

import org.junit.jupiter.api.Test;
import org.kie.api.runtime.rule.FactHandle;
import uk.co.scattercode.drools.util.DroolsResource;
import uk.co.scattercode.drools.util.KnowledgeEnvironment;

class ValidateTest {

    private final KnowledgeEnvironment kenv = new KnowledgeEnvironment(
            new DroolsResource[] {new DroolsResource("rules/demos/validate/validate.drl", CLASSPATH, DRL)});

    @Test
    void shouldValidate() {
        FirstName name = new FirstName();
        name.setValue("John");
        name.setValid(true);

        FactHandle handle = kenv.insert(name);

        kenv.fireAllRules();

        assertTrue(name.isValid());

        name.setValue("John 123");
        name.setValid(true);

        kenv.update(handle, name);
        kenv.fireAllRules();

        assertFalse(name.isValid());
    }
}
