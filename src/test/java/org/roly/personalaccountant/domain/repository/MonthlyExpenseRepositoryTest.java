package org.roly.personalaccountant.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.roly.personalaccountant.domain.model.entity.MonthlyExpenseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class MonthlyExpenseRepositoryTest {

    private static final LocalDate START_DATE = LocalDate.of(2025, 5, 27);
    private static final LocalDate END_DATE = LocalDate.of(2025, 6, 27);

    @Autowired
    private MonthlyExpenseRepository repository;

    @BeforeEach
    void setUp() {
        repository.save(new MonthlyExpenseEntity(2025, 6, START_DATE, END_DATE, "June 2025"));
    }

    @Test
    void shouldFindMonthForDateWithinRange() {
        assertThat(repository.findByDateInRange(LocalDate.of(2025, 6, 10)))
                .isPresent()
                .get()
                .extracting(MonthlyExpenseEntity::getExpenseName)
                .isEqualTo("June 2025");
    }

    @Test
    void shouldFindMonthOnStartDateBoundary() {
        assertThat(repository.findByDateInRange(START_DATE)).isPresent();
    }

    @Test
    void shouldFindMonthOnEndDateBoundary() {
        assertThat(repository.findByDateInRange(END_DATE)).isPresent();
    }

    @Test
    void shouldNotFindMonthBeforeRange() {
        assertThat(repository.findByDateInRange(START_DATE.minusDays(1))).isEmpty();
    }

    @Test
    void shouldNotFindMonthAfterRange() {
        assertThat(repository.findByDateInRange(END_DATE.plusDays(1))).isEmpty();
    }
}
