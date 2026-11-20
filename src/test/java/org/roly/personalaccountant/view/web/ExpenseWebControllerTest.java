package org.roly.personalaccountant.view.web;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.YearMonth;
import org.junit.jupiter.api.Test;
import org.roly.personalaccountant.domain.model.services.CategoryService;
import org.roly.personalaccountant.domain.model.services.ExpenseManager;
import org.roly.personalaccountant.domain.model.services.RecurringPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ExpenseWebController.class)
class ExpenseWebControllerTest {

    private static final YearMonth MISSING_MONTH = YearMonth.of(2026, 10);
    private static final String REFERER = "/month/2026-11";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ExpenseManager expenseManager;

    @MockitoBean
    private RecurringPaymentService recurringPaymentService;

    @MockitoBean
    private CategoryService categoryService;

    @Test
    void shouldRedirectHomeWithErrorWhenMonthDoesNotExist() throws Exception {
        when(expenseManager.getExpense(MISSING_MONTH)).thenReturn(null);

        mockMvc.perform(get("/month/" + MISSING_MONTH))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("error", "No month exists for " + MISSING_MONTH));
    }

    @Test
    void shouldRedirectBackWithErrorWhenEditedIncomeIsRejected() throws Exception {
        LocalDate date = LocalDate.of(2026, 12, 1);
        when(expenseManager.editIncome(1L, "Wage", date, 100.0)).thenThrow(new IllegalArgumentException("Date outside month"));

        mockMvc.perform(post("/month/editincome/1")
                        .param("source", "Wage")
                        .param("date", date.toString())
                        .param("value", "100")
                        .header("Referer", REFERER))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(REFERER))
                .andExpect(flash().attribute("error", "Date outside month"));
    }
}
