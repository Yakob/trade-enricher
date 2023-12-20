package com.fireup.tradeenricher.trade.api;

import java.util.Optional;

public interface TradeApi {
    Optional<EnrichedTradeDto> enrich(TradeDto tradeDto);

}
