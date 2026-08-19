# Feature Ideas

## Correctness & Reliability Follow-ups

- [ ] REST: return `201 Created` after creating a month; current success path returns `404`.
- [ ] REST: fix remove-payment-by-fields; remove the managed payment, not a new entity instance.
- [ ] Reject overlapping month date ranges on create and edit (date lookup must be unambiguous).
- [ ] Validate edited income dates stay inside the owning month.
- [ ] Make `MonthlyExpenses.addPayment` reject an unknown day with a clear error (never NPE).
- [ ] Do not swallow `ReactiveList` listener failures; fail the mutation atomically.
- [ ] Define/enforce duplicate recurring-template pull behavior per month.
- [ ] Define zero-income percentage behavior; never render `NaN`/infinity.
- [ ] Enforce server-side non-negative budget/amount values and valid saving percentages.
- [ ] Clarify manual budget overwrite vs pending-template add/skip semantics.

## Persistence & Architecture Follow-ups

- [ ] Add a Flyway-enabled migration integration test for a fresh DB (V1 and future migrations).
- [ ] Restore/add the missing `MonthlyExpenseRepositoryTest`; test plan currently marks it done.
- [ ] Move `EndDateBackfillRunner` into a versioned Flyway data migration, then remove the runner.
- [ ] Replace `findAll()` child-ID scans with targeted repository/jOOQ queries before Search grows.
- [ ] Add optimistic locking (`@Version`) to month writes if multi-tab/device use is supported.
- [ ] Migrate money from `double` to `BigDecimal` in a dedicated Flyway-backed change.
- [ ] Extract repeated detail-page cards/modals and inline CSS/JS into fragments/static assets.

## Operational Follow-ups

- [ ] Improve `.gitignore` for DB/build/IDE/local chat artifacts; decide whether templates/fragments is tracked.
- [ ] Expand README: setup, DB backup, migration/release, and local-only security notes.
- [ ] Keep H2 console/local server bound to localhost outside development; add auth before any remote use.

## General improvements.

- now that category is dynamic, when adding a new payment, one should be able to either add a category, or choose from existing ones.
- when adding a recurring payment, one could change the value of the payment on the fly.
- add a button on detail page to add expense, not only in the actual day. When pressing it, the default date should be "today".
- mark month as done. in the overview page, color different the month tickets that are done, highlighting the remaining cash.

## Search

- Find a specific payment across all months
- Filter by description, category, amount range, date range
- **Implement with jOOQ** — type-safe, dynamic query building for the optional filters
  (description / category / amount range / date range). Good technical fit *and* a deliberate
  learning goal (jOOQ is a sought-after skill; hobby project = safe place to learn it).
    - Flyway + the jOOQ dependency are already in place. Remaining: wire jOOQ **codegen off
      the migrations**, then build the search query with jOOQ.
  - `CategoryService` is now a working example of manual jOOQ `DSLContext` usage
    (`DSL.table`/`DSL.field`, `fetchExists`, insert/update) to build on.

## Tags & Notes

- Flexible labeling beyond fixed categories
- Free-text notes on payments

## Multi-Month Views & Charts

- Spending trends over time by category
- Income growth visualization
- Month-over-month comparison

## Bank Import

- CSV/OFX file import to avoid manual entry
- Column mapping configuration

## Reports & Export

- PDF/CSV export for tax or personal review
- Monthly/yearly summary reports

## DB Management (UI)

- Load/switch database files from the main page
- Backup/restore functionality
- Export full data as JSON

## Installable App (PWA)

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

## Idea - iOS app

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
