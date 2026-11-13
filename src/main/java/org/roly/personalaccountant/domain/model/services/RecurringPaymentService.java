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

    private static final List<PaymentType> TEMPLATE_TYPES = List.of(PaymentType.FIXED, PaymentType.SAVING, PaymentType.LEISURE);

    private final RecurringPaymentTemplateRepository repository;

    public RecurringPaymentService(RecurringPaymentTemplateRepository repository) {
        this.repository = repository;
    }

    public List<RecurringPaymentTemplate> getAllTemplates() {
        return repository.findAll();
    }

    public List<PaymentType> getTemplateTypes() {
        return TEMPLATE_TYPES;
    }

    public List<RecurringPaymentTemplate> getPullableTemplates() {
        return repository.findAll().stream()
                .filter(template -> TEMPLATE_TYPES.contains(template.getType()))
                .toList();
    }

    /**
     * Templates grouped for tabbed display: Fixed, Saving and Leisure are always present (possibly empty);
     */
    public Map<PaymentType, List<RecurringPaymentTemplate>> getTemplatesByType() {
        Map<PaymentType, List<RecurringPaymentTemplate>> byType = new LinkedHashMap<>();
        TEMPLATE_TYPES.forEach(type -> byType.put(type, new ArrayList<>()));
        for (RecurringPaymentTemplate template : repository.findAll()) {
            byType.computeIfAbsent(template.getType(), type -> new ArrayList<>()).add(template);
        }
        return byType;
    }

    public Map<PaymentType, List<RecurringPaymentTemplate>> getPullableTemplatesByType() {
        Map<PaymentType, List<RecurringPaymentTemplate>> byType = getTemplatesByType();
        byType.keySet().retainAll(TEMPLATE_TYPES);
        return byType;
    }

    public RecurringPaymentTemplate addTemplate(String name, double defaultAmount, String category, PaymentType type) {
        requireTemplateType(type);
        return repository.save(new RecurringPaymentTemplate(name, defaultAmount, category, type));
    }

    public RecurringPaymentTemplate editTemplate(Long id, String name, double defaultAmount, String category, PaymentType type) {
        RecurringPaymentTemplate template = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Template not found: " + id));
        if (type != template.getType()) {
            requireTemplateType(type);
        }
        template.setName(name);
        template.setDefaultAmount(defaultAmount);
        template.setCategory(category);
        template.setType(type);
        return repository.save(template);
    }

    public void deleteTemplate(Long id) {
        repository.deleteById(id);
    }

    private static void requireTemplateType(PaymentType type) {
        if (!TEMPLATE_TYPES.contains(type)) {
            throw new IllegalArgumentException("Recurring payments cannot be of type " + type.getDisplayName());
        }
    }
}
