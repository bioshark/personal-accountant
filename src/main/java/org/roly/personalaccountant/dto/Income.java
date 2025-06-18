package org.roly.personalaccountant.dto;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

public record Income(
        String source,
        LocalDate date,
        double value
) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
}
