package org.roly.personalaccountant.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.roly.personalaccountant.domain.model.dto.Payment.Category.FOOD;
import static org.roly.personalaccountant.domain.model.dto.Payment.Category.MEDIA;
import static org.roly.personalaccountant.domain.model.dto.Payment.PaymentType.DAILY;
import static org.roly.personalaccountant.domain.model.dto.Payment.PaymentType.FIXED;
import static org.roly.personalaccountant.domain.model.dto.Payment.PaymentType.LEISURE;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.roly.personalaccountant.domain.model.dto.Income;
import org.roly.personalaccountant.domain.model.dto.Payment;
import org.roly.personalaccountant.domain.model.entity.MonthlyExpenseEntity;
import org.roly.personalaccountant.domain.model.services.ExpenseManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ExpenseManagerTest {

    private static final LocalDate START_DATE = LocalDate.of(2025, 5, 27);
    public static final Income INVALID_INCOME = new Income(null, "salariu", START_DATE.minusDays(1), 1122.0d);
    public static final Income WAGE_INCOME = new Income(null, "Wage", START_DATE.plusDays(1), 1122.0d);
    private static final LocalDate PAYMENT_DATE = LocalDate.of(2025, 6, 10);
    private static final YearMonth EXPENSE_MONTH = YearMonth.of(2025, Month.JUNE);
    private static final String EXPENSE_MONTH_NAME = "June 2025";

    @Autowired
    private ExpenseManager manager;

    @BeforeEach
    void setUp() {
        manager.addNewMonthlyExpense(null, START_DATE, null);
    }

    @AfterEach
    void cleanUp() {
        manager.clearExpenses();
    }

    @Test
    void shouldCreateExpense() {
        assertThat(manager.getExpenses().values()).isNotNull();
        assertThat(manager.getExpenses().get(EXPENSE_MONTH).getPayments()).hasSize(32);
        assertThat(manager.getExpenses().get(EXPENSE_MONTH).getStartDate()).isEqualTo(START_DATE);
        assertThat(manager.getExpenses().get(EXPENSE_MONTH).getYearMonth()).isEqualTo(EXPENSE_MONTH);
        assertThat(manager.getExpenses().get(EXPENSE_MONTH).getExpenseName()).isEqualTo(EXPENSE_MONTH_NAME);
    }

    @Test
    void shouldAddPaymentToExpense() {
        Payment payment = new Payment(null, "food", FOOD, FIXED, 11.3d, PAYMENT_DATE);

        manager.addPayment(payment);

        assertThat(manager.getExpense(EXPENSE_MONTH).getPayments().get(PAYMENT_DATE))
                .anyMatch(p -> ((Payment) p).description().equals(payment.description())
                        && ((Payment) p).amount().equals(payment.amount()));
        assertThat(manager.getExpense(EXPENSE_MONTH).getStatistics().getCashLeft()).isEqualTo(-1 * payment.amount());
        assertThat(manager.getExpense(EXPENSE_MONTH).getStatistics().getFixedExpenseTotal()).isEqualTo(payment.amount());
    }

    @Test
    void shouldNotAddPaymentToFixedExpense() {
        Payment payment = new Payment(null, "food", MEDIA, LEISURE, 11.3d, PAYMENT_DATE);

        manager.addPayment(payment);

        assertThat(manager.getExpense(EXPENSE_MONTH).getPayments().get(PAYMENT_DATE))
                .anyMatch(p -> ((Payment) p).description().equals(payment.description())
                        && ((Payment) p).amount().equals(payment.amount()));
        assertThat(manager.getExpense(EXPENSE_MONTH).getStatistics().getCashLeft()).isEqualTo(-1 * payment.amount());
        assertThat(manager.getExpense(EXPENSE_MONTH).getStatistics().getFixedExpenseTotal()).isZero();
    }

    @Test
    void shouldNotAddPaymentToNonExistentExpenseDay() {
        Payment payment = new Payment(null, "food", FOOD, DAILY, 11.3d, PAYMENT_DATE.minusDays(100));

        assertThrows(IllegalArgumentException.class, () -> manager.addPayment(payment));
    }

    @Test
    void shouldCorrectlyAddIncomeToExpense() {
        MonthlyExpenseEntity monthlyExpenseEntity = manager.addIncome(WAGE_INCOME);

        assertThat(monthlyExpenseEntity).isNotNull();
        assertThat(monthlyExpenseEntity.getIncomes().getFirst().getValue()).isEqualTo(WAGE_INCOME.value());
        assertThat(manager.getExpense(EXPENSE_MONTH).getIncomes())
                .anyMatch(i -> ((Income) i).source().equals(WAGE_INCOME.source())
                        && ((Income) i).date().equals(WAGE_INCOME.date())
                        && ((Income) i).value() == WAGE_INCOME.value());
        assertThat(manager.getExpense(EXPENSE_MONTH).getStatistics().getCashTotal()).isEqualTo(WAGE_INCOME.value());
        assertThat(manager.getExpense(EXPENSE_MONTH).getStatistics().getCashLeft()).isEqualTo(WAGE_INCOME.value());
    }

    @Test
    void shouldNotAddIncomeToNonExistentExpense() {
        assertThrows(IllegalArgumentException.class, () -> manager.addIncome(INVALID_INCOME));
    }
}
