package org.roly.personalaccountant.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.roly.personalaccountant.domain.model.dto.Payment.PaymentType.DAILY;
import static org.roly.personalaccountant.domain.model.dto.Payment.PaymentType.FIXED;
import static org.roly.personalaccountant.domain.model.dto.Payment.PaymentType.LEISURE;
import static org.roly.personalaccountant.domain.model.dto.Payment.PaymentType.SAVING;

import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.roly.personalaccountant.domain.model.entity.RecurringPaymentTemplate;
import org.roly.personalaccountant.domain.model.services.RecurringPaymentService;
import org.roly.personalaccountant.domain.repository.RecurringPaymentTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RecurringPaymentServiceTest {

    @Autowired
    private RecurringPaymentService service;

    @Autowired
    private RecurringPaymentTemplateRepository repository;

    @AfterEach
    void cleanUp() {
        repository.deleteAll();
    }

    @Test
    void shouldAlwaysExposeFixedSavingLeisureTabsInOrder() {
        assertThat(service.getTemplatesByType()).containsOnlyKeys(FIXED, SAVING, LEISURE);
        assertThat(service.getTemplatesByType().keySet()).containsExactly(FIXED, SAVING, LEISURE);
        assertThat(service.getTemplatesByType().values()).allMatch(List::isEmpty);
    }

    @Test
    void shouldGroupTemplatesByType() {
        service.addTemplate("Rent", 900, "House", FIXED);
        service.addTemplate("Phone", 30, "Telco", FIXED);
        service.addTemplate("ETF", 200, "Etf", SAVING);

        var byType = service.getTemplatesByType();

        assertThat(byType.get(FIXED)).extracting(RecurringPaymentTemplate::getName).containsExactlyInAnyOrder("Rent", "Phone");
        assertThat(byType.get(SAVING)).extracting(RecurringPaymentTemplate::getName).containsExactly("ETF");
        assertThat(byType.get(LEISURE)).isEmpty();
    }

    @Test
    void shouldAddDailyTabOnlyWhenDailyTemplatesExist() {
        service.addTemplate("Lunch", 10, "Food", DAILY);

        assertThat(service.getTemplatesByType().keySet()).containsExactly(FIXED, SAVING, LEISURE, DAILY);
        assertThat(service.getTemplatesByType().get(DAILY)).extracting(RecurringPaymentTemplate::getName).containsExactly("Lunch");
    }
}
