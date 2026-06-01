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

    private static LinkedList<LocalDate> generateDaysForMonth(LocalDate currentDate, LocalDate endDate) {
        if (endDate == null || endDate.isAfter(currentDate)) {
            endDate = currentDate.plusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
        }
        LocalDate adjustedEndDate = getAdjustedEndDate(endDate);

        LinkedList<LocalDate> generatedDays = new LinkedList<>();
        Stream.iterate(currentDate.plusDays(1), date -> !date.isAfter(adjustedEndDate), date -> date.plusDays(1)).forEach(generatedDays::add);

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
