package com.example.demo.model;


import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@NoArgsConstructor
@Document(collection = "customer")
@Data
public class Customer {

    @Id
    Integer customerId;
    String customerName;
    Date creationDate;
    CustomerType customerType;
    boolean isActive;

    public Customer(CustomerDTO customerDTO) {
        this.customerId = customerDTO.getCustomerId();
        this.customerName = customerDTO.getCustomerName();
        this.creationDate = customerDTO.getCreationDate();
        this.customerType = customerDTO.getCustomerType();
        this.isActive = customerDTO.isActive();
    }
}