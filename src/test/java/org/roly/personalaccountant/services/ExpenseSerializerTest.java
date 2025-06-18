package org.roly.personalaccountant.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.roly.personalaccountant.dto.Payment.Category.FOOD;
import static org.roly.personalaccountant.dto.Payment.PaymentType.FIXED;

import java.time.LocalDate;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.roly.personalaccountant.dto.MonthlyExpenses;
import org.roly.personalaccountant.dto.Payment;
import org.roly.personalaccountant.utils.PaymentsGenerator;

class ExpenseSerializerTest {

    private static final LocalDate START_DATE = LocalDate.of(2025, 5, 27);
    private static final LocalDate PAYMENT_DATE = LocalDate.of(2025, 6, 10);

    private final MonthlyExpenses monthlyExpenses = new MonthlyExpenses(
            PaymentsGenerator.initializeEmptyMonth(START_DATE),
            START_DATE,
            new ArrayList<>()
    );
    private final ExpenseSerializer expenseSerializer = new ExpenseSerializer();

    @Test
    void shouldJavaSerializeMonthlyExpense() {
        Payment payment = new Payment("food", FOOD, FIXED, 11.3d, PAYMENT_DATE);
        monthlyExpenses.addPayment(payment);

        expenseSerializer.javaSerialize(monthlyExpenses);
        MonthlyExpenses deserializedExpense = expenseSerializer.javaDeserialize("2025-05-27-expense.ser");

        assertThat(deserializedExpense).isEqualTo(monthlyExpenses);
    }
}