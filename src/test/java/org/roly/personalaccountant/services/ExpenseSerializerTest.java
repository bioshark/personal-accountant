package org.roly.personalaccountant.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.roly.personalaccountant.dto.Payment.Category.FOOD;
import static org.roly.personalaccountant.dto.Payment.PaymentType.FIXED;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.roly.personalaccountant.dto.MonthlyExpenses;
import org.roly.personalaccountant.dto.Payment;
import org.roly.personalaccountant.utils.PaymentsGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

@JsonTest
class ExpenseSerializerTest {

    private static final LocalDate START_DATE = LocalDate.of(2025, 5, 27);
    private static final LocalDate PAYMENT_DATE = LocalDate.of(2025, 6, 10);
    private static final Payment TEST_PAYMENT = new Payment("food", FOOD, FIXED, 11.3d, PAYMENT_DATE);

    @Autowired
    private JacksonTester<Object> jsonObject;
    @Autowired
    private JacksonTester<Payment> jsonDto;
    @Autowired
    private ResourceLoader resourceLoader;

    private final MonthlyExpenses monthlyExpenses = new MonthlyExpenses(
            PaymentsGenerator.initializeEmptyMonth(START_DATE),
            START_DATE,
            new ArrayList<>()
    );
    private final ExpenseSerializer expenseSerializer = new ExpenseSerializer();

    @Test
    void shouldDeserializePaymentFromDTO() throws IOException {
        JsonContent<Object> jsonContent = jsonObject.write(TEST_PAYMENT);

        assertThat(jsonContent)
                .hasJsonPathStringValue("@.description")
                .extractingJsonPathStringValue("@.description")
                .isEqualTo("food");
        assertThat(jsonContent)
                .hasJsonPathStringValue("@.date")
                .extractingJsonPathStringValue("@.date")
                .isEqualTo("2025-06-10");
    }

    @Test
    void testSerializeFromFile() throws IOException {
        String expectedJson = loadJsonFile("dto/payment.json");

        assertThat(jsonDto.write(TEST_PAYMENT)).isEqualToJson(expectedJson);
    }

    @Test
    void shouldJavaSerializeMonthlyExpense() {
        monthlyExpenses.addPayment(TEST_PAYMENT);

        expenseSerializer.javaSerialize(monthlyExpenses);
        MonthlyExpenses deserializedExpense = expenseSerializer.javaDeserialize("2025-05-27-expense.ser");

        assertThat(deserializedExpense).isEqualTo(monthlyExpenses);
    }

    private String loadJsonFile(String fileName) throws IOException {
        Resource resource = resourceLoader.getResource("classpath:/" + fileName);
        return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }
}