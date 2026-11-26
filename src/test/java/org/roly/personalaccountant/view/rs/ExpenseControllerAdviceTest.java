package org.roly.personalaccountant.view.rs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.YearMonth;
import org.junit.jupiter.api.Test;
import org.roly.personalaccountant.domain.model.services.ExpenseManager;
import org.roly.personalaccountant.domain.model.services.MonthClosedException;
import org.roly.personalaccountant.view.rs.controller.ExpenseController;
import org.roly.personalaccountant.view.rs.mapper.ExpenseConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ExpenseController.class)
class ExpenseControllerAdviceTest {

    private static final String REMOVE_SAVING = "/v1/accountant/expenses/removeSaving";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ExpenseManager expenseManager;

    @MockitoBean
    private ExpenseConverter expenseConverter;

    @Test
    void shouldReturnBadRequestWithMessageForInvalidInput() throws Exception {
        when(expenseManager.removeSavingById(any())).thenThrow(new IllegalArgumentException("Saving not found: 42"));

        mockMvc.perform(post(REMOVE_SAVING).param("id", "42"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Saving not found: 42"));
    }

    @Test
    void shouldReturnConflictWhenMonthIsClosed() throws Exception {
        when(expenseManager.removeSavingById(any())).thenThrow(new MonthClosedException(YearMonth.of(2026, 10)));

        mockMvc.perform(post(REMOVE_SAVING).param("id", "42"))
                .andExpect(status().isConflict());
    }
}
