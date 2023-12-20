package com.fireup.tradeenricher;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TradeEnricherApplicationTest {

    @LocalServerPort
    private int serverPort;

    @Test
    void tradeEnrichSmokeTest() {
        // given
        RestTemplate restTemplate = new RestTemplateBuilder()
                .rootUri("http://localhost:" + serverPort)
                .defaultHeader(HttpHeaders.ACCEPT, "text/csv")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "text/csv")
                .build();

        String tradeCsv = """
                date,product_id,currency,price
                20160101,1,EUR,10.0
                20160101,2,EUR,20.1
                20160101,3,EUR,30.34
                20160101,11,EUR,35.34
                """;

        String enrichedTradeCsv = """
                date,product_name,currency,price
                20160101,Treasury Bills Domestic,EUR,10.0
                20160101,Corporate Bonds Domestic,EUR,20.1
                20160101,REPO Domestic,EUR,30.34
                20160101,Missing Product Name,EUR,35.34
                """;

        // when
        var response = restTemplate.postForEntity("/api/v1/enrich", tradeCsv, String.class);

        // then
        assertThat(response.getBody()).isEqualTo(enrichedTradeCsv);
    }
}