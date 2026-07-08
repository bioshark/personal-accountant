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

- [ ] `onAdd(Payment FIXED)` — updates cashLeft, fixedExpenseTotal
- [ ] `onAdd(Payment DAILY)` — updates cashLeft, dailyExpenseTotal, dailyPayments
- [ ] `onAdd(Payment LEISURE)` — updates cashLeft, leisureExpenseTotal
- [ ] `onAdd(Payment SAVING)` — updates cashLeft, savingExpenseTotal
- [ ] `onAdd(Income)` — updates cashTotal, cashLeft
- [ ] `addSaving` — adjusts unallocated percentage
- [ ] `addSaving` — throws when exceeding unallocated
- [ ] `removeSaving` — restores unallocated percentage
- [ ] `getTotalDailyAllocation` — correct sum of all day allocations
- [ ] `getTotalDailyDiff` — correct total (only for done days)
- [ ] `getPercentages` — correct core/want/save split

### 2. DailyStatistics (Unit Tests) — Small unit, quick wins

- [ ] `getDailyDifference` — returns 0 when day not done
- [ ] `getDailyDifference` — returns allocation - expenditure when done
- [ ] `isWeekEnd` — Saturday/Sunday true, weekdays false
- [ ] `isSaturday` — only Saturday
- [ ] Daily allocation values (weekday vs weekend)

### 3. PaymentsGenerator (Unit Tests) — Critical for date correctness

- [ ] `initializeEmptyMonth` with explicit endDate — includes start and end
- [ ] `initializeEmptyMonth` with null endDate — auto-computes and adjusts
- [ ] Generated days are in order

### 4. ExpenseManager (Integration Tests) — CRUD + validations

#### Month Management

- [ ] `editMonthDates` — successful date change
- [ ] `editMonthDates` — validation error when payments exist outside new range
- [ ] `deleteMonthlyExpense` — successful delete
- [ ] `deleteMonthlyExpense` — delete non-existent month returns false
- [ ] `addNewMonthlyExpense` — duplicate month throws exception

#### Payment CRUD

- [ ] `removePayment` — by fields
- [ ] `removePaymentById` — successful removal
- [ ] `removePaymentById` — non-existent payment throws
- [ ] `editPayment` — successful field update
- [ ] `editPayment` — non-existent payment throws

#### Income CRUD

- [ ] `removeIncome` — by fields
- [ ] `removeIncomeById` — successful removal
- [ ] `removeIncomeById` — non-existent income throws
- [ ] `editIncome` — successful field update
- [ ] `editIncome` — non-existent income throws

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

#### Recurring/Pending Payments

- [ ] `pullTemplatesIntoMonth` — templates become pending payments
- [ ] `payPendingPayment` — converts to real payment, removes pending
- [ ] `removePendingPayment` — removes without creating payment
- [ ] `getPendingPayments` — returns correct list
- [ ] `getTotalPendingPayments` — returns correct sum

### 5. RecurringPaymentService (Integration Tests)

- [ ] `addTemplate` — creates and persists
- [ ] `editTemplate` — updates fields
- [ ] `editTemplate` — non-existent throws
- [ ] `deleteTemplate` — removes
- [ ] `getAllTemplates` — returns all

### 6. ExpenseController (Unit Tests - extend existing)

- [ ] `deleteExpense` — success
- [ ] `deleteExpense` — not found
- [ ] `addIncome` / `removeIncome` / `editIncome`
- [ ] `addPayment` / `removePayment` / `editPayment`
- [ ] `addSaving` / `removeSaving` / `editSaving`

### 7. MonthlyExpenses (Unit Tests)

- [ ] `computeMonth` — derives correct YearMonth from middle date
- [ ] `getEndDate` — returns last day from payments map
- [ ] `getPaymentsTypePerCategory` — groups correctly by category for given type
- [ ] `getSumsTypePerCategory` — computes correct sums per category
