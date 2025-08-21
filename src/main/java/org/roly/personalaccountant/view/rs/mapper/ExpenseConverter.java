package org.roly.personalaccountant.view.rs.mapper;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.roly.personalaccountant.domain.model.dto.MonthlyExpenses;
import org.roly.personalaccountant.view.rs.dto.ExpenseResult;
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

}
