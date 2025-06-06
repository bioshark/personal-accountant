package org.roly.personalaccountant.services;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.roly.personalaccountant.dto.Payment;
import org.roly.personalaccountant.utils.PaymentsGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentManager {

    private final LinkedHashMap<LocalDate, List<Payment>> payments;
    private final LocalDate startDate;

    @Autowired
    public PaymentManager(LocalDate startDate) {
        this.startDate = startDate;
        this.payments = PaymentsGenerator.initializeEmptyMonth(startDate);
    }

    public void addPayment(Payment payment) {
        LocalDate paymentDate = payment.date();
        if (!isPaymentWithingInterval(paymentDate)) {
            throw new IllegalArgumentException("Payment date is out of range");
        }
        payments.get(paymentDate).add(payment);
    }

    private boolean isPaymentWithingInterval(LocalDate paymentDate) {
        return paymentDate.isAfter(payments.sequencedKeySet().getFirst()) || paymentDate.isBefore(payments.sequencedKeySet().getLast());
    }

    public Map<LocalDate, List<Payment>> getPayments() {
        return Map.copyOf(payments);
    }

    public LocalDate getStartDate() {
        return startDate;
    }
}
