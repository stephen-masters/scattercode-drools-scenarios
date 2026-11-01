# AGENTS.md

Guidance for anyone working in this repository with a coding agent (Claude
Code, Codex, Cursor, Gemini CLI or whatever else reads this file) and a fair
summary of the house rules for contributors who use none.

## What this repository is

**scattercode-drools-scenarios** is a bundle of Drools experiments written as
JUnit tests, with a small kit in `src/main/java` for writing them. It is not
an application and has no Spring Boot: nothing is released or deployed.

`KnowledgeEnvironment` compiles DRL (or any other `ResourceType`) from the
classpath, a file or a URL into a knowledge base, opens a stateful session,
and attaches two listeners: `TrackingAgendaEventListener`, which records the
rules that fire, and `TrackingWorkingMemoryEventListener`, which records
insertions, updates and retractions. `DroolsTestUtil` turns those records
into assertions.

The one scenario on `main` is `demos.validate`: `validate.drl` marks a
`FirstName` invalid when it holds anything but letters, punctuation and
white space, and inserts a `Message`; once the name is valid again the
message is retracted. `ValidateTest` also covers property change support:
`Value` fires `PropertyChangeEvent`s, and with `FirstName` declared
`@propertyChangeSupport` a setter call updates working memory on its own.

## Hard rules

### Leave the old branches alone

`2012-08-bmi`, `2013-03-dsl-example`, `2013-07-30-validate-first-name` and
`based_on_drools_5.4.0.Final` keep the code that old blog posts describe, on
the Drools of the day. Do not rebase, update or delete them.

### Rules files change only when Drools requires it

A scenario is a claim about how Drools behaves with a given DRL. Change the
`.drl` only when a Drools upgrade forces it, and say so in the commit. Put
DRL that exists only to support a test under `src/test/resources`.

### Do not weaken the scenario assertions

A failing scenario after an upgrade is a finding about Drools, not a test to
relax. Keep the property change tests: they are the reason `Value` carries a
`PropertyChangeSupport`.

### Stay on current versions

The project tracks the latest LTS JDK (`java.version` in `pom.xml`, and
`java-version` in the workflows; change them together) and the latest Apache
KIE Drools release. With no Spring Boot parent, versions come from the
imported `drools-bom` and `junit-bom`; anything they do not manage, plugins
included, is a property in `pom.xml`. Do not pin a version a BOM already
manages.

## Layout

| Path | What |
|---|---|
| `src/main/java/uk/co/scattercode/drools/util/` | `KnowledgeEnvironment`, `DroolsUtil`, `DroolsTestUtil`, the tracking listeners |
| `src/main/java/uk/co/scattercode/beans/` | `BeanMatcher` and `BeanPropertyFilter`, for matching facts on their properties |
| `src/main/java/demos/validate/` | The validation scenario's facts |
| `src/main/resources/rules/demos/validate/validate.drl` | The validation rules |
| `src/test/java/` | The scenarios, and tests of the kit itself |
| `src/test/resources/` | Test-only DRL and `logback-test.xml` |
| `.githooks/commit-msg` | Conventional Commits check, shared from `scattercode/release-pipelines` |

## Checks

```bash
./mvnw spotless:apply   # format the Java sources and the POM
./mvnw verify           # everything below, and the tests
```

`./mvnw verify` runs, in order:

- the **enforcer**: the JDK is at least `java.version`, Maven is at least
  3.9, and dependency versions converge;
- the **Spotless** check (palantir-java-format for Java, sortpom for
  `pom.xml`) in the `validate` phase;
- the CycloneDX **SBOM** at `target/classes/META-INF/sbom/application.cdx.json`
  in `generate-resources`, which CI scans with Trivy;
- the **compiler** with `-Xlint:all -Werror`, so any warning fails the build;
- the **tests**.

It needs JDK 25; the Maven wrapper fetches Maven itself. All of it must
pass before a commit. The lefthook hooks run the format check before each
commit and the full build before each push; CI runs the build and a Trivy
scan on every push and pull request.

## Conventions

- British English; sentence case for headings.
- Conventional Commits, enforced by `.githooks/commit-msg` and, for pull
  request titles, by `pr-title.yml`. Dependency updates are `build:`,
  workflow changes `ci:`.
- `.githooks/commit-msg` is a byte-for-byte copy of the template in
  `scattercode/release-pipelines`; CI checks it has not drifted. Change it
  there, not here.
- Logging goes through SLF4J; the tests bind it to Logback. The jar ships no
  logging configuration.
- `main` is protected: changes arrive through pull requests with the CI
  checks passing.
- Hook setup in a fresh clone: `lefthook install`, or without lefthook,
  `git config core.hooksPath .githooks` for the commit message check alone.
