package org.roly.personalaccountant.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ExpenseManagerTest {

    private static final LocalDate START_DATE = LocalDate.of(2025, 5, 27);
    private static final YearMonth EXPENSE_MONTH = YearMonth.of(2025, Month.JUNE);
    private final ExpenseManager manager = new ExpenseManager();

    @BeforeEach
    void setUp() {
        manager.addNewMonthlyExpense(EXPENSE_MONTH, START_DATE);
    }

    @AfterEach
    void cleanUp() {
        manager.clearExpenses();
    }

    @Test
    void shouldCreateExpense() {
        assertThat(manager.getExpenses().values()).isNotNull();
        assertThat(manager.getExpenses().get(EXPENSE_MONTH).payments()).hasSize(31);
    }

    @Test
    void shouldAddPaymentToExpense() {
        // TODO finish test
    }

}