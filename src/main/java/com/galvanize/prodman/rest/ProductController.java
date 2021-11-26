package com.galvanize.prodman.rest;

import com.galvanize.prodman.model.CreatedResponse;
import com.galvanize.prodman.model.Currency;
import com.galvanize.prodman.model.ProductDTO;
import com.galvanize.prodman.service.ProductService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

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
    public ResponseEntity<CreatedResponse> addProduct(@Valid @RequestBody ProductDTO productDTO) {
        final Integer productId = productService.create(productDTO);

        final URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(productId)
                .toUri();

        final CreatedResponse response = new CreatedResponse();
        response.setId(productId);
        response.setHref(uri.toASCIIString());

        return ResponseEntity.created(uri).body(response);
    }
}
