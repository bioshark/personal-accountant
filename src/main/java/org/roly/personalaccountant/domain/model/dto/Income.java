package org.roly.personalaccountant.domain.model.dto;

import java.time.LocalDate;

public record Income(
        Long id,
        String source,
        LocalDate date,
        double value
) implements BaseTransaction {

    public Income {
        if (!Double.isFinite(value) || value < 0) {
            throw new IllegalArgumentException("Can't create an income with negative amount: " + value);
        }
    }
}
