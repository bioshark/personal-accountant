package org.roly.personalaccountant.dto;

import java.time.LocalDate;

public record Income(
        String source,
        LocalDate date,
        double value
) {

}
