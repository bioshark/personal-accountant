package org.roly.personalaccountant.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class ExpenseManagerTest {

    @Test
    void shouldCreateExpense() {
        ExpenseManager manager = new ExpenseManager();
        LocalDate startDate = LocalDate.of(2025, 5, 27);
        manager.initializeMonthExpense(startDate);

        assertThat(manager.getExpenses().values()).isNotNull();
        assertThat(manager.getExpenses().get(startDate).payments()).hasSize(31);
    }
}