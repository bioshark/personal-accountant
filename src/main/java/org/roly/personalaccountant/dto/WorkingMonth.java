package org.roly.personalaccountant.dto;

import java.time.LocalDate;
import java.time.YearMonth;

public record WorkingMonth(
        LocalDate startDate,
        YearMonth yearMonth
) {

}
