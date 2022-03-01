package com.example.demo.service;

import com.example.demo.exception.CustomerAlreadyExistsException;
import com.example.demo.exception.CustomerNotActiveException;
import com.example.demo.exception.CustomerNotFoundException;
import com.example.demo.feign.AccountFeign;
import com.example.demo.model.*;
import com.example.demo.repo.CustomerRepo;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CustomerServiceImpl implements CustomerService {

    private static Logger log = LoggerFactory.getLogger(CustomerServiceImpl.class);

    @Autowired
    CustomerRepo customerRepo;

    @Autowired
    AccountFeign accountFeign;

    @Override
    public ResponseEntity<CustomerAccountResponse> createCustomer(Customer customer) {
        try {
            log.info("adding customer");

            if (!customerRepo.findByCustomerId(customer.getCustomerId()).isPresent()) {

                if (!customer.isActive()) {
                    log.error("cannot create customer that is not active");
                    throw new CustomerNotActiveException("cannot create customer that is not active");
                }

                CustomerAccountResponse customerAccountResponse = new CustomerAccountResponse();
                customer.setCreationDate(new Date());
                customer.setCustomerType(CustomerType.INDIVIDUAL);
                Customer savedCustomer = customerRepo.save(customer);
                log.info("customer added to database");

                customerAccountResponse.setCustomer(new CustomerDTO(savedCustomer));

                customerAccountResponse.setAccounts(Arrays.asList(createAccountForCustomer(customer)));

                log.info("customer and account added to the database");
                return new ResponseEntity<>(customerAccountResponse, HttpStatus.CREATED);
            } else {
                log.info("customer already exists");
                throw new CustomerAlreadyExistsException("customer already exists with given customer id");
            }
        } catch (CustomerAlreadyExistsException | CustomerNotActiveException e) {
            log.error("error creating customer", e);
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<List<CustomerDTO>> getAllCustomers() {
        try {
            log.info("retrieving list of customers");
            List<CustomerDTO> customers = new ArrayList<>();
            for (Customer cus : customerRepo.findAll()) {
                customers.add(new CustomerDTO(cus));
            }

            if (customers.isEmpty()) {
                log.info("list of customers is empty");
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            log.info("list of customers retrieved");
            return new ResponseEntity<>(customers, HttpStatus.OK);
        } catch (Exception e) {
            log.error("error retrieving list of customers", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<CustomerAccountResponse> getCustomerById(Integer id) {
        try {
            log.info("retrieving customer id - {}", id);
            Optional<Customer> selectedCustomer = customerRepo.findByCustomerId(id);

            CustomerAccountResponse car = new CustomerAccountResponse();

            if (selectedCustomer.isPresent()) {
                car.setCustomer(new CustomerDTO(selectedCustomer.get()));
            } else {
                log.error("customer not found for id - {}", id);
                throw new CustomerNotFoundException("customer not found for the id - " + id);
            }

            car.setAccounts(getAccountsByCustomerId(id));

            log.info("customer id {} retrieved", id);
            return new ResponseEntity<>(car, HttpStatus.OK);
        } catch (CustomerNotFoundException e) {
            log.error("error retrieving customer", e);
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private AccountDTO createAccountForCustomer(Customer customer) {
        try {
            log.info("calling account service for account creation");
            AccountDTO newAccount = accountFeign.createAccount(
                    new AccountDTO(customer.getCustomerId(),
                            customer.getCustomerName() + "-account-cash",
                            new Date(), AccountType.CASH, Boolean.TRUE, 5000.0)).getBody();

            log.info("a cash account created for customer in the database");
            return newAccount;
        } catch (Exception e) {
            log.error("error creating account for customer {} ", customer.getCustomerId());
            log.error(e.getMessage());
            throw e;
        }
    }

    private List<AccountDTO> getAccountsByCustomerId(Integer id) {
        try {
            log.info("retrieving accounts for customer with customer id - {}", id);
            return accountFeign.getAccountsById(id).getBody();
        } catch (FeignException ex) {
            log.error("error retrieving account by customer id.", ex);
            if (ex.status() == HttpStatus.NOT_FOUND.value()) {
                log.info("account service returned account not found");
                throw new CustomerNotFoundException("account not found for the customer");
            }
            throw ex;
        }
    }
}