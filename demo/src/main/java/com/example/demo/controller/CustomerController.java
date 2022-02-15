package com.example.demo.controller;

import com.example.demo.model.Customer;
import com.example.demo.repo.CustomerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@RequestMapping("/customer")
@RestController
public class CustomerController {

    @Autowired
    CustomerRepo customerRepo;

    @Autowired
    RestTemplate restTemplate;

    @PostMapping("/create")
    public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer) {
        try {
            boolean customerExists = customerRepo.findByCustomerId(
                    customer.getCustomerId()).isPresent();

            if(!customerExists) {
                try {
                    ResponseEntity response = restTemplate.exchange(
                            "http://account-sql/account/accounts/" + customer.getCustomerId(),
                            HttpMethod.GET, null,
                            new ParameterizedTypeReference<String>() {
                            });

                    if (response.getStatusCode() == HttpStatus.OK) {
                        customerExists = true;
                    }
                } catch (HttpClientErrorException ex) {
                    if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                        customerExists = false;
                    }
                }
            }

            if(!customerExists)
            {
                Customer savedCustomer = customerRepo.save(new Customer(
                        customer.getCustomerId(), customer.getCustomerName(),
                        new Date(), customer.isActive()));

                return new ResponseEntity<>(savedCustomer, HttpStatus.CREATED);
            } else
                return new ResponseEntity<>(null, HttpStatus.ALREADY_REPORTED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/customers")
    public ResponseEntity<List<Customer>> getAllCustomers() {
        try {
            List<Customer> customers = new ArrayList<>();
            customerRepo.findAll().forEach(customers::add);
            if (customers.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(customers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/customers/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable("id") Integer id) {
        Optional<Customer> selectedCustomer = customerRepo.findByCustomerId(id);
        if (selectedCustomer.isPresent()) {
            return new ResponseEntity<>(selectedCustomer.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


}
