package org.roly.personalaccountant.domain.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;

@Entity
public class IncomeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String source;
    private LocalDate date;

    @jakarta.persistence.Column(name = "income_value")
    private double value;

    @ManyToOne
    private MonthlyExpenseEntity monthlyExpense;

    protected IncomeEntity() {
    }

    public IncomeEntity(String source, LocalDate date, double value) {
        this.source = source;
        this.date = date;
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public String getSource() {
        return source;
    }

    public LocalDate getDate() {
        return date;
    }

    public double getValue() {
        return value;
    }

    public MonthlyExpenseEntity getMonthlyExpense() {
        return monthlyExpense;
    }

    public void setMonthlyExpense(MonthlyExpenseEntity monthlyExpense) {
        this.monthlyExpense = monthlyExpense;
    }
}
