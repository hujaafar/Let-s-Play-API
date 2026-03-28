package com.reboot.lets_play.controller;

import com.reboot.lets_play.dto.ProductRequest;
import com.reboot.lets_play.model.Product;
import com.reboot.lets_play.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable String id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@Valid @RequestBody ProductRequest req,
                                                  @AuthenticationPrincipal String email) {
        return ResponseEntity.status(201).body(productService.createProduct(req, email));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable String id,
                                                  @Valid @RequestBody ProductRequest req,
                                                  @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(productService.updateProduct(id, req, email));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id,
                                               @AuthenticationPrincipal String email) {
        productService.deleteProduct(id, email);
        return ResponseEntity.noContent().build();
    }
}
