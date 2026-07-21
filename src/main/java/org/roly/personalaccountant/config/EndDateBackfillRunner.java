package org.roly.personalaccountant.config;

import static org.roly.personalaccountant.utils.StructuredLoggerHelper.ACTION_1_PARAMS;
import static org.roly.personalaccountant.utils.StructuredLoggerHelper.action;
import static org.roly.personalaccountant.utils.StructuredLoggerHelper.key;

import java.time.LocalDate;
import java.util.List;
import org.roly.personalaccountant.domain.model.entity.MonthlyExpenseEntity;
import org.roly.personalaccountant.domain.repository.MonthlyExpenseRepository;
import org.roly.personalaccountant.utils.PaymentsGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * One-time, idempotent data migration that ensures every {@link MonthlyExpenseEntity} has a concrete {@code endDate}. Historically the end date could
 * be persisted as {@code null} (auto-computed only in the DTO layer), which prevents the indexed {@code findByDateInRange} lookup from matching those
 * months.
 * <p>
 * Runs on every startup but only touches rows whose {@code endDate} is {@code null}, so it is a no-op once the data has been backfilled.
 */
@Component
public class EndDateBackfillRunner implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(EndDateBackfillRunner.class);

    private final MonthlyExpenseRepository repository;

    public EndDateBackfillRunner(MonthlyExpenseRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        List<MonthlyExpenseEntity> toBackfill = repository.findAll().stream()
                .filter(entity -> entity.getEndDate() == null)
                .toList();

        if (toBackfill.isEmpty()) {
            return;
        }

        for (MonthlyExpenseEntity entity : toBackfill) {
            LocalDate resolvedEnd = PaymentsGenerator.resolveEndDate(entity.getStartDate(), null);
            entity.setEndDate(resolvedEnd);
        }
        repository.saveAll(toBackfill);

        LOGGER.info(ACTION_1_PARAMS, action("Backfilled endDate for monthly expenses"), key(toBackfill.size()));
    }
}
