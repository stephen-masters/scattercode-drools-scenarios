package uk.co.scattercode.drools.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.kie.api.io.ResourceType.DRL;

import demos.validate.FirstName;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.kie.api.KieBase;

class DroolsUtilTest {

    private final List<Activation> activations =
            List.of(new Activation("Row 1 Check_Limit"), new Activation("first name is valid"));

    @Test
    void aRuleFiredIfAnActivationEndsWithItsNameIgnoringCase() {
        assertTrue(DroolsUtil.ruleFired(activations, "check_limit"));
        assertTrue(DroolsUtil.ruleFired(activations, "FIRST NAME IS VALID"));
        assertFalse(DroolsUtil.ruleFired(activations, "Row 1"));
    }

    @Test
    void allRulesFiredOnlyWhenEveryNameIsFound() {
        assertTrue(DroolsUtil.allRulesFired(activations, new String[] {"Check_Limit", "first name is valid"}));
        assertFalse(DroolsUtil.allRulesFired(activations, new String[] {"Check_Limit", "missing"}));
    }

    @Test
    void prettyRulesFiredShowsWhichRulesFired() {
        assertEquals(
                "\n    Check_Limit : Y\n    missing : N",
                DroolsUtil.prettyRulesFired(activations, new String[] {"Check_Limit", "missing"}));
    }

    @Test
    void objectDetailsListsTheBeanProperties() {
        FirstName name = new FirstName();
        name.setValue("John");
        String details = DroolsUtil.objectDetails(name);
        assertTrue(details.startsWith("FirstName"), details);
        assertTrue(details.contains("value=\"John\""), details);
        assertTrue(details.contains("valid=\"false\""), details);
    }

    @Test
    void theKnowledgeBaseIsBuiltFromEveryResourceAndListsItsRules() {
        KieBase kbase = DroolsUtil.createKnowledgeBase(new DroolsResource[] {
            new DroolsResource("rules/demos/validate/validate.drl", ResourcePathType.CLASSPATH, DRL)
        });
        String details = DroolsUtil.knowledgeBaseDetails(kbase);
        assertTrue(details.contains("Package: [demos.validate]"), details);
        assertTrue(details.contains("Rule: [first name contains invalid characters]"), details);
        assertTrue(details.contains("Rule: [first name is valid]"), details);
        assertEquals("Knowledge Base is null.", DroolsUtil.knowledgeBaseDetails(null));
    }

    @Test
    void aResourceThatDoesNotCompileIsReported() {
        RuntimeException e = assertThrows(
                RuntimeException.class,
                () -> DroolsUtil.createKnowledgeBase(
                        new DroolsResource[] {new DroolsResource("rules/broken.drl", ResourcePathType.CLASSPATH, DRL)
                        }));
        assertTrue(e.getMessage().contains("NoSuchFact"), e.getMessage());
    }

    @Test
    void findInsertedFactMatchesOnTypeAndFilters() {
        KnowledgeEnvironment kenv = new KnowledgeEnvironment(new DroolsResource[] {
            new DroolsResource("rules/demos/validate/validate.drl", ResourcePathType.CLASSPATH, DRL)
        });
        FirstName john = new FirstName();
        john.setValue("John");
        FirstName jane = new FirstName();
        jane.setValue("Jane");
        kenv.insert(john, jane);

        var insertions = kenv.getWorkingMemoryEventListener().getInsertions();
        assertSame(jane, DroolsUtil.findInsertedFact(insertions, "FirstName", new String[] {"value=Jane"}));
        assertNull(DroolsUtil.findInsertedFact(insertions, "FirstName", new String[] {"value=Bob"}));
        assertNull(DroolsUtil.findInsertedFact(insertions, "Message", new String[0]));
    }
}
