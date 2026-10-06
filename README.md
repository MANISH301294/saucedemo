# SauceDemo Selenium + Cucumber Framework

A small, maintainable Java 21 UI automation framework for [SauceDemo](https://www.saucedemo.com/). It demonstrates the required purchase and invalid-login flows using Selenium WebDriver, Cucumber Gherkin, Page Objects, explicit waits, test fixtures, failure screenshots, reporting, and a config-driven JDBC utility.

## Project layout

```
src/main/java/com/saucedemo/
  config/       runtime configuration
  pages/        reusable page objects: locators and page actions only
  support/      reusable driver, wait, and database utilities
src/test/java/com/saucedemo/
  runner/       JUnit Platform Cucumber runner
  steps/        Gherkin bindings; no selectors
  support/      Cucumber lifecycle hooks
  testdata/     fixture loader
src/test/resources/
  features/     Gherkin scenarios only
  testdata/     JSON fixtures
config/         environment-variable documentation
```

## Setup and execution

Prerequisites: Java 21+, Maven 3.9+, and locally installed Chrome or Firefox. Selenium Manager resolves the matching browser driver automatically.

```bash
mvn test
mvn test -Dcucumber.filter.tags="@smoke"
mvn test -Dbrowser=firefox -Dheadless=false
mvn test -DrerunFailingTestsCount=0
```

Configuration may be supplied as JVM properties (shown above) or environment variables. See [`config/.env.example`](config/.env.example). The defaults are `chrome`, headless mode, a 10-second explicit wait, and `https://www.saucedemo.com/`. Reports are written to `target/cucumber-report.html`.

## Retry policy

Maven Surefire reruns a failed test class once (`rerunFailingTestsCount=1`) to reduce the impact of transient browser or public-demo outages. A persistent failure still fails the build, and Surefire records the rerun outcome in its test reports. Set `-DrerunFailingTestsCount=0` when diagnosing a failure without a retry. This is intentionally limited to one rerun so genuine defects are not hidden.

The retry policy applies when tests run through Maven (`mvn test`) or CI. Running `CucumberTest` directly from an IDE bypasses Maven Surefire, so it does not retry; use the IDE's Maven `test` goal when a local retry is needed.

## Locator Strategy for Adding “Sauce Labs Backpack”

**Preferred and implemented:** `By.cssSelector("[data-test='add-to-cart-sauce-labs-backpack']")`. SauceDemo exposes this stable, semantic test hook, which identifies the action and product rather than depending on position, presentation, or text.

**Fallback:** locate the inventory card using its product link text (`Sauce Labs Backpack`), then find that card's `button` through a short, scoped parent relationship. This remains product-aware but is less ideal because it depends on the card's DOM hierarchy. Neither approach uses array indexes or long brittle XPath paths.

## Engineering decisions

1. **Structure:** Reusable automation framework code lives in `src/main`; executable test specifications live in `src/test`. Gherkin documents behavior, thin step definitions orchestrate page methods, and pages own selectors and UI actions. This separation keeps test behavior isolated while allowing framework components to be reused.
2. **Wait strategy:** implicit waits are explicitly disabled. Each interaction waits for the relevant condition (visible or clickable) with `WebDriverWait`; no `Thread.sleep` is used. This synchronizes against application state instead of guessed timing.
3. **Stable locators:** centralized `data-test` locators are resilient to visual redesigns and simple to update in one place.
4. **Scaling to 50+ scenarios:** keep pages domain-focused, use fixture builders/JSON per test domain, introduce tags by risk and feature, use scenario outlines for variations, and run isolated drivers in parallel after ensuring data isolation.
5. **CI/CD:** the included GitHub Actions workflow runs `@smoke` on push and pull request, then uploads the HTML report. Secrets such as DB credentials belong in the CI secret store, not the repository.
6. **More time:** add a richer report with screenshots linked to failed steps and establish a dedicated test-data API/database cleanup fixture for parallel-safe environments.

## Database validation design

`DatabaseClient` supplies a config-driven, parameterized JDBC query method, error propagation with the original SQL exception as cause, and `AutoCloseable` teardown. It is not invoked against public SauceDemo because no database access is available.

Example UI-to-database validation pseudocode (the bounded retry is intentional: an order record may be eventually consistent):

```java
String correlationId = UUID.randomUUID().toString();
// Submit checkout with correlationId injected via a test-only API/header or fixture.
Instant deadline = Instant.now().plusSeconds(20);
AssertionError lastFailure = null;

while (Instant.now().isBefore(deadline)) {
  try (DatabaseClient db = new DatabaseClient()) {
    var rows = db.query("SELECT status, total FROM orders WHERE external_reference = ?", List.of(correlationId));
    assertEquals("COMPLETED", rows.getFirst().get("status"));
    lastFailure = null;
    break;
  } catch (AssertionError failure) {
    lastFailure = failure;
    Thread.sleep(500); // bounded polling for eventual consistency only
  }
}
if (lastFailure != null) throw lastFailure;
// finally: DELETE FROM orders WHERE external_reference = ? (or call test-only cleanup API)
```

The correlation ID isolates each execution; parameter binding prevents query injection; bounded polling handles eventual consistency; `finally` cleanup removes data. In a shared environment, use a unique per-run namespace and never delete broad production-like data.

## Video walkthrough outline

Record a 5–8-minute walkthrough: (1) project architecture and folder responsibilities, (2) Gherkin-to-step-to-page flow, (3) the stable Backpack locator and fallback, (4) config, driver, hooks, waits, screenshots, and DB utility, then (5) run `mvn test` and open the generated report. This repository intentionally does not contain a recording; add your own video link/file before submission.

## AI usage disclosure

AI assistance was used only for documentation clarity, README formatting, and general code-cleanliness review. The framework structure, Page Objects, step definitions, feature files, configuration, test data, driver/wait implementation, database utility, and CI workflow were implemented and reviewed by me.

I used the review feedback to improve wording in the README, add the assumptions section requested by the assignment, and clarify the database-validation pseudocode. These documentation changes make the project easier to understand without changing the intended automation design. I understand the implementation and can explain or modify the locator, assertions, wait strategy, and framework structure during the technical discussion.

## Assumptions

1. SauceDemo remains publicly available and its documented `standard_user` / `secret_sauce` credentials continue to be valid for this exercise.
2. Tests run against a disposable public demo environment, so the UI flow validates order confirmation only; it does not create a real payment or persistent order record that can be queried.
3. Chrome or Firefox is installed locally. Selenium Manager resolves the matching browser driver.
4. Any future database-enabled environment supplies `DB_URL`, `DB_USER`, and `DB_PASSWORD` through local environment variables or CI secrets; credentials and connection strings are never committed.
