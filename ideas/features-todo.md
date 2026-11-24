# Feature Ideas

Priority order: bugs → data integrity → tech debt → operational → features. Clear tech debt before adding features.

## 1. Bugs

- [ ] **Invalid ids and rejected mutations return a 500 page instead of an error message.**
    - Where: in `ExpenseWebController`, `removeIncome`, `removePayment`, `removeSaving`, `editSaving`, `toggleDayDone` and
      `skipPendingPayment` don't catch `IllegalArgumentException` (only `MonthClosedException` has an `@ExceptionHandler`). The REST
      `ExpenseController` catches nothing.
    - Effect: a stale page (e.g. deleting a payment already deleted in another tab) or a day outside any month shows the white error page;
      the REST API answers 500 where a 4xx is expected. This is the same kind of problem as your editIncome fix.

## 2. Data Integrity & Validation

- [ ] **Validate inputs at service and DB level.**
    - Amounts/budgets: negative values are accepted everywhere (`addPayment`, `addIncome`, `adjustBudget`, templates). Spring also binds the
      strings `NaN` and `Infinity` to `double` params, which then poison every sum.
    - Dates: an end date before the start date generates no days. On create this fails with the unclear "No dates found" from
      `computeMonth`; on edit `PaymentsGenerator.resolveEndDate` calls `getLast()` on the empty list and throws `NoSuchElementException` (500).
    - Text: description, source, category and names can be blank; anything over 255 chars fails at the DB (`varchar(255)` in V1) as a 500.
    - Saving percentages: `addSaving` only checks against the unallocated share (negative passes); `editSaving` checks nothing, so savings
      can sum past 100% or go negative.
    - DB: no `CHECK` constraints exist, so the DB accepts any of the above.
- [ ] **Define/enforce duplicate recurring-template pull behavior per month.**
    - Where: `pullTemplatesIntoMonth` creates a new pending payment and adds its amount to the budget for every selected template, with no
      check for what the month already has.
    - Effect: pulling "Rent" twice gives two pending Rent entries and doubles the fixed budget.
- [ ] **Clarify manual budget overwrite vs pending-template add/skip semantics.**
    - Where: `adjustBudget(type, value)` overwrites the budget; pulling a template adds to it; skipping a pending payment (`removePendingPayment`)
      subtracts from it.
    - Effect: after a manual overwrite, skipping a pending payment subtracts from a number it was never part of, so the budget can end up
      too low or negative.
- [ ] **Migrate money from `double` to `BigDecimal`** in a dedicated Flyway-backed change.
    - Why: binary floating point accumulates rounding errors in sums (`0.1 + 0.2 != 0.3`), and the projection compares totals.
    - Scope: entity columns to `DECIMAL(19,2)`, DTOs, `OverallSumsTracker` arithmetic, controller params, templates' number formatting.

## 3. Tech Debt: Tests

- [ ] **Add a Flyway-enabled migration integration test for a fresh DB (V1 and future migrations).**
    - Where: `src/test/resources/application.properties` sets `spring.flyway.enabled=false` and `ddl-auto=create-drop`, so every test runs
      on a schema Hibernate generates itself.
    - Effect: the real V1–V4 chain and `ddl-auto=validate` against the migrated schema are never tested; a broken migration only shows up
      when the app boots on a real DB (as happened with V2 and V3).
- [ ] Add MVC integration tests: real request/status behavior, invalid input, `MonthClosedException` handling.
- [ ] Add the missing `MonthlyExpenseRepositoryTest` (see test plan §7).

## 4. Tech Debt: Persistence & Architecture

- [ ] Move `EndDateBackfillRunner` into a versioned Flyway data migration, then remove the runner.
- [ ] **Replace `findAll()` child-ID scans and eager loading before Search grows.**
    - Where: `removePaymentById`, `editPayment`, `removeIncomeById`, `editIncome`, `removeSavingById`, `editSaving` and pending-payment lookups
      load every month to find one child; all five child collections of `MonthlyExpenseEntity` are `FetchType.EAGER`.
    - Effect: each such action loads the whole database into memory; fine for a few years of data, increasingly slow after that.
- [ ] **Split `ExpenseManager` and do it as a Facade.**
    - Move income, payment and month logic into `IncomeService`, `PaymentService` and `MonthService`; `ExpenseManager` keeps its public API
      and only delegates, so controllers are unchanged.
    - Shared helpers (`getEntity`, `findExpenseForDate`, `requireOpen`, `toDto`, `validatePaymentDateInMonth`) move to a package-private
      `MonthLookup` `@Component` injected by the services, avoiding a circular dependency on the facade.
    - Side benefit: `@Transactional` calls between separate beans go through the Spring proxy (no self-invocation).
- [ ] Extract repeated detail-page cards/modals and inline CSS/JS into fragments/static assets.

## 5. Operational

- [ ] Expand README: setup, DB backup/recovery, migration/release, and local-only security notes.
- [ ] Exclude `src/main/resources/db/migration` from IDE reformat-on-commit (avoids Flyway checksum mismatches).
- [ ] **`ThreadConfig` is dead configuration.**
    - Where: it only activates on `spring.thread-executor=virtual`, but `application.properties` sets `spring.threads.virtual.enabled=true`.
    - Effect: the custom executor and Tomcat customizer never load; virtual threads come from Spring Boot's own support. The class and
      `ThreadConfigTest` suggest otherwise.

## 6. Security (required before offering to others / any remote use)

- [ ] Bind to `127.0.0.1` by default; disable the H2 console outside development.
- [ ] Non-empty DB credentials.
- [ ] Authentication + CSRF protection on all mutation endpoints.
- Blocks: PWA distribution to others, remote DB, iOS companion.

## 7. Features

### Site functionality

- when toggle a day done, the page should not jump automatically to the top. It's annoying when you are at the last days of the month, you have to scroll down.
- format all sums according to system settings (so thousands separators)

### Search

- Find a specific payment across all months
- Filter by description, category, amount range, date range
- **Implement with jOOQ** — type-safe, dynamic query building for the optional filters
  (description / category / amount range / date range). Good technical fit *and* a deliberate
  learning goal (jOOQ is a sought-after skill; hobby project = safe place to learn it).
    - Flyway + the jOOQ dependency are already in place. Remaining: wire jOOQ **codegen off
      the migrations**, then build the search query with jOOQ.
  - `CategoryService` is now a working example of manual jOOQ `DSLContext` usage
    (`DSL.table`/`DSL.field`, `fetchExists`, insert/update) to build on.

### DB Management (UI)

- Load/switch database files from the main page
- Backup/restore functionality
- Export full data as JSON

### Tags & Notes

- Flexible labeling beyond fixed categories
- Free-text notes on payments

### Multi-Month Views & Charts

- Spending trends over time by category
- Income growth visualization
- Month-over-month comparison

### Bank Import

- CSV/OFX file import to avoid manual entry
- Column mapping configuration

### Reports & Export

- PDF/CSV export for tax or personal review
- Monthly/yearly summary reports

### Installable App (PWA)

Make the existing Thymeleaf web UI installable so it opens as a standalone window with a
dock/taskbar icon — an app-like feel without leaving the web stack or maintaining a second UI.

- Add a web app manifest (`manifest.webmanifest`): name, icons, `"display": "standalone"`,
  theme/background colors, start URL.
- Add a service worker to satisfy installability (and enable basic offline shell/caching of
  static assets later).
- Link the manifest from the page `<head>`; provide app icons (192px, 512px, maskable).
- Result: "Install" option in the browser → launches in its own window, pinned to the dock.
- Chosen over a native JavaFX desktop app (see git branch `exploration/javafx-desktop-ui`):
  single codebase, no double UI maintenance. Mirrors how web-based finance apps (e.g. Actual
  Budget) deliver a desktop experience.

### Idea - iOS app

- a companion app for iOS. Just to add expenses, simple app. Connects to a remote db and just able to record a payment. (So this would mean that a
  remote DB option has already been created)

## Notes

#Release

./mvnw release:prepare release:perform

This will:

1. Prompt for the release version (e.g. 0.0.1) and next dev version (e.g. 0.0.2-SNAPSHOT)
2. Remove -SNAPSHOT, commit, create git tag v0.0.1
3. Bump to next snapshot, commit
4. Push both commits and the tag to origin

If you want to skip the prompts and set versions non-interactively:

./mvnw release:prepare -DreleaseVersion=1.0.0 -DdevelopmentVersion=1.0.1-SNAPSHOT
