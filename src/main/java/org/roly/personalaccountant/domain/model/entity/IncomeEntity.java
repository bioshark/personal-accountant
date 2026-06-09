package org.roly.personalaccountant.domain.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import java.util.Objects;

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

    public void setSource(String source) {
        this.source = source;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public MonthlyExpenseEntity getMonthlyExpense() {
        return monthlyExpense;
    }

    public void setMonthlyExpense(MonthlyExpenseEntity monthlyExpense) {
        this.monthlyExpense = monthlyExpense;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        IncomeEntity that = (IncomeEntity) o;
        return Double.compare(value, that.value) == 0 && Objects.equals(source, that.source) && Objects.equals(date,
                that.date);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(source);
        result = 31 * result + Objects.hashCode(date);
        result = 31 * result + Double.hashCode(value);
        return result;
    }
}
