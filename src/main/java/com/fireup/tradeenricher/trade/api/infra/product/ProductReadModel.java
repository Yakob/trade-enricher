package com.fireup.tradeenricher.trade.api.infra.product;

import java.util.Optional;

public interface ProductReadModel {
    Optional<String> findProductNameById(long id);
}
