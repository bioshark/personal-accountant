package org.roly.personalaccountant.domain.model.services;

import static org.roly.personalaccountant.domain.model.services.ExpenseManagerHelper.requireOpen;
import static org.roly.personalaccountant.domain.model.services.ExpenseManagerHelper.validatePaymentDateInMonth;

import java.time.LocalDate;
import org.roly.personalaccountant.domain.model.dto.Income;
import org.roly.personalaccountant.domain.model.entity.IncomeEntity;
import org.roly.personalaccountant.domain.model.entity.MonthlyExpenseEntity;
import org.roly.personalaccountant.domain.repository.MonthlyExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IncomeService {

    private final MonthlyExpenseRepository repository;
    private final ExpenseManagerHelper helper;

    @Autowired
    public IncomeService(MonthlyExpenseRepository repository, ExpenseManagerHelper helper) {
        this.repository = repository;
        this.helper = helper;
    }

    public MonthlyExpenseEntity addIncome(Income income) {
        MonthlyExpenseEntity entity = helper.findExpenseForDate(income.date())
                .orElseThrow(() -> new IllegalArgumentException("Can't find Expense for date " + income.date()));
        requireOpen(entity);
        entity.addIncome(new IncomeEntity(income.source(), income.date(), income.value()));
        return repository.save(entity);
    }

    public MonthlyExpenseEntity removeIncome(Income income) {
        MonthlyExpenseEntity entity = helper.findExpenseForDate(income.date())
                .orElseThrow(() -> new IllegalArgumentException("Can't find Expense for date " + income.date()));
        requireOpen(entity);
        entity.removeIncome(new IncomeEntity(income.source(), income.date(), income.value()));
        return repository.save(entity);
    }

    public MonthlyExpenseEntity removeIncomeById(Long incomeId) {
        MonthlyExpenseEntity entity = repository.findAll().stream()
                .filter(e -> e.getIncomes().stream().anyMatch(i -> i.getId().equals(incomeId)))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Income not found: " + incomeId));
        requireOpen(entity);
        entity.getIncomes().removeIf(i -> i.getId().equals(incomeId));
        return repository.save(entity);
    }

    public MonthlyExpenseEntity editIncome(Long incomeId, String source, LocalDate date, double value) {
        MonthlyExpenseEntity entity = repository.findAll().stream()
                .filter(e -> e.getIncomes().stream().anyMatch(i -> i.getId().equals(incomeId)))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Income not found: " + incomeId));
        requireOpen(entity);
        validatePaymentDateInMonth(entity, date);
        IncomeEntity income = entity.getIncomes().stream()
                .filter(i -> i.getId().equals(incomeId))
                .findFirst().orElseThrow();
        income.setSource(source);
        income.setDate(date);
        income.setValue(value);
        return repository.save(entity);
    }

}
