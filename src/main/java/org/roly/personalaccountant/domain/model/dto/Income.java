package org.roly.personalaccountant.domain.model.dto;

import java.time.LocalDate;

public record Income(
        String source,
        LocalDate date,
        double value
) implements BaseTransaction {

}
