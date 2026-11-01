package uk.co.scattercode.drools.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.kie.api.io.ResourceType.DRL;

import com.sun.net.httpserver.HttpServer;
import demos.validate.FirstName;
import demos.validate.Message;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.kie.api.runtime.rule.FactHandle;

class KnowledgeEnvironmentTest {

    private static final String DRL_PATH = "rules/demos/validate/validate.drl";
    private static final String INVALID = "first name contains invalid characters";

    private static KnowledgeEnvironment fromClasspath() {
        return new KnowledgeEnvironment(
                new DroolsResource[] {new DroolsResource(DRL_PATH, ResourcePathType.CLASSPATH, DRL)});
    }

    private static FirstName firstName(String value) {
        FirstName name = new FirstName();
        name.setValue(value);
        name.setValid(true);
        return name;
    }

    private static byte[] validateDrl() throws IOException {
        try (InputStream in = KnowledgeEnvironmentTest.class.getClassLoader().getResourceAsStream(DRL_PATH)) {
            return in.readAllBytes();
        }
    }

    /** Fires the rules for an invalid name, which proves the rules were loaded. */
    private static void assertRulesLoaded(KnowledgeEnvironment kenv) {
        FirstName name = firstName("R2D2");
        kenv.insert(name);
        kenv.fireAllRules();
        assertFalse(name.isValid());
        DroolsTestUtil.assertRuleFired(kenv, INVALID);
    }

    @Test
    void rulesCanBeLoadedFromAFile(@TempDir Path dir) throws IOException {
        Path drl = Files.write(dir.resolve("validate.drl"), validateDrl());

        assertRulesLoaded(new KnowledgeEnvironment(
                new DroolsResource[] {new DroolsResource(drl.toString(), ResourcePathType.FILE, DRL)}));
    }

    @Test
    void rulesCanBeLoadedFromAUrlBehindBasicAuthentication() throws IOException {
        byte[] drl = validateDrl();
        String expected =
                "Basic " + Base64.getEncoder().encodeToString("rules:secret".getBytes(StandardCharsets.UTF_8));
        HttpServer server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 0);
        server.createContext("/rules/validate.drl", exchange -> {
            boolean authorised = expected.equals(exchange.getRequestHeaders().getFirst("Authorization"));
            byte[] body = authorised ? drl : new byte[0];
            exchange.sendResponseHeaders(authorised ? 200 : 401, authorised ? body.length : -1);
            exchange.getResponseBody().write(body);
            exchange.close();
        });
        server.start();
        try {
            String url = "http://localhost:" + server.getAddress().getPort() + "/rules/validate.drl";

            assertRulesLoaded(new KnowledgeEnvironment(
                    new DroolsResource[] {new DroolsResource(url, ResourcePathType.URL, DRL, "rules", "secret")}));

            IllegalStateException e = assertThrows(
                    IllegalStateException.class,
                    () -> new KnowledgeEnvironment(
                            new DroolsResource[] {new DroolsResource(url, ResourcePathType.URL, DRL)}));
            assertTrue(e.getMessage().contains("HTTP 401"), e.getMessage());
        } finally {
            server.stop(0);
        }
    }

    @Test
    void theTrackingListenerRecordsInsertionsUpdatesAndRetractions() {
        KnowledgeEnvironment kenv = fromClasspath();
        FirstName name = firstName("John");
        FactHandle handle = kenv.insert(name);
        name.setValue("Jane");
        kenv.update(handle, name);
        kenv.retract(handle);

        TrackingWorkingMemoryEventListener listener = kenv.getWorkingMemoryEventListener();
        assertEquals(1, listener.getInsertions().size());
        assertEquals(1, listener.getUpdates().size());
        assertEquals(1, listener.getRetractions().size());
        assertEquals(3, listener.getAllEvents().size());
        assertEquals("Jane", listener.getFactChanges().get(0).get("value"));
        assertEquals(
                "TrackingWorkingMemoryEventListener: insertions=[1], retractions=[1], updates=[1]",
                listener.getPrintableSummary());
        assertSame(
                name,
                kenv.getWorkingMemoryEventListener().getRetractions().get(0).getOldObject());
    }

    @Test
    void aClassFilteredListenerIgnoresOtherFacts() {
        KnowledgeEnvironment kenv = fromClasspath();
        TrackingWorkingMemoryEventListener messages = new TrackingWorkingMemoryEventListener(Message.class);
        kenv.addEventListener(messages);

        kenv.insert(firstName("John 123"));
        kenv.fireAllRules();

        assertEquals(1, messages.getInsertions().size());
        assertTrue(messages.getInsertions().get(0).getObject() instanceof Message);
        // The unfiltered listener saw the name as well, and the rule's modify of it.
        assertEquals(2, kenv.getWorkingMemoryEventListener().getInsertions().size());
        assertEquals(1, kenv.getWorkingMemoryEventListener().getUpdates().size());
    }

    @Test
    void aHandleFilteredListenerOnlySeesThatFact() {
        KnowledgeEnvironment kenv = fromClasspath();
        FactHandle john = kenv.insert(firstName("John"));
        TrackingWorkingMemoryEventListener johnOnly = new TrackingWorkingMemoryEventListener(john);
        kenv.addEventListener(johnOnly);

        kenv.insert(firstName("Jane"));
        kenv.update(john, kenv.getObject(john));

        assertEquals(0, johnOnly.getInsertions().size());
        assertEquals(1, johnOnly.getUpdates().size());
    }

    @Test
    void theAgendaListenerRecordsEachRuleThatFires() {
        KnowledgeEnvironment kenv = fromClasspath();
        kenv.insert(firstName("John 123"));
        kenv.fireAllRules();

        TrackingAgendaEventListener agenda = kenv.getAgendaEventListener();
        assertTrue(agenda.isRuleFired(INVALID));
        assertFalse(agenda.isRuleFired("first name is valid"));
        assertEquals(
                List.of(INVALID),
                kenv.findActivations().stream().map(Activation::getRuleName).toList());
        assertEquals("Activations: \n  rule: " + INVALID, agenda.activationsToString());

        agenda.reset();
        assertEquals("No activations occurred.", agenda.activationsToString());
    }

    @Test
    void anAgendaFilterStopsOtherRulesFiring() {
        KnowledgeEnvironment kenv = fromClasspath();
        FirstName name = firstName("John 123");
        kenv.insert(name);
        kenv.fireAllRules(match -> !match.getRule().getName().equals(INVALID));

        assertTrue(name.isValid());
        DroolsTestUtil.assertRuleNotFired(kenv, INVALID);
    }

    @Test
    void reinitialisingTheSessionRetractsEveryFactAndResetsTheListeners() {
        KnowledgeEnvironment kenv = fromClasspath();
        kenv.insert(firstName("John 123"));
        kenv.fireAllRules();
        assertEquals(2, kenv.getFactHandles().size());

        kenv.initialiseSession();

        assertTrue(kenv.getFactHandles().isEmpty());
        assertTrue(kenv.findActivations().isEmpty());
        assertTrue(kenv.getWorkingMemoryEventListener().getAllEvents().isEmpty());
    }

    @Test
    void retractAllWithAFilterOnlyRetractsMatchingFacts() {
        KnowledgeEnvironment kenv = fromClasspath();
        kenv.insert(firstName("John 123"));
        kenv.fireAllRules();

        kenv.retractAll(fact -> fact instanceof Message);

        DroolsTestUtil.assertFactNotInWorkingMemory(kenv, "Message");
        DroolsTestUtil.assertFactIsInWorkingMemory(kenv, "FirstName");
        assertEquals(1, kenv.getObjects(fact -> true).size());
    }

    @Test
    void factsCanBeInsertedAndRetractedInBulk() {
        KnowledgeEnvironment kenv = fromClasspath();
        List<FactHandle> handles = kenv.insert(List.<Object>of(firstName("John"), firstName("Jane")));
        assertEquals(2, kenv.getFactHandles().size());

        kenv.retract(handles);

        assertTrue(kenv.getFactHandles().isEmpty());
    }

    @Test
    void toStringListsThePackagesAndRules() {
        String description = fromClasspath().toString();
        assertTrue(description.contains("Package : demos.validate"), description);
        assertTrue(description.contains("Rule: " + INVALID), description);
    }
}
