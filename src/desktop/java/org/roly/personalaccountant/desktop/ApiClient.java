package org.roly.personalaccountant.desktop;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import org.roly.personalaccountant.view.rs.dto.MonthDetailResult;
import org.roly.personalaccountant.view.rs.dto.MonthSummaryResult;

/**
 * Thin REST client for the desktop UI. Talks to the Spring Boot server over HTTP and
 * deserializes JSON into the shared {@code view.rs.dto} records.
 *
 * <p>In a full open-core split these DTOs would be duplicated/generated client-side so the
 * desktop app doesn't depend on server code; for the prototype we reuse them since it's the
 * same Maven module.
 */
public class ApiClient {

    private static final String BASE_PATH = "/v1/accountant/expenses";

    private final String baseUrl;
    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();
    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    public ApiClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public List<MonthSummaryResult> listMonths() throws Exception {
        HttpResponse<String> response = get(BASE_PATH + "/months");
        if (response.statusCode() == 404) {
            return List.of();
        }
        requireOk(response);
        return mapper.readValue(response.body(), new TypeReference<List<MonthSummaryResult>>() {
        });
    }

    public MonthDetailResult getMonthDetail(String yearMonth) throws Exception {
        HttpResponse<String> response = get(BASE_PATH + "/expense/" + yearMonth);
        if (response.statusCode() == 404) {
            return null;
        }
        requireOk(response);
        return mapper.readValue(response.body(), MonthDetailResult.class);
    }

    private HttpResponse<String> get(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create(baseUrl + path))
                .GET()
                .header("Accept", "application/json")
                .timeout(Duration.ofSeconds(10))
                .build();
        return http.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private void requireOk(HttpResponse<String> response) {
        if (response.statusCode() != 200) {
            throw new IllegalStateException("Unexpected HTTP status " + response.statusCode()
                    + " from " + response.uri());
        }
    }
}
