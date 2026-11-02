package org.roly.personalaccountant.domain.model.services;

import java.time.YearMonth;

public class MonthClosedException extends IllegalStateException {

    public MonthClosedException(YearMonth yearMonth) {
        super("Month " + yearMonth + " is marked as done; reopen it before making changes");
    }
}
