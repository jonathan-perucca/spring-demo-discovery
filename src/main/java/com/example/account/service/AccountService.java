package com.example.account.service;

import com.example.account.model.Account;
import com.example.account.repository.AccountRepository;
import com.example.exceptions.CreditNotAuthorizedException;
import com.example.exceptions.NotAuthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.util.Assert.notNull;

@Service
public class AccountService {

    private final AuthorizationService authorizationService;
    private final AccountRepository accountRepository;

    @Autowired
    public AccountService(AuthorizationService authorizationService,
                          AccountRepository accountRepository) {
        this.authorizationService = authorizationService;
        this.accountRepository = accountRepository;
    }

    @Transactional(readOnly = true)
    public List<Account> getAccounts() {
        return accountRepository.findAll();
    }

    @Transactional
    public boolean updateAccount(Account account) {
        accountRepository.save(account);

        return true;
    }

    @Transactional
    public void updateBalance(Account account, int amount) {
        validations(account, amount);

        if(!authorizationService.isAllowed(account)) {
            throw new NotAuthorizedException();
        }

        account.setBalance(account.getBalance() + amount);
        updateAccount(account);
    }

    private void validations(Account account, int amount) {
        notNull(account);
        if(account.wouldNeedCredit(amount))
            throw new CreditNotAuthorizedException();
    }
}
