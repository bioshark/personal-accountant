package org.roly.personalaccountant.dto;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;

public class MonthlyExpenses {

    private final LinkedHashMap<LocalDate, List<Payment>> payments;
    private final LocalDate startDate;
    private final List<Income> incomes;
    private double cashTotal;
    private double cashLeft;

    public MonthlyExpenses(LinkedHashMap<LocalDate, List<Payment>> payments, LocalDate startDate, List<Income> incomes) {
        this.payments = payments;
        this.startDate = startDate;
        this.incomes = incomes;
    }

    public void addPayment(Payment payment) {
        payments.get(payment.date()).add(payment);
        cashLeft -= payment.amount();
    }

    public LinkedHashMap<LocalDate, List<Payment>> getPayments() {
        return new LinkedHashMap<>(payments);
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public List<Income> getIncomes() {
        return List.copyOf(incomes);
    }

    public double getCashTotal() {
        return cashTotal;
    }

    public double getCashLeft() {
        return cashLeft;
    }
}
