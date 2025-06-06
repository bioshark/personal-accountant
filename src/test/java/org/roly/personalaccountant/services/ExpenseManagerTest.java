package org.roly.personalaccountant.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import org.junit.jupiter.api.Test;
import org.roly.personalaccountant.dto.WorkingMonth;

class ExpenseManagerTest {

    @Test
    void shouldCreateExpense() {
        ExpenseManager manager = new ExpenseManager();
        LocalDate startDate = LocalDate.of(2025, 5, 27);
        WorkingMonth workingMonth = new WorkingMonth(startDate, YearMonth.of(2025, Month.JUNE));
        manager.initializeMonthExpense(workingMonth, startDate);

        assertThat(manager.getExpenses().values()).isNotNull();
        assertThat(manager.getExpenses().get(workingMonth).payments()).hasSize(31);
    }
}