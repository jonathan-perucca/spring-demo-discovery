package com.example.account.web;

import com.example.account.service.AccountService;
import com.example.exceptions.AccountEmptyException;
import com.example.account.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @RequestMapping(method = GET)
    public List<Account> getAccounts() {
        return accountService.getAccounts();
    }

    @RequestMapping(method = POST)
    @ResponseStatus(CREATED)
    public Account registerAccount(@RequestBody Account account) {
        if(account.getBalance() == 0)
            throw new AccountEmptyException("Account cannot be empty");

        accountService.updateAccount(account);

        return account;
    }
}
