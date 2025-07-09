package org.roly.personalaccountant.domain.notifiers;

import static org.roly.personalaccountant.utils.StructuredLoggerHelper.ACTION_1_PARAMS;
import static org.roly.personalaccountant.utils.StructuredLoggerHelper.action;
import static org.roly.personalaccountant.utils.StructuredLoggerHelper.key;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReactiveList<T> extends ArrayList<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReactiveList.class);

    Set<Listener<T>> listeners = new HashSet<>();

    public void registerListener(Listener<T> listener) {
        listeners.add(listener);
    }

    @Override
    public boolean add(T element) {
        boolean added = super.add(element);

        if (added) {
            notifyListeners(element);
        }
        return added;
    }

    private void notifyListeners(T element) {
        for (Listener<T> listener : listeners) {
            try {
                listener.onAdd(element);
            } catch (RuntimeException e) {
                LOGGER.debug(ACTION_1_PARAMS, action("Listener notification exception"), key(listener));
            }
        }
    }
}
