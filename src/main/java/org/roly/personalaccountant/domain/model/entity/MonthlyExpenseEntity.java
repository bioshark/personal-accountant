package org.roly.personalaccountant.domain.model.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class MonthlyExpenseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "expense_year")
    private int year;

    @Column(name = "expense_month")
    private int month;
    private LocalDate startDate;
    private LocalDate endDate;
    private String expenseName;

    @OneToMany(mappedBy = "monthlyExpense", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<PaymentEntity> payments = new ArrayList<>();

    @OneToMany(mappedBy = "monthlyExpense", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<IncomeEntity> incomes = new ArrayList<>();

    protected MonthlyExpenseEntity() {
    }

    public MonthlyExpenseEntity(int year, int month, LocalDate startDate, LocalDate endDate, String expenseName) {
        this.year = year;
        this.month = month;
        this.startDate = startDate;
        this.endDate = endDate;
        this.expenseName = expenseName;
    }

    public Long getId() {
        return id;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public String getExpenseName() {
        return expenseName;
    }

    public List<PaymentEntity> getPayments() {
        return payments;
    }

    public List<IncomeEntity> getIncomes() {
        return incomes;
    }

    public void addPayment(PaymentEntity payment) {
        payments.add(payment);
        payment.setMonthlyExpense(this);
    }

    public void addIncome(IncomeEntity income) {
        incomes.add(income);
        income.setMonthlyExpense(this);
    }
}
