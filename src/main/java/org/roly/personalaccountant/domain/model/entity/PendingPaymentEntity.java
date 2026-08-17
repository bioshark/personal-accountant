package org.roly.personalaccountant.domain.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import org.roly.personalaccountant.domain.model.dto.Payment.PaymentType;

@Entity
public class PendingPaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private double amount;

    private String category;

    @Enumerated(EnumType.STRING)
    private PaymentType type;

    @ManyToOne
    private MonthlyExpenseEntity monthlyExpense;

    protected PendingPaymentEntity() {
    }

    public PendingPaymentEntity(String name, double amount, String category, PaymentType type) {
        this.name = name;
        this.amount = amount;
        this.category = category;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public PaymentType getType() {
        return type;
    }

    public void setType(PaymentType type) {
        this.type = type;
    }

    public MonthlyExpenseEntity getMonthlyExpense() {
        return monthlyExpense;
    }

    public void setMonthlyExpense(MonthlyExpenseEntity monthlyExpense) {
        this.monthlyExpense = monthlyExpense;
    }
}
