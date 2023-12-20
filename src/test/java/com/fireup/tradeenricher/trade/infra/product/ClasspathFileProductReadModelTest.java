package com.fireup.tradeenricher.trade.infra.product;

import com.fireup.tradeenricher.trade.api.infra.product.ProductReadModel;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;

class ClasspathFileProductReadModelTest {
    private final ProductReadModel readModel = new ClasspathFileProductReadModel("test-product.csv");

    @Test
    void findProductNameByIdReturnNameIfFound() {
        // when
        Optional<String> productName = readModel.findProductNameById(1);

        // then
        assertThat(productName).contains("Foo");
    }

    @Test
    void findProductNameByIdReturnEmptyIfNotFound() {
        // when
        Optional<String> productName = readModel.findProductNameById(100);

        // then
        assertThat(productName).isEmpty();
    }

    @Test
    void findProductNameByIdThrowExceptionWhenCsvFileContainsMalformedEntries() {
        // given
        var readModel = new ClasspathFileProductReadModel("malformed-product.csv");

        // when
        var exception = catchException(() -> readModel.findProductNameById(1));

        // then
        assertThat(exception).isInstanceOf(IllegalStateException.class)
                .hasMessageStartingWith("Unable to read products from")
                .hasMessageContaining("malformed-product.csv")
                .hasRootCauseInstanceOf(ArrayIndexOutOfBoundsException.class);
    }
}