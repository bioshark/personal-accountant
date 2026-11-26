package org.roly.personalaccountant.domain.model.services;

import static org.roly.personalaccountant.domain.model.services.ExpenseManagerHelper.requireOpen;
import static org.roly.personalaccountant.domain.model.services.ExpenseManagerHelper.toDto;

import java.time.YearMonth;
import org.roly.personalaccountant.domain.model.dto.MonthlyExpenses;
import org.roly.personalaccountant.domain.model.dto.Saving;
import org.roly.personalaccountant.domain.model.entity.MonthlyExpenseEntity;
import org.roly.personalaccountant.domain.model.entity.SavingEntity;
import org.roly.personalaccountant.domain.repository.MonthlyExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SavingsService {

    private static final String EXPENSE_NOT_FOUND = "Expense not found: ";

    private final MonthlyExpenseRepository repository;
    private final ExpenseManagerHelper helper;

    @Autowired
    SavingsService(MonthlyExpenseRepository repository, ExpenseManagerHelper helper) {
        this.repository = repository;
        this.helper = helper;
    }

    public MonthlyExpenseEntity addSaving(YearMonth yearMonth, String name, double percentage) {
        MonthlyExpenseEntity entity = helper.getEntity(yearMonth)
                .orElseThrow(() -> new IllegalArgumentException(EXPENSE_NOT_FOUND + yearMonth));
        requireOpen(entity);
        MonthlyExpenses dto = toDto(entity);
        dto.getStatistics().addSaving(new Saving(null, name, percentage));
        entity.addSaving(new SavingEntity(name, percentage));
        return repository.save(entity);
    }

    public MonthlyExpenseEntity removeSavingById(Long savingId) {
        MonthlyExpenseEntity entity = repository.findAll().stream()
                .filter(e -> e.getSavings().stream().anyMatch(s -> s.getId().equals(savingId)))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Saving not found: " + savingId));
        requireOpen(entity);
        entity.getSavings().removeIf(s -> s.getId().equals(savingId));
        return repository.save(entity);
    }

    public MonthlyExpenseEntity editSaving(Long savingId, String name, double percentage) {
        MonthlyExpenseEntity entity = repository.findAll().stream()
                .filter(e -> e.getSavings().stream().anyMatch(s -> s.getId().equals(savingId)))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Saving not found: " + savingId));
        requireOpen(entity);
        SavingEntity saving = entity.getSavings().stream()
                .filter(s -> s.getId().equals(savingId))
                .findFirst().orElseThrow();
        saving.setName(name);
        saving.setPercentage(percentage);
        return repository.save(entity);
    }
}
