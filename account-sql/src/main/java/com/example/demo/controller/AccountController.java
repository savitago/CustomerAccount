package com.example.demo.controller;

import com.example.demo.model.Account;
import com.example.demo.model.AccountDTO;
import com.example.demo.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequestMapping("/account")
@RestController
public class AccountController {

    @Autowired
    AccountService accountService;

    @PostMapping("/create")
    public ResponseEntity<AccountDTO> createAccount(@Valid @RequestBody AccountDTO accountDTO) {
        return accountService.createAccount(new Account(accountDTO));
    }

    @GetMapping("/accounts")
    public ResponseEntity<List<AccountDTO>> getAllAccounts() {
        return accountService.getAllAccounts();
    }

    @GetMapping("/accounts/{id}")
    public ResponseEntity<List<AccountDTO>> getAccountsById(@PathVariable("id") Integer id) {
        return accountService.getAccountsById(id);
    }
}