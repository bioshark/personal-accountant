package org.roly.personalaccountant.domain.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import org.roly.personalaccountant.domain.model.dto.Payment.PaymentType;

@Entity
public class PaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    private String category;

    @Enumerated(EnumType.STRING)
    private PaymentType type;

    private Double amount;
    private LocalDate date;

    @ManyToOne
    private MonthlyExpenseEntity monthlyExpense;

    protected PaymentEntity() {
    }

    public PaymentEntity(String description, String category, PaymentType type, Double amount, LocalDate date) {
        this.description = description;
        this.category = category;
        this.type = type;
        this.amount = amount;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    private void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public MonthlyExpenseEntity getMonthlyExpense() {
        return monthlyExpense;
    }

    public void setMonthlyExpense(MonthlyExpenseEntity monthlyExpense) {
        this.monthlyExpense = monthlyExpense;
    }
}
