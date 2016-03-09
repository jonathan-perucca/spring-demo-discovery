package com.example.account.service;

import com.example.exceptions.CreditNotAuthorizedException;
import com.example.exceptions.NotAuthorizedException;
import com.example.account.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.util.Assert.notNull;

@Service
public class AccountService {

    private List<Account> accounts = new ArrayList<>();


    private final AuthorizationService authorizationService;

    @Autowired
    public AccountService(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
        accounts.add(Account.builder().balance(10).build());
        accounts.add(Account.builder().balance(20).build());
        accounts.add(Account.builder().balance(50).build());
    }

    public List<Account> getAccounts() {
        return this.accounts;
    }

    public boolean addAccount(Account account) {
        return accounts.add(account);
    }

    public void updateBalance(Account account, int amount) {
        validations(account, amount);

        if(!authorizationService.isAllowed(account)) {
            throw new NotAuthorizedException();
        }

        account.setBalance(account.getBalance() + amount);
    }

    private void validations(Account account, int amount) {
        notNull(account);
        if(account.wouldNeedCredit(amount))
            throw new CreditNotAuthorizedException();
    }
}
