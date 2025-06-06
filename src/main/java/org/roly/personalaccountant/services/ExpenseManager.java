package org.roly.personalaccountant.services;

import java.time.LocalDate;
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

    private final Map<LocalDate, MonthlyExpenses> expenses = new HashMap<>();

    public void addPayment(LocalDate month, Payment payment) {
        MonthlyExpenses expense = expenses.get(month);
        LocalDate paymentDate = payment.date();
        if (!isPaymentWithingInterval(expense.payments(), paymentDate)) {
            throw new IllegalArgumentException("Payment date is out of range");
        }
        expense.addPayment(paymentDate, payment);
    }

    public void initializeMonthExpense(LocalDate startDate) {
        expenses.put(startDate,
                new MonthlyExpenses(
                        PaymentsGenerator.initializeEmptyMonth(startDate),
                        startDate,
                        new ArrayList<>()
                )
        );
    }

    private boolean isPaymentWithingInterval(LinkedHashMap<LocalDate, List<Payment>> payments, LocalDate paymentDate) {
        return paymentDate.isAfter(payments.sequencedKeySet().getFirst()) || paymentDate.isBefore(payments.sequencedKeySet().getLast());
    }

    public Map<LocalDate, MonthlyExpenses> getExpenses() {
        return Map.copyOf(expenses);
    }
}
