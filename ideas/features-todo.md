# Feature Ideas

## Bugs
- 
## General stuff

## Month

## Recurring Transactions (Full Implementation Plan)

### Concept

Recurring payments as "pending items" per month, converted to real payments when confirmed/paid.

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