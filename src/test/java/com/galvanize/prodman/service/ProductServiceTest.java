package com.galvanize.prodman.service;

import com.galvanize.prodman.domain.Product;
import com.galvanize.prodman.model.Currency;
import com.galvanize.prodman.model.ProductDTO;
import com.galvanize.prodman.repository.ProductRepository;
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
class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;

    @Mock
    private FxService fxService;

    @InjectMocks
    private ProductService productService;

    @Test
    void retrieves_existing_products() {
        final Product product = new Product();
        product.setId(123);
        product.setName("Sample");
        product.setDescription("Sample product");
        product.setPrice(BigDecimal.valueOf(100.0));
        product.setViews(0); // irrelevant, required so that code doesn't NPE

        when(productRepository.findById(123)).thenReturn(Optional.of(product));

        final ProductDTO productDTO = productService.fetch(123, Currency.USD);

        assertThat(productDTO)
                .extracting("id", "name", "description", "price")
                .contains(123, "Sample", "Sample product", BigDecimal.valueOf(100.0));
    }

    @Test
    void retrieves_price_in_non_USD_currency() {
        final Product product = new Product();
        product.setPrice(BigDecimal.valueOf(200.0));
        product.setViews(0); // irrelevant, required so that code doesn't NPE

        when(productRepository.findById(456)).thenReturn(Optional.of(product));
        when(fxService.convert(BigDecimal.valueOf(200.0), Currency.EUR)).thenReturn(BigDecimal.valueOf(251.34));

        final ProductDTO productDTO = productService.fetch(456, Currency.EUR);

        assertThat(productDTO)
                .extracting("price")
                .isEqualTo(BigDecimal.valueOf(251.34));
    }

    @Test
    void increments_views_when_fetching_product() {
        final Product product = new Product();
        product.setViews(6);

        when(productRepository.findById(789)).thenReturn(Optional.of(product));

        final ProductDTO productDTO = productService.fetch(789, Currency.USD);

        assertThat(productDTO)
                .extracting("views")
                .isEqualTo(7);
    }
}
