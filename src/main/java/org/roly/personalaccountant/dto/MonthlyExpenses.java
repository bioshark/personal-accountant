package org.roly.personalaccountant.dto;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

public class MonthlyExpenses {

    // TODO add yearmonth
    private final LinkedHashMap<LocalDate, List<Payment>> payments;
    private final LocalDate startDate;
    private final List<Income> incomes;
    private double cashTotal;
    private double cashLeft;

    public MonthlyExpenses(LinkedHashMap<LocalDate, List<Payment>> payments, LocalDate startDate, List<Income> incomes) {
        this.payments = payments;
        this.startDate = startDate;
        this.incomes = incomes;
        this.cashTotal = calculateTotalFromIncomes();
        this.cashLeft = cashTotal;
    }

    private double calculateTotalFromIncomes() {
        return incomes.stream()
                .mapToDouble(Income::value)
                .sum();
    }

    public void addIncome(Income income) {
        this.incomes.add(income);
        this.cashTotal += income.value();
        this.cashLeft += income.value();
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MonthlyExpenses that = (MonthlyExpenses) o;
        return Double.compare(cashTotal, that.cashTotal) == 0 &&
                Double.compare(cashLeft, that.cashLeft) == 0 &&
                Objects.equals(payments, that.payments) &&
                Objects.equals(startDate, that.startDate) &&
                Objects.equals(incomes, that.incomes);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(payments);
        result = 31 * result + Objects.hashCode(startDate);
        result = 31 * result + Objects.hashCode(incomes);
        result = 31 * result + Double.hashCode(cashTotal);
        result = 31 * result + Double.hashCode(cashLeft);
        return result;
    }

    @Override
    public String toString() {
        return "MonthlyExpenses{" +
                "payments=" + payments +
                ", startDate=" + startDate +
                ", incomes=" + incomes +
                ", cashTotal=" + cashTotal +
                ", cashLeft=" + cashLeft +
                '}';
    }
}
