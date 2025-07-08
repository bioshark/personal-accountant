package org.roly.personalaccountant.domain.notifiers;

import static org.roly.personalaccountant.utils.StructuredLoggerHelper.ACTION_1_PARAMS;
import static org.roly.personalaccountant.utils.StructuredLoggerHelper.action;
import static org.roly.personalaccountant.utils.StructuredLoggerHelper.key;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.roly.personalaccountant.domain.model.expenses.Payment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PaymentList extends ArrayList<Payment> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentList.class);

    Set<PaymentListener> listeners = new HashSet<>();

    public void registerListener(PaymentListener listener) {
        listeners.add(listener);
    }

    @Override
    public boolean add(Payment element) {
        boolean added = super.add(element);

        if (added) {
            notifyListeners(element);
        }
        return added;
    }

    private void notifyListeners(Payment element) {
        for (PaymentListener listener : listeners) {
            try {
                listener.onAdd(element);
            } catch (RuntimeException e) {
                LOGGER.debug(ACTION_1_PARAMS, action("Listener notification exception"), key(listener));
            }
        }
    }
}
