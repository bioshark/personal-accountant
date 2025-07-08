package org.roly.personalaccountant.domain.notifiers;

import org.roly.personalaccountant.domain.model.expenses.Payment;

public interface PaymentListener {

    boolean onAdd(Payment payment);
}
