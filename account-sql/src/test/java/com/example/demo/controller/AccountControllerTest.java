package com.example.demo.controller;

import com.example.demo.exception.AccountNotFoundException;
import com.example.demo.exception.CustomerNotActiveException;
import com.example.demo.model.Account;
import com.example.demo.model.AccountDTO;
import com.example.demo.model.AccountType;
import com.example.demo.service.AccountService;
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
@WebMvcTest(AccountController.class)
public class AccountControllerTest {

    @MockBean
    AccountService accountService;

    @Autowired
    MockMvc mockMvc;

    @Test
    public void testCreateAccount() throws Exception {
        Mockito.when(accountService.createAccount(Mockito.any(Account.class)))
                .thenReturn(createAccountResponse());

        mockMvc.perform(post("/account/create")
                        .content(TestUtil.asJsonString(createAccountForPost()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accountNumber", Matchers.is(4000)));
    }

    @Test
    public void testCreateAccountInactive() throws Exception {
        AccountDTO accountForPost = createAccountForPost();
        accountForPost.setIsCustomerActive(Boolean.FALSE);

        Mockito.when(accountService.createAccount(Mockito.any(Account.class)))
                .thenThrow(new CustomerNotActiveException("customer not active for the account"));

        mockMvc.perform(post("/account/create")
                        .content(TestUtil.asJsonString(accountForPost))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isExpectationFailed())
                .andExpect(result -> assertTrue(
                        result.getResolvedException() instanceof CustomerNotActiveException));
    }

    @Test
    public void testCreateAccountInvalidFields() throws Exception {
        AccountDTO accountForPost = createAccountForPost();
        accountForPost.setAccountName("");
        accountForPost.setAccountType(null);

        mockMvc.perform(post("/account/create")
                        .content(TestUtil.asJsonString(accountForPost))
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
    public void testGetAllAccounts() throws Exception {
        Mockito.when(accountService.getAllAccounts()).thenReturn(createAccountList());

        mockMvc.perform(get("/account/accounts"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(3)))
                .andExpect(jsonPath("$[0].accountId", Matchers.is(1)))
                .andExpect(jsonPath("$[0].accountNumber", Matchers.is(1000)))
                .andExpect(jsonPath("$[0].accountName", Matchers.is("account-test-1")))
                .andExpect(jsonPath("$[1].accountId", Matchers.is(20)))
                .andExpect(jsonPath("$[1].accountNumber", Matchers.is(2000)))
                .andExpect(jsonPath("$[1].accountName", Matchers.is("account-test-2")))
                .andExpect(jsonPath("$[2].accountId", Matchers.is(20)))
                .andExpect(jsonPath("$[2].accountNumber", Matchers.is(2001)))
                .andExpect(jsonPath("$[2].accountName", Matchers.is("account-test-3")));
    }

    @Test
    public void testGetAllAccountsEmpty() throws Exception {
        Mockito.when(accountService.getAllAccounts()).thenReturn(createEmptyAccountList());

        mockMvc.perform(get("/account/accounts"))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$", Matchers.hasSize(0)));
    }

    @Test
    public void testGetAccountsById() throws Exception {
        ResponseEntity<List<AccountDTO>> allAccountsOfOneCustomer = getAllAccountsOfCustomer();

        Mockito.when(accountService.getAccountsById(20)).thenReturn(allAccountsOfOneCustomer);

        mockMvc.perform(get("/account/accounts/20"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(2)))
                .andExpect(jsonPath("$[0].accountId", Matchers.is(20)))
                .andExpect(jsonPath("$[0].accountName", Matchers.is("account-test-2")))
                .andExpect(jsonPath("$[0].accountType", Matchers.is("CASH")))
                .andExpect(jsonPath("$[1].accountId", Matchers.is(20)))
                .andExpect(jsonPath("$[1].accountName", Matchers.is("account-test-3")))
                .andExpect(jsonPath("$[1].accountType", Matchers.is("CURRENT")));
    }

    @Test
    public void testGetAccountsByIdException() throws Exception {
        Mockito.when(accountService.getAccountsById(30)).
                thenThrow(new AccountNotFoundException("no account found for id - 30"));

        mockMvc.perform(get("/account/accounts/30"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(
                        result.getResolvedException() instanceof AccountNotFoundException));
    }

    private AccountDTO createAccountForPost() {
        return createAccountDTOMin();
    }

    private ResponseEntity<AccountDTO> createAccountResponse() {
        AccountDTO accountDTO = createAccountDTOMin();
        accountDTO.setAccountNumber(4000);
        accountDTO.setCreationDate(new Date());
        return new ResponseEntity<>(accountDTO, HttpStatus.CREATED);
    }

    private AccountDTO createAccountDTOMin() {
        AccountDTO account = new AccountDTO();
        account.setAccountId(40);
        account.setAccountName("account-test-5");
        account.setAccountBalance(8000.0);
        account.setAccountType(AccountType.CASH);
        account.setIsCustomerActive(Boolean.TRUE);
        return account;
    }

    private ResponseEntity<List<AccountDTO>> getAllAccountsOfCustomer() {
        List<AccountDTO> accountDTOs = new ArrayList<>();

        AccountDTO accountDTO1 = new AccountDTO();
        accountDTO1.setAccountId(20);
        accountDTO1.setAccountName("account-test-2");
        accountDTO1.setAccountType(AccountType.CASH);
        accountDTO1.setAccountNumber(2000);
        accountDTO1.setAccountBalance(8000.0);
        accountDTO1.setIsCustomerActive(Boolean.TRUE);
        accountDTOs.add(accountDTO1);

        AccountDTO accountDTO2 = new AccountDTO();
        accountDTO2.setAccountId(20);
        accountDTO2.setAccountName("account-test-3");
        accountDTO2.setAccountType(AccountType.CURRENT);
        accountDTO2.setAccountNumber(2001);
        accountDTO2.setAccountBalance(11000.0);
        accountDTO2.setIsCustomerActive(Boolean.TRUE);
        accountDTOs.add(accountDTO2);

        return new ResponseEntity<>(accountDTOs, HttpStatus.OK);
    }

    private ResponseEntity<List<AccountDTO>> createEmptyAccountList() {
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NO_CONTENT);
    }

    private ResponseEntity<List<AccountDTO>> createAccountList() {
        List<AccountDTO> accountDTOs = new ArrayList<>();
        AccountDTO accountDTO1 = new AccountDTO();
        accountDTO1.setAccountId(1);
        accountDTO1.setAccountName("account-test-1");
        accountDTO1.setAccountType(AccountType.CASH);
        accountDTO1.setAccountNumber(1000);
        accountDTO1.setAccountBalance(5000.0);
        accountDTO1.setIsCustomerActive(Boolean.TRUE);
        accountDTOs.add(accountDTO1);

        AccountDTO accountDTO2 = new AccountDTO();
        accountDTO2.setAccountId(20);
        accountDTO2.setAccountName("account-test-2");
        accountDTO2.setAccountType(AccountType.CASH);
        accountDTO2.setAccountNumber(2000);
        accountDTO2.setAccountBalance(8000.0);
        accountDTO2.setIsCustomerActive(Boolean.TRUE);
        accountDTOs.add(accountDTO2);

        AccountDTO accountDTO3 = new AccountDTO();
        accountDTO3.setAccountId(20);
        accountDTO3.setAccountName("account-test-3");
        accountDTO3.setAccountType(AccountType.CURRENT);
        accountDTO3.setAccountNumber(2001);
        accountDTO3.setAccountBalance(11000.0);
        accountDTO3.setIsCustomerActive(Boolean.TRUE);
        accountDTOs.add(accountDTO3);

        return new ResponseEntity<>(accountDTOs, HttpStatus.OK);
    }
}