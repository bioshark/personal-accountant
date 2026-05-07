package org.roly.personalaccountant.view.rs.controller;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import org.roly.personalaccountant.domain.model.dto.MonthlyExpenses;
import org.roly.personalaccountant.domain.model.services.ExpenseManager;
import org.roly.personalaccountant.view.rs.dto.ExpenseResult;
import org.roly.personalaccountant.view.rs.mapper.ExpenseConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/v1/accountant/expenses"})
public class ExpenseController {

    private final ExpenseManager expenseManager;
    private final ExpenseConverter expenseConverter;

    @Autowired
    public ExpenseController(ExpenseManager expenseManager, ExpenseConverter expenseConverter) {
        this.expenseManager = expenseManager;
        this.expenseConverter = expenseConverter;
    }

    @PostMapping("/expense/{yearMonth}")
    public ResponseEntity<Void> generateNewExpense(@PathVariable YearMonth yearMonth, @RequestParam("startDate") LocalDate startDate) {
        MonthlyExpenses monthlyExpenses = expenseManager.addNewMonthlyExpense(yearMonth, startDate);
        if (expenseManager.getExpense(yearMonth).equals(monthlyExpenses)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    // TODO add a test
    @GetMapping("/expenses")
    public ResponseEntity<List<ExpenseResult>> getExpenses() {
        Map<YearMonth, MonthlyExpenses> expenses = expenseManager.getExpenses();
        if (expenses != null && !expenses.isEmpty()) {
            return ResponseEntity.ok(expenseConverter.convertToList(expenses));
        }
        return ResponseEntity.notFound().build();
    }
}
