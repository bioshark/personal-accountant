package org.roly.personalaccountant.domain.model.expenses;

import java.time.LocalDate;

public record Payment(
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
        MEDIA
    }

    public boolean isFixed() {
        return type == PaymentType.FIXED;
    }
}
