package com.fireup.tradeenricher.trade.infra.product;

import com.fireup.tradeenricher.trade.api.infra.product.ProductReadModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Optional;

@Repository
class ClasspathFileProductReadModel implements ProductReadModel {

    public static final String CSV_DELIMITER = ",";
    private final Resource productResource;

    public ClasspathFileProductReadModel(@Value("${trade-enricher.product.csv}") String productsFilePath) {
        productResource = new ClassPathResource(productsFilePath);
    }

    @Override
    public Optional<String> findProductNameById(long id) {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(productResource.getInputStream()))) {
            return bufferedReader.lines()
                    .skip(1)
                    .map(csvLine -> csvLine.split(CSV_DELIMITER))
                    .filter(csvFields -> Long.parseLong(csvFields[0]) == id)
                    .map(csvFields -> csvFields[1])
                    .findAny();
        } catch (Exception e) {
            throw new IllegalStateException("Unable to read products from " + productResource, e);
        }
    }
}
