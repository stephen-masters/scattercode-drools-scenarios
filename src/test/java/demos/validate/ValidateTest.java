package demos.validate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.kie.api.io.ResourceType.DRL;
import static uk.co.scattercode.drools.util.ResourcePathType.CLASSPATH;

import org.junit.jupiter.api.Test;
import org.kie.api.runtime.rule.FactHandle;
import uk.co.scattercode.beans.BeanPropertyFilter;
import uk.co.scattercode.drools.util.DroolsResource;
import uk.co.scattercode.drools.util.DroolsTestUtil;
import uk.co.scattercode.drools.util.KnowledgeEnvironment;

class ValidateTest {

    private static final String INVALID = "first name contains invalid characters";
    private static final String VALID = "first name is valid";

    private final KnowledgeEnvironment kenv = new KnowledgeEnvironment(
            new DroolsResource[] {new DroolsResource("rules/demos/validate/validate.drl", CLASSPATH, DRL)});

    private static FirstName firstName(String value) {
        FirstName name = new FirstName();
        name.setValue(value);
        name.setValid(true);
        return name;
    }

    @Test
    void shouldValidate() {
        FirstName name = firstName("John");

        FactHandle handle = kenv.insert(name);

        kenv.fireAllRules();

        assertTrue(name.isValid());

        name.setValue("John 123");
        name.setValid(true);

        kenv.update(handle, name);
        kenv.fireAllRules();

        assertFalse(name.isValid());
    }

    @Test
    void aNameOfLettersPunctuationAndSpacesIsLeftValid() {
        FirstName name = firstName("Mary-Jane O'Neill");

        kenv.insert(name);
        kenv.fireAllRules();

        assertTrue(name.isValid());
        DroolsTestUtil.assertRuleNotFired(kenv, INVALID);
        DroolsTestUtil.assertFactNotInWorkingMemory(kenv, "Message");
    }

    @Test
    void lettersFromOtherAlphabetsAreValid() {
        FirstName name = firstName("Ստեփան");

        kenv.insert(name);
        kenv.fireAllRules();

        assertTrue(name.isValid());
        DroolsTestUtil.assertRuleNotFired(kenv, INVALID);
    }

    @Test
    void digitsMakeTheNameInvalidAndRaiseAMessage() {
        FirstName name = firstName("John 123");

        kenv.insert(name);
        kenv.fireAllRules();

        assertFalse(name.isValid());
        DroolsTestUtil.assertRuleFired(kenv, INVALID);
        DroolsTestUtil.assertFactIsInWorkingMemory(
                kenv, "Message", new BeanPropertyFilter("message", "The first name is invalid!"));
        // The name and the message.
        assertEquals(2, kenv.getWorkingMemoryEventListener().getInsertions().size());
    }

    @Test
    void correctingTheNameRetractsTheMessage() {
        FirstName name = firstName("John 123");
        FactHandle handle = kenv.insert(name);
        kenv.fireAllRules();
        DroolsTestUtil.assertFactIsInWorkingMemory(kenv, "Message");

        name.setValue("John");
        name.setValid(true);
        kenv.update(handle, name);
        kenv.fireAllRules();

        assertTrue(name.isValid());
        DroolsTestUtil.assertRuleFired(kenv, VALID);
        DroolsTestUtil.assertFactNotInWorkingMemory(kenv, "Message");
        assertEquals(1, kenv.getWorkingMemoryEventListener().getRetractions().size());
    }

    @Test
    void withPropertyChangeSupportSettersUpdateWorkingMemoryWithoutAnUpdateCall() {
        KnowledgeEnvironment dynamic = new KnowledgeEnvironment(new DroolsResource[] {
            new DroolsResource("rules/demos/validate/property-change-support.drl", CLASSPATH, DRL),
            new DroolsResource("rules/demos/validate/validate.drl", CLASSPATH, DRL)
        });
        FirstName name = firstName("John");
        dynamic.insert(name);
        dynamic.fireAllRules();
        assertTrue(name.isValid());

        // No call to update(): the property change events tell Drools.
        name.setValue("John 123");
        dynamic.fireAllRules();

        assertFalse(name.isValid());
        DroolsTestUtil.assertRuleFired(dynamic, INVALID);
        DroolsTestUtil.assertFactIsInWorkingMemory(dynamic, "Message");
    }

    @Test
    void withoutPropertyChangeSupportAChangeGoesUnseenUntilUpdateIsCalled() {
        FirstName name = firstName("John");
        FactHandle handle = kenv.insert(name);
        kenv.fireAllRules();

        name.setValue("John 123");
        kenv.fireAllRules();

        assertTrue(name.isValid());
        DroolsTestUtil.assertRuleNotFired(kenv, INVALID);

        kenv.update(handle, name);
        kenv.fireAllRules();

        assertFalse(name.isValid());
    }
}
