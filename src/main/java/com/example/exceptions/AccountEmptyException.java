package com.example.exceptions;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ResponseStatus(BAD_REQUEST)
public class AccountEmptyException extends RuntimeException {

    public AccountEmptyException(String message) {
        super(message);
    }
}
