package org.roly.personalaccountant.utils;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.roly.personalaccountant.domain.model.dto.BaseTransaction;
import org.roly.personalaccountant.domain.notifiers.ReactiveList;

public class PaymentsGenerator {

    public static LinkedHashMap<LocalDate, ReactiveList<BaseTransaction>> initializeEmptyMonth(LocalDate currentDate, LocalDate endDate) {
        LinkedList<LocalDate> days = generateDaysForMonth(currentDate, endDate);
        return days.stream()
                .collect(Collectors.toMap(
                        day -> day,
                        day -> new ReactiveList<>(),
                        (v1, v2) -> v1,
                        LinkedHashMap::new
                ));
    }

    /**
     * Resolves the effective (inclusive) end date for a month given its start date and an optional requested end date. When {@code endDate} is
     * {@code null} the end date is auto-computed using the same rules applied when generating a month's days.
     */
    public static LocalDate resolveEndDate(LocalDate currentDate, LocalDate endDate) {
        return generateDaysForMonth(currentDate, endDate).getLast();
    }

    private static LinkedList<LocalDate> generateDaysForMonth(LocalDate currentDate, LocalDate endDate) {
        LocalDate finalEndDate;
        if (endDate == null) {
            finalEndDate = getAdjustedEndDate(currentDate.plusMonths(1).with(TemporalAdjusters.lastDayOfMonth()));
        } else {
            finalEndDate = endDate;
        }

        LinkedList<LocalDate> generatedDays = new LinkedList<>();
        Stream.iterate(currentDate, date -> !date.isAfter(finalEndDate), date -> date.plusDays(1)).forEach(generatedDays::add);

        return generatedDays;
    }

    private static LocalDate getAdjustedEndDate(LocalDate lastDayOfNextMonth) {
        LocalDate adjusterLastDay = lastDayOfNextMonth.minusDays(3);
        while (isWeekEnd(adjusterLastDay)) {
            adjusterLastDay = adjusterLastDay.minusDays(1);
        }
        return adjusterLastDay;
    }

    private static boolean isWeekEnd(LocalDate currentDate) {
        return switch (currentDate.getDayOfWeek()) {
            case SATURDAY, SUNDAY -> true;
            default -> false;
        };
    }


}
