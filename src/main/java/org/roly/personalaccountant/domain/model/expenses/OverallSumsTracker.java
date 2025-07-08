package org.roly.personalaccountant.domain.model.expenses;

import org.roly.personalaccountant.domain.notifiers.PaymentListener;

public class OverallSumsTracker implements PaymentListener {

    private double overallCashLeft;
    private double dailyExpenseTotal;
    private double fixedExpenseTotal;

    @Override
    public void onAdd(Payment payment) {

    }
}
