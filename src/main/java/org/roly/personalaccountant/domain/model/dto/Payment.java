package org.roly.personalaccountant.domain.model.dto;

import java.time.LocalDate;

public record Payment(
        Long id,
        String description,
        Category category,
        PaymentType type,
        Double amount,
        LocalDate date
) implements BaseTransaction {

    public enum PaymentType {
        DAILY,
        FIXED,
        LEISURE,
        SAVING
    }

    public enum Category {
        FOOD,
        MEDIA,
        INVOICE,
        HEALTH,
        FUEL,
        TRAVEL,
        VACATION,
        TELCO,
        FUN,
        GIFT,
        OTHER
    }

    public boolean isFixed() {
        return type == PaymentType.FIXED;
    }

    public boolean isLeisure() {
        return type == PaymentType.LEISURE;
    }

    public boolean isSaving() {
        return type == PaymentType.SAVING;
    }

}
