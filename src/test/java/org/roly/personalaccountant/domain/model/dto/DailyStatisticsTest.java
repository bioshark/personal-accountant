package org.roly.personalaccountant.domain.model.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DailyStatisticsTest {

    private static final LocalDate TEST_DATE = LocalDate.of(2026, 1, 1);

    private DailyStatistics dailyStatistics;

    @BeforeEach
    void setUp() {
        dailyStatistics = new DailyStatistics(TEST_DATE);
    }

    @Test
    void shouldReturnZeroWhenDayNotDone() {
        double dailyDifference = dailyStatistics.getDailyDifference();

        assertThat(dailyDifference).isZero();
    }

    @Test
    void shouldReturnReturnAllocationWhenDayDone() {
        dailyStatistics.setDayDone(true);
        double dailyDifference = dailyStatistics.getDailyDifference();

        assertThat(dailyDifference).isEqualTo(10);
    }

    @Test
    void shouldCheckIfItsWeekEnd() {
        assertThat(dailyStatistics.isWeekEnd()).isFalse();
        dailyStatistics = new DailyStatistics(TEST_DATE.plusDays(2));
        assertThat(dailyStatistics.isWeekEnd()).isTrue();
    }

    @Test
    void shouldCheckIfItsWeekEndOnSaturday() {
        dailyStatistics = new DailyStatistics(TEST_DATE.plusDays(2));
        assertThat(dailyStatistics.isWeekEnd()).isTrue();
    }

    @Test
    void shouldReturnAllocationMinusExpenditureWhenDayDone() {
        dailyStatistics.setDayDone(true);
        dailyStatistics.addDailyExpenditure(4);

        assertThat(dailyStatistics.getDailyDifference()).isEqualTo(6);
    }

    @Test
    void shouldReturnDefaultAllocationOnWeekday() {
        assertThat(dailyStatistics.getDailyMaxAllocation()).isEqualTo(10);
    }

    @Test
    void shouldReturnHigherAllocationOnSaturday() {
        dailyStatistics = new DailyStatistics(TEST_DATE.plusDays(2));

        assertThat(dailyStatistics.getDailyMaxAllocation()).isEqualTo(100);
    }

    @Test
    void shouldAccumulateDailyExpenditure() {
        dailyStatistics.addDailyExpenditure(3);
        dailyStatistics.addDailyExpenditure(2);

        assertThat(dailyStatistics.getDailyTotalExpenditure()).isEqualTo(5);
    }

    @Test
    void shouldBeSaturdayOnlyOnSaturday() {
        assertThat(dailyStatistics.isSaturday()).isFalse();
        assertThat(new DailyStatistics(TEST_DATE.plusDays(2)).isSaturday()).isTrue();
        assertThat(new DailyStatistics(TEST_DATE.plusDays(3)).isSaturday()).isFalse();
    }

    @Test
    void shouldCheckIfItsWeekEndOnSunday() {
        dailyStatistics = new DailyStatistics(TEST_DATE.plusDays(3));

        assertThat(dailyStatistics.isWeekEnd()).isTrue();
    }

    @Test
    void shouldReturnAllocationForGivenDate() {
        assertThat(DailyStatistics.allocationFor(TEST_DATE)).isEqualTo(10);
        assertThat(DailyStatistics.allocationFor(TEST_DATE.plusDays(2))).isEqualTo(100);
    }

    @Test
    void shouldSumTotalAllocationAcrossRange() {
        assertThat(DailyStatistics.totalAllocation(TEST_DATE, TEST_DATE)).isEqualTo(10);
        // 2026-01-01 (Thu) .. 2026-01-07 (Wed): six weekdays (10 each) + one Saturday (100)
        assertThat(DailyStatistics.totalAllocation(TEST_DATE, TEST_DATE.plusDays(6))).isEqualTo(160);
    }
}