package org.roly.personalaccountant.dto;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;

public record MonthlyExpenses(
        LinkedHashMap<LocalDate, List<Payment>> payments,
        LocalDate startDate,
        List<Income> incomes
) {

    public void addPayment(LocalDate date, Payment payment) {
        payments.get(date).add(payment);
    }

}
