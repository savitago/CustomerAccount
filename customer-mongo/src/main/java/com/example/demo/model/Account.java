package com.example.demo.model;

import lombok.Data;

import java.util.Date;

@Data
public class Account {

    private Integer accountId;
    private Integer accountNumber;
    private String accountName;
    private Date creationDate;
    private AccountType accountType;
    private Boolean isCustomerActive;
    private Double accountBalance;

    public Account(Integer accountId, String accountName, Date creationDate,
                   AccountType accountType, Boolean isCustomerActive, Double accountBalance) {
        this.accountId = accountId;
        this.accountName = accountName;
        this.creationDate = creationDate;
        this.accountType = accountType;
        this.isCustomerActive = isCustomerActive;
        this.accountBalance = accountBalance;
    }

    public Account()
    {

    }

}
