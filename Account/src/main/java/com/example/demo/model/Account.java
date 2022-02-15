package com.example.demo.model;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name="account")
@Data
public class Account {

    @Id
    Integer accountId;
    String accountName;
    Date creationDate;
    Double accountBalance;

    public Account(Integer accountId, String accountName, Date creationDate, Double accountBalance) {
        this.accountId = accountId;
        this.accountName = accountName;
        this.creationDate = creationDate;
        this.accountBalance = accountBalance;
    }

    public Account() {

    }
}
