package com.example.demo.controller;
import com.example.demo.model.Account;
import com.example.demo.repo.AccountRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RequestMapping("/account")
@RestController
public class AccountController {

    @Autowired
    AccountRepo accountRepo;

    @PostMapping("/create")
    public ResponseEntity<Account> createAccount(@RequestBody Account account) {
        try {
            Account savedAccount = accountRepo.save(new Account(
                    account.getAccountId(), account.getAccountName(),
                    new Date(), account.getAccountBalance()));
            return new ResponseEntity<>(savedAccount, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/accounts")
    public ResponseEntity<List<Account>> getAllAccounts() {
        try {
            List<Account> accounts = new ArrayList<>();
            accountRepo.findAll().forEach(accounts::add);
            if (accounts.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(accounts, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/accounts/{id}")
    public ResponseEntity<Account> getAccountById(@PathVariable("id") Integer id) {
        Optional<Account> selectedAccount = accountRepo.findByAccountId(id);
        if (selectedAccount.isPresent()) {
            return new ResponseEntity<>(selectedAccount.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}