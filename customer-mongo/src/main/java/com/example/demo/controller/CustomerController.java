package com.example.demo.controller;
import com.example.demo.model.Customer;
import com.example.demo.model.CustomerAccountResponse;
import com.example.demo.model.CustomerDTO;
import com.example.demo.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequestMapping("/customer")
@RestController
public class CustomerController {

    @Autowired
    CustomerService customerService;

    @PostMapping("/create")
    public ResponseEntity<CustomerAccountResponse> createCustomer(@Valid @RequestBody CustomerDTO customerDTO) {
        return customerService.createCustomer(new Customer(customerDTO));
    }

    @GetMapping("/customers")
    public ResponseEntity<List<CustomerDTO>> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    @GetMapping("/customers/{id}")
    public ResponseEntity<CustomerAccountResponse> getCustomerById(@PathVariable("id") Integer id) {
        return customerService.getCustomerById(id);
    }
}
