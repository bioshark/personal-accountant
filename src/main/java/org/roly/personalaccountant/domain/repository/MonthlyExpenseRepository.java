package org.roly.personalaccountant.domain.repository;

import java.time.LocalDate;
import java.util.Optional;
import org.roly.personalaccountant.domain.model.entity.MonthlyExpenseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MonthlyExpenseRepository extends JpaRepository<MonthlyExpenseEntity, Long> {

    Optional<MonthlyExpenseEntity> findByYearAndMonth(int year, int month);

    @Query("select m from MonthlyExpenseEntity m where :date >= m.startDate and :date <= m.endDate")
    Optional<MonthlyExpenseEntity> findByDateInRange(@Param("date") LocalDate date);
}
