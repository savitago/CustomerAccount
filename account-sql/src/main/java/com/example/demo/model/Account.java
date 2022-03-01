package com.example.demo.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@NoArgsConstructor
@Entity
@Table(name = "account")
@Data
public class Account {

    private Integer accountId;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer accountNumber;

    private String accountName;

    private Date creationDate;

    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    private Boolean isCustomerActive;

    private Double accountBalance;

    public Account(AccountDTO accountDTO) {
        this.accountId = accountDTO.getAccountId();
        this.accountNumber = accountDTO.getAccountNumber();
        this.accountName = accountDTO.getAccountName();
        this.creationDate = accountDTO.getCreationDate();
        this.accountType = accountDTO.getAccountType();
        this.isCustomerActive = accountDTO.getIsCustomerActive();
        this.accountBalance = accountDTO.getAccountBalance();
    }
}