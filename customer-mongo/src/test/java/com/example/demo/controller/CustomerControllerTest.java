package com.example.demo.controller;

import com.example.demo.exception.CustomerAlreadyExistsException;
import com.example.demo.exception.CustomerNotActiveException;
import com.example.demo.exception.CustomerNotFoundException;
import com.example.demo.model.*;
import com.example.demo.service.CustomerService;
import com.example.demo.util.TestUtil;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@WebMvcTest(CustomerController.class)
public class CustomerControllerTest {

    @MockBean
    CustomerService customerService;

    @Autowired
    MockMvc mockMvc;

    @Test
    public void testCreateCustomer() throws Exception {
        Mockito.when(customerService.createCustomer(Mockito.any(Customer.class)))
                .thenReturn(createCustomerAccountResponse());

        mockMvc.perform(post("/customer/create")
                        .content(TestUtil.asJsonString(createCustomerForPost()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customer.customerId", Matchers.is(40)))
                .andExpect(jsonPath("$.accounts", Matchers.hasSize(1)))
                .andExpect(jsonPath("$.accounts[0].accountId", Matchers.is(40)))
                .andExpect(jsonPath("$.accounts[0].accountType", Matchers.is("CASH")));
    }

    @Test
    public void testCreateCustomerInactive() throws Exception {
        CustomerDTO customerForPost = createCustomerForPost();
        customerForPost.setActive(false);

        Mockito.when(customerService.createCustomer(Mockito.any(Customer.class)))
                .thenThrow(new CustomerNotActiveException("cannot create customer that is not active"));

        mockMvc.perform(post("/customer/create")
                        .content(TestUtil.asJsonString(customerForPost))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isExpectationFailed())
                .andExpect(result -> assertTrue(
                        result.getResolvedException() instanceof CustomerNotActiveException));
    }

    @Test
    public void testCreateCustomerDuplicate() throws Exception {
        CustomerDTO customerForPost = createCustomerForPost();

        Mockito.when(customerService.createCustomer(Mockito.any(Customer.class)))
                .thenThrow(new CustomerAlreadyExistsException("customer already exists with given customer id"));

        mockMvc.perform(post("/customer/create")
                        .content(TestUtil.asJsonString(customerForPost))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isAlreadyReported())
                .andExpect(result -> assertTrue(
                        result.getResolvedException() instanceof CustomerAlreadyExistsException));
    }

    @Test
    public void testCreateCustomerInvalidFields() throws Exception {
        CustomerDTO customerForPost = createCustomerForPost();
        customerForPost.setCustomerName("");
        customerForPost.setCustomerType(null);

        mockMvc.perform(post("/customer/create")
                        .content(TestUtil.asJsonString(customerForPost))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException);
                    assertEquals(((MethodArgumentNotValidException) result.getResolvedException())
                            .getAllErrors().size(), 2);
                });
    }

    @Test
    public void testGetAllCustomers() throws Exception {
        Mockito.when(customerService.getAllCustomers()).thenReturn(createCustomerList());

        mockMvc.perform(get("/customer/customers"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(3)))
                .andExpect(jsonPath("$[0].customerId", Matchers.is(1)))
                .andExpect(jsonPath("$[0].customerName", Matchers.is("customer-test-1")))
                .andExpect(jsonPath("$[1].customerId", Matchers.is(20)))
                .andExpect(jsonPath("$[1].customerName", Matchers.is("customer-test-2")))
                .andExpect(jsonPath("$[2].customerId", Matchers.is(25)))
                .andExpect(jsonPath("$[2].customerName", Matchers.is("customer-test-3")));
    }

    @Test
    public void testGetAllCustomersEmpty() throws Exception {
        Mockito.when(customerService.getAllCustomers()).thenReturn(createEmptyCustomerList());

        mockMvc.perform(get("/customer/customers"))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$", Matchers.hasSize(0)));
    }

    @Test
    public void testGetCustomerById() throws Exception {
        ResponseEntity<CustomerAccountResponse> allCustomersOfOneCustomer
                = generateCustomerAccountResponse();

        Mockito.when(customerService.getCustomerById(20)).thenReturn(allCustomersOfOneCustomer);

        mockMvc.perform(get("/customer/customers/20"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customer.customerId", Matchers.is(20)))
                .andExpect(jsonPath("$.customer.customerName", Matchers.is("customer-test-4")))
                .andExpect(jsonPath("$.accounts", Matchers.hasSize(2)))
                .andExpect(jsonPath("$.accounts[0].accountId", Matchers.is(20)))
                .andExpect(jsonPath("$.accounts[0].accountType", Matchers.is("CASH")))
                .andExpect(jsonPath("$.accounts[1].accountId", Matchers.is(20)))
                .andExpect(jsonPath("$.accounts[1].accountType", Matchers.is("CURRENT")));
    }

    @Test
    public void testGetCustomerByIdException() throws Exception {
        Mockito.when(customerService.getCustomerById(30)).
                thenThrow(new CustomerNotFoundException("customer not found for the id - 30"));

        mockMvc.perform(get("/customer/customers/30"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(
                        result.getResolvedException() instanceof CustomerNotFoundException));
    }

    private CustomerDTO createCustomerForPost() {
        return createCustomerDTOMin();
    }

    private ResponseEntity<CustomerAccountResponse> createCustomerAccountResponse() {
        CustomerDTO customerDTO = createCustomerDTOMin();
        customerDTO.setCreationDate(new Date());

        CustomerAccountResponse customerAccountResponse = new CustomerAccountResponse();
        customerAccountResponse.setCustomer(customerDTO);
        customerAccountResponse.setAccounts(Arrays.asList(
                new AccountDTO(customerDTO.getCustomerId(),
                        customerDTO.getCustomerName() + "-account-cash",
                        new Date(), AccountType.CASH, Boolean.TRUE, 5000.0)));
        return new ResponseEntity<>(customerAccountResponse, HttpStatus.CREATED);
    }

    private CustomerDTO createCustomerDTOMin() {
        CustomerDTO customer = new CustomerDTO();
        customer.setCustomerId(40);
        customer.setCustomerName("customer-test-5");
        customer.setCustomerType(CustomerType.INDIVIDUAL);
        customer.setActive(true);
        return customer;
    }

    private ResponseEntity<CustomerAccountResponse> generateCustomerAccountResponse() {
        CustomerAccountResponse customerAccountResponse = new CustomerAccountResponse();

        CustomerDTO customerDTO1 = new CustomerDTO();
        customerDTO1.setCustomerId(20);
        customerDTO1.setCustomerName("customer-test-4");
        customerDTO1.setCustomerType(CustomerType.INDIVIDUAL);
        customerDTO1.setActive(true);
        customerAccountResponse.setCustomer(customerDTO1);

        List<AccountDTO> accountDTOS = new ArrayList<>();
        accountDTOS.add(new AccountDTO(20,
                customerDTO1.getCustomerName() + "-account-cash",
                null, AccountType.CASH, Boolean.TRUE, 8000.0));
        accountDTOS.add(new AccountDTO(20,
                customerDTO1.getCustomerName() + "-account-current",
                null, AccountType.CURRENT, Boolean.TRUE, 20000.0));
        customerAccountResponse.setAccounts(accountDTOS);

        return new ResponseEntity<>(customerAccountResponse, HttpStatus.OK);
    }

    private ResponseEntity<List<CustomerDTO>> createEmptyCustomerList() {
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NO_CONTENT);
    }

    private ResponseEntity<List<CustomerDTO>> createCustomerList() {
        List<CustomerDTO> customerDTOs = new ArrayList<>();
        CustomerDTO customerDTO1 = new CustomerDTO();
        customerDTO1.setCustomerId(1);
        customerDTO1.setCustomerName("customer-test-1");
        customerDTO1.setCustomerType(CustomerType.INDIVIDUAL);
        customerDTO1.setActive(true);
        customerDTOs.add(customerDTO1);

        CustomerDTO customerDTO2 = new CustomerDTO();
        customerDTO2.setCustomerId(20);
        customerDTO2.setCustomerName("customer-test-2");
        customerDTO2.setCustomerType(CustomerType.INDIVIDUAL);
        customerDTO2.setActive(true);
        customerDTOs.add(customerDTO2);

        CustomerDTO customerDTO3 = new CustomerDTO();
        customerDTO3.setCustomerId(25);
        customerDTO3.setCustomerName("customer-test-3");
        customerDTO3.setCustomerType(CustomerType.INDIVIDUAL);
        customerDTO3.setActive(true);
        customerDTOs.add(customerDTO3);

        return new ResponseEntity<>(customerDTOs, HttpStatus.OK);
    }
}