package com.example.demo.service;
import com.example.demo.model.Account;
import com.example.demo.model.AccountDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface AccountService {
    public ResponseEntity<AccountDTO> createAccount(Account account);

    public ResponseEntity<List<AccountDTO>> getAllAccounts();

    public ResponseEntity<List<AccountDTO>> getAccountsById(Integer id);
}