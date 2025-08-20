package org.roly.personalaccountant.view.rs;

import java.time.LocalDate;
import java.time.YearMonth;
import org.roly.personalaccountant.domain.model.expenses.MonthlyExpenses;
import org.roly.personalaccountant.domain.model.services.ExpenseManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/v1/accountant/expenses"})
public class ExpenseController {

    private final ExpenseManager expenseManager;

    @Autowired
    public ExpenseController(ExpenseManager expenseManager) {
        this.expenseManager = expenseManager;
    }

    @GetMapping("/expense/{yearMonth}")
    public ResponseEntity<Void> generateNewExpense(@PathVariable YearMonth yearMonth, @RequestParam("startDate") LocalDate startDate) {
        MonthlyExpenses monthlyExpenses = expenseManager.addNewMonthlyExpense(yearMonth, startDate);
        if (expenseManager.getExpense(yearMonth).equals(monthlyExpenses)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
