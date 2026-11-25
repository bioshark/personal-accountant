package org.roly.personalaccountant.domain.model.services;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Objects;
import org.roly.personalaccountant.domain.model.dto.Income;
import org.roly.personalaccountant.domain.model.dto.MonthlyExpenses;
import org.roly.personalaccountant.domain.model.dto.Payment;
import org.roly.personalaccountant.domain.model.dto.Payment.PaymentType;
import org.roly.personalaccountant.domain.model.dto.Saving;
import org.roly.personalaccountant.domain.model.entity.IncomeEntity;
import org.roly.personalaccountant.domain.model.entity.MonthlyExpenseEntity;
import org.roly.personalaccountant.domain.model.entity.PaymentEntity;
import org.roly.personalaccountant.domain.model.entity.PendingPaymentEntity;
import org.roly.personalaccountant.domain.model.entity.RecurringPaymentTemplate;
import org.roly.personalaccountant.domain.model.entity.SavingEntity;
import org.roly.personalaccountant.domain.repository.MonthlyExpenseRepository;
import org.roly.personalaccountant.utils.PaymentsGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
class ExpenseManagerHelper {

    private static final String EXPENSE_NOT_FOUND = "Expense not found: ";
    private static final String PENDING_PAYMENT_NOT_FOUND = "Pending payment not found: ";

    private final MonthlyExpenseRepository repository;

    @Autowired
    ExpenseManagerHelper(MonthlyExpenseRepository repository) {
        this.repository = repository;
    }

    MonthlyExpenseEntity findExpenseForDate(LocalDate date) {
        return repository.findByDateInRange(date)
                .orElseThrow(() -> new IllegalArgumentException("Can't find Expense for date " + date));
    }

    @NonNull
    MonthlyExpenseEntity getEntity(YearMonth yearMonth) {
        return repository.findByYearAndMonth(yearMonth.getYear(), yearMonth.getMonthValue())
                .orElseThrow(() -> new IllegalArgumentException(EXPENSE_NOT_FOUND + yearMonth));
    }

    static void requireOpen(MonthlyExpenseEntity entity) {
        if (entity.isMonthDone()) {
            throw new MonthClosedException(YearMonth.of(entity.getYear(), entity.getMonth()));
        }
    }

    /**
     * {@code PaymentEntity} has no value-based equals, so a payment identified by its fields must be matched to the stored instance before removal.
     */
    static boolean matches(PaymentEntity stored, Payment payment) {
        return stored.getDate().equals(payment.date())
                && stored.getType() == payment.type()
                && Objects.equals(stored.getDescription(), payment.description())
                && Objects.equals(stored.getCategory(), payment.category())
                && Double.compare(stored.getAmount(), payment.amount()) == 0;
    }

    static void validatePaymentDateInMonth(MonthlyExpenseEntity entity, LocalDate date) {
        LocalDate start = entity.getStartDate();
        LocalDate end = effectiveEndDate(entity);
        if (date.isBefore(start) || date.isAfter(end)) {
            throw new IllegalArgumentException(
                    "Date " + date + " is outside the month range (" + start + " to " + end + ")");
        }
    }

    /**
     * Rejects DAILY templates before touching the month, so a bad selection never leaves some templates applied and others not.
     */
    static void applyTemplates(MonthlyExpenseEntity entity, List<RecurringPaymentTemplate> templates) {
        templates.stream()
                .filter(template -> template.getType() == PaymentType.DAILY)
                .findFirst()
                .ifPresent(template -> {
                    throw new IllegalArgumentException("Daily recurring payment cannot be pulled into a month: " + template.getName());
                });
        for (RecurringPaymentTemplate template : templates) {
            PendingPaymentEntity pending = new PendingPaymentEntity(
                    template.getName(), template.getDefaultAmount(), template.getCategory(), template.getType());
            entity.addPendingPayment(pending);
            entity.adjustBudget(pending);
        }
    }

    static MonthlyExpenses toDto(MonthlyExpenseEntity entity) {
        MonthlyExpenses dto = getMonthlyExpenses(entity);
        dto.setDone(entity.isMonthDone());
        for (SavingEntity saving : entity.getSavings()) {
            dto.getStatistics().addSaving(new Saving(saving.getId(), saving.getName(), saving.getPercentage()));
        }
        for (LocalDate doneDay : entity.getDoneDays()) {
            var stats = dto.getStatistics().getDailyPayments().get(doneDay);
            if (stats != null) {
                stats.setDayDone(true);
            }
        }
        return dto;
    }

    @NonNull
    MonthlyExpenseEntity getEntityById(Long pendingId) {
        return repository.findAll().stream()
                .filter(e -> e.getPendingPayments().stream().anyMatch(p -> p.getId().equals(pendingId)))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(PENDING_PAYMENT_NOT_FOUND + pendingId));
    }

    @NonNull
    static PendingPaymentEntity getPendingFromEntity(Long pendingId, MonthlyExpenseEntity entity) {
        return entity.getPendingPayments().stream()
                .filter(p -> p.getId().equals(pendingId))
                .findFirst().orElseThrow();
    }

    @NonNull
    private static MonthlyExpenses getMonthlyExpenses(MonthlyExpenseEntity entity) {
        MonthlyExpenses dto = new MonthlyExpenses(
                entity.getExpenseName(),
                entity.getStartDate(),
                entity.getEndDate());
        for (IncomeEntity income : entity.getIncomes()) {
            dto.addIncome(new Income(income.getId(), income.getSource(), income.getDate(), income.getValue()));
        }
        for (PaymentEntity payment : entity.getPayments()) {
            dto.addPayment(new Payment(payment.getId(), payment.getDescription(), payment.getCategory(), payment.getType(), payment.getAmount(),
                    payment.getDate()));
        }
        return dto;
    }

    private static LocalDate effectiveEndDate(MonthlyExpenseEntity entity) {
        return entity.getEndDate() != null
                ? entity.getEndDate()
                : PaymentsGenerator.resolveEndDate(entity.getStartDate(), null);
    }

}
