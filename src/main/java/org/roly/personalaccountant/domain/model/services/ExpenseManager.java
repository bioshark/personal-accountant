package org.roly.personalaccountant.domain.model.services;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import org.roly.personalaccountant.domain.model.dto.Income;
import org.roly.personalaccountant.domain.model.dto.MonthlyExpenses;
import org.roly.personalaccountant.domain.model.dto.Payment;
import org.roly.personalaccountant.domain.model.dto.Payment.PaymentType;
import org.roly.personalaccountant.domain.model.entity.MonthlyExpenseEntity;
import org.roly.personalaccountant.domain.model.entity.PendingPaymentEntity;
import org.roly.personalaccountant.domain.model.entity.RecurringPaymentTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExpenseManager {

    private final PaymentService paymentService;
    private final IncomeService incomeService;
    private final MontlyExpenseService montlyExpenseService;
    private final BudgetCalculatorService budgetCalculatorService;
    private final SavingsService savingsService;
    private final PendingPaymentService pendingPaymentService;

    @Autowired
    public ExpenseManager(PaymentService paymentService, IncomeService incomeService, MontlyExpenseService montlyExpenseService,
            BudgetCalculatorService budgetCalculatorService, SavingsService savingsService, PendingPaymentService pendingPaymentService) {
        this.paymentService = paymentService;
        this.incomeService = incomeService;
        this.montlyExpenseService = montlyExpenseService;
        this.budgetCalculatorService = budgetCalculatorService;
        this.savingsService = savingsService;
        this.pendingPaymentService = pendingPaymentService;
    }

    @Transactional
    public void addNewMonthlyExpense(String expenseName, LocalDate startDate, LocalDate endDate, List<RecurringPaymentTemplate> templates) {
        montlyExpenseService.addNewMonthlyExpense(expenseName, startDate, endDate, templates);
    }

    @Transactional
    public void editMonth(YearMonth yearMonth, String newName, LocalDate newStartDate, LocalDate newEndDate) {
        montlyExpenseService.editMonth(yearMonth, newName, newStartDate, newEndDate);
    }

    public boolean deleteMonthlyExpense(YearMonth yearMonth) {
        return montlyExpenseService.deleteMonthlyExpense(yearMonth);
    }

    public MonthlyExpenses getExpense(YearMonth yearMonth) {
        return montlyExpenseService.getExpense(yearMonth);
    }

    public MonthlyExpenseEntity toggleDayDone(LocalDate date) {
        return montlyExpenseService.toggleDayDone(date);
    }

    public MonthlyExpenseEntity toggleMonthDone(YearMonth yearMonth) {
        return montlyExpenseService.toggleMonthDone(yearMonth);
    }

    public Map<YearMonth, MonthlyExpenses> getExpenses() {
        return montlyExpenseService.getAllExpenses();
    }

    public void clearExpenses() {
        montlyExpenseService.clearExpenses();
    }

    public MonthlyExpenseEntity addPayment(Payment payment) {
        return paymentService.addPayment(payment);
    }

    public MonthlyExpenseEntity removePayment(Payment payment) {
        return paymentService.removePayment(payment);
    }

    public MonthlyExpenseEntity removePaymentById(Long paymentId) {
        return paymentService.removePaymentById(paymentId);
    }

    public MonthlyExpenseEntity editPayment(Long paymentId, String description, LocalDate date, double amount, PaymentType type, String category) {
        return paymentService.editPayment(paymentId, description, date, amount, type, category);
    }

    public double getTotalPendingPayments(YearMonth yearMonth) {
        return paymentService.getTotalPendingPayments(yearMonth);
    }

    public List<PendingPaymentEntity> getPendingPayments(YearMonth yearMonth) {
        return paymentService.getPendingPayments(yearMonth);
    }

    public MonthlyExpenseEntity addIncome(Income income) {
        return incomeService.addIncome(income);
    }

    public MonthlyExpenseEntity removeIncome(Income income) {
        return incomeService.removeIncome(income);
    }

    public MonthlyExpenseEntity removeIncomeById(Long incomeId) {
        return incomeService.removeIncomeById(incomeId);
    }

    public MonthlyExpenseEntity editIncome(Long incomeId, String source, LocalDate date, double value) {
        return incomeService.editIncome(incomeId, source, date, value);
    }

    public double getFixedBudget(YearMonth yearMonth) {
        return budgetCalculatorService.getFixedBudget(yearMonth);
    }

    public double getSavingBudget(YearMonth yearMonth) {
        return budgetCalculatorService.getSavingBudget(yearMonth);
    }

    public double getLeisureBudget(YearMonth yearMonth) {
        return budgetCalculatorService.getLeisureBudget(yearMonth);
    }

    public void adjustBudget(YearMonth yearMonth, PaymentType paymentType, double newValue) {
        budgetCalculatorService.adjustBudget(yearMonth, paymentType, newValue);
    }

    public double getProjectedSpending(YearMonth yearMonth) {
        return budgetCalculatorService.getProjectedSpending(yearMonth);
    }

    public MonthlyExpenseEntity addSaving(YearMonth yearMonth, String name, double percentage) {
        return savingsService.addSaving(yearMonth, name, percentage);
    }

    public MonthlyExpenseEntity removeSavingById(Long savingId) {
        return savingsService.removeSavingById(savingId);
    }

    public MonthlyExpenseEntity editSaving(Long savingId, String name, double percentage) {
        return savingsService.editSaving(savingId, name, percentage);
    }

    @Transactional
    public MonthlyExpenseEntity pullTemplatesIntoMonth(YearMonth yearMonth, List<RecurringPaymentTemplate> templates) {
        return pendingPaymentService.pullTemplatesIntoMonth(yearMonth, templates);
    }

    public MonthlyExpenseEntity payPendingPayment(Long pendingId, LocalDate date, double amount) {
        return pendingPaymentService.payPendingPayment(pendingId, date, amount);
    }

    public MonthlyExpenseEntity removePendingPayment(Long pendingId) {
        return pendingPaymentService.removePendingPayment(pendingId);
    }

}
