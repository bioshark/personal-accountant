package org.roly.personalaccountant.view.web;

import java.time.YearMonth;
import org.roly.personalaccountant.domain.model.services.ExpenseManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ExpenseWebController {

    private final ExpenseManager expenseManager;

    public ExpenseWebController(ExpenseManager expenseManager) {
        this.expenseManager = expenseManager;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("expenses", expenseManager.getExpenses().values());
        return "index";
    }

    @GetMapping("/month/{yearMonth}")
    public String detail(@PathVariable YearMonth yearMonth, Model model) {
        model.addAttribute("expense", expenseManager.getExpense(yearMonth));
        return "detail";
    }
}
