package org.roly.personalaccountant.domain.model.expenses;

import static org.roly.personalaccountant.utils.StructuredLoggerHelper.ACTION_1_PARAMS;
import static org.roly.personalaccountant.utils.StructuredLoggerHelper.action;
import static org.roly.personalaccountant.utils.StructuredLoggerHelper.key;

import org.roly.personalaccountant.domain.model.expenses.Payment.PaymentType;
import org.roly.personalaccountant.domain.notifiers.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OverallSumsTracker implements Listener<BaseTransaction> {

    private static final Logger LOGGER = LoggerFactory.getLogger(OverallSumsTracker.class);

    private double cashTotal;
    private double cashLeft;
    private double fixedExpenseTotal;

    @Override
    public void onAdd(BaseTransaction transaction) {
        switch (transaction) {
            case Payment payment -> {
                cashLeft -= payment.amount();
                addToFixedExpense(payment);
            }
            case Income income -> {
                cashTotal += income.value();
                cashLeft += income.value();
            }
            default -> LOGGER.error(ACTION_1_PARAMS, action("Transaction is of wrong type"), key(transaction));
        }
    }

    // TODO write a test for this.
    private void addToFixedExpense(Payment payment) {
        if (payment.type().equals(PaymentType.FIXED)) {
            fixedExpenseTotal += payment.amount();
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
}
