package org.roly.personalaccountant.view.rs.mapper;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.roly.personalaccountant.domain.model.dto.DailyStatistics;
import org.roly.personalaccountant.domain.model.dto.MonthlyExpenses;
import org.roly.personalaccountant.domain.model.dto.OverallSumsTracker;
import org.roly.personalaccountant.domain.model.dto.Percentages;
import org.roly.personalaccountant.view.rs.dto.ExpenseResult;
import org.roly.personalaccountant.view.rs.dto.MonthDetailResult;
import org.roly.personalaccountant.view.rs.dto.MonthSummaryResult;
import org.springframework.stereotype.Service;

@Service
public class ExpenseConverter {

    public ExpenseResult convert(MonthlyExpenses monthlyExpenses) {
        return new ExpenseResult(monthlyExpenses.getExpenseName(), monthlyExpenses.getStatistics().getCashLeft());
    }

    public List<ExpenseResult> convertToList(Map<YearMonth, MonthlyExpenses> monthlyExpenses) {
        List<ExpenseResult> result = new ArrayList<>();
        monthlyExpenses.values()
                .forEach(monthlyExpense ->
                        result.add(convert(monthlyExpense)));
        return result;
    }

    public List<MonthSummaryResult> convertSummaries(Map<YearMonth, MonthlyExpenses> monthlyExpenses) {
        return monthlyExpenses.values().stream()
                .sorted(Comparator.comparing(MonthlyExpenses::getYearMonth))
                .map(m -> new MonthSummaryResult(
                        m.getYearMonth().toString(),
                        m.getExpenseName(),
                        m.getStatistics().getCashLeft()))
                .toList();
    }

    public MonthDetailResult convertDetail(MonthlyExpenses monthlyExpenses, double fixedBudget,
            double leisureBudget, double savingBudget) {
        OverallSumsTracker stats = monthlyExpenses.getStatistics();
        Percentages percentages = stats.getPercentages();
        List<MonthDetailResult.DayResult> days = stats.getDailyPayments().values().stream()
                .sorted(Comparator.comparing(DailyStatistics::getDate))
                .map(day -> new MonthDetailResult.DayResult(
                        day.getDate(),
                        day.getDailyMaxAllocation(),
                        day.getDailyTotalExpenditure(),
                        day.isDayDone(),
                        day.isWeekEnd()))
                .toList();
        return new MonthDetailResult(
                monthlyExpenses.getExpenseName(),
                monthlyExpenses.getYearMonth().toString(),
                stats.getCashTotal(),
                stats.getCashLeft(),
                fixedBudget,
                leisureBudget,
                savingBudget,
                stats.getFixedExpenseTotal(),
                stats.getLeisureExpenseTotal(),
                stats.getSavingExpenseTotal(),
                stats.getDailyExpenseTotal(),
                percentages.getCorePercentage(),
                percentages.getWantPercentage(),
                percentages.getSavePercentage(),
                days);
    }

}
