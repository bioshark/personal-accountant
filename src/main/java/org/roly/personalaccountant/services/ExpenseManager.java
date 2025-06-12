package org.roly.personalaccountant.services;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.roly.personalaccountant.dto.MonthlyExpenses;
import org.roly.personalaccountant.dto.Payment;
import org.roly.personalaccountant.utils.PaymentsGenerator;
import org.springframework.stereotype.Service;

@Service
public class ExpenseManager {

    private final Map<YearMonth, MonthlyExpenses> expenses = new HashMap<>();

    public void addPayment(Payment payment) {
        LocalDate paymentDate = payment.date();
        MonthlyExpenses expense = getExpenseMonth(paymentDate);
        if (!isPaymentWithingInterval(expense.getPayments(), paymentDate)) {
            throw new IllegalArgumentException("Payment date is out of range");
        }
        expense.addPayment(payment);
    }

    public void addNewMonthlyExpense(YearMonth yearMonth, LocalDate startDate) {
        expenses.put(
                YearMonth.of(yearMonth.getYear(), yearMonth.getMonth()),
                new MonthlyExpenses(
                        PaymentsGenerator.initializeEmptyMonth(startDate),
                        startDate,
                        new ArrayList<>()
                )
        );
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

    private boolean isPaymentWithingInterval(LinkedHashMap<LocalDate, List<Payment>> payments, LocalDate paymentDate) {
        return paymentDate.isAfter(payments.sequencedKeySet().getFirst()) || paymentDate.isBefore(payments.sequencedKeySet().getLast());
    }
}
