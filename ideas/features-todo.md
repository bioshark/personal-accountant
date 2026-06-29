# Feature Ideas

## Bugs
- 
## General stuff

## Month

## Recurring Transactions (Full Implementation Plan)

### Concept

Recurring payments as "pending items" per month, converted to real payments when confirmed/paid.

### Data Model

1. `RecurringPaymentTemplate` entity (global, not per-month)
    - name, default amount (editable), category, type
2. `PendingPaymentEntity` (per-month, linked to `MonthlyExpenseEntity`)
    - references template (or copies values), amount (overridable), status (pending/paid)

### Service Layer

- CRUD for templates (add/edit/remove recurring payment definitions)
- Pull templates into month as pending items (selective — user picks which ones)
- Pay action: convert pending → real `PaymentEntity` (user confirms date + final amount)
- Remove pending (skip this month)

### Controllers

- REST + Web controllers for all template CRUD operations
- Web endpoints for: pull into month, pay, remove pending

### UI

1. **Main page or settings section:** Manage recurring payment templates (list with add/edit/delete)
2. **Month creation modal:** Checklist showing all templates — user selects which to include → creates pending items
3. **Detail page (new section):** Pending payments list showing:
    - Name, default amount (editable before paying)
    - "Pay" button → prompts for date (default: today) and final amount (default: template amount) → creates real payment, removes from pending
    - "Skip" button → removes pending item without creating payment

### Flow

1. User defines recurring templates once (rent: €800, Netflix: €15, etc.)
2. When creating a new month, user selects which templates to pull in
3. Pending items appear in the detail page as "outstanding"
4. As payments happen, user clicks "Pay", confirms amount/date → becomes a real payment
5. Unpaid items remain visible as reminders

## Multi-Month Views & Charts

- Spending trends over time by category
- Income growth visualization
- Month-over-month comparison

## Bank Import

- CSV/OFX file import to avoid manual entry
- Column mapping configuration

## Tags & Notes

- Flexible labeling beyond fixed categories
- Free-text notes on payments

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