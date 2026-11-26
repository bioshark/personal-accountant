package org.roly.personalaccountant.domain.model.services;

import static org.roly.personalaccountant.domain.model.services.ExpenseManagerHelper.applyTemplates;
import static org.roly.personalaccountant.domain.model.services.ExpenseManagerHelper.requireOpen;
import static org.roly.personalaccountant.domain.model.services.ExpenseManagerHelper.toDto;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.roly.personalaccountant.domain.model.dto.MonthlyExpenses;
import org.roly.personalaccountant.domain.model.entity.IncomeEntity;
import org.roly.personalaccountant.domain.model.entity.MonthlyExpenseEntity;
import org.roly.personalaccountant.domain.model.entity.PaymentEntity;
import org.roly.personalaccountant.domain.model.entity.RecurringPaymentTemplate;
import org.roly.personalaccountant.domain.repository.MonthlyExpenseRepository;
import org.roly.personalaccountant.utils.PaymentsGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MonthlyExpenseService {

    private static final String EXPENSE_NOT_FOUND = "Expense not found: ";

    private final MonthlyExpenseRepository repository;
    private final ExpenseManagerHelper helper;

    @Autowired
    MonthlyExpenseService(MonthlyExpenseRepository repository, ExpenseManagerHelper helper) {
        this.repository = repository;
        this.helper = helper;
    }

    @Transactional
    public void addNewMonthlyExpense(String expenseName, LocalDate startDate, LocalDate endDate, List<RecurringPaymentTemplate> templates) {
        MonthlyExpenses dto = new MonthlyExpenses(expenseName, startDate, endDate);
        if (repository.findByYearAndMonth(dto.getYearMonth().getYear(), dto.getYearMonth().getMonthValue()).isPresent()) {
            throw new IllegalArgumentException("An expense for " + dto.getYearMonth() + " already exists");
        }
        MonthlyExpenseEntity entity = new MonthlyExpenseEntity(
                dto.getYearMonth().getYear(),
                dto.getYearMonth().getMonthValue(),
                dto.getStartDate(),
                dto.getEndDate(),
                dto.getExpenseName()
        );
        if (thereIsAnOverlap(entity)) {
            throw new IllegalArgumentException("An expense already contains the new boundaries");
        }
        applyTemplates(entity, templates);
        repository.save(entity);
    }

    @Transactional
    public void editMonth(YearMonth yearMonth, String newName, LocalDate newStartDate, LocalDate newEndDate) {
        MonthlyExpenseEntity entity = helper.getEntity(yearMonth)
                .orElseThrow(() -> new IllegalArgumentException(EXPENSE_NOT_FOUND + yearMonth));
        requireOpen(entity);
        if (newName == null || newName.isBlank()) {
            throw new IllegalArgumentException("Month name must not be empty");
        }
        if (newEndDate != null && newStartDate.isAfter(newEndDate)) {
            throw new IllegalArgumentException("Start date can't be after end date");
        }
        LocalDate effectiveEnd = PaymentsGenerator.resolveEndDate(newStartDate, newEndDate);
        checkForLoosePayments(newStartDate, entity, effectiveEnd);
        checkForLooseIncomes(newStartDate, entity, effectiveEnd);

        entity.setExpenseName(newName.trim());
        entity.setStartDate(newStartDate);
        entity.setEndDate(effectiveEnd);
        if (thereIsAnOverlap(entity)) {
            throw new IllegalArgumentException("An expense already contains the boundaries");
        }
        repository.save(entity);
    }

    public boolean deleteMonthlyExpense(YearMonth yearMonth) {
        Optional<MonthlyExpenseEntity> monthlyExpenseEntity = repository.findByYearAndMonth(yearMonth.getYear(), yearMonth.getMonthValue());

        if (monthlyExpenseEntity.isEmpty()) {
            return false;
        }
        repository.delete(monthlyExpenseEntity.get());
        return true;
    }

    public MonthlyExpenses getExpense(YearMonth yearMonth) {
        return repository.findByYearAndMonth(yearMonth.getYear(), yearMonth.getMonthValue())
                .map(ExpenseManagerHelper::toDto)
                .orElse(null);
    }

    public MonthlyExpenseEntity toggleDayDone(LocalDate date) {
        MonthlyExpenseEntity entity = helper.findExpenseForDate(date)
                .orElseThrow(() -> new IllegalArgumentException("Can't find Expense for date " + date));
        requireOpen(entity);
        entity.toggleDayDone(date);
        return repository.save(entity);
    }

    public MonthlyExpenseEntity toggleMonthDone(YearMonth yearMonth) {
        MonthlyExpenseEntity entity = helper.getEntity(yearMonth)
                .orElseThrow(() -> new IllegalArgumentException(EXPENSE_NOT_FOUND + yearMonth));
        entity.toggleMonthDone();
        return repository.save(entity);
    }

    public Map<YearMonth, MonthlyExpenses> getAllExpenses() {
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

    private boolean thereIsAnOverlap(MonthlyExpenseEntity entity) {
        return repository.existsOverlappingMonth(entity.getStartDate(), entity.getEndDate(), entity.getId());
    }

    private static void checkForLooseIncomes(LocalDate newStartDate, MonthlyExpenseEntity entity, LocalDate effectiveEnd) {
        for (IncomeEntity income : entity.getIncomes()) {
            if (income.getDate().isBefore(newStartDate) || income.getDate().isAfter(effectiveEnd)) {
                throw new IllegalArgumentException("Cannot change dates: income exists on " + income.getDate());
            }
        }
    }

    private static void checkForLoosePayments(LocalDate newStartDate, MonthlyExpenseEntity entity, LocalDate effectiveEnd) {
        for (PaymentEntity payment : entity.getPayments()) {
            if (payment.getDate().isBefore(newStartDate) || payment.getDate().isAfter(effectiveEnd)) {
                throw new IllegalArgumentException("Cannot change dates: payment exists on " + payment.getDate());
            }
        }
    }

}
