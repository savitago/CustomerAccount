package com.example.demo.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@NoArgsConstructor
@Data
public class AccountDTO {
    @NotNull(message = "account id cannot be null")
    private Integer accountId;

    private Integer accountNumber;

    @NotBlank(message = "account name cannot be empty")
    private String accountName;

    private Date creationDate;

    @NotNull(message = "account type cannot be null")
    private AccountType accountType;

    @NotNull(message = "customer active status should be known")
    private Boolean isCustomerActive;

    @Min(2000)
    private Double accountBalance;

    public AccountDTO(Account account) {
        this.accountId = account.getAccountId();
        this.accountNumber = account.getAccountNumber();
        this.accountName = account.getAccountName();
        this.creationDate = account.getCreationDate();
        this.accountType = account.getAccountType();
        this.isCustomerActive = account.getIsCustomerActive();
        this.accountBalance = account.getAccountBalance();
    }
}