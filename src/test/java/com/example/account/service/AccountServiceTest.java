package com.example.account.service;

import com.example.account.model.Account;
import com.example.account.repository.AccountRepository;
import com.example.exceptions.CreditNotAuthorizedException;
import com.example.exceptions.NotAuthorizedException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AccountServiceTest {

    @InjectMocks    private AccountService accountService;

    @Mock           private AuthorizationService mockAuthorizationService;
    @Mock           private AccountRepository accountRepository;

    private Account account = new Account();

    @Before
    public void setup() {
        when(mockAuthorizationService.isAllowed(any())).thenReturn(true);
    }

    @Test
    public void should_get_accounts_nominal() {
        accountService.getAccounts();

        verify(accountRepository).findAll();
    }

    @Test
    public void should_add_money_nominal() {
        account.setBalance(10);

        accountService.updateBalance(account, 40);

        assertThat(account.getBalance(), is(50));
    }

    @Test
    public void should_remove_money_nominal() {
        account.setBalance(100);

        accountService.updateBalance(account, -30);

        assertThat(account.getBalance(), is(70));
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_throw_exception_when_account_is_null() {
        accountService.updateBalance(null, 10);
    }

    @Test(expected = CreditNotAuthorizedException.class)
    public void should_throw_credit_not_authorized_when_account_under_zero() {
        account.setBalance(10);

        accountService.updateBalance(account, -50);
    }

    @Test(expected = NotAuthorizedException.class)
    public void should_throw_not_authorized_when_authentication_service_not_allowed() {
        when(mockAuthorizationService.isAllowed(any())).thenReturn(false);

        accountService.updateBalance(account, 30);
    }
}
