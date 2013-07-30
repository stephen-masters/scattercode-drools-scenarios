package demos.validate;

import static org.drools.builder.ResourceType.*;
import static uk.co.scattercode.drools.util.ResourcePathType.*;

import org.drools.runtime.rule.FactHandle;
import org.junit.Test;

import uk.co.scattercode.drools.util.DroolsResource;
import uk.co.scattercode.drools.util.KnowledgeEnvironment;

public class ValidateTest {

    private KnowledgeEnvironment kenv = new KnowledgeEnvironment(
            new DroolsResource[] {
                    new DroolsResource("rules/demos/validate/validate.drl", CLASSPATH, DRL)});

    @Test
    public void shouldValidate() {
        FirstName name = new FirstName();
        name.setValue("John");
        name.setValid(true);

        FactHandle handle = kenv.insert(name);

        kenv.fireAllRules();

        assert name.isValid();

        name.setValue("John 123");
        name.setValid(true);

        kenv.update(handle, name);
        kenv.fireAllRules();

        assert !name.isValid();
    }
    
}
