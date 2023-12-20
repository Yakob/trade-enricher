package com.fireup.tradeenricher.trade.api;

import java.math.BigDecimal;

public record TradeDto(
        String date,
        long productId,
        String currency,
        BigDecimal price
) {
}
