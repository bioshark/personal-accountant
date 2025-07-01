package org.roly.personalaccountant.services;

import static org.roly.personalaccountant.dto.Payment.Category.FOOD;
import static org.roly.personalaccountant.dto.Payment.PaymentType.FIXED;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.roly.personalaccountant.dto.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DiskServiceTest {

    private static final LocalDate PAYMENT_DATE = LocalDate.of(2025, 6, 10);

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private DiskService diskService;

    @Test
    void shouldSerializeToDisk() throws IOException {
        Payment payment = new Payment("food", FOOD, FIXED, 11.3d, PAYMENT_DATE);

        diskService.writeToJson(payment, "/Users/sovarszkir/dev/workbench/tmp/Payment-" + UUID.randomUUID() + ".json");
    }

}