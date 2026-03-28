package com.reboot.lets_play.service;

import com.reboot.lets_play.dto.ProductRequest;
import com.reboot.lets_play.exception.*;
import com.reboot.lets_play.model.Product;
import com.reboot.lets_play.model.Role;
import com.reboot.lets_play.repository.ProductRepository;
import com.reboot.lets_play.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public ProductService(ProductRepository productRepository, UserRepository userRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    public Product createProduct(ProductRequest req, String email) {
        String userId = userRepository.findByEmail(email)
                .orElseThrow(() -> new ForbiddenException("Access denied"))
                .getId();
        Product product = Product.builder()
                .name(req.getName())
                .description(req.getDescription())
                .price(req.getPrice())
                .userId(userId)
                .build();
        return productRepository.save(product);
    }

    public Product updateProduct(String id, ProductRequest req, String email) {
        Product product = getProductById(id);
        checkOwnership(product, email);
        product.setName(req.getName());
        product.setDescription(req.getDescription());
        product.setPrice(req.getPrice());
        return productRepository.save(product);
    }

    public void deleteProduct(String id, String email) {
        Product product = getProductById(id);
        checkOwnership(product, email);
        productRepository.deleteById(id);
    }

    private void checkOwnership(Product product, String email) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ForbiddenException("Access denied"));
        if (user.getRole() == Role.ROLE_ADMIN) return;
        if (!product.getUserId().equals(user.getId()))
            throw new ForbiddenException("You do not own this product");
    }
}
