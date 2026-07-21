package org.roly.personalaccountant.view.web;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import org.roly.personalaccountant.domain.model.dto.Income;
import org.roly.personalaccountant.domain.model.dto.MonthlyExpenses;
import org.roly.personalaccountant.domain.model.dto.Payment;
import org.roly.personalaccountant.domain.model.dto.Payment.Category;
import org.roly.personalaccountant.domain.model.dto.Payment.PaymentType;
import org.roly.personalaccountant.domain.model.entity.MonthlyExpenseEntity;
import org.roly.personalaccountant.domain.model.services.ExpenseManager;
import org.roly.personalaccountant.domain.model.services.RecurringPaymentService;
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
    private final RecurringPaymentService recurringPaymentService;

    public ExpenseWebController(ExpenseManager expenseManager, RecurringPaymentService recurringPaymentService) {
        this.expenseManager = expenseManager;
        this.recurringPaymentService = recurringPaymentService;
    }

    @GetMapping("/")
    public String index(Model model) {
        var expenses = expenseManager.getExpenses().values().stream()
                .sorted(Comparator.comparing(MonthlyExpenses::getStartDate))
                .toList();
        var expensesByYear = expenses.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        e -> e.getYearMonth().getYear(),
                        java.util.TreeMap::new,
                        java.util.stream.Collectors.toList()));
        model.addAttribute("expenses", expenses);
        model.addAttribute("expensesByYear", expensesByYear);
        model.addAttribute("recurringTemplates", recurringPaymentService.getAllTemplates());
        return "index";
    }

    @GetMapping("/month/{yearMonth}")
    public String detail(@PathVariable YearMonth yearMonth, Model model) {
        MonthlyExpenses expense = expenseManager.getExpense(yearMonth);
        model.addAttribute("expense", expense);
        model.addAttribute("pendingPayments", expenseManager.getPendingPayments(yearMonth));
        model.addAttribute("totalPendingPayments", expenseManager.getTotalPendingPayments(yearMonth));
        model.addAttribute("fixedBudget", expenseManager.getFixedBudget(yearMonth));
        model.addAttribute("leisureBudget", expenseManager.getLeisureBudget(yearMonth));
        model.addAttribute("savingBudget", expenseManager.getSavingBudget(yearMonth));
        model.addAttribute("recurringTemplates", recurringPaymentService.getAllTemplates());
        model.addAttribute("defaultDate", defaultDateFor(expense));
        return "detail";
    }

    /**
     * Default date to pre-fill in the "add" forms: today when it falls within the month's range, otherwise the closest boundary (start or end).
     */
    private LocalDate defaultDateFor(MonthlyExpenses expense) {
        LocalDate today = LocalDate.now();
        if (expense == null) {
            return today;
        }
        LocalDate start = expense.getStartDate();
        LocalDate end = expense.getEndDate();
        if (today.isBefore(start)) {
            return start;
        }
        if (today.isAfter(end)) {
            return end;
        }
        return today;
    }

    @PostMapping("/month/addincome")
    public String addIncome(@RequestParam String source, @RequestParam LocalDate date,
            @RequestParam double value) {
        MonthlyExpenseEntity entity = expenseManager.addIncome(new Income(null, source, date, value));
        YearMonth ym = YearMonth.of(entity.getYear(), entity.getMonth());
        return "redirect:/month/" + ym;
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
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) List<Long> templateIds,
            RedirectAttributes redirectAttributes) {
        try {
            var monthlyExpenses = expenseManager.addNewMonthlyExpense(expenseName, startDate, endDate);
            if (templateIds != null && !templateIds.isEmpty()) {
                var templates = recurringPaymentService.getAllTemplates().stream()
                        .filter(t -> templateIds.contains(t.getId()))
                        .toList();
                expenseManager.pullTemplatesIntoMonth(monthlyExpenses.getYearMonth(), templates);
            }
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/";
    }

    @PostMapping("/month/addpayment")
    public String addPayment(@RequestParam String description, @RequestParam Category category,
            @RequestParam PaymentType type, @RequestParam double amount, @RequestParam LocalDate date) {
        MonthlyExpenseEntity entity = expenseManager.addPayment(new Payment(null, description, category, type, amount, date));
        YearMonth ym = YearMonth.of(entity.getYear(), entity.getMonth());
        return "redirect:/month/" + ym;
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

    @PostMapping("/month/editdates/{yearMonth}")
    public String editMonthDates(@PathVariable YearMonth yearMonth,
            @RequestParam LocalDate startDate, @RequestParam(required = false) LocalDate endDate,
            RedirectAttributes redirectAttributes) {
        try {
            expenseManager.editMonthDates(yearMonth, startDate, endDate);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
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

    @PostMapping("/month/pullrecurring/{yearMonth}")
    public String pullRecurringIntoMonth(@PathVariable YearMonth yearMonth,
            @RequestParam List<Long> templateIds) {
        var templates = recurringPaymentService.getAllTemplates().stream()
                .filter(t -> templateIds.contains(t.getId()))
                .toList();
        expenseManager.pullTemplatesIntoMonth(yearMonth, templates);
        return "redirect:/month/" + yearMonth;
    }

    @PostMapping("/month/paypending/{pendingId}")
    public String payPendingPayment(@PathVariable Long pendingId, @RequestParam LocalDate date,
            @RequestParam double amount) {
        MonthlyExpenseEntity entity = expenseManager.payPendingPayment(pendingId, date, amount);
        YearMonth ym = YearMonth.of(entity.getYear(), entity.getMonth());
        return "redirect:/month/" + ym;
    }

    @PostMapping("/month/skippending/{pendingId}")
    public String skipPendingPayment(@PathVariable Long pendingId) {
        MonthlyExpenseEntity entity = expenseManager.removePendingPayment(pendingId);
        YearMonth ym = YearMonth.of(entity.getYear(), entity.getMonth());
        return "redirect:/month/" + ym;
    }
}
