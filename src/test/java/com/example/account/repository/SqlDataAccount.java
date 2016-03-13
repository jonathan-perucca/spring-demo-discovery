package com.example.account.repository;

import org.springframework.test.context.jdbc.Sql;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Sql(
        statements = {
                "insert into Account (uuid, balance) values ('abc-123', 30)",
                "insert into Account (uuid, balance) values ('abc-523', 50)",
                "insert into Account (uuid, balance) values ('abc-643', 90)"
        },
        executionPhase = BEFORE_TEST_METHOD
)
@Sql(
        statements = {
                "delete from Account"
        },
        executionPhase = AFTER_TEST_METHOD
)
public @interface SqlDataAccount {
}
