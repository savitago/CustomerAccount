package com.example.demo.repo;

import com.example.demo.model.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CustomerRepo extends MongoRepository<Customer, Integer> {
    Optional<Customer> findByCustomerId(Integer customerId);
}
