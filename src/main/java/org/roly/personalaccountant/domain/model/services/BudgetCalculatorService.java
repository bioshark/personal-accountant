package org.roly.personalaccountant.domain.model.services;

import static org.roly.personalaccountant.domain.model.services.ExpenseManagerHelper.requireOpen;
import static org.roly.personalaccountant.domain.model.services.ExpenseManagerHelper.toDto;

import java.time.YearMonth;
import org.roly.personalaccountant.domain.model.dto.OverallSumsTracker;
import org.roly.personalaccountant.domain.model.dto.Payment.PaymentType;
import org.roly.personalaccountant.domain.model.entity.MonthlyExpenseEntity;
import org.roly.personalaccountant.domain.repository.MonthlyExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BudgetCalculatorService {

    private static final String EXPENSE_NOT_FOUND = "Expense not found: ";

    private final MonthlyExpenseRepository repository;
    private final ExpenseManagerHelper helper;

    @Autowired
    BudgetCalculatorService(MonthlyExpenseRepository repository, ExpenseManagerHelper helper) {
        this.repository = repository;
        this.helper = helper;
    }

    public double getFixedBudget(YearMonth yearMonth) {
        return repository.findByYearAndMonth(yearMonth.getYear(), yearMonth.getMonthValue())
                .map(MonthlyExpenseEntity::getFixedBudget)
                .orElse(0.0);
    }

    public double getSavingBudget(YearMonth yearMonth) {
        return repository.findByYearAndMonth(yearMonth.getYear(), yearMonth.getMonthValue())
                .map(MonthlyExpenseEntity::getSavingBudget)
                .orElse(0.0);
    }

    public double getLeisureBudget(YearMonth yearMonth) {
        return repository.findByYearAndMonth(yearMonth.getYear(), yearMonth.getMonthValue())
                .map(MonthlyExpenseEntity::getLeisureBudget)
                .orElse(0.0);
    }

    public void adjustBudget(YearMonth yearMonth, PaymentType paymentType, double newValue) {
        MonthlyExpenseEntity entity = helper.getEntity(yearMonth)
                .orElseThrow(() -> new IllegalArgumentException(EXPENSE_NOT_FOUND + yearMonth));
        requireOpen(entity);
        entity.adjustBudget(paymentType, newValue);
        repository.save(entity);
    }

    /**
     * Each budget counts as the larger of planned and spent, so overspending lowers the projection; the daily part is the allocation minus the Total
     * daily difference, so done days count their actual spend and the projection converges to the actual cash left as the month fills in.
     */
    public double getProjectedSpending(YearMonth yearMonth) {
        return repository.findByYearAndMonth(yearMonth.getYear(), yearMonth.getMonthValue())
                .map(entity -> {
                    OverallSumsTracker stats = toDto(entity).getStatistics();
                    return Math.max(entity.getFixedBudget(), stats.getFixedExpenseTotal())
                            + Math.max(entity.getLeisureBudget(), stats.getLeisureExpenseTotal())
                            + Math.max(entity.getSavingBudget(), stats.getSavingExpenseTotal())
                            + stats.getTotalDailyAllocation() - stats.getTotalDailyDiff();
                })
                .orElse(0.0);
    }

}
