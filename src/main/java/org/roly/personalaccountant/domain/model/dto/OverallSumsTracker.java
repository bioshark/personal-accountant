package org.roly.personalaccountant.domain.model.dto;

import static org.roly.personalaccountant.utils.StructuredLoggerHelper.ACTION_1_PARAMS;
import static org.roly.personalaccountant.utils.StructuredLoggerHelper.action;
import static org.roly.personalaccountant.utils.StructuredLoggerHelper.key;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.roly.personalaccountant.domain.notifiers.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OverallSumsTracker implements Listener<BaseTransaction> {

    private static final Logger LOGGER = LoggerFactory.getLogger(OverallSumsTracker.class);

    private final Map<LocalDate, DailyStatistics> dailyPayments = new HashMap<>();
    private final List<Saving> savings = new ArrayList<>();
    private double unallocatedPercentage = 100L;
    private final Saving defaultSaving = new Saving(null, "Unallocated", unallocatedPercentage);
    private final Percentages percentages = new Percentages();
    private double cashTotal;
    private double cashLeft;
    private double fixedExpenseTotal;
    private double dailyExpenseTotal;
    private double leisureExpenseTotal;
    private double savingExpenseTotal;

    public OverallSumsTracker(Set<LocalDate> days) {
        days.forEach(day -> dailyPayments.put(day, new DailyStatistics(day)));
        savings.add(this.defaultSaving);
    }

    @Override
    public void onAdd(BaseTransaction transaction) {
        switch (transaction) {
            case Payment payment -> {
                cashLeft -= payment.amount();
                switch (payment.type()) {
                    case FIXED -> fixedExpenseTotal += payment.amount();
                    case DAILY -> {
                        dailyPayments.get(payment.date()).addDailyExpenditure(payment.amount());
                        dailyExpenseTotal += payment.amount();
                    }
                    case LEISURE -> leisureExpenseTotal += payment.amount();
                    case SAVING -> savingExpenseTotal += payment.amount();
                }
            }
            case Income income -> {
                cashTotal += income.value();
                cashLeft += income.value();
            }
            default -> LOGGER.error(ACTION_1_PARAMS, action("Transaction is of wrong type"), key(transaction));
        }
        adjustSavings(cashLeft);
        adjustPercentages();
    }

    private void adjustPercentages() {
        percentages.setCorePercentage((dailyExpenseTotal + fixedExpenseTotal) / cashTotal * 100);
        percentages.setWantPercentage(leisureExpenseTotal / cashTotal * 100);
        percentages.setSavePercentage((savingExpenseTotal + cashLeft) / cashTotal * 100);
    }

    private void adjustSavings(double cashLeft) {
        for (Saving saving : savings) {
            saving.calculateValue(cashLeft);
        }
    }

    public double getCashTotal() {
        return cashTotal;
    }

    public double getCashLeft() {
        return cashLeft;
    }

    public double getFixedExpenseTotal() {
        return fixedExpenseTotal;
    }

    public double getDailyExpenseTotal() {
        return dailyExpenseTotal;
    }

    public double getLeisureExpenseTotal() {
        return leisureExpenseTotal;
    }

    public double getSavingExpenseTotal() {
        return savingExpenseTotal;
    }

    public Map<LocalDate, DailyStatistics> getDailyPayments() {
        return Map.copyOf(dailyPayments);
    }

    public List<Saving> getSavings() {
        return List.copyOf(savings);
    }

    public void addSaving(Saving saving) {
        if (saving.getPercentage() > unallocatedPercentage) {
            throw new IllegalArgumentException("Saving percentage is higher than unallocated percentage");
        }
        this.savings.add(saving);
        adjustUnallocated();
        adjustSavings(cashLeft);
    }

    public void removeSaving(Saving saving) {
        this.savings.remove(saving);
        adjustUnallocated();
        adjustSavings(cashLeft);
    }

    private void adjustUnallocated() {
        double allPercentages = this.savings.stream()
                .filter(s -> !s.equals(this.defaultSaving))
                .mapToDouble(Saving::getPercentage)
                .sum();
        this.unallocatedPercentage = 100 - allPercentages;
        this.defaultSaving.setPercentage(unallocatedPercentage);
    }

    public Percentages getPercentages() {
        return percentages;
    }
}
