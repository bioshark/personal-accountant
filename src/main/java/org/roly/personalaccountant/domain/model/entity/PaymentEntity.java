package org.roly.personalaccountant.domain.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import org.roly.personalaccountant.domain.model.dto.Payment.Category;
import org.roly.personalaccountant.domain.model.dto.Payment.PaymentType;

@Entity
public class PaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    @Enumerated(EnumType.STRING)
    private Category category;

    @Enumerated(EnumType.STRING)
    private PaymentType type;

    private Double amount;
    private LocalDate date;

    @ManyToOne
    private MonthlyExpenseEntity monthlyExpense;

    protected PaymentEntity() {
    }

    public PaymentEntity(String description, Category category, PaymentType type, Double amount, LocalDate date) {
        this.description = description;
        this.category = category;
        this.type = type;
        this.amount = amount;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public Category getCategory() {
        return category;
    }

    public PaymentType getType() {
        return type;
    }

    public Double getAmount() {
        return amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public MonthlyExpenseEntity getMonthlyExpense() {
        return monthlyExpense;
    }

    public void setMonthlyExpense(MonthlyExpenseEntity monthlyExpense) {
        this.monthlyExpense = monthlyExpense;
    }
}
