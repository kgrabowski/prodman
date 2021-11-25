package com.galvanize.prodman.service;

import com.galvanize.prodman.domain.Product;
import com.galvanize.prodman.model.Currency;
import com.galvanize.prodman.model.ProductDTO;
import com.galvanize.prodman.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Map;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final FxService fxService;

    public ProductService(final ProductRepository productRepository, final FxService fxService) {
        this.productRepository = productRepository;
        this.fxService = fxService;
    }

    public Integer create(final ProductDTO productDTO) {
        final Product product = new Product();
        mapToEntity(productDTO, product);
        return productRepository.save(product).getId();
    }

    public void delete(final Integer id) {
        productRepository.deleteById(id);
    }

    @Transactional
    public ProductDTO fetch(final Integer id, final Currency currency) {
        final Product product = getProduct(id);
        product.setViews(product.getViews() + 1);
        return mapToDTO(product, currency);
    }

    private Product mapToEntity(final ProductDTO productDTO, final Product product) {
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setViews(0);
        product.setDeleted(false);
        return product;
    }

    private ProductDTO mapToDTO(final Product product, final Currency currency) {
        final ProductDTO productDTO = new ProductDTO();
        productDTO.setId(product.getId());
        productDTO.setName(product.getName());
        productDTO.setDescription(product.getDescription());
        productDTO.setPrice(convertPrice(product.getPrice(), currency));
        productDTO.setViews(product.getViews());
        return productDTO;
    }

    private Product getProduct(final Integer id) {
        return productRepository
                .findById(id)
                .orElseThrow(EntityNotFoundException::new);
    }

    private Double convertPrice(final Double price, final Currency targetCurrency) {
        final Map<String, Double> quotes = fxService.getQuotes().getQuotes();
        final Double conversionRate = quotes.get("USD" + targetCurrency);
        if (conversionRate == null) {
            throw new IllegalStateException("Couldn't find conversion rate for USD to " + targetCurrency);
        }
        return price * conversionRate;
    }
}
