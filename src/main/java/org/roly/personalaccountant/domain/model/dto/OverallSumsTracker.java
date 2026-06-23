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
import org.roly.personalaccountant.domain.model.dto.Payment.PaymentType;
import org.roly.personalaccountant.domain.notifiers.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OverallSumsTracker implements Listener<BaseTransaction> {

    private static final Logger LOGGER = LoggerFactory.getLogger(OverallSumsTracker.class);

    private final Map<LocalDate, DailyStatistics> dailyPayments = new HashMap<>();
    private final List<Saving> savings = new ArrayList<>();
    private double unallocatedPercentage = 100L;
    private final Saving defaultSaving = new Saving(null, "Unallocated", unallocatedPercentage);
    private double cashTotal;
    private double cashLeft;
    private double fixedExpenseTotal;
    private double dailyExpenseTotal;

    public OverallSumsTracker(Set<LocalDate> days) {
        days.forEach(day -> dailyPayments.put(day, new DailyStatistics(day)));
        savings.add(this.defaultSaving);
    }

    @Override
    public void onAdd(BaseTransaction transaction) {
        switch (transaction) {
            case Payment payment -> {
                cashLeft -= payment.amount();
                if (payment.isFixed()) {
                    fixedExpenseTotal += payment.amount();
                } else if (payment.type() == PaymentType.DAILY) {
                    dailyPayments.get(payment.date()).addDailyExpenditure(payment.amount());
                    dailyExpenseTotal += payment.amount();
                }
            }
            case Income income -> {
                cashTotal += income.value();
                cashLeft += income.value();
            }
            default -> LOGGER.error(ACTION_1_PARAMS, action("Transaction is of wrong type"), key(transaction));
        }
        adjustSavings(cashLeft);
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
        adjustUnallocated(saving);
        adjustSavings(cashLeft);
    }

    public void removeSaving(Saving saving) {
        this.savings.remove(saving);
        adjustUnallocated(saving);
        adjustSavings(cashLeft);
    }

    private void adjustUnallocated(Saving saving) {
        double allPercentages = this.savings.stream()
                .filter(s -> !s.equals(this.defaultSaving))
                .mapToDouble(Saving::getPercentage)
                .sum();
        this.unallocatedPercentage = 100 - allPercentages;
        this.defaultSaving.setPercentage(unallocatedPercentage);
    }

}
