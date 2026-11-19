package org.roly.personalaccountant.domain.model.dto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.roly.personalaccountant.domain.model.dto.Payment.PaymentType.FIXED;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class MonthlyExpensesTest {

    private static final LocalDate START_DATE = LocalDate.of(2025, 5, 27);
    private static final LocalDate END_DATE = LocalDate.of(2025, 6, 26);

    @Test
    void shouldRejectPaymentOutsideMonthWithClearError() {
        MonthlyExpenses month = new MonthlyExpenses(null, START_DATE, END_DATE);
        Payment outside = new Payment(null, "food", "food", FIXED, 10.0d, END_DATE.plusDays(1));

        assertThatThrownBy(() -> month.addPayment(outside))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(END_DATE.plusDays(1).toString());
        assertThat(month.getStatistics().getFixedExpenseTotal()).isZero();
    }
}
