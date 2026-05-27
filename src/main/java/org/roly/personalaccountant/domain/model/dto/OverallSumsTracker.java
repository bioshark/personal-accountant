package org.roly.personalaccountant.domain.model.dto;

import static org.roly.personalaccountant.utils.StructuredLoggerHelper.ACTION_1_PARAMS;
import static org.roly.personalaccountant.utils.StructuredLoggerHelper.action;
import static org.roly.personalaccountant.utils.StructuredLoggerHelper.key;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import org.roly.personalaccountant.domain.model.dto.Payment.PaymentType;
import org.roly.personalaccountant.domain.notifiers.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OverallSumsTracker implements Listener<BaseTransaction> {

    private static final Logger LOGGER = LoggerFactory.getLogger(OverallSumsTracker.class);

    private double cashTotal;
    private double cashLeft;
    private double fixedExpenseTotal;
    // TODO the value has  to be more than the payment.amount sum. need to keep track of stats like: week-end day, estimate for the day and diff to it.
    private final Map<LocalDate, Double> dailyPayments = new HashMap<>();

    @Override
    public void onAdd(BaseTransaction transaction) {
        switch (transaction) {
            case Payment payment -> {
                cashLeft -= payment.amount();
                if (payment.isFixed()) {
                    fixedExpenseTotal += payment.amount();
                } else if (payment.type() == PaymentType.DAILY) {
                    dailyPayments.put(payment.date(), dailyPayments.getOrDefault(payment.date(), 0.0d) + payment.amount());
                }
            }
            case Income income -> {
                cashTotal += income.value();
                cashLeft += income.value();
            }
            default -> LOGGER.error(ACTION_1_PARAMS, action("Transaction is of wrong type"), key(transaction));
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

    public Map<LocalDate, Double> getDailyPayments() {
        return Map.copyOf(dailyPayments);
    }
}
