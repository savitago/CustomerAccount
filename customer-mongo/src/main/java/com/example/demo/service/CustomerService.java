package com.example.demo.service;

import com.example.demo.model.Customer;
import com.example.demo.model.CustomerAccountResponse;
import com.example.demo.model.CustomerDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CustomerService {
    public ResponseEntity<CustomerAccountResponse> createCustomer(Customer customer);

    public ResponseEntity<List<CustomerDTO>> getAllCustomers();

    public ResponseEntity<CustomerAccountResponse> getCustomerById(Integer id);
}