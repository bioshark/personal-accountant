package org.roly.personalaccountant.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.roly.personalaccountant.dto.Payment.Category.FOOD;
import static org.roly.personalaccountant.dto.Payment.PaymentType.MANDATORY;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.roly.personalaccountant.dto.Payment;

class ExpenseManagerTest {

    private static final LocalDate START_DATE = LocalDate.of(2025, 5, 27);
    private static final LocalDate PAYMENT_DATE = LocalDate.of(2025, 6, 10);
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
        assertThat(manager.getExpenses().get(EXPENSE_MONTH).getPayments()).hasSize(31);
    }

    @Test
    void shouldAddPaymentToExpense() {
        Payment payment = new Payment("food", FOOD, MANDATORY, 11.3d, PAYMENT_DATE);

        manager.addPayment(payment);

        assertThat(manager.getExpense(EXPENSE_MONTH).getPayments().get(PAYMENT_DATE)).contains(payment);
    }

}