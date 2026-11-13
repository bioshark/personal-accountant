package org.roly.personalaccountant.view.rs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.roly.personalaccountant.domain.model.services.ExpenseManager;
import org.roly.personalaccountant.view.rs.controller.ExpenseController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class ExpenseControllerTest {

    private static final LocalDate START_DATE = LocalDate.of(2025, 5, 27);
    private static final String EXPENSE_NAME = "June 2025";

    @Mock
    private ExpenseManager expenseManager;

    @InjectMocks
    private ExpenseController expenseController;

    @Test
    void shouldReturnCreatedWhenMonthIsGenerated() {
        ResponseEntity<Void> response = expenseController.generateNewExpense(EXPENSE_NAME, START_DATE, null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNull();
        verify(expenseManager).addNewMonthlyExpense(EXPENSE_NAME, START_DATE, null, List.of());
    }

}