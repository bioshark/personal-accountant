package org.roly.personalaccountant.view.rs;

import java.time.LocalDate;
import java.time.YearMonth;
import org.roly.personalaccountant.domain.model.services.ExpenseManager;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping("/{yearMonth}")
    public void getNonCombinable(@PathVariable YearMonth yearMonth, @RequestParam("startDate") LocalDate startDate) {
        expenseManager.addNewMonthlyExpense(yearMonth, startDate);
    }
}
