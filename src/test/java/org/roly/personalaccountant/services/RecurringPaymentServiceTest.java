package org.roly.personalaccountant.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
        repository.save(new RecurringPaymentTemplate("Lunch", 10, "Food", DAILY));

        assertThat(service.getTemplatesByType().keySet()).containsExactly(FIXED, SAVING, LEISURE, DAILY);
        assertThat(service.getTemplatesByType().get(DAILY)).extracting(RecurringPaymentTemplate::getName).containsExactly("Lunch");
    }

    @Test
    void shouldExcludeDailyTemplatesFromPullableTemplates() {
        repository.save(new RecurringPaymentTemplate("Lunch", 10, "Food", DAILY));
        service.addTemplate("Rent", 900, "House", FIXED);

        assertThat(service.getPullableTemplates()).extracting(RecurringPaymentTemplate::getName).containsExactly("Rent");
        assertThat(service.getPullableTemplatesByType().keySet()).containsExactly(FIXED, SAVING, LEISURE);
    }

    @Test
    void shouldRejectCreatingDailyTemplate() {
        assertThatThrownBy(() -> service.addTemplate("Lunch", 10, "Food", DAILY)).isInstanceOf(IllegalArgumentException.class);
        assertThat(repository.findAll()).isEmpty();
    }

    @Test
    void shouldRejectSwitchingTemplateToDaily() {
        Long id = service.addTemplate("Rent", 900, "House", FIXED).getId();

        assertThatThrownBy(() -> service.editTemplate(id, "Rent", 900, "House", DAILY)).isInstanceOf(IllegalArgumentException.class);
        assertThat(repository.findById(id)).get().extracting(RecurringPaymentTemplate::getType).isEqualTo(FIXED);
    }

    @Test
    void shouldAllowEditingExistingDailyTemplateWithoutChangingType() {
        Long id = repository.save(new RecurringPaymentTemplate("Lunch", 10, "Food", DAILY)).getId();

        service.editTemplate(id, "Lunch", 12, "Food", DAILY);

        assertThat(repository.findById(id)).get().extracting(RecurringPaymentTemplate::getDefaultAmount).isEqualTo(12.0);
    }
}
