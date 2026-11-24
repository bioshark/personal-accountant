package org.roly.personalaccountant.domain.model.services;

import static org.roly.personalaccountant.domain.model.services.ExpenseManagerHelper.matches;
import static org.roly.personalaccountant.domain.model.services.ExpenseManagerHelper.requireOpen;
import static org.roly.personalaccountant.domain.model.services.ExpenseManagerHelper.validatePaymentDateInMonth;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import org.roly.personalaccountant.domain.model.dto.Payment;
import org.roly.personalaccountant.domain.model.dto.Payment.PaymentType;
import org.roly.personalaccountant.domain.model.entity.MonthlyExpenseEntity;
import org.roly.personalaccountant.domain.model.entity.PaymentEntity;
import org.roly.personalaccountant.domain.model.entity.PendingPaymentEntity;
import org.roly.personalaccountant.domain.repository.MonthlyExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    private static final String PAYMENT_NOT_FOUND = "Payment not found: ";

    private final MonthlyExpenseRepository repository;
    private final ExpenseManagerHelper helper;

    @Autowired
    public PaymentService(MonthlyExpenseRepository repository, ExpenseManagerHelper helper) {
        this.repository = repository;
        this.helper = helper;
    }

    public MonthlyExpenseEntity addPayment(Payment payment) {
        LocalDate paymentDate = payment.date();
        MonthlyExpenseEntity entity = helper.findExpenseForDate(paymentDate);
        requireOpen(entity);
        entity.addPayment(new PaymentEntity(payment.description(), payment.category(), payment.type(), payment.amount(), payment.date()));
        return repository.save(entity);
    }

    public MonthlyExpenseEntity removePayment(Payment payment) {
        MonthlyExpenseEntity entity = helper.findExpenseForDate(payment.date());
        requireOpen(entity);
        PaymentEntity stored = entity.getPayments().stream()
                .filter(candidate -> matches(candidate, payment))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(PAYMENT_NOT_FOUND + payment.description() + " on " + payment.date()));
        entity.removePayment(stored);
        return repository.save(entity);
    }

    public MonthlyExpenseEntity removePaymentById(Long paymentId) {
        MonthlyExpenseEntity entity = repository.findAll().stream()
                .filter(e -> e.getPayments().stream().anyMatch(i -> i.getId().equals(paymentId)))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(PAYMENT_NOT_FOUND + paymentId));
        requireOpen(entity);
        entity.getPayments().removeIf(i -> i.getId().equals(paymentId));
        return repository.save(entity);
    }

    public MonthlyExpenseEntity editPayment(Long paymentId, String description, LocalDate date, double amount, PaymentType type, String category) {
        MonthlyExpenseEntity entity = repository.findAll().stream()
                .filter(e -> e.getPayments().stream().anyMatch(i -> i.getId().equals(paymentId)))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(PAYMENT_NOT_FOUND + paymentId));
        requireOpen(entity);
        validatePaymentDateInMonth(entity, date);
        PaymentEntity payment = entity.getPayments().stream()
                .filter(p -> p.getId().equals(paymentId))
                .findFirst().orElseThrow();
        payment.setDescription(description);
        payment.setDate(date);
        payment.setAmount(amount);
        payment.setType(type);
        payment.setCategory(category);
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
