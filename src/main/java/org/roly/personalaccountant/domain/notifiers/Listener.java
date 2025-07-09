package org.roly.personalaccountant.domain.notifiers;

public interface Listener<T> {

    void onAdd(T element);
}
