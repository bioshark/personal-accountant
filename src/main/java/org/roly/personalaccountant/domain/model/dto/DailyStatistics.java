package org.roly.personalaccountant.domain.model.dto;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Objects;

public class DailyStatistics {

    private static final int DEFAULT_MAX_ALLOCATION = 10;
    private static final int SATURDAY_MAX_ALLOCATION = 100;
    private final LocalDate date;
    private final double dailyMaxAllocation;

    private double dailyTotalExpenditure = 0.0d;
    private boolean isDayDone = false;

    public DailyStatistics(LocalDate date) {
        this.date = date;
        this.dailyMaxAllocation = isSaturday() ? SATURDAY_MAX_ALLOCATION : DEFAULT_MAX_ALLOCATION;
    }

    public LocalDate getDate() {
        return date;
    }

    public double getDailyMaxAllocation() {
        return dailyMaxAllocation;
    }

    public double getDailyTotalExpenditure() {
        return dailyTotalExpenditure;
    }

    public void addDailyExpenditure(double dailyTotalExpenditure) {
        this.dailyTotalExpenditure += dailyTotalExpenditure;
    }

    public boolean isSaturday() {
        return (this.date.getDayOfWeek() == DayOfWeek.SATURDAY);
    }

    public double getDailyDifference() {
        if (isDayDone()) {
            return getDailyMaxAllocation() - getDailyTotalExpenditure();
        }
        return 0.0d;
    }

    public boolean isDayDone() {
        return isDayDone;
    }

    public void setDayDone(boolean dayDone) {
        isDayDone = dayDone;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DailyStatistics that = (DailyStatistics) o;
        return Double.compare(dailyMaxAllocation, that.dailyMaxAllocation) == 0
                && Double.compare(dailyTotalExpenditure, that.dailyTotalExpenditure) == 0 && Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(date);
        result = 31 * result + Double.hashCode(dailyMaxAllocation);
        result = 31 * result + Double.hashCode(dailyTotalExpenditure);
        return result;
    }
}
