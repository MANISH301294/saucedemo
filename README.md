# SauceDemo Selenium + Cucumber Framework

A small, maintainable Java 21 UI automation framework for [SauceDemo](https://www.saucedemo.com/). It demonstrates the required purchase and invalid-login flows using Selenium WebDriver, Cucumber Gherkin, Page Objects, explicit waits, test fixtures, failure screenshots, reporting, and a config-driven JDBC utility.

## Project layout

```
src/test/java/com/saucedemo/
  config/       environment configuration
  pages/        locators and page actions only
  runner/       JUnit Platform Cucumber runner
  steps/        Gherkin bindings; no selectors
  support/      driver factory, hooks, waits, database client
  testdata/     fixture loader
src/test/resources/
  features/     Gherkin scenarios only
  testdata/     JSON fixtures
config/         .env.example documents configuration
```

## Setup and execution

Prerequisites: Java 21+, Maven 3.9+, and locally installed Chrome or Firefox. Selenium Manager resolves the matching browser driver automatically.

```bash
mvn test
mvn test -Dcucumber.filter.tags="@smoke"
mvn test -Dbrowser=firefox -Dheadless=false
```

Configuration may be supplied as JVM properties (shown above) or environment variables. See [`config/.env.example`](config/.env.example). The defaults are `chrome`, headless mode, a 10-second explicit wait, and `https://www.saucedemo.com/`. Reports are written to `target/cucumber-report.html` and `target/cucumber-report.json`.

## Locator Strategy for Adding “Sauce Labs Backpack”

**Preferred and implemented:** `By.cssSelector("[data-test='add-to-cart-sauce-labs-backpack']")`. SauceDemo exposes this stable, semantic test hook, which identifies the action and product rather than depending on position, presentation, or text.

**Fallback:** locate the inventory card using its product link text (`Sauce Labs Backpack`), then find that card's `button` through a short, scoped parent relationship. This remains product-aware but is less ideal because it depends on the card's DOM hierarchy. Neither approach uses array indexes or long brittle XPath paths.

## Engineering decisions

1. **Structure:** Gherkin documents behavior; thin step definitions orchestrate page methods; pages own selectors and UI actions. Support code centralizes cross-cutting concerns, so adding scenarios does not duplicate browser or wait logic.
2. **Wait strategy:** implicit waits are explicitly disabled. Each interaction waits for the relevant condition (visible or clickable) with `WebDriverWait`; no `Thread.sleep` is used. This synchronizes against application state instead of guessed timing.
3. **Stable locators:** centralized `data-test` locators are resilient to visual redesigns and simple to update in one place.
4. **Scaling to 50+ scenarios:** keep pages domain-focused, use fixture builders/JSON per test domain, introduce tags by risk and feature, use scenario outlines for variations, and run isolated drivers in parallel after ensuring data isolation.
5. **CI/CD:** the included GitHub Actions workflow runs `@smoke` on push and pull request, then uploads the HTML report. Secrets such as DB credentials belong in the CI secret store, not the repository.
6. **More time:** add a richer report with screenshots linked to failed steps and establish a dedicated test-data API/database cleanup fixture for parallel-safe environments.

## Database validation design

`DatabaseClient` supplies a config-driven, parameterized JDBC query method, error propagation with the original SQL exception as cause, and `AutoCloseable` teardown. It is not invoked against public SauceDemo because no database access is available.

Example UI-to-database validation pseudocode:

```java
String correlationId = UUID.randomUUID().toString();
// Submit checkout with correlationId injected via a test-only API/header or fixture.
await().atMost(20, SECONDS).pollInterval(500, MILLISECONDS).untilAsserted(() -> {
  try (DatabaseClient db = new DatabaseClient()) {
    var rows = db.query("SELECT status, total FROM orders WHERE external_reference = ?", List.of(correlationId));
    assertEquals("COMPLETED", rows.getFirst().get("status"));
  }
});
// finally: DELETE FROM orders WHERE external_reference = ? (or call test-only cleanup API)
```

The correlation ID isolates each execution; parameter binding prevents query injection; bounded polling handles eventual consistency; `finally` cleanup removes data. In a shared environment, use a unique per-run namespace and never delete broad production-like data.

## Video walkthrough outline

Record a 5–8-minute walkthrough: (1) project architecture and folder responsibilities, (2) Gherkin-to-step-to-page flow, (3) the stable Backpack locator and fallback, (4) config, driver, hooks, waits, screenshots, and DB utility, then (5) run `mvn test` and open the generated report. This repository intentionally does not contain a recording; add your own video link/file before submission.

## AI usage disclosure

AI assistance was used to scaffold the initial project, test structure, and documentation from the supplied assignment brief. Review, understand, adapt, and execute the code yourself before submitting—especially the locator and wait decisions—so you can explain and modify it during the technical discussion.
