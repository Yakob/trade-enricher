package com.fireup.tradeenricher.trade;

import com.fireup.tradeenricher.trade.api.EnrichedTradeDto;
import com.fireup.tradeenricher.trade.api.TradeDto;
import com.fireup.tradeenricher.trade.api.infra.product.ProductReadModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TradeServiceTest {

    @InjectMocks
    private TradeService tradeService;

    @Mock
    private ProductReadModel productReadModel;

    @Test
    void enrichReplaceProductIdWithProductNameWhenFound() {
        // given
        var trade = new TradeDto("19700101", 1, "EUR", BigDecimal.ONE);

        // when
        when(productReadModel.findProductNameById(1)).thenReturn(Optional.of("foo"));
        var enrichedTrade = tradeService.enrich(trade);

        // then
        assertThat(enrichedTrade).contains(
                new EnrichedTradeDto("19700101", "foo", "EUR", BigDecimal.ONE)
        );
    }

    @Test
    void enrichSkipTradesWithInvalidDate() {
        // given
        var trade = new TradeDto("1111", 1, "EUR", BigDecimal.ONE);

        // when
        var enrichedTrade = tradeService.enrich(trade);

        // then
        assertThat(enrichedTrade).isEmpty();
    }

    @Test
    void enrichUseDefaultProductNameWhenNotFound() {
        // given
        var trade = new TradeDto("19700101", 1, "EUR", BigDecimal.ONE);

        // when
        var enrichedTrade = tradeService.enrich(trade);

        // then
        assertThat(enrichedTrade).contains(
                new EnrichedTradeDto("19700101", TradeService.DEFAULT_PRODUCT_NAME, "EUR", BigDecimal.ONE)
        );
    }
}