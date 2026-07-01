package org.roly.personalaccountant.domain.repository;

import org.roly.personalaccountant.domain.model.entity.RecurringPaymentTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecurringPaymentTemplateRepository extends JpaRepository<RecurringPaymentTemplate, Long> {

}
