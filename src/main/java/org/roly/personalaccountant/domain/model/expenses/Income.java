package org.roly.personalaccountant.domain.model.expenses;

import java.time.LocalDate;

public record Income(
        String source,
        LocalDate date,
        double value
) {

}
