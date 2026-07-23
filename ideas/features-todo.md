# Feature Ideas

## General

- Change colors adn text on Spending split.
- Adjust budgets

## Tags & Notes

- Flexible labeling beyond fixed categories
- Free-text notes on payments

## Search

- Find a specific payment across all months
- Filter by description, category, amount range, date range

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

## Search

- Find a specific payment across all months
- Filter by description, category, amount range, date range

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