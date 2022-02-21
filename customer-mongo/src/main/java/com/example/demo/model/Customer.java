package com.example.demo.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@NoArgsConstructor
@Document(collection="customer")
@Data
public class Customer {

    @Id
    @NotNull(message = "customer id cannot be null")
    Integer customerId;

    @NotBlank(message="customer name cannot be empty")
    String customerName;

    Date creationDate;

    @NotNull(message = "customer type cannot be null")
    CustomerType customerType;

    boolean isActive;

    public Customer(Integer customerId, String customerName,
                    Date creationDate, CustomerType customerType, boolean isActive) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.creationDate = creationDate;
        this.customerType = customerType;
        this.isActive = isActive;
    }
}