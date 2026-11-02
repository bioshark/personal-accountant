package org.roly.personalaccountant.domain.model.services;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.roly.personalaccountant.domain.model.dto.Payment.PaymentType;
import org.roly.personalaccountant.domain.model.entity.RecurringPaymentTemplate;
import org.roly.personalaccountant.domain.repository.RecurringPaymentTemplateRepository;
import org.springframework.stereotype.Service;

@Service
public class RecurringPaymentService {

    private static final List<PaymentType> TAB_ORDER = List.of(PaymentType.FIXED, PaymentType.SAVING, PaymentType.LEISURE);

    private final RecurringPaymentTemplateRepository repository;

    public RecurringPaymentService(RecurringPaymentTemplateRepository repository) {
        this.repository = repository;
    }

    public List<RecurringPaymentTemplate> getAllTemplates() {
        return repository.findAll();
    }

    /**
     * Templates grouped for tabbed display: Fixed, Saving and Leisure are always present (possibly empty); Daily only appears when such templates
     * exist, so none are ever hidden.
     */
    public Map<PaymentType, List<RecurringPaymentTemplate>> getTemplatesByType() {
        Map<PaymentType, List<RecurringPaymentTemplate>> byType = new LinkedHashMap<>();
        TAB_ORDER.forEach(type -> byType.put(type, new ArrayList<>()));
        for (RecurringPaymentTemplate template : repository.findAll()) {
            byType.computeIfAbsent(template.getType(), type -> new ArrayList<>()).add(template);
        }
        return byType;
    }

    public RecurringPaymentTemplate addTemplate(String name, double defaultAmount, String category, PaymentType type) {
        return repository.save(new RecurringPaymentTemplate(name, defaultAmount, category, type));
    }

    public RecurringPaymentTemplate editTemplate(Long id, String name, double defaultAmount, String category, PaymentType type) {
        RecurringPaymentTemplate template = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Template not found: " + id));
        template.setName(name);
        template.setDefaultAmount(defaultAmount);
        template.setCategory(category);
        template.setType(type);
        return repository.save(template);
    }

    public void deleteTemplate(Long id) {
        repository.deleteById(id);
    }
}
