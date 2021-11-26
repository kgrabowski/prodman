package com.galvanize.prodman.service;

import com.galvanize.prodman.domain.Product;
import com.galvanize.prodman.model.Currency;
import com.galvanize.prodman.model.ProductDTO;
import com.galvanize.prodman.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;

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
    public ProductDTO fetch(final Integer productId, final Currency currency) {
        final Product product = getProduct(productId);
        incrementViews(product);

        final ProductDTO productDTO = mapToDTO(product);
        if (currency != Currency.USD) {
            convertPrice(productDTO, currency);
        }

        return productDTO;
    }

    private Product mapToEntity(final ProductDTO productDTO, final Product product) {
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setViews(0);
        product.setDeleted(false);
        return product;
    }

    private ProductDTO mapToDTO(final Product product) {
        final ProductDTO productDTO = new ProductDTO();
        productDTO.setId(product.getId());
        productDTO.setName(product.getName());
        productDTO.setDescription(product.getDescription());
        productDTO.setPrice(product.getPrice());
        productDTO.setViews(product.getViews());
        return productDTO;
    }

    private Product getProduct(final Integer id) {
        return productRepository
                .findById(id)
                .orElseThrow(() -> productNotFound(id));
    }

    private EntityNotFoundException productNotFound(Integer id) {
        final String message = String.format("Product with ID %s does not exist in the database", id);
        return new EntityNotFoundException(message);
    }

    private void incrementViews(Product product) {
        product.setViews(product.getViews() + 1);
    }

    private void convertPrice(ProductDTO productDTO, Currency currency) {
        final BigDecimal originalPrice = productDTO.getPrice();
        final BigDecimal convertedPrice = fxService.convert(originalPrice, currency);
        productDTO.setPrice(convertedPrice);
    }
}
