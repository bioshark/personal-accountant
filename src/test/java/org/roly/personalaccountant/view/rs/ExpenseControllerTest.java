package org.roly.personalaccountant.view.rs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.roly.personalaccountant.domain.model.dto.MonthlyExpenses;
import org.roly.personalaccountant.domain.model.services.ExpenseManager;
import org.roly.personalaccountant.view.rs.controller.ExpenseController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class ExpenseControllerTest {

    private static final LocalDate START_DATE = LocalDate.of(2025, 5, 27);
    private static final YearMonth EXPENSE_MONTH = YearMonth.of(2025, Month.JUNE);
    private static final MonthlyExpenses DUMMY_EXPENSE = new MonthlyExpenses(START_DATE, EXPENSE_MONTH);

    @Mock
    private ExpenseManager expenseManager;

    @InjectMocks
    private ExpenseController expenseController;

    @Test
    void shouldGenerateNewExpense() {
        when(expenseManager.addNewMonthlyExpense(EXPENSE_MONTH, START_DATE)).thenReturn(DUMMY_EXPENSE);
        when(expenseManager.getExpense(EXPENSE_MONTH)).thenReturn(DUMMY_EXPENSE);

        ResponseEntity<Void> response = expenseController.generateNewExpense(EXPENSE_MONTH, START_DATE);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNull();
    }

    @Test
    void shouldHandleNonGeneratableExpense() {
        when(expenseManager.addNewMonthlyExpense(EXPENSE_MONTH, START_DATE)).thenReturn(DUMMY_EXPENSE);
        when(expenseManager.getExpense(EXPENSE_MONTH)).thenReturn(new MonthlyExpenses(START_DATE.plusDays(1), EXPENSE_MONTH));

        ResponseEntity<Void> response = expenseController.generateNewExpense(EXPENSE_MONTH, START_DATE);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }

}