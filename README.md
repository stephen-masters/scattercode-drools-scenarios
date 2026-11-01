scattercode-drools-scenarios
============================

A bundle of test scenarios, used to help me experiment with Drools. It is not an application:
the scenarios are JUnit tests, and the main code is a small kit for writing them.

| Package | What |
|---|---|
| `uk.co.scattercode.drools.util` | `KnowledgeEnvironment`, which builds a knowledge base from classpath, file or URL resources and opens a session with tracking listeners attached; `DroolsTestUtil`, with assertions that a rule fired or a fact is in working memory |
| `uk.co.scattercode.beans` | `BeanMatcher`, which matches facts on the string values of their bean properties |
| `demos.validate` | The validation scenario: `validate.drl` marks a `FirstName` invalid when it holds anything but letters, punctuation and spaces, and raises a `Message` that is retracted again once the name is corrected |

`ValidateTest` also shows property change support. `Value` fires `PropertyChangeEvent`s from its
setters, so once a DRL declares `FirstName` with `@propertyChangeSupport`, Drools listens to each
name it is given and a setter call is enough to update working memory, with no call to `update()`.

It needs Java 25 and uses Apache KIE Drools 10. The Maven wrapper downloads Maven itself, so to
run a full build, including the format check and the tests:

    ./mvnw clean verify

If the format check fails, `./mvnw spotless:apply` fixes it. To install the git hooks, which
check formatting before each commit and run the build before each push:

    lefthook install

The branches `2012-08-bmi`, `2013-03-dsl-example`, `2013-07-30-validate-first-name` and
`based_on_drools_5.4.0.Final` keep the code as it was for some older blog posts, on the Drools
versions of the day. They are not maintained.

See [AGENTS.md](AGENTS.md) for the conventions and [SECURITY.md](SECURITY.md) for reporting a
vulnerability.

Licensed under the [MIT License](LICENSE): copy and adapt it freely.
