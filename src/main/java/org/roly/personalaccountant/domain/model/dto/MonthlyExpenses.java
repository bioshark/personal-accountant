package org.roly.personalaccountant.domain.model.dto;

import static org.roly.personalaccountant.utils.Utils.initCap;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.roly.personalaccountant.domain.notifiers.ReactiveList;
import org.roly.personalaccountant.utils.PaymentsGenerator;

public class MonthlyExpenses {

    private final LinkedHashMap<LocalDate, ReactiveList<BaseTransaction>> payments;
    private final LocalDate startDate;
    private final ReactiveList<BaseTransaction> incomes;
    private final YearMonth yearMonth;
    private final OverallSumsTracker statistics;
    private final String expenseName;

    public MonthlyExpenses(String expenseName, LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.incomes = new ReactiveList<>();
        this.payments = PaymentsGenerator.initializeEmptyMonth(startDate, endDate);
        this.yearMonth = computeMonth(this.payments.keySet());
        this.expenseName = expenseName == null ?
                initCap(yearMonth.getMonth().toString()) + " " + yearMonth.getYear() :
                expenseName;
        this.statistics = new OverallSumsTracker(payments.keySet());
        registrations();
    }

    private YearMonth computeMonth(Set<LocalDate> localDates) {
        if (!localDates.isEmpty()) {
            return YearMonth.from(localDates.stream().skip(localDates.size() / 2).findFirst().orElseThrow());
        }
        throw new IllegalArgumentException("No dates found");
    }

    private void registrations() {
        this.incomes.registerListener(statistics);
        this.payments.forEach((key, value) -> value.registerListener(statistics));
    }

    public void addIncome(Income income) {
        this.incomes.add(income);
    }

    public void addPayment(Payment payment) {
        payments.get(payment.date()).add(payment);
    }

    public LinkedHashMap<LocalDate, List<BaseTransaction>> getPayments() {
        return new LinkedHashMap<>(payments);
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public List<BaseTransaction> getIncomes() {
        return List.copyOf(incomes);
    }

    public YearMonth getYearMonth() {
        return yearMonth;
    }

    public OverallSumsTracker getStatistics() {
        return statistics;
    }

    public String getExpenseName() {
        return expenseName;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MonthlyExpenses that = (MonthlyExpenses) o;
        return Objects.equals(payments, that.payments) && Objects.equals(startDate, that.startDate) && Objects.equals(
                incomes, that.incomes) && Objects.equals(yearMonth, that.yearMonth) && Objects.equals(statistics,
                that.statistics);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(payments);
        result = 31 * result + Objects.hashCode(startDate);
        result = 31 * result + incomes.hashCode();
        result = 31 * result + Objects.hashCode(yearMonth);
        return result;
    }

    @Override
    public String toString() {
        return "MonthlyExpenses{" +
                "payments=" + payments +
                ", startDate=" + startDate +
                ", incomes=" + incomes +
                '}';
    }
}
