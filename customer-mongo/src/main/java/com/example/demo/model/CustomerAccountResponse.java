package com.example.demo.model;

import lombok.Data;

import java.util.List;

@Data
public class CustomerAccountResponse {
    private CustomerDTO customer;
    private List<AccountDTO> accounts;
}