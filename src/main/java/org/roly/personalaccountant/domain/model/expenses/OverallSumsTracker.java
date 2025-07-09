package org.roly.personalaccountant.domain.model.expenses;

import org.roly.personalaccountant.domain.notifiers.Listener;

public class OverallSumsTracker implements Listener<Payment> {

    private double overallCashLeft;
    private double dailyExpenseTotal;
    private double fixedExpenseTotal;

    @Override
    public void onAdd(Payment payment) {

    }
}
