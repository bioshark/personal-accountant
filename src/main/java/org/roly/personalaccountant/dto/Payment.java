package org.roly.personalaccountant.dto;

import java.time.LocalDate;

public record Payment(
        String description,
        Category category,
        PaymentType type,
        Float amount,
        LocalDate date
) {

    public enum PaymentType {
        MANDATORY, LEISURE
    }

    public enum Category {

    }
}
