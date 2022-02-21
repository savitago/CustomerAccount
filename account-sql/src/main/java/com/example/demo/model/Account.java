package com.example.demo.model;

import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.Date;

@NoArgsConstructor
@Entity
@Table(name="account")
@Data
public class Account {

    @NotNull(message = "account id cannot be null")
    private Integer accountId;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer accountNumber;

    @NotBlank(message="account name cannot be empty")
    private String accountName;

    private Date creationDate;

    @NotNull(message = "account type cannot be null")
    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    @NotNull(message = "customer active status should be known")
    private Boolean isCustomerActive;

    @Min(2000)
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
}