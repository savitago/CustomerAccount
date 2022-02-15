package com.example.demo.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection="customer")
@Data
public class Customer {

    @Id
    Integer customerId;
    String customerName;
    Date creationDate;
    boolean isActive;

    public Customer(Integer customerId, String customerName, Date creationDate, boolean isActive) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.creationDate = creationDate;
        this.isActive = isActive;
    }

    public Customer()
    {

    }
}
