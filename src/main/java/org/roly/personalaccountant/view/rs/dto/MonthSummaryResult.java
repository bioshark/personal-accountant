package org.roly.personalaccountant.view.rs.dto;

/**
 * Lightweight month summary for list/selector views (includes the yearMonth key needed to
 * fetch a month's detail).
 */
public record MonthSummaryResult(String yearMonth, String expenseName, double cashLeft) {

}
