package com.fireup.tradeenricher.ui.rest;

import com.fireup.tradeenricher.trade.api.EnrichedTradeDto;
import com.fireup.tradeenricher.trade.api.TradeApi;
import com.fireup.tradeenricher.trade.api.TradeDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = TradeRestController.class)
class TradeRestControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private TradeApi tradeApi;

    @Test
    void enrichReturnEnrichedCsvWhenEnrichedTradesFound() throws Exception {
        // given
        given(tradeApi.enrich(new TradeDto("19700101", 1, "EUR", BigDecimal.TEN)))
                .willReturn(Optional.of(new EnrichedTradeDto("19700101", "foo", "EUR", BigDecimal.TEN)));

        // expect
        mvc.perform(post("/api/v1/enrich")
                        .accept(TradeRestController.TEXT_CSV_TYPE)
                        .contentType(TradeRestController.TEXT_CSV_TYPE).content("""
                                date,product_id,currency,price
                                19700101,1,EUR,10
                                """))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(TradeRestController.TEXT_CSV_TYPE))
                .andExpect(content().string("""
                        date,product_name,currency,price
                        19700101,foo,EUR,10
                        """));
    }

    @Test
    void enrichReturnOnlyCsvHeadersWhenEnrichedTradesNotFound() throws Exception {
        mvc.perform(post("/api/v1/enrich")
                        .accept(TradeRestController.TEXT_CSV_TYPE)
                        .contentType(TradeRestController.TEXT_CSV_TYPE).content("""
                                date,product_id,currency,price
                                19700101,1,EUR,10
                                """))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(TradeRestController.TEXT_CSV_TYPE))
                .andExpect(content().string("""
                        date,product_name,currency,price
                        """));
    }

    @Test
    void enrichReturn422WhenTradeCsvContainsInvalidProductId() throws Exception {
        mvc.perform(post("/api/v1/enrich")
                        .accept(TradeRestController.TEXT_CSV_TYPE)
                        .contentType(TradeRestController.TEXT_CSV_TYPE).content("""
                                date,product_id,currency,price
                                19700101,foo,EUR,10
                                """))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void enrichReturn422WhenTradeCsvContainsInvalidPrice() throws Exception {
        mvc.perform(post("/api/v1/enrich")
                        .accept(TradeRestController.TEXT_CSV_TYPE)
                        .contentType(TradeRestController.TEXT_CSV_TYPE).content("""
                                date,product_id,currency,price
                                19700101,1,EUR,foo
                                """))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void enrichReturn400WhenRequestBodyIsMissing() throws Exception {
        mvc.perform(post("/api/v1/enrich")
                        .accept(TradeRestController.TEXT_CSV_TYPE)
                        .contentType(TradeRestController.TEXT_CSV_TYPE))
                .andExpect(status().isBadRequest());
    }
}