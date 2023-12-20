package com.fireup.tradeenricher.ui.rest;

import com.fireup.tradeenricher.trade.api.TradeApi;
import com.fireup.tradeenricher.trade.api.TradeDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RestController
@RequestMapping(TradeRestController.API_V1_PREFIX)
@RequiredArgsConstructor
class TradeRestController {
    public static final String API_V1_PREFIX = "api/v1";
    public static final String TEXT_CSV_TYPE = "text/csv";
    public static final String CSV_HEADER = "date,product_name,currency,price";
    public static final String CSV_DELIMITER = ",";

    private final TradeApi tradeApi;

    @PostMapping(path = "enrich", consumes = TEXT_CSV_TYPE, produces = TEXT_CSV_TYPE)
    public ResponseEntity<String> enrich(RequestEntity<String> request) {
        try {
            return ResponseEntity.ok(Stream.concat(CSV_HEADER.lines(), Objects.requireNonNull(request.getBody()).lines()
                            .skip(1)
                            .map(csvLine -> csvLine.split(CSV_DELIMITER))
                            .map(csvFields -> new TradeDto(csvFields[0], Long.parseLong(csvFields[1]), csvFields[2], new BigDecimal(csvFields[3])))
                            .map(tradeApi::enrich)
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .map(trade -> String.join(CSV_DELIMITER, trade.date(), trade.productName(), trade.currency(), trade.price().toPlainString())))
                    .collect(Collectors.joining(System.lineSeparator()))
                    .concat(System.lineSeparator()));
        } catch (NullPointerException e) {
            return ResponseEntity.badRequest()
                    .build();
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
            return ResponseEntity.unprocessableEntity()
                    .build();
        }
    }
}
