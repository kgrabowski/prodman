package com.galvanize.prodman.service;

import com.galvanize.prodman.domain.Product;
import com.galvanize.prodman.model.Currency;
import com.galvanize.prodman.model.ProductDTO;
import com.galvanize.prodman.repository.ProductRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ProductServiceTest {
    private final ProductRepository productRepository = mock(ProductRepository.class);
    private final CurrencyLayerGateway fxGateway = mock(CurrencyLayerGateway.class);

    private final ProductService productService = new ProductService(productRepository, fxGateway);

    @Test
    void convertsPriceToRequestedCurrencyWhenRetrievingProduct() {
        final Product product = new Product();
        product.setId(1);
        product.setName("Sample");
        product.setDescription("Sample product");
        product.setPrice(BigDecimal.valueOf(100.0));
        product.setViews(5);

        final Map<String, BigDecimal> quotes = new HashMap<>();
        quotes.put("USDCAD", BigDecimal.valueOf(1.5));

        when(productRepository.findById(1)).thenReturn(Optional.of(product));
        when(fxGateway.getQuotes()).thenReturn(quotes);

        final ProductDTO productDTO = productService.fetch(1, Currency.CAD);

        assertThat(productDTO)
                .extracting("id", "name", "description", "price")
                .contains(1, "Sample", "Sample product", new BigDecimal("150.00"));
    }
}
