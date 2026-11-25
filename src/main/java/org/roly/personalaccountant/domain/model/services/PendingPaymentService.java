package org.roly.personalaccountant.domain.model.services;

import static org.roly.personalaccountant.domain.model.services.ExpenseManagerHelper.applyTemplates;
import static org.roly.personalaccountant.domain.model.services.ExpenseManagerHelper.getPendingFromEntity;
import static org.roly.personalaccountant.domain.model.services.ExpenseManagerHelper.requireOpen;
import static org.roly.personalaccountant.domain.model.services.ExpenseManagerHelper.validatePaymentDateInMonth;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import org.roly.personalaccountant.domain.model.entity.MonthlyExpenseEntity;
import org.roly.personalaccountant.domain.model.entity.PaymentEntity;
import org.roly.personalaccountant.domain.model.entity.PendingPaymentEntity;
import org.roly.personalaccountant.domain.model.entity.RecurringPaymentTemplate;
import org.roly.personalaccountant.domain.repository.MonthlyExpenseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PendingPaymentService {

    private static final String EXPENSE_NOT_FOUND = "Expense not found: ";

    private final MonthlyExpenseRepository repository;
    private final ExpenseManagerHelper helper;

    public PendingPaymentService(MonthlyExpenseRepository repository, ExpenseManagerHelper helper) {
        this.repository = repository;
        this.helper = helper;
    }

    @Transactional
    public MonthlyExpenseEntity pullTemplatesIntoMonth(YearMonth yearMonth, List<RecurringPaymentTemplate> templates) {
        MonthlyExpenseEntity entity = helper.getEntity(yearMonth)
                .orElseThrow(() -> new IllegalArgumentException(EXPENSE_NOT_FOUND + yearMonth));
        requireOpen(entity);
        applyTemplates(entity, templates);
        return repository.save(entity);
    }

    public MonthlyExpenseEntity payPendingPayment(Long pendingId, LocalDate date, double amount) {
        MonthlyExpenseEntity entity = helper.getEntityById(pendingId);
        PendingPaymentEntity pending = getPendingFromEntity(pendingId, entity);
        requireOpen(entity);
        validatePaymentDateInMonth(entity, date);
        entity.addPayment(new PaymentEntity(pending.getName(), pending.getCategory(), pending.getType(), amount, date));
        entity.getPendingPayments().removeIf(p -> p.getId().equals(pendingId));
        return repository.save(entity);
    }

    public MonthlyExpenseEntity removePendingPayment(Long pendingId) {
        MonthlyExpenseEntity entity = helper.getEntityById(pendingId);
        PendingPaymentEntity pending = getPendingFromEntity(pendingId, entity);
        requireOpen(entity);
        entity.removeFromBudget(pending);
        entity.getPendingPayments().removeIf(p -> p.getId().equals(pendingId));
        return repository.save(entity);
    }

    public double getTotalPendingPayments(YearMonth yearMonth) {
        return repository.findByYearAndMonth(yearMonth.getYear(), yearMonth.getMonthValue())
                .map(MonthlyExpenseEntity::getPendingPayments)
                .orElse(List.of()).stream()
                .mapToDouble(PendingPaymentEntity::getAmount)
                .sum();
    }

    public List<PendingPaymentEntity> getPendingPayments(YearMonth yearMonth) {
        return repository.findByYearAndMonth(yearMonth.getYear(), yearMonth.getMonthValue())
                .map(MonthlyExpenseEntity::getPendingPayments)
                .orElse(List.of());
    }


}
