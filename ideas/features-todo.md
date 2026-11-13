# Feature Ideas

Priority order: bugs → data integrity → tech debt → operational → features. Clear tech debt before adding features.

## 1. Bugs

- [ ] Define zero-income percentage behavior; never render `NaN`/infinity.
- [ ] Do not swallow `ReactiveList` listener failures; fail the mutation atomically.

## 2. Data Integrity & Validation

- [ ] Reject overlapping month date ranges on create and edit (date lookup must be unambiguous).
- [ ] Validate edited income dates stay inside the owning month.
- [ ] Validate inputs at service and DB level: non-negative budgets/amounts, reject `NaN`/infinity, start ≤ end dates,
  non-blank and length-bounded text, valid saving percentages.
- [ ] Define/enforce duplicate recurring-template pull behavior per month.
- [ ] Clarify manual budget overwrite vs pending-template add/skip semantics.
- [ ] Migrate money from `double` to `BigDecimal` in a dedicated Flyway-backed change.

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
