package org.roly.personalaccountant.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.roly.personalaccountant.dto.Payment.Category.FOOD;
import static org.roly.personalaccountant.dto.Payment.PaymentType.FIXED;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.roly.personalaccountant.dto.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

@SpringBootTest
class DiskServiceTest {

    private static final LocalDate PAYMENT_DATE = LocalDate.of(2025, 6, 10);
    private static final Payment PAYMENT = new Payment("food", FOOD, FIXED, 11.3d, PAYMENT_DATE);
    private static final String FILE_PATH = "./Payment-TEST.json";

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private DiskService diskService;

    private JacksonTester<Payment> jacksonTester;

    @BeforeEach
    public void setup() {
        JacksonTester.initFields(this, objectMapper);
    }


    @Test
    void shouldSerializeToDisk() throws IOException {

        diskService.writeToJson(PAYMENT, FILE_PATH);

        assertThat(Path.of(FILE_PATH)).exists();
    }

    @Test
    void shouldReadObjectFromDisk() throws IOException {
        diskService.writeToJson(PAYMENT, FILE_PATH);

        Payment readPayment = diskService.readSave(FILE_PATH, Payment.class);

        JsonContent<Payment> jsonContent = jacksonTester.write(readPayment);
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
    void testSerializeFromFile() throws IOException, URISyntaxException {
        String expectedJson = Files.readString(Paths.get(
                        Objects.requireNonNull(getClass().getClassLoader().getResource("dto/payment.json")).toURI()),
                StandardCharsets.UTF_8);

        assertThat(jacksonTester.write(PAYMENT)).isEqualToJson(expectedJson);
    }
}