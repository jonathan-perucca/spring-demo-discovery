package com.example.account.web;

import com.example.AbstractBigTest;
import com.example.SqlDataAccount;
import com.example.account.model.Account;
import com.example.exceptions.AccountEmptyException;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;
import static com.jayway.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.springframework.http.HttpStatus.*;

@SqlDataAccount
public class AccountControllerBigTest extends AbstractBigTest {

    @Test
    public void should_get_list_of_3_accounts() {
        when()
            .get("/accounts")
        .then()
            .log().all()
            .statusCode(OK.value())
            .body("$", hasSize(3));
    }

    @Test
    public void should_create_new_account() throws JsonProcessingException {
        final int balance = 15;
        final Account account = Account.builder().balance(balance).build();

        given()
            .contentType(JSON)
            .body(toJson(account))
        .when()
            .post("/accounts")
        .then()
            .log().all()
            .statusCode(CREATED.value())
            .body("balance", is(balance));
    }

    @Test
    public void should_not_create_new_account_when_account_is_empty() throws JsonProcessingException {
        final Account account = Account.builder().balance(0).build();

        given()
            .contentType(JSON)
            .body(toJson(account))
        .when()
            .post("/accounts")
        .then()
            .log().all()
            .statusCode(BAD_REQUEST.value())
            .body("error", is(BAD_REQUEST.getReasonPhrase()))
            .body("exception", is(AccountEmptyException.class.getName()))
            .body("path", is("/accounts"));
    }
}
