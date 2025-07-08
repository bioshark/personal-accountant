package org.roly.personalaccountant.domain.model;

import java.time.LocalDate;

public record Income(
        String source,
        LocalDate date,
        double value
) {

}
