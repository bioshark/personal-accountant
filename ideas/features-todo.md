# Feature Ideas

## Dynamic Categories (unlock the enum) — Option A

> **Migration notes (carry-over from the Flyway foundation, now in place):**
> - Existing DBs have drifted native `ENUM` category/type columns (missing newer values);
    > `validate` tolerates them, but normalize to VARCHAR via a migration during this change.
> - Also migrate money `double` → `BigDecimal` (DECIMAL columns) — doubles drift
    > (e.g. `3678.5600000000002`), currently only worked around in the UI.

Goal: keep Category **one-to-one** with a Payment (never in 2 categories, sums stay a clean
partition), but allow **adding categories on the fly** and **pre-defining** a list — i.e.
remove the compile-time lock of the `Category` enum. (This is the chosen approach over the
many-to-many "flexible labeling" idea above, for the Payment case. Incomes: decide later.)

Key insight: the `CATEGORY` column already stores plain strings (VARCHAR per
`preferred_enum_jdbc_type=VARCHAR`), so the lock is only in the Java enum — **no column-type
change or drop needed**. We only add a lookup table and a light value-normalization `UPDATE`
(pretty names) in the V2 migration.

Decisions (resolved):

- **Stored value format:** convert existing category values to pretty display names
  (`CREDIT_CARD` → "Credit card") via an `UPDATE` **inside the V2 Flyway migration** — not a
  manual console command. New free-typed names are stored as-is.
- **jOOQ:** no codegen for now — use the auto-configured `DSLContext` with manual refs
  (`DSL.table("category")`) for the lookup table. Add jOOQ codegen when building Search.
- `PaymentType` stays an enum — only `Category` changes.

Code-change checklist:

### Phase 1 — Flyway migration `V2__dynamic_categories.sql`

- [ ] Create `category` table: `id` identity PK, `name varchar(255)` unique not null,
  `archived boolean default false`.
- [ ] Seed it with the 23 current values (pretty names).
- [ ] Normalize drift: `ALTER` `pending_payment_entity.category` and
  `recurring_payment_template.category` (native `ENUM`) to `varchar(255)`
  (`payment_entity.category` is already VARCHAR).
- [ ] `UPDATE` existing category values in `payment_entity` / `pending_payment_entity` /
  `recurring_payment_template` to the pretty form.

### Phase 2 — Domain: enum → String

- [ ] `Payment` record: `Category category` → `String category` (keep enum until Phase 7).
- [ ] `PaymentEntity`, `PendingPaymentEntity`, `RecurringPaymentTemplate`: field + constructor
  params + accessors `Category` → `String`.
- [ ] `MonthlyExpenses.getPaymentsTypePerCategory` / `getSumsTypePerCategory` → `Map<String, …>`.

### Phase 3 — Services

- [ ] `ExpenseManager.editPayment(...)`: `Category` param → `String`.
- [ ] `RecurringPaymentService.addTemplate` / `editTemplate`: `Category` params → `String`.

### Phase 4 — Controllers (`@RequestParam Category` → `String`)

- [ ] `ExpenseWebController`: `addPayment`, `editPayment`.
- [ ] `RecurringPaymentWebController`: add/edit template.
- [ ] `view/rs/controller/ExpenseController` (REST): `addPayment`, `editPayment`, `removePayment`.

### Phase 5 — Category lookup access + wiring

- [ ] `CategoryService` (uses `DSLContext`): `listActive()`, `addIfAbsent(name)` (trim/dedup),
  optional `archive(name)`.
- [ ] Call `addIfAbsent(category)` on add/edit payment and add/edit template (add-on-the-fly).
- [ ] Add a `categories` model attribute in `ExpenseWebController.detail(...)` and the
  recurring page mapping.

### Phase 6 — Templates

- [ ] `detail.html`: Add/Edit Payment modals — replace the enum `<select>` with
  `<input list="categoryOptions"> + <datalist>` sourced from `${categories}` (pick or type).
- [ ] `detail.html`: `payment.category.displayName` → `payment.category`; expense breakdown
  badges (`catEntry.key`, `getSumsTypePerCategory[...]`) now String-keyed — drop `.displayName`.
- [ ] `recurring.html`: same datalist change; `template.category.displayName` → `template.category`.

### Phase 7 — Remove enum + tests

- [ ] Delete `Payment.Category` enum (keep `PaymentType`); trim the Category branch from
  `Payment.getDisplayName`.
- [ ] Update tests referencing `Category.FOOD` etc. (`ExpenseManagerTest`) to plain strings.
- [ ] Add `CategoryService` tests (list, add-on-the-fly, dedup/normalize).
- [ ] Verify: full suite green; boot fresh DB (V1+V2) and a copy of the real DB (baseline→V2)
  → `validate` passes, categories load.

> Deploy Phase 1 (migration) and Phase 2 (String change) together, since `validate` checks the
> String field against the columns.

Caveats: normalize on add (trim, unique); **archive** instead of delete so history keeps its
category; no FK integrity between `payment.category` and the lookup table (a rename won't
cascade — would need an UPDATE). Option B (FK entity + backfill + drop column) remains the
alternative if integrity ever matters.

## Search

- Find a specific payment across all months
- Filter by description, category, amount range, date range
- **Implement with jOOQ** — type-safe, dynamic query building for the optional filters
  (description / category / amount range / date range). Good technical fit *and* a deliberate
  learning goal (jOOQ is a sought-after skill; hobby project = safe place to learn it).
    - Flyway + the jOOQ dependency are already in place. Remaining: wire jOOQ **codegen off
      the migrations**, then build the search query with jOOQ.

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

ALTER TABLE PAYMENT_ENTITY ALTER COLUMN CATEGORY VARCHAR(255);

ALTER TABLE RECURRING_PAYMENT_TEMPLATE / pending_payment_entity / PAYMENT_ENTITY
ALTER COLUMN CATEGORY ENUM(
'FOOD',
'MEDIA',
'INVOICE',
'INSURANCE',
'HEALTH',
'FUEL',
'TRAVEL',
'VACATION',
'TELCO',
'FUN',
'GIFT',
'ETF',
'PAY_OFF',
'GARDEN',
'HOUSE',
'OTHER',
'TOBACCO',
'CLOTHES',
'MORTGAGE',
'LEASING',
'CAT',
'ELECTRICITY',
'CREDIT_CARD'
);

ALTER TABLE MONTHLY_EXPENSE_ENTITY ADD COLUMN IF NOT EXISTS FIXED_BUDGET DOUBLE DEFAULT 0;
ALTER TABLE MONTHLY_EXPENSE_ENTITY ADD COLUMN IF NOT EXISTS SAVING_BUDGET DOUBLE DEFAULT 0;
ALTER TABLE MONTHLY_EXPENSE_ENTITY ADD COLUMN IF NOT EXISTS LEISURE_BUDGET DOUBLE DEFAULT 0;