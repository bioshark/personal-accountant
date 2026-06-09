package org.roly.personalaccountant.domain.model.services;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
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

    public MonthlyExpenses addNewMonthlyExpense(String expenseName, LocalDate startDate, LocalDate endDate) {
        MonthlyExpenses dto = new MonthlyExpenses(expenseName, startDate, endDate);
        MonthlyExpenseEntity entity = new MonthlyExpenseEntity(
                dto.getYearMonth().getYear(),
                dto.getYearMonth().getMonthValue(),
                startDate,
                endDate,
                dto.getExpenseName()
        );
        repository.save(entity);
        return dto;
    }

    public boolean deleteMonthlyExpense(YearMonth yearMonth) {
        Optional<MonthlyExpenseEntity> monthlyExpenseEntity = repository.findByYearAndMonth(yearMonth.getYear(), yearMonth.getMonthValue());

        if (monthlyExpenseEntity.isEmpty()) {
            return false;
        }
        repository.delete(monthlyExpenseEntity.get());
        return true;
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

    public MonthlyExpenseEntity removeIncome(Income income) {
        MonthlyExpenseEntity entity = findExpenseForDate(income.date());
        entity.removeIncome(new IncomeEntity(income.source(), income.date(), income.value()));
        return repository.save(entity);
    }

    public MonthlyExpenseEntity removeIncomeById(Long incomeId) {
        MonthlyExpenseEntity entity = repository.findAll().stream()
                .filter(e -> e.getIncomes().stream().anyMatch(i -> i.getId().equals(incomeId)))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Income not found: " + incomeId));
        entity.getIncomes().removeIf(i -> i.getId().equals(incomeId));
        return repository.save(entity);
    }

    public MonthlyExpenseEntity editIncome(Long incomeId, String source, LocalDate date, double value) {
        MonthlyExpenseEntity entity = repository.findAll().stream()
                .filter(e -> e.getIncomes().stream().anyMatch(i -> i.getId().equals(incomeId)))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Income not found: " + incomeId));
        IncomeEntity income = entity.getIncomes().stream()
                .filter(i -> i.getId().equals(incomeId))
                .findFirst().orElseThrow();
        income.setSource(source);
        income.setDate(date);
        income.setValue(value);
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
        MonthlyExpenses dto = new MonthlyExpenses(
                entity.getExpenseName(),
                entity.getStartDate(),
                entity.getEndDate());
        for (IncomeEntity ie : entity.getIncomes()) {
            dto.addIncome(new Income(ie.getId(), ie.getSource(), ie.getDate(), ie.getValue()));
        }
        for (PaymentEntity pe : entity.getPayments()) {
            dto.addPayment(new Payment(pe.getDescription(), pe.getCategory(), pe.getType(), pe.getAmount(), pe.getDate()));
        }
        return dto;
    }
}
