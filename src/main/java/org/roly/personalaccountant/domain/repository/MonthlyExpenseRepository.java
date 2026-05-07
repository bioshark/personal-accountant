package org.roly.personalaccountant.domain.repository;

import java.util.Optional;
import org.roly.personalaccountant.domain.model.entity.MonthlyExpenseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MonthlyExpenseRepository extends JpaRepository<MonthlyExpenseEntity, Long> {

    Optional<MonthlyExpenseEntity> findByYearAndMonth(int year, int month);
}
