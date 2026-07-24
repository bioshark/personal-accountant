package org.roly.personalaccountant.domain.model.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.hibernate.annotations.ColumnDefault;
import org.roly.personalaccountant.domain.model.dto.Payment.PaymentType;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"expense_year", "expense_month"}))
public class MonthlyExpenseEntity {

    private static final String NO_BUDGET_FOR_TYPE = "No budget for type";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "expense_year")
    private int year;

    @Column(name = "expense_month")
    private int month;
    private LocalDate startDate;
    private LocalDate endDate;
    private String expenseName;
    @ColumnDefault("0")
    private double fixedBudget;
    @ColumnDefault("0")
    private double leisureBudget;
    @ColumnDefault("0")
    private double savingBudget;

    @OneToMany(mappedBy = "monthlyExpense", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private final List<PaymentEntity> payments = new ArrayList<>();

    @OneToMany(mappedBy = "monthlyExpense", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private final List<IncomeEntity> incomes = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    private final Set<LocalDate> doneDays = new HashSet<>();

    @OneToMany(mappedBy = "monthlyExpense", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private final List<SavingEntity> savings = new ArrayList<>();

    @OneToMany(mappedBy = "monthlyExpense", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private final List<PendingPaymentEntity> pendingPayments = new ArrayList<>();

    protected MonthlyExpenseEntity() {
    }

    public MonthlyExpenseEntity(int year, int month, LocalDate startDate, LocalDate endDate, String expenseName) {
        this.year = year;
        this.month = month;
        this.startDate = startDate;
        this.endDate = endDate;
        this.expenseName = expenseName;
    }

    public Long getId() {
        return id;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getExpenseName() {
        return expenseName;
    }

    public double getFixedBudget() {
        return fixedBudget;
    }

    public double getSavingBudget() {
        return savingBudget;
    }

    public double getLeisureBudget() {
        return leisureBudget;
    }

    public void adjustBudget(PendingPaymentEntity pending) {
        switch (pending.getType()) {
            case FIXED -> fixedBudget += pending.getAmount();
            case LEISURE -> leisureBudget += pending.getAmount();
            case SAVING -> savingBudget += pending.getAmount();
            default -> throw new IllegalArgumentException(NO_BUDGET_FOR_TYPE);
        }
    }

    public void adjustBudget(PaymentType paymentType, double newValue) {
        switch (paymentType) {
            case FIXED -> fixedBudget = newValue;
            case LEISURE -> leisureBudget = newValue;
            case SAVING -> savingBudget = newValue;
            default -> throw new IllegalArgumentException(NO_BUDGET_FOR_TYPE);
        }
    }

    public void removeFromBudget(PendingPaymentEntity pending) {
        switch (pending.getType()) {
            case FIXED -> fixedBudget -= pending.getAmount();
            case LEISURE -> leisureBudget -= pending.getAmount();
            case SAVING -> savingBudget -= pending.getAmount();
            default -> throw new IllegalArgumentException(NO_BUDGET_FOR_TYPE);
        }
    }

    public List<PaymentEntity> getPayments() {
        return payments;
    }

    public List<IncomeEntity> getIncomes() {
        return incomes;
    }

    public void addPayment(PaymentEntity payment) {
        payments.add(payment);
        payment.setMonthlyExpense(this);
    }

    public void addIncome(IncomeEntity income) {
        incomes.add(income);
        income.setMonthlyExpense(this);
    }

    public void removeIncome(IncomeEntity income) {
        incomes.remove(income);
    }

    public void removePayment(PaymentEntity payment) {
        payments.remove(payment);
    }

    public Set<LocalDate> getDoneDays() {
        return doneDays;
    }

    public List<SavingEntity> getSavings() {
        return savings;
    }

    public void addSaving(SavingEntity saving) {
        savings.add(saving);
        saving.setMonthlyExpense(this);
    }

    public List<PendingPaymentEntity> getPendingPayments() {
        return pendingPayments;
    }

    public void addPendingPayment(PendingPaymentEntity pendingPayment) {
        pendingPayments.add(pendingPayment);
        pendingPayment.setMonthlyExpense(this);
    }

    public void toggleDayDone(LocalDate date) {
        if (!doneDays.remove(date)) {
            doneDays.add(date);
        }
    }
}
