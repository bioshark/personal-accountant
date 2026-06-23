package org.roly.personalaccountant.view.web;

import java.time.LocalDate;
import java.time.YearMonth;
import org.roly.personalaccountant.domain.model.dto.Income;
import org.roly.personalaccountant.domain.model.dto.Payment;
import org.roly.personalaccountant.domain.model.dto.Payment.Category;
import org.roly.personalaccountant.domain.model.dto.Payment.PaymentType;
import org.roly.personalaccountant.domain.model.entity.MonthlyExpenseEntity;
import org.roly.personalaccountant.domain.model.services.ExpenseManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ExpenseWebController {

    private final ExpenseManager expenseManager;

    public ExpenseWebController(ExpenseManager expenseManager) {
        this.expenseManager = expenseManager;
    }

    @GetMapping("/")
    public String index(Model model) {
        var expenses = expenseManager.getExpenses().values().stream()
                .sorted((a, b) -> a.getStartDate().compareTo(b.getStartDate()))
                .toList();
        model.addAttribute("expenses", expenses);
        return "index";
    }

    @GetMapping("/month/{yearMonth}")
    public String detail(@PathVariable YearMonth yearMonth, Model model) {
        model.addAttribute("expense", expenseManager.getExpense(yearMonth));
        return "detail";
    }

    @PostMapping("/month/addincome")
    public String addIncome(@RequestParam String source, @RequestParam LocalDate date,
            @RequestParam double value) {
        expenseManager.addIncome(new Income(null, source, date, value));
        return "redirect:/month/" + YearMonth.from(date);
    }

    @PostMapping("/month/removeincome/{incomeId}")
    public String removeIncome(@PathVariable Long incomeId) {
        MonthlyExpenseEntity entity = expenseManager.removeIncomeById(incomeId);
        YearMonth ym = YearMonth.of(entity.getYear(), entity.getMonth());
        return "redirect:/month/" + ym;
    }

    @PostMapping("/month/editincome/{incomeId}")
    public String editIncome(@PathVariable Long incomeId, @RequestParam String source,
            @RequestParam LocalDate date, @RequestParam double value) {
        MonthlyExpenseEntity entity = expenseManager.editIncome(incomeId, source, date, value);
        YearMonth ym = YearMonth.of(entity.getYear(), entity.getMonth());
        return "redirect:/month/" + ym;
    }

    @PostMapping("/month/generate")
    public String generateExpense(@RequestParam(required = false) String expenseName, @RequestParam LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate, RedirectAttributes redirectAttributes) {
        try {
            expenseManager.addNewMonthlyExpense(expenseName, startDate, endDate);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/";
    }

    @PostMapping("/month/addpayment")
    public String addPayment(@RequestParam String description, @RequestParam Category category,
            @RequestParam PaymentType type, @RequestParam double amount, @RequestParam LocalDate date) {
        expenseManager.addPayment(new Payment(null, description, category, type, amount, date));
        return "redirect:/month/" + YearMonth.from(date);
    }

    @PostMapping("/month/removepayment/{paymentId}")
    public String removePayment(@PathVariable Long paymentId) {
        MonthlyExpenseEntity entity = expenseManager.removePaymentById(paymentId);
        YearMonth ym = YearMonth.of(entity.getYear(), entity.getMonth());
        return "redirect:/month/" + ym;
    }

    @PostMapping("/month/editpayment/{paymentId}")
    public String editPayment(@PathVariable Long paymentId, @RequestParam String description,
            @RequestParam Category category, @RequestParam PaymentType type,
            @RequestParam double amount, @RequestParam LocalDate date) {
        MonthlyExpenseEntity entity = expenseManager.editPayment(paymentId, description, date, amount, type, category);
        YearMonth ym = YearMonth.of(entity.getYear(), entity.getMonth());
        return "redirect:/month/" + ym;
    }

    @PostMapping("/month/delete/{yearMonth}")
    public String deleteExpense(@PathVariable YearMonth yearMonth) {
        expenseManager.deleteMonthlyExpense(yearMonth);
        return "redirect:/";
    }

    @PostMapping("/month/toggleday")
    public String toggleDayDone(@RequestParam LocalDate date) {
        MonthlyExpenseEntity entity = expenseManager.toggleDayDone(date);
        YearMonth ym = YearMonth.of(entity.getYear(), entity.getMonth());
        return "redirect:/month/" + ym;
    }

    @PostMapping("/month/addsaving/{yearMonth}")
    public String addSaving(@PathVariable YearMonth yearMonth, @RequestParam String name,
            @RequestParam double percentage, RedirectAttributes redirectAttributes) {
        try {
            expenseManager.addSaving(yearMonth, name, percentage);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/month/" + yearMonth;
    }

    @PostMapping("/month/removesaving/{savingId}")
    public String removeSaving(@PathVariable Long savingId) {
        MonthlyExpenseEntity entity = expenseManager.removeSavingById(savingId);
        YearMonth ym = YearMonth.of(entity.getYear(), entity.getMonth());
        return "redirect:/month/" + ym;
    }

    @PostMapping("/month/editsaving/{savingId}")
    public String editSaving(@PathVariable Long savingId, @RequestParam String name,
            @RequestParam double percentage) {
        MonthlyExpenseEntity entity = expenseManager.editSaving(savingId, name, percentage);
        YearMonth ym = YearMonth.of(entity.getYear(), entity.getMonth());
        return "redirect:/month/" + ym;
    }
}
