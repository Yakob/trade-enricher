package com.fireup.tradeenricher.trade.api;

import java.math.BigDecimal;

public record EnrichedTradeDto(
        String date,
        String productName,
        String currency,
        BigDecimal price
) {
}
