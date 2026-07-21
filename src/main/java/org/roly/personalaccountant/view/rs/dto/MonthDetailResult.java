package org.roly.personalaccountant.view.rs.dto;

import java.time.LocalDate;
import java.util.List;

/**
 * Read-only projection of a month's detail for API clients (e.g. the JavaFX desktop UI).
 * Flat and JSON-friendly to avoid serializing the rich domain objects directly.
 */
public record MonthDetailResult(
        String expenseName,
        String yearMonth,
        double cashTotal,
        double cashLeft,
        double fixedBudget,
        double leisureBudget,
        double savingBudget,
        double fixedExpenseTotal,
        double leisureExpenseTotal,
        double savingExpenseTotal,
        double dailyExpenseTotal,
        double corePercentage,
        double wantPercentage,
        double savePercentage,
        List<DayResult> days
) {

    public record DayResult(
            LocalDate date,
            double maxAllocation,
            double expenditure,
            boolean dayDone,
            boolean weekend
    ) {
    }
}
