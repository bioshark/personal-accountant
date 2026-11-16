# Feature Ideas

Priority order: bugs → data integrity → tech debt → operational → features. Clear tech debt before adding features.

## 1. Bugs

- [ ] **`ReactiveList` swallows listener exceptions, leaving the list and the statistics out of sync.**
    - Where: `ReactiveList.notifyListeners` catches every `RuntimeException` and logs it at DEBUG (invisible by default). `super.add` has
      already run, so the element is in the list while `OverallSumsTracker` may be half-updated (e.g. `onAdd` decrements `cashLeft` first, then
      fails on `dailyPayments.get(date)` for an unknown day, so `dailyExpenseTotal` is never increased).
    - Effect: totals, cash left and percentages silently disagree with the listed payments; the real bug is hidden.

## 2. Data Integrity & Validation

- [ ] **Reject overlapping month date ranges on create and edit.**
    - Where: `addNewMonthlyExpense` only rejects a duplicate `yearMonth`; `editMonth` checks nothing. Two months with different `yearMonth`
      can still share dates (e.g. editing one month's end past the next month's start).
    - Effect: `MonthlyExpenseRepository.findByDateInRange` returns `Optional`, so two matches throw `IncorrectResultSizeDataAccessException`
      (500) on every add/remove payment/income and toggle-day for those dates.
- [ ] **Validate edited income dates stay inside the owning month.**
    - Where: `editIncome` sets the date unchecked; `editPayment` already calls `validatePaymentDateInMonth`. `editMonth` also only checks that
      payments still fit the new range, not incomes.
    - Effect: an income can be dated outside its month; it still counts in that month, but date-based lookups (`removeIncome` by fields)
      go to a different or no month.
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

- [ ] Add a Flyway-enabled migration integration test for a fresh DB (V1 and future migrations).
- [ ] Add MVC integration tests: real request/status behavior, invalid input, `MonthClosedException` handling.
- [ ] Add the missing `MonthlyExpenseRepositoryTest` (see test plan §7).

## 4. Tech Debt: Persistence & Architecture

- [ ] Move `EndDateBackfillRunner` into a versioned Flyway data migration, then remove the runner.
- [ ] Replace `findAll()` child-ID scans with targeted repository/jOOQ queries before Search grows.
- [ ] Extract repeated detail-page cards/modals and inline CSS/JS into fragments/static assets.
- [ ] Remove unused `fragments/income-list.html` (or wire it in).

## 5. Operational

- [ ] Improve `.gitignore`: `data/`, `.idea/`, `.DS_Store`, `*.ser`, JSON exports/chat artifacts, `.java-version`.
- [ ] Expand README: setup, DB backup/recovery, migration/release, and local-only security notes.
- [ ] Exclude `src/main/resources/db/migration` from IDE reformat-on-commit (avoids Flyway checksum mismatches).

## 6. Security (required before offering to others / any remote use)

- [ ] Bind to `127.0.0.1` by default; disable the H2 console outside development.
- [ ] Non-empty DB credentials.
- [ ] Authentication + CSRF protection on all mutation endpoints.
- Blocks: PWA distribution to others, remote DB, iOS companion.

## 7. Features

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
