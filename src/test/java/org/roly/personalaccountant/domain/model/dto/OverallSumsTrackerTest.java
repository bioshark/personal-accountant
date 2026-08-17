package org.roly.personalaccountant.domain.model.dto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.roly.personalaccountant.domain.model.dto.Payment.Category;
import org.roly.personalaccountant.domain.model.dto.Payment.PaymentType;

class OverallSumsTrackerTest {

    private static final Set<LocalDate> DATES = IntStream.range(0, 30)
            .mapToObj(i -> LocalDate.now().plusDays(i))
            .collect(LinkedHashSet::new, LinkedHashSet::add, LinkedHashSet::addAll);
    private static final Payment FIXED_PAYMENT_MORTGAGE = new Payment(1L, "House", Category.MORTGAGE.toString(), PaymentType.FIXED, 1000D,
            LocalDate.now());
    private static final Payment DAILY_PAYMENT_FOOD = new Payment(1L, "Food", Category.FOOD.toString(), PaymentType.DAILY, 12.3D, LocalDate.now());
    private static final Payment LEISURE_PAYMENT_MUSIC = new Payment(1L, "Music", Category.MEDIA.toString(), PaymentType.LEISURE, 44.9D,
            LocalDate.now());
    private static final Payment SAVING_PAYMENT_ETF = new Payment(1L, "Etf", Category.ETF.toString(), PaymentType.SAVING, 150D, LocalDate.now());
    private static final Income WAGE_INCOME = new Income(null, "Wage", LocalDate.now(), 3000D);

    private OverallSumsTracker overallSumsTracker;

    @BeforeEach
    void setUp() {
        overallSumsTracker = new OverallSumsTracker(DATES);
    }

    @Test
    void shouldUpdateStatisticsOnFixedPayment() {
        overallSumsTracker.onAdd(FIXED_PAYMENT_MORTGAGE);

        assertThat(overallSumsTracker.getCashLeft()).isEqualTo(-1000D);
        assertThat(overallSumsTracker.getFixedExpenseTotal()).isEqualTo(1000D);
    }

    @Test
    void shouldUpdateStatisticsOnDailyPayment() {
        overallSumsTracker.onAdd(DAILY_PAYMENT_FOOD);

        assertThat(overallSumsTracker.getCashLeft()).isEqualTo(-12.3D);
        assertThat(overallSumsTracker.getDailyExpenseTotal()).isEqualTo(12.3D);
        assertThat(overallSumsTracker.getDailyPayments().get(LocalDate.now()).getDailyTotalExpenditure()).isEqualTo(12.3D);
    }

    @Test
    void shouldUpdateStatisticsOnLeisurePayment() {
        overallSumsTracker.onAdd(LEISURE_PAYMENT_MUSIC);

        assertThat(overallSumsTracker.getCashLeft()).isEqualTo(-44.9D);
        assertThat(overallSumsTracker.getLeisureExpenseTotal()).isEqualTo(44.9D);
    }

    @Test
    void shouldUpdateStatisticsOnSavingPayment() {
        overallSumsTracker.onAdd(SAVING_PAYMENT_ETF);

        assertThat(overallSumsTracker.getCashLeft()).isEqualTo(-150D);
        assertThat(overallSumsTracker.getSavingExpenseTotal()).isEqualTo(150D);
    }

    @Test
    void shouldUpdateStatisticsOnIncome() {
        overallSumsTracker.onAdd(WAGE_INCOME);

        assertThat(overallSumsTracker.getCashTotal()).isEqualTo(3000D);
        assertThat(overallSumsTracker.getCashLeft()).isEqualTo(3000D);
    }

    @Test
    void shouldNetIncomeAndPaymentsCorrectly() {
        overallSumsTracker.onAdd(WAGE_INCOME);
        overallSumsTracker.onAdd(FIXED_PAYMENT_MORTGAGE);
        overallSumsTracker.onAdd(DAILY_PAYMENT_FOOD);

        assertThat(overallSumsTracker.getCashTotal()).isEqualTo(3000D);
        assertThat(overallSumsTracker.getCashLeft()).isEqualTo(3000D - 1000D - 12.3D);
        assertThat(overallSumsTracker.getFixedExpenseTotal()).isEqualTo(1000D);
        assertThat(overallSumsTracker.getDailyExpenseTotal()).isEqualTo(12.3D);
    }

    @Test
    void shouldAdjustUnallocatedPercentageOnAddSaving() {
        Saving saving = new Saving(null, "Emergency Fund", 30);
        overallSumsTracker.addSaving(saving);

        Saving unallocated = overallSumsTracker.getSavings().getFirst();
        assertThat(unallocated.getName()).isEqualTo("Unallocated");
        assertThat(unallocated.getPercentage()).isEqualTo(70D);
    }

    @Test
    void shouldThrowWhenSavingExceedsUnallocated() {
        overallSumsTracker.addSaving(new Saving(null, "Fund A", 60));

        assertThatThrownBy(() -> overallSumsTracker.addSaving(new Saving(null, "Fund B", 50)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("higher than unallocated");
    }

    @Test
    void shouldRestoreUnallocatedOnRemoveSaving() {
        Saving saving = new Saving(null, "Emergency Fund", 30);
        overallSumsTracker.addSaving(saving);
        overallSumsTracker.removeSaving(saving);

        Saving unallocated = overallSumsTracker.getSavings().getFirst();
        assertThat(unallocated.getPercentage()).isEqualTo(100D);
    }

    @Test
    void shouldComputeTotalDailyAllocation() {
        double totalAllocation = overallSumsTracker.getTotalDailyAllocation();

        assertThat(totalAllocation).isGreaterThan(0);
        assertThat(totalAllocation).isEqualTo(
                DATES.stream().mapToDouble(d -> d.getDayOfWeek() == java.time.DayOfWeek.SATURDAY ? 100D : 10D).sum());
    }

    @Test
    void shouldComputeTotalDailyDiffOnlyForDoneDays() {
        assertThat(overallSumsTracker.getTotalDailyDiff()).isEqualTo(0D);

        overallSumsTracker.onAdd(DAILY_PAYMENT_FOOD);
        overallSumsTracker.getDailyPayments().get(LocalDate.now()).setDayDone(true);

        double expectedAllocation = LocalDate.now().getDayOfWeek() == java.time.DayOfWeek.SATURDAY ? 100D : 10D;
        assertThat(overallSumsTracker.getTotalDailyDiff()).isCloseTo(expectedAllocation - 12.3D, within(0.001));
    }

    @Test
    void shouldComputePercentagesCorrectly() {
        overallSumsTracker.onAdd(WAGE_INCOME);
        overallSumsTracker.onAdd(FIXED_PAYMENT_MORTGAGE);
        overallSumsTracker.onAdd(DAILY_PAYMENT_FOOD);
        overallSumsTracker.onAdd(LEISURE_PAYMENT_MUSIC);
        overallSumsTracker.onAdd(SAVING_PAYMENT_ETF);
        double expectedCore = (12.3D + 1000D) / 3000D * 100;
        double expectedWant = 44.9D / 3000D * 100;
        double cashLeft = 3000D - 1000D - 12.3D - 44.9D - 150D;
        double expectedSave = (150D + cashLeft) / 3000D * 100;

        Percentages percentages = overallSumsTracker.getPercentages();

        assertThat(percentages.getCorePercentage()).isCloseTo(expectedCore, within(0.001));
        assertThat(percentages.getWantPercentage()).isCloseTo(expectedWant, within(0.001));
        assertThat(percentages.getSavePercentage()).isCloseTo(expectedSave, within(0.001));
    }
}