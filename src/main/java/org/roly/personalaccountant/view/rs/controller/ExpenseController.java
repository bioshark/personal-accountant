package org.roly.personalaccountant.view.rs.controller;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import org.roly.personalaccountant.domain.model.dto.Income;
import org.roly.personalaccountant.domain.model.dto.MonthlyExpenses;
import org.roly.personalaccountant.domain.model.dto.Payment;
import org.roly.personalaccountant.domain.model.dto.Payment.Category;
import org.roly.personalaccountant.domain.model.dto.Payment.PaymentType;
import org.roly.personalaccountant.domain.model.entity.MonthlyExpenseEntity;
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

    @PostMapping("/expense/{expenseName}")
    public ResponseEntity<Void> generateNewExpense(@PathVariable String expenseName,
            @RequestParam("startDate") LocalDate startDate,
            @RequestParam("endDate") LocalDate endDate) {
        MonthlyExpenses monthlyExpenses = expenseManager.addNewMonthlyExpense(expenseName, startDate, endDate);
        if (expenseManager.getExpense(monthlyExpenses.getYearMonth()).equals(monthlyExpenses)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/expense/delete/{yearMonth}")
    public ResponseEntity<Void> deleteExpense(@PathVariable YearMonth yearMonth) {
        if (expenseManager.deleteMonthlyExpense(yearMonth)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/expenses")
    public ResponseEntity<List<ExpenseResult>> getExpenses() {
        Map<YearMonth, MonthlyExpenses> expenses = expenseManager.getExpenses();
        if (expenses != null && !expenses.isEmpty()) {
            return ResponseEntity.ok(expenseConverter.convertToList(expenses));
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/addIncome")
    public ResponseEntity<Void> addIncome(@RequestParam("source") String source,
            @RequestParam("date") LocalDate date,
            @RequestParam("value") double value) {
        MonthlyExpenseEntity monthlyExpenseEntity = expenseManager.addIncome(new Income(null, source, date, value));
        if (monthlyExpenseEntity != null) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.internalServerError().build();
    }

    @PostMapping("/removeIncome")
    public ResponseEntity<Void> removeIncome(@RequestParam("source") String source,
            @RequestParam("date") LocalDate date,
            @RequestParam("value") double value) {
        MonthlyExpenseEntity monthlyExpenseEntity = expenseManager.removeIncome(new Income(null, source, date, value));
        if (monthlyExpenseEntity != null) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.internalServerError().build();
    }

    @PostMapping("/editIncome")
    public ResponseEntity<Void> editIncome(@RequestParam("id") Long id,
            @RequestParam("source") String source,
            @RequestParam("date") LocalDate date,
            @RequestParam("value") double value) {
        MonthlyExpenseEntity monthlyExpenseEntity = expenseManager.editIncome(id, source, date, value);
        if (monthlyExpenseEntity != null) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.internalServerError().build();
    }


    @PostMapping("/addpayment")
    public ResponseEntity<Void> addPayment(@RequestParam("description") String description,
            @RequestParam("category") Category category,
            @RequestParam("type") PaymentType type,
            @RequestParam("amount") double amount,
            @RequestParam("date") LocalDate date) {
        MonthlyExpenseEntity monthlyExpenseEntity = expenseManager.addPayment(new Payment(description, category, type, amount, date));
        if (monthlyExpenseEntity != null) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.internalServerError().build();
    }

}
