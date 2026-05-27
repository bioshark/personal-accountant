package org.roly.personalaccountant.domain.model.services;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.Map;
import org.roly.personalaccountant.domain.model.dto.Income;
import org.roly.personalaccountant.domain.model.dto.MonthlyExpenses;
import org.roly.personalaccountant.domain.model.dto.Payment;
import org.roly.personalaccountant.domain.model.entity.IncomeEntity;
import org.roly.personalaccountant.domain.model.entity.MonthlyExpenseEntity;
import org.roly.personalaccountant.domain.model.entity.PaymentEntity;
import org.roly.personalaccountant.domain.repository.MonthlyExpenseRepository;
import org.springframework.stereotype.Service;

@Service
public class ExpenseManager {

    private final MonthlyExpenseRepository repository;

    public ExpenseManager(MonthlyExpenseRepository repository) {
        this.repository = repository;
    }

    public MonthlyExpenses addNewMonthlyExpense(YearMonth yearMonth, LocalDate startDate) {
        MonthlyExpenses dto = new MonthlyExpenses(startDate, yearMonth);
        MonthlyExpenseEntity entity = new MonthlyExpenseEntity(
                yearMonth.getYear(), yearMonth.getMonthValue(), startDate, dto.getExpenseName());
        repository.save(entity);
        return dto;
    }

    public MonthlyExpenseEntity addPayment(Payment payment) {
        LocalDate paymentDate = payment.date();
        MonthlyExpenseEntity entity = findExpenseForDate(paymentDate);
        entity.addPayment(new PaymentEntity(payment.description(), payment.category(), payment.type(), payment.amount(), payment.date()));
        return repository.save(entity);
    }

    public MonthlyExpenseEntity addIncome(Income income) {
        MonthlyExpenseEntity entity = findExpenseForDate(income.date());
        entity.addIncome(new IncomeEntity(income.source(), income.date(), income.value()));
        return repository.save(entity);
    }

    public MonthlyExpenses getExpense(YearMonth yearMonth) {
        return repository.findByYearAndMonth(yearMonth.getYear(), yearMonth.getMonthValue())
                .map(this::toDto)
                .orElse(null);
    }

    public Map<YearMonth, MonthlyExpenses> getExpenses() {
        Map<YearMonth, MonthlyExpenses> result = new LinkedHashMap<>();
        for (MonthlyExpenseEntity entity : repository.findAll()) {
            YearMonth ym = YearMonth.of(entity.getYear(), entity.getMonth());
            result.put(ym, toDto(entity));
        }
        return result;
    }

    public void clearExpenses() {
        repository.deleteAll();
    }

    private MonthlyExpenseEntity findExpenseForDate(LocalDate date) {
        for (MonthlyExpenseEntity entity : repository.findAll()) {
            MonthlyExpenses dto = toDto(entity);
            if (dto.getPayments().containsKey(date)) {
                return entity;
            }
        }
        throw new IllegalArgumentException("Can't find Expense for date " + date);
    }

    private MonthlyExpenses toDto(MonthlyExpenseEntity entity) {
        YearMonth ym = YearMonth.of(entity.getYear(), entity.getMonth());
        MonthlyExpenses dto = new MonthlyExpenses(entity.getStartDate(), ym);
        for (IncomeEntity ie : entity.getIncomes()) {
            dto.addIncome(new Income(ie.getSource(), ie.getDate(), ie.getValue()));
        }
        for (PaymentEntity pe : entity.getPayments()) {
            dto.addPayment(new Payment(pe.getDescription(), pe.getCategory(), pe.getType(), pe.getAmount(), pe.getDate()));
        }
        return dto;
    }
}
