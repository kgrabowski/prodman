package com.galvanize.prodman.rest;

import com.galvanize.prodman.model.Currency;
import com.galvanize.prodman.model.ProductDTO;
import com.galvanize.prodman.service.ProductService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/api/", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProductController {

    private final ProductService productService;

    public ProductController(final ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/products/{productId}")
    public ProductDTO getProduct(@PathVariable Integer productId,
                                 @RequestParam(required = false, defaultValue = "USD") Currency currency) {
        return productService.fetch(productId, currency);
    }

    @PostMapping("/products")
    public Integer addProduct(@Valid @RequestBody ProductDTO productDTO) {
        return productService.create(productDTO);
    }
}
