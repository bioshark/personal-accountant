package org.roly.personalaccountant.utils;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.stream.Stream;
import org.roly.personalaccountant.domain.model.expenses.BaseTransaction;
import org.roly.personalaccountant.domain.notifiers.Listener;
import org.roly.personalaccountant.domain.notifiers.ReactiveList;

public class PaymentsGenerator {

    private PaymentsGenerator() {}

    @SuppressWarnings("java:S1319")
    public static LinkedHashMap<LocalDate, ReactiveList<BaseTransaction>> initializeEmptyMonth(LocalDate currentDate,
            Listener<BaseTransaction> listener) {
        LinkedList<LocalDate> days = generateDaysForMonth(currentDate);
        LinkedHashMap<LocalDate, ReactiveList<BaseTransaction>> result = new LinkedHashMap<>();
        for (LocalDate day : days) {
            ReactiveList<BaseTransaction> payments = new ReactiveList<>();
            payments.registerListener(listener);
            result.put(day, payments);
        }
        return result;
    }

    private static LinkedList<LocalDate> generateDaysForMonth(LocalDate currentDate) {
        LocalDate lastDayOfNextMonth = currentDate.plusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
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
        return adjusterLastDay;
    }

    private static boolean isWeekEnd(LocalDate currentDate) {
        return switch (currentDate.getDayOfWeek()) {
            case SATURDAY, SUNDAY -> true;
            default -> false;
        };
    }


}
