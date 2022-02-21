package com.example.demo.controller;

import com.example.demo.exception.AccountNotFoundException;
import com.example.demo.exception.CustomerNotActiveException;
import com.example.demo.model.Account;
import com.example.demo.repo.AccountRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@RequestMapping("/account")
@RestController
public class AccountController {

    @Autowired
    AccountRepo accountRepo;

    @PostMapping("/create")
    public ResponseEntity<Account> createAccount(@Valid @RequestBody Account account) {
        try {
            log.info("creating account");

            if(!account.getIsCustomerActive())
            {
                log.error("customer not active for the account");
                throw new CustomerNotActiveException("customer not active for the account");
            }

            Account savedAccount = accountRepo.save(new Account(
                    account.getAccountId(), account.getAccountName(),
                    new Date(), account.getAccountType(), account.getIsCustomerActive(),
                    account.getAccountBalance()));

            log.info("account created");
            return new ResponseEntity<>(savedAccount, HttpStatus.CREATED);
        } catch (CustomerNotActiveException e) {
            log.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/accounts")
    public ResponseEntity<List<Account>> getAllAccounts() {
        try {
            log.info("retrieving list of accounts");
            List<Account> accounts = new ArrayList<>();
            accountRepo.findAll().forEach(accounts::add);

            if (accounts.isEmpty()) {

                log.info("list of accounts is empty");
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            log.info("list of accounts retrieved");
            return new ResponseEntity<>(accounts, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/accounts/{id}")
    public ResponseEntity<List<Account>> getAccountsById(@PathVariable("id") Integer id) {
        try {
            log.info("retrieving accounts by id - " + id);
            List<Account> allAccounts = accountRepo.findAllByAccountId(id);

            if(allAccounts.isEmpty())
            {
                log.error("no account found for id - " + id);
                throw new AccountNotFoundException("no account found for id - " + id);
            }
            log.info("accounts retrieved for id - " + id);
            return new ResponseEntity<>(allAccounts, HttpStatus.OK);
        } catch (AccountNotFoundException e) {
            log.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}