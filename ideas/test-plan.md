# Test Plan

## Currently Covered

- `ExpenseManagerTest` (integration, @SpringBootTest):
    - Create expense month
    - Add payment (fixed type → statistics)
    - Add payment (leisure type → not fixed)
    - Add payment to non-existent day (error)
    - Add income (statistics updated)
    - Add income to non-existent expense (error)
- `ExpenseControllerTest` (unit, Mockito):
    - Generate new expense (success)
    - Generate new expense (failure)
- `DiskServiceTest` — file operations
- `ThreadConfigTest` — virtual threads config
- `PersonalAccountantApplicationTests` — context loads

---

## Not Yet Tested (ordered by priority)

### 1. OverallSumsTracker (Unit Tests) — Core business logic

- [x] `onAdd(Payment FIXED)` — updates cashLeft, fixedExpenseTotal
- [x] `onAdd(Payment DAILY)` — updates cashLeft, dailyExpenseTotal, dailyPayments
- [x] `onAdd(Payment LEISURE)` — updates cashLeft, leisureExpenseTotal
- [x] `onAdd(Payment SAVING)` — updates cashLeft, savingExpenseTotal
- [x] `onAdd(Income)` — updates cashTotal, cashLeft
- [x] `addSaving` — adjusts unallocated percentage
- [x] `addSaving` — throws when exceeding unallocated
- [x] `removeSaving` — restores unallocated percentage
- [x] `getTotalDailyAllocation` — correct sum of all day allocations
- [x] `getTotalDailyDiff` — correct total (only for done days)
- [x] `getPercentages` — correct core/want/save split
- [ ] `getPercentages` — zero income never produces `NaN`/infinity

### 2. DailyStatistics (Unit Tests) — Small unit, quick wins

- [x] `getDailyDifference` — returns 0 when day not done
- [x] `getDailyDifference` — returns allocation - expenditure when done
- [x] `isWeekEnd` — Saturday/Sunday true, weekdays false
- [x] `isSaturday` — only Saturday
- [x] Daily allocation values (weekday vs weekend)
- [x] `allocationFor` (static) — default allowance on weekdays, larger on Saturday
- [x] `totalAllocation` (static) — correct sum across a mixed weekday/weekend range (no DTO built)

### 3. PaymentsGenerator (Unit Tests) — Critical for date correctness

- [ ] `initializeEmptyMonth` with explicit endDate — includes start and end
- [ ] `initializeEmptyMonth` with null endDate — auto-computes and adjusts
- [ ] Generated days are in order

### 4. ExpenseManager (Integration Tests) — CRUD + validations

#### Month Management

- [ ] `editMonthDates` — successful date change
- [ ] `editMonthDates` — validation error when payments exist outside new range
- [ ] `editMonthDates` — rejects overlap with another month range
- [ ] `deleteMonthlyExpense` — successful delete
- [ ] `deleteMonthlyExpense` — delete non-existent month returns false
- [ ] `addNewMonthlyExpense` — duplicate month throws exception
- [ ] `addNewMonthlyExpense` — rejects overlap with another month range

#### Date Range Lookup & End Date

- [x] `addNewMonthlyExpense` — persists resolved endDate when none provided
- [x] `addPayment` — resolves owning month on start-date boundary
- [x] `addPayment` — resolves owning month on end-date boundary

#### Payment CRUD

- [ ] `removePayment` — by fields actually removes the persisted payment
- [ ] `removePaymentById` — successful removal
- [ ] `removePaymentById` — non-existent payment throws
- [ ] `editPayment` — successful field update
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

- [ ] `pullTemplatesIntoMonth` — templates become pending payments
- [ ] `pullTemplatesIntoMonth` — increments FIXED/LEISURE/SAVING budgets per template type
- [ ] `pullTemplatesIntoMonth` — duplicate template behavior is enforced as designed
- [ ] `payPendingPayment` — converts to real payment, removes pending
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
- [ ] `getProjectedSpendings` — budgets + total daily allocation, computed without building a DTO

### 5. RecurringPaymentService (Integration Tests)

- [ ] `addTemplate` — creates and persists
- [ ] `editTemplate` — updates fields
- [ ] `editTemplate` — non-existent throws
- [ ] `deleteTemplate` — removes
- [ ] `getAllTemplates` — returns all

### 6. ExpenseController (Unit Tests - extend existing)

- [ ] `generateNewExpense` — real successful create returns `201 Created` (not `404`)
- [ ] `deleteExpense` — success
- [ ] `deleteExpense` — not found
- [ ] `addIncome` / `removeIncome` / `editIncome`
- [ ] `addPayment` / `removePayment` / `editPayment`
- [ ] `removePayment` — response is successful only after the payment is removed
- [ ] `addSaving` / `removeSaving` / `editSaving`

### 7. MonthlyExpenses (Unit Tests)

- [ ] `computeMonth` — derives correct YearMonth from middle date
- [ ] `getEndDate` — returns last day from payments map
- [ ] `addPayment` — unknown day throws a clear validation error (not NPE)
- [ ] `getPaymentsTypePerCategory` — groups correctly by category for given type
- [ ] `getSumsTypePerCategory` — computes correct sums per category

### 8. MonthlyExpenseRepository (Integration Tests)

> The plan previously marked these done, but `MonthlyExpenseRepositoryTest.java` is absent from
> the current checkout; restore/add the test source before marking them complete.

- [ ] `findByDateInRange` — returns month for a date within range
- [ ] `findByDateInRange` — matches on start-date boundary (inclusive)
- [ ] `findByDateInRange` — matches on end-date boundary (inclusive)
- [ ] `findByDateInRange` — empty for a date before range
- [ ] `findByDateInRange` — empty for a date after range

### 9. ExpenseWebController (Web/MVC Tests)

- [ ] `detail` — exposes `projectedCashLeft` = totalIncome − projectedSpendings
- [ ] `defaultDate` — today when it falls within the month range
- [ ] `defaultDate` — clamped to start date when today is before the range
- [ ] `defaultDate` — clamped to end date when today is after the range
- [ ] `adjustBudget` — updates the budget and redirects to the month
- [ ] `adjustBudget` — DAILY (invalid) flashes an error, no crash
- [ ] `addPayment` / `addIncome` — out-of-range date flashes an error (no 500, no corruption)
- [ ] `editPayment` / `payPendingPayment` — out-of-range date flashes an error, redirects back
- [ ] `editIncome` — out-of-range date flashes an error, redirects back

### 10. Flyway Migration (Integration Tests)

- [ ] Fresh H2 database — Flyway applies V1 and Hibernate `validate` passes
- [ ] Migration history — a baselined existing database records V1 without re-running it

### 11. ReactiveList (Unit Tests)

- [ ] Listener failure — mutation fails rather than silently persisting inconsistent statistics

