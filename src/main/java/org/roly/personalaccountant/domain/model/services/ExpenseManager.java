package org.roly.personalaccountant.domain.model.services;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;
import org.roly.personalaccountant.domain.model.expenses.Income;
import org.roly.personalaccountant.domain.model.expenses.MonthlyExpenses;
import org.roly.personalaccountant.domain.model.expenses.Payment;
import org.roly.personalaccountant.utils.PaymentsGenerator;
import org.springframework.stereotype.Service;

@Service
public class ExpenseManager {

    private final Map<YearMonth, MonthlyExpenses> expenses = new HashMap<>();

    public void addNewMonthlyExpense(YearMonth yearMonth, LocalDate startDate) {
        expenses.put(
                YearMonth.of(yearMonth.getYear(), yearMonth.getMonth()),
                new MonthlyExpenses(
                        PaymentsGenerator.initializeEmptyMonth(startDate),
                        startDate,
                        yearMonth
                )
        );
    }

    public void addPayment(Payment payment) {
        LocalDate paymentDate = payment.date();
        MonthlyExpenses expense = getExpenseMonth(paymentDate);
        expense.addPayment(payment);
    }

    public void addIncome(Income income) {
        MonthlyExpenses expense = getExpenseMonth(income.date());
        expense.addIncome(income);
    }

    public MonthlyExpenses getExpense(YearMonth yearMonth) {
        return expenses.get(yearMonth);
    }

    public Map<YearMonth, MonthlyExpenses> getExpenses() {
        return Map.copyOf(expenses);
    }

    public void clearExpenses() {
        expenses.clear();
    }

    private MonthlyExpenses getExpenseMonth(LocalDate searchingDate) {
        for (MonthlyExpenses monthlyExpenses :  expenses.values()) {
            if (monthlyExpenses.getPayments().keySet().stream()
                    .anyMatch(searchingDate::equals)) {
                return monthlyExpenses;
            }
        }
        throw new IllegalArgumentException("Can't find Expense for date " + searchingDate);
    }

}
