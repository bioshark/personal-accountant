package org.roly.personalaccountant.domain.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.roly.personalaccountant.domain.model.dto.Payment.PaymentType;

@Entity
public class RecurringPaymentTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private double defaultAmount;

    private String category;

    @Enumerated(EnumType.STRING)
    private PaymentType type;

    protected RecurringPaymentTemplate() {
    }

    public RecurringPaymentTemplate(String name, double defaultAmount, String category, PaymentType type) {
        this.name = name;
        this.defaultAmount = defaultAmount;
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

    public double getDefaultAmount() {
        return defaultAmount;
    }

    public void setDefaultAmount(double defaultAmount) {
        this.defaultAmount = defaultAmount;
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
}
