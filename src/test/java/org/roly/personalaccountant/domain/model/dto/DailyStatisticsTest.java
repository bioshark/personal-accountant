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
}