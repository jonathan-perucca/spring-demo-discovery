package com.example.account.service;

import com.example.account.model.Account;

public interface AuthorizationService {
    boolean isAllowed(Account account);
}
