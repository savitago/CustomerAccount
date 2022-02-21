package com.example.demo.controller;

import com.example.demo.exception.CustomerAlreadyExistsException;
import com.example.demo.exception.CustomerNotActiveException;
import com.example.demo.exception.CustomerNotFoundException;
import com.example.demo.feign.AccountFeign;
import com.example.demo.model.*;
import com.example.demo.repo.CustomerRepo;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@Slf4j
@RequestMapping("/customer")
@RestController
public class CustomerController {

    @Autowired
    CustomerRepo customerRepo;

    @Autowired
    AccountFeign accountFeign;

    @PostMapping("/create")
    public ResponseEntity<CustomerAccountResponse> createCustomer(@Valid @RequestBody Customer customer) {
        try {
            log.info("adding customer");

            if (!customerRepo.findByCustomerId(customer.getCustomerId()).isPresent()) {

                if(!customer.isActive())
                {
                    log.error("cannot create customer that is not active");
                    throw new CustomerNotActiveException("cannot create customer that is not active");
                }
                CustomerAccountResponse customerAccountResponse = new CustomerAccountResponse();
                Date currentDate = new Date();
                Customer savedCustomer = customerRepo.save(new Customer(
                        customer.getCustomerId(), customer.getCustomerName(),
                        currentDate, CustomerType.INDIVIDUAL, customer.isActive()));
                log.info("customer added to database");
                customerAccountResponse.setCustomer(savedCustomer);

                try {
                    log.info("calling account service for account creation");
                    Account newAccount = accountFeign.createAccount(
                            new Account(customer.getCustomerId(),
                                    customer.getCustomerName() + "-account-cash",
                                    currentDate, AccountType.CASH, Boolean.TRUE, 5000.0)).getBody();

                    log.info("a cash account created for customer in the database");
                    customerAccountResponse.setAccounts(Arrays.asList(newAccount));
                } catch (Exception e) {
                    log.error("error creating account for customer " + customer.getCustomerId());
                    log.error(e.getMessage());
                    throw e;
                }
                log.info("customer and account added to the database");
                return new ResponseEntity<>(customerAccountResponse, HttpStatus.CREATED);
            } else {
                log.info("customer already exists");
                throw new CustomerAlreadyExistsException("customer already exists with given customer id");
            }
        } catch (CustomerAlreadyExistsException e) {
            log.error(e.getMessage());
            throw e;
        } catch (CustomerNotActiveException e) {
            log.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/customers")
    public ResponseEntity<List<Customer>> getAllCustomers() {
        try {
            log.info("retrieving list of customers");
            List<Customer> customers = new ArrayList<>();
            customerRepo.findAll().forEach(customers::add);

            if (customers.isEmpty()) {
                log.info("list of customers is empty");
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            log.info("list of customers retrieved");
            return new ResponseEntity<>(customers, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/customers/{id}")
    public ResponseEntity<CustomerAccountResponse> getCustomerById(@PathVariable("id") Integer id) {
        try {
            log.info("retrieving customer id - " + id);
            Optional<Customer> selectedCustomer = customerRepo.findByCustomerId(id);

            CustomerAccountResponse car = new CustomerAccountResponse();
            try {
                ResponseEntity<List<Account>> accountResponse = accountFeign.getAccountsById(id);
                List<Account> retrievedAccounts = accountResponse.getBody();
                car.setAccounts(retrievedAccounts);

                if (selectedCustomer.isPresent()) {
                    car.setCustomer(selectedCustomer.get());
                } else {
                    log.error("customer not found for id " + id);
                    throw new CustomerNotFoundException("customer not found for the id " + id);
                }

                log.info("customer id " + id + " retrieved");
                return new ResponseEntity<>(car, HttpStatus.OK);
            } catch (FeignException ex) {
                log.error(ex.getMessage());
                if (ex.status() == HttpStatus.NOT_FOUND.value()) {
                    log.info("account service returned account not found");
                    throw new CustomerNotFoundException("account not found for the customer");
                }
                throw ex;
            }
        } catch (CustomerNotFoundException e) {
            log.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
