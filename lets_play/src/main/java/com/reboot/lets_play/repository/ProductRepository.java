package com.reboot.lets_play.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.reboot.lets_play.model.Product;

public interface ProductRepository extends MongoRepository<Product, String> {
    List<Product> findByUserId(String userId);
}