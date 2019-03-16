package com.example.account.web;

import com.example.AbstractBigTest;
import com.example.account.model.Account;
import com.example.account.repository.SqlDataAccount;
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
    public void should_create_new_account() {
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
    public void should_not_create_new_account_when_account_is_empty() {
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
            .body("message", is("Account cannot be empty"))
            .body("path", is("/accounts"));
    }
}
