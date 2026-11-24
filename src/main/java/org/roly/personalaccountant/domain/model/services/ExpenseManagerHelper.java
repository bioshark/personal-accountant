package org.roly.personalaccountant.domain.model.services;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Objects;
import org.roly.personalaccountant.domain.model.dto.Payment;
import org.roly.personalaccountant.domain.model.entity.MonthlyExpenseEntity;
import org.roly.personalaccountant.domain.model.entity.PaymentEntity;
import org.roly.personalaccountant.domain.repository.MonthlyExpenseRepository;
import org.roly.personalaccountant.utils.PaymentsGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ExpenseManagerHelper {

    private final MonthlyExpenseRepository repository;

    @Autowired
    ExpenseManagerHelper(MonthlyExpenseRepository repository) {
        this.repository = repository;
    }

    MonthlyExpenseEntity findExpenseForDate(LocalDate date) {
        return repository.findByDateInRange(date)
                .orElseThrow(() -> new IllegalArgumentException("Can't find Expense for date " + date));
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

    private static LocalDate effectiveEndDate(MonthlyExpenseEntity entity) {
        return entity.getEndDate() != null
                ? entity.getEndDate()
                : PaymentsGenerator.resolveEndDate(entity.getStartDate(), null);
    }


}
