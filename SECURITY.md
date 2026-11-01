# Security policy

## Reporting a vulnerability

Please report suspected vulnerabilities privately through GitHub's
[private vulnerability reporting](https://github.com/stephen-masters/scattercode-drools-scenarios/security/advisories/new)
(Security tab → "Report a vulnerability"). Do not open a public issue for a
security problem.

## Supported versions

This is a collection of experiments with no releases. Only `main` is
maintained.

## What is done to keep it safe

- Dependency versions come from the Drools and JUnit BOMs and a few pinned
  properties in `pom.xml`, kept current by Dependabot, which also keeps the
  Maven wrapper and the workflow actions current.
- Trivy scans the dependencies for HIGH and CRITICAL vulnerabilities on
  every push and pull request, and weekly on a schedule, and CI publishes a
  CycloneDX SBOM.
- The scenarios run in memory inside the test JVM. The only network access
  is a URL resource, which a test serves from a loopback HTTP server it
  starts itself; the project holds no credentials and opens no ports
  otherwise.
