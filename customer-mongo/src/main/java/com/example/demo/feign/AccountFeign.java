package com.example.demo.feign;
import com.example.demo.model.Account;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

@FeignClient(name="account-sql", fallbackFactory=HystrixFallBackFactory.class)
public interface AccountFeign {

    @GetMapping(value = "/account/accounts/{id}")
    public ResponseEntity<List<Account>> getAccountsById(@PathVariable("id") Integer id);

    @PostMapping(value = "/account/create")
    public ResponseEntity<Account> createAccount(@Valid @RequestBody Account account);
}

