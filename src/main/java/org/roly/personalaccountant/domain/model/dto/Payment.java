package org.roly.personalaccountant.domain.model.dto;

import java.time.LocalDate;

public record Payment(
        Long id,
        String description,
        String category,
        PaymentType type,
        Double amount,
        LocalDate date
) implements BaseTransaction {

    public Payment {
        if (!Double.isFinite(amount) || amount < 0) {
            throw new IllegalArgumentException("Can't create a payment with negative amount: " + amount);
        }
    }

    public enum PaymentType {
        DAILY,
        FIXED,
        LEISURE,
        SAVING;

        public String getDisplayName() {
            return Payment.getDisplayName(this);
        }
    }

    public boolean isFixed() {
        return type == PaymentType.FIXED;
    }

    public boolean isLeisure() {
        return type == PaymentType.LEISURE;
    }

    public boolean isSaving() {
        return type == PaymentType.SAVING;
    }

    private static String getDisplayName(Enum<?> e) {
        String name = e.name().replace('_', ' ').toLowerCase();
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

}
