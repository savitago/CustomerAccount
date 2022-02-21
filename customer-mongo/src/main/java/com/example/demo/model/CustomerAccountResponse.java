package com.example.demo.model;

import lombok.Data;

import java.util.List;

@Data
public class CustomerAccountResponse {
    private Customer customer;
    private List<Account> accounts;
}