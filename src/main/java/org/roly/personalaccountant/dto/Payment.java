package org.roly.personalaccountant.dto;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

public record Payment(
        String description,
        Category category,
        PaymentType type,
        Double amount,
        LocalDate date
) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public enum PaymentType {
        DAILY,
        FIXED,
        LEISURE
    }

    public enum Category {
        FOOD,
    }
}
