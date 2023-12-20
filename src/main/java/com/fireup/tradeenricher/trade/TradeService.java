package com.fireup.tradeenricher.trade;

import com.fireup.tradeenricher.trade.api.EnrichedTradeDto;
import com.fireup.tradeenricher.trade.api.TradeApi;
import com.fireup.tradeenricher.trade.api.TradeDto;
import com.fireup.tradeenricher.trade.api.infra.product.ProductReadModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
class TradeService implements TradeApi {
    public static final String DEFAULT_PRODUCT_NAME = "Missing Product Name";

    private final ProductReadModel productReadModel;

    @Override
    public Optional<EnrichedTradeDto> enrich(TradeDto tradeDto) {
        try {
            DateTimeFormatter.BASIC_ISO_DATE.parse(tradeDto.date());
            var productName = productReadModel.findProductNameById(tradeDto.productId())
                    .orElseGet(() -> {
                        log.warn("Product name not found for productId {}, using '{}' instead", tradeDto.productId(), DEFAULT_PRODUCT_NAME);
                        return DEFAULT_PRODUCT_NAME;
                    });
            return Optional.of(new EnrichedTradeDto(tradeDto.date(), productName, tradeDto.currency(), tradeDto.price()));
        } catch (DateTimeParseException exception) {
            log.error("Invalid date format for {}", tradeDto);
            return Optional.empty();
        }
    }
}
