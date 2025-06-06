package org.roly.personalaccountant.utils;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.roly.personalaccountant.dto.Payment;

public class PaymentsGenerator {

    public static LinkedHashMap<LocalDate, List<Payment>> initializeEmptyMonth(LocalDate currentDate) {
        LinkedList<LocalDate> days = generateDaysForMonth(currentDate);
        System.out.println("days = " + days);
        return days.stream()
                .collect(Collectors.toMap(
                        day -> day,
                        day -> new ArrayList<>(),
                        (v1, v2) -> v1,
                        LinkedHashMap::new
                ));
    }


    private static LinkedList<LocalDate> generateDaysForMonth(LocalDate currentDate) {
        LocalDate lastDayOfNextMonth = currentDate.plusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
        System.out.println("lastDayOfNextMonth = " + lastDayOfNextMonth);
        LocalDate adjustedEndDate = getAdjustedEndDate(lastDayOfNextMonth);

        LinkedList<LocalDate> generatedDays = new LinkedList<>();
        Stream.iterate(currentDate.plusDays(1), date -> !date.isAfter(adjustedEndDate), date -> date.plusDays(1)).forEach(generatedDays::add);

        return generatedDays;
    }

    private static LocalDate getAdjustedEndDate(LocalDate lastDayOfNextMonth) {
        LocalDate adjusterLastDay = lastDayOfNextMonth.minusDays(3);
        while (isWeekEnd(adjusterLastDay)) {
            adjusterLastDay = adjusterLastDay.minusDays(1);
        }
        System.out.println("adjusterLastDay = " + adjusterLastDay);
        return adjusterLastDay;
    }

    private static boolean isWeekEnd(LocalDate currentDate) {
        return switch (currentDate.getDayOfWeek()) {
            case SATURDAY, SUNDAY -> true;
            default -> false;
        };
    }


}
