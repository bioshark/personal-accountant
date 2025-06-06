package org.roly.personalaccountant.services;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.roly.personalaccountant.dto.MonthlyExpenses;
import org.roly.personalaccountant.dto.Payment;
import org.roly.personalaccountant.dto.WorkingMonth;
import org.roly.personalaccountant.utils.PaymentsGenerator;
import org.springframework.stereotype.Service;

@Service
public class ExpenseManager {

    private final Map<WorkingMonth, MonthlyExpenses> expenses = new HashMap<>();

    public void addPayment(LocalDate month, Payment payment) {
        MonthlyExpenses expense = expenses.get(month);
        LocalDate paymentDate = payment.date();
        if (!isPaymentWithingInterval(expense.payments(), paymentDate)) {
            throw new IllegalArgumentException("Payment date is out of range");
        }
        expense.addPayment(paymentDate, payment);
    }

    public void initializeMonthExpense(WorkingMonth workingMonth, LocalDate startDate) {
        initializeMonthExpense(workingMonth.yearMonth().getYear(), workingMonth.yearMonth().getMonth(), startDate);
    }

    public void initializeMonthExpense(int year, Month month, LocalDate startDate) {
        expenses.put(
                new WorkingMonth(startDate, YearMonth.of(year, month)),
                new MonthlyExpenses(
                        PaymentsGenerator.initializeEmptyMonth(startDate),
                        startDate,
                        new ArrayList<>()
                )
        );
    }

    public MonthlyExpenses getExpense(WorkingMonth workingMonth) {
        return expenses.get(workingMonth);
    }

    private boolean isPaymentWithingInterval(LinkedHashMap<LocalDate, List<Payment>> payments, LocalDate paymentDate) {
        return paymentDate.isAfter(payments.sequencedKeySet().getFirst()) || paymentDate.isBefore(payments.sequencedKeySet().getLast());
    }

    public Map<WorkingMonth, MonthlyExpenses> getExpenses() {
        return Map.copyOf(expenses);
    }
}
