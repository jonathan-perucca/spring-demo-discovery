package com.example.account.web;

import com.example.account.model.Account;
import com.example.account.service.AccountService;
import com.example.exceptions.AccountEmptyException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AccountControllerTest {

    @InjectMocks    AccountController accountController;
    @Mock           AccountService accountService;

    @Test
    public void should_get_list_of_3_accounts() {
        accountController.getAccounts();

        verify(accountService).getAccounts();
    }

    @Test
    public void should_create_new_account() {
        final int balance = 15;
        final Account account = Account.builder().balance(balance).build();

        accountController.registerAccount(account);

        verify(accountService).updateAccount(account);
    }

    @Test(expected = AccountEmptyException.class)
    public void should_not_create_new_account_when_account_is_empty() {
        final Account account = Account.builder().balance(0).build();

        accountController.registerAccount(account);
    }
}
