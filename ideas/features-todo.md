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

Key insight: the `CATEGORY` column is already stored as VARCHAR (per
`preferred_enum_jdbc_type=VARCHAR` + the `ALTER ... VARCHAR(255)` already applied), so the DB
holds plain strings like "FOOD". The lock is only in the Java enum → **no column drop / data
migration needed**.

Plan (Option A — String + managed lookup list):

1. Change `PaymentEntity.category` (and `RecurringPaymentTemplate`, `PendingPaymentEntity`)
   from the `Category` enum to `String`. Column stays VARCHAR — no schema change, existing
   rows keep working.
2. Change the `Payment` DTO's `category` from enum to `String`.
3. Add a `category` table (`id`, `name` unique, `archived` flag) that powers the picker,
   **created and seeded (the 23 current enum values) via a Flyway migration, accessed via
   jOOQ — no JPA entity/repository** (decided: a lookup list of strings doesn't warrant a
   full entity, and jOOQ is already in the stack). Since `payment.category` is a free String
   (no FK), this table is a suggestion/known-names list, not an enforced relationship.
4. UI (`detail.html`, `recurring.html`): dropdown sourced from the lookup table + allow
   typing a new name; a new name is saved on the payment and (optionally) added to the list.
5. Update grouping (`MonthlyExpenses.getSumsTypePerCategory` /
   `getPaymentsTypePerCategory`) and controllers to key by the String instead of the enum.
6. Remove the `Payment.Category` enum once nothing references it.

Decisions / caveats:

- Normalize on add (trim, unique constraint; consider case-folding) to avoid "Food" vs
  "food" duplicates.
- **Archive** instead of delete for retiring a category, so historical payments keep it.
- Keep a display-name convention (old enum prettified `CREDIT_CARD` → "Credit card"); with
  free-form names, store the display string directly.
- Trade-off accepted: no FK integrity between `payment.category` (string) and the lookup
  table — a later rename won't cascade to old payments (would need an update query). Fine for
  a personal app; Option B (FK entity + Flyway backfill + drop column) is the alternative if
  integrity becomes important.

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