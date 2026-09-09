# Test Plan

## Currently Covered

- `OverallSumsTrackerTest` (unit) — payment/income statistics, savings allocation, daily allocation/diff totals, core/want/save percentages.
- `DailyStatisticsTest` (unit) — daily difference, weekend/Saturday detection, weekday vs weekend allocation, static `allocationFor` /
  `totalAllocation`.
- `ExpenseManagerTest` (integration, @SpringBootTest) — create month; add payment (fixed → statistics); add payment (leisure → not fixed); add payment
  to non-existent day (error); add income (statistics); add income to non-existent expense (error); resolved `endDate`
  persisted when none provided; add payment on start-date and end-date boundaries.
- `CategoryServiceTest` (integration, @SpringBootTest, jOOQ) — `listActive` ordering and archived-exclusion; `addIfAbsent` insert /
  idempotent+case-insensitive / trim / null-blank no-op; `archive` hides from active list.
- `ExpenseControllerTest` (unit, Mockito) — generate new expense (success / failure).
- `DiskServiceTest` — file read/write.
- `ThreadConfigTest` — virtual threads config.
- `PersonalAccountantApplicationTests` — context loads / main runs.

---

## Not Yet Tested (ordered by priority)

### 1. OverallSumsTracker (Unit Tests)

- [ ] `getPercentages` — zero income never produces `NaN`/infinity.

### 2. PaymentsGenerator (Unit Tests) — Critical for date correctness

- [ ] `initializeEmptyMonth` with explicit endDate — includes start and end
- [ ] `initializeEmptyMonth` with null endDate — auto-computes and adjusts
- [ ] Generated days are in order

### 3. ExpenseManager (Integration Tests) — CRUD + validations

#### Month Management

- [ ] `editMonthDates` — successful date change
- [ ] `editMonthDates` — validation error when payments exist outside new range
- [ ] `editMonthDates` — rejects overlap with another month range
- [ ] `deleteMonthlyExpense` — successful delete
- [ ] `deleteMonthlyExpense` — delete non-existent month returns false
- [ ] `addNewMonthlyExpense` — duplicate month throws exception
- [ ] `addNewMonthlyExpense` — rejects overlap with another month range

#### Payment CRUD

- [ ] `removePayment` — by fields actually removes the persisted payment
- [ ] `removePaymentById` — successful removal
- [ ] `removePaymentById` — non-existent payment throws
- [ ] `editPayment` — successful field update (incl. free-text `String` category)
- [ ] `editPayment` — non-existent payment throws
- [ ] `editPayment` — date outside month range throws
- [ ] `editPayment` — date on month boundary succeeds

#### Income CRUD

- [ ] `removeIncome` — by fields
- [ ] `removeIncomeById` — successful removal
- [ ] `removeIncomeById` — non-existent income throws
- [ ] `editIncome` — successful field update
- [ ] `editIncome` — non-existent income throws
- [ ] `editIncome` — date outside month range throws
- [ ] `editIncome` — date on month boundary succeeds

#### Day Done Toggle

- [ ] `toggleDayDone` — marks day as done
- [ ] `toggleDayDone` — unmarks day (toggle off)
- [ ] `toggleDayDone` — persists across reload (toDto reflects state)

#### Savings

- [ ] `addSaving` — successful add
- [ ] `addSaving` — validation error when percentage exceeds unallocated
- [ ] `removeSavingById` — successful removal
- [ ] `editSaving` — successful update
- [ ] Savings values computed correctly (percentage of cashLeft)
- [ ] Saving percentage — rejects negative or over-100 values server-side

#### Recurring/Pending Payments

- [ ] `pullTemplatesIntoMonth` — templates become pending payments (with `String` category)
- [ ] `pullTemplatesIntoMonth` — increments FIXED/LEISURE/SAVING budgets per template type
- [ ] `pullTemplatesIntoMonth` — duplicate template behavior is enforced as designed
- [ ] `payPendingPayment` — converts to real payment, removes pending
- [ ] `payPendingPayment` — uses the amount supplied at pay time (on-the-fly value), not the template default
- [ ] `payPendingPayment` — date outside month range throws (no corruption)
- [ ] `removePendingPayment` — removes without creating payment
- [ ] `removePendingPayment` — decrements the type's budget
- [ ] `getPendingPayments` — returns correct list
- [ ] `getTotalPendingPayments` — returns correct sum

#### Budgets

- [ ] `adjustBudget` — sets FIXED budget to the given value (overwrite)
- [ ] `adjustBudget` — sets LEISURE budget
- [ ] `adjustBudget` — sets SAVING budget
- [ ] `adjustBudget` — DAILY throws (no daily budget)
- [ ] `adjustBudget` — rejects a negative value server-side
- [ ] `getFixedBudget` / `getLeisureBudget` / `getSavingBudget` — return stored value (0.0 default)
- [ ] `getProjectedSpending` — budgets + total daily allocation, computed without building a DTO

### 4. RecurringPaymentService (Integration Tests)

- [ ] `addTemplate` — creates and persists (with `String` category)
- [ ] `editTemplate` — updates fields
- [ ] `editTemplate` — non-existent throws
- [ ] `deleteTemplate` — removes
- [ ] `getAllTemplates` — returns all

### 5. CategoryService (extend existing)

- [ ] `addIfAbsent` — concurrent/duplicate insert relies on the unique constraint (no crash)
- [ ] `listActive` — reflects newly added categories after `addIfAbsent`

### 6. ExpenseController (Unit Tests - extend existing)

- [ ] `generateNewExpense` — real successful create returns `201 Created` (not `404`)
- [ ] `deleteExpense` — success
- [ ] `deleteExpense` — not found
- [ ] `addIncome` / `removeIncome` / `editIncome`
- [ ] `addPayment` / `removePayment` / `editPayment` (free-text `String` category)
- [ ] `removePayment` — response is successful only after the payment is removed
- [ ] `addSaving` / `removeSaving` / `editSaving`

### 7. MonthlyExpenseRepository (Integration Tests)

> `MonthlyExpenseRepositoryTest.java` is absent from the current checkout; add it.

- [ ] `findByDateInRange` — returns month for a date within range
- [ ] `findByDateInRange` — matches on start-date boundary (inclusive)
- [ ] `findByDateInRange` — matches on end-date boundary (inclusive)
- [ ] `findByDateInRange` — empty for a date before range
- [ ] `findByDateInRange` — empty for a date after range

### 8. MonthlyExpenses (Unit Tests)

- [ ] `computeMonth` — derives correct YearMonth from middle date
- [ ] `getEndDate` — returns last day from payments map
- [ ] `addPayment` — unknown day throws a clear validation error (not NPE)
- [ ] `getPaymentsTypePerCategory` — groups correctly by `String` category for given type
- [ ] `getSumsTypePerCategory` — computes correct sums per `String` category

### 9. ExpenseWebController (Web/MVC Tests)

- [ ] `detail` — exposes `projectedCashLeft` = totalIncome − projectedSpendings
- [ ] `detail` — exposes the `categories` model attribute (datalist source)
- [ ] `defaultDate` — today when it falls within the month range
- [ ] `defaultDate` — clamped to start date when today is before the range
- [ ] `defaultDate` — clamped to end date when today is after the range
- [ ] `adjustBudget` — updates the budget and redirects to the month
- [ ] `adjustBudget` — DAILY (invalid) flashes an error, no crash
- [ ] `addPayment` / `editPayment` — call `CategoryService.addIfAbsent` (a brand-new typed category is persisted)
- [ ] `addPayment` / `addIncome` — out-of-range date flashes an error (no 500, no corruption)
- [ ] `editPayment` / `payPendingPayment` — out-of-range date flashes an error, redirects back
- [ ] `editIncome` — out-of-range date flashes an error, redirects back

### 10. RecurringPaymentWebController (Web/MVC Tests)

- [ ] `listTemplates` — exposes the `categories` model attribute (datalist source)
- [ ] `addTemplate` / `editTemplate` — call `CategoryService.addIfAbsent` (new typed category is persisted)

### 11. Flyway Migration (Integration Tests)

- [ ] Fresh H2 database — applies V1 + V2 + V3 and Hibernate `validate` passes
- [ ] V2 — `category` lookup table created and seeded; drifted `pending_payment_entity` /
  `recurring_payment_template` category columns normalized to VARCHAR + pretty values
- [ ] V3 — a native `ENUM` `payment_entity.category` (as in the 1.1.6 production DB) is converted to VARCHAR with pretty values, and `validate` then
  passes
- [ ] Migration history — a baselined existing database records V1 without re-running it

### 12. ReactiveList (Unit Tests)

- [ ] Listener failure — mutation fails rather than silently persisting inconsistent statistics
