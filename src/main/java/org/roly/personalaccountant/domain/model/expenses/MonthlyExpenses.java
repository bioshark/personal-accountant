package org.roly.personalaccountant.domain.model.expenses;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import org.roly.personalaccountant.domain.notifiers.PaymentList;

public class MonthlyExpenses {

    private final LinkedHashMap<LocalDate, PaymentList> payments;
    private final LocalDate startDate;
    private final List<Income> incomes;
    private final YearMonth yearMonth;
    private double cashTotal;
    private double cashLeft;
    private OverallSumsTracker overallSumsTracker;

    public MonthlyExpenses(LinkedHashMap<LocalDate, PaymentList> payments, LocalDate startDate, YearMonth yearMonth) {
        this.payments = payments;
        this.startDate = startDate;
        this.incomes = new ArrayList<>();
        this.cashTotal = calculateTotalFromIncomes();
        this.cashLeft = cashTotal;
        this.yearMonth = yearMonth;
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

    public YearMonth getYearMonth() {
        return yearMonth;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MonthlyExpenses that = (MonthlyExpenses) o;
        return Double.compare(cashTotal, that.cashTotal) == 0 && Double.compare(cashLeft, that.cashLeft) == 0
                && Objects.equals(payments, that.payments) && Objects.equals(startDate, that.startDate) && incomes.equals(
                that.incomes) && Objects.equals(yearMonth, that.yearMonth);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(payments);
        result = 31 * result + Objects.hashCode(startDate);
        result = 31 * result + incomes.hashCode();
        result = 31 * result + Double.hashCode(cashTotal);
        result = 31 * result + Double.hashCode(cashLeft);
        result = 31 * result + Objects.hashCode(yearMonth);
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
