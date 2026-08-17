package org.roly.personalaccountant.domain.model.services;

import java.util.List;
import org.roly.personalaccountant.domain.model.dto.Payment.PaymentType;
import org.roly.personalaccountant.domain.model.entity.RecurringPaymentTemplate;
import org.roly.personalaccountant.domain.repository.RecurringPaymentTemplateRepository;
import org.springframework.stereotype.Service;

@Service
public class RecurringPaymentService {

    private final RecurringPaymentTemplateRepository repository;

    public RecurringPaymentService(RecurringPaymentTemplateRepository repository) {
        this.repository = repository;
    }

    public List<RecurringPaymentTemplate> getAllTemplates() {
        return repository.findAll();
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
