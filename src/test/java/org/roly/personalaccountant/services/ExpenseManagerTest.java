package org.roly.personalaccountant.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import org.junit.jupiter.api.Test;

class ExpenseManagerTest {

    @Test
    void shouldCreateExpense() {
        ExpenseManager manager = new ExpenseManager();
        LocalDate startDate = LocalDate.of(2025, 5, 27);
        YearMonth yearMonth = YearMonth.of(2025, Month.JUNE);
        manager.addNewMonthlyExpense(yearMonth, startDate);

        assertThat(manager.getExpenses().values()).isNotNull();
        assertThat(manager.getExpenses().get(yearMonth).payments()).hasSize(31);
    }
}