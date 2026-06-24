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
        LEISURE
    }

    public enum Category {
        FOOD,
        MEDIA,
        INVOICE,
        HEALTH,
        SAVING
    }

    public boolean isFixed() {
        return type == PaymentType.FIXED;
    }

    public boolean isLeisure() {
        return type == PaymentType.LEISURE;
    }
}
