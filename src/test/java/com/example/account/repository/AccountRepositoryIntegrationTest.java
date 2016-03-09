package com.example.account.repository;

import com.example.DemoApplication;
import com.example.account.model.Account;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.example.AsyncAsserts.asyncAssert;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DemoApplication.class)

@Sql(
        statements = {
                "insert into Account (uuid, balance) values ('abc-123', 30)",
                "insert into Account (uuid, balance) values ('abc-523', 50)"
        },
        executionPhase = BEFORE_TEST_METHOD
)
@Sql(
        statements = {
                "delete from Account"
        },
        executionPhase = AFTER_TEST_METHOD
)
public class AccountRepositoryIntegrationTest {

    private static final Logger LOGGER = getLogger(AccountRepositoryIntegrationTest.class);

    @Autowired AccountRepository accountRepository;
    @Autowired EntityManager entityManager;

    @Test
    public void should_find_all_accounts() {
        assertThat(accountRepository.findAll(), hasSize(2));
    }

    @Test
    public void should_find_by_uuid() {
        final Account account = accountRepository.findOne("abc-123");

        assertThat(account, notNullValue());
        assertThat(account.getBalance(), is(30));
    }

    @Test
    public void should_find_by_balance() {
        final List<Account> accounts = accountRepository.findByBalance(50);

        assertThat(accounts, hasSize(1));
        assertThat(accounts.get(0).getUuid(), is("abc-523"));
    }

    @Test
    public void should_find_from_balance() {
        final List<Account> accounts = accountRepository.findFromBalance(50);

        assertThat(accounts, hasSize(1));
        assertThat(accounts.get(0).getUuid(), is("abc-523"));
    }

    @Test
    public void should_find_page_accounts() {
        final int page = 0;
        final int size = 1;

        final Page<Account> firstPage = accountRepository.findAllBy(new PageRequest(page, size));
        final Page<Account> secondPage = accountRepository.findAllBy(firstPage.nextPageable());

        assertThat(firstPage.getTotalElements(), is(2L));
        assertThat(firstPage.getTotalPages(), is(2));

        assertThat(firstPage.getContent().get(0).getBalance(), is(30));
        assertThat(firstPage.hasNext(), is(true));
        assertThat(secondPage.getContent().get(0).getBalance(), is(50));
        assertThat(secondPage.hasNext(), is(false));
    }

    @Test
    public void should_async_find_by_uuid() throws ExecutionException, InterruptedException {
        asyncAssert(
                accountRepository.findByUuid("abc-523"),
                account -> assertThat(account.getBalance(), is(50))
        );
    }

    @Test
    public void should_create_new_account() {
        accountRepository.save(Account.builder().balance(100).build());

        assertThat(accountRepository.findAll().size(), is(3));
    }

    @Transactional
    @Test
    public void should_update_account() {
        final Account account = accountRepository.findAll().get(0);
        final int newBalance = 1000;

        account.setBalance(newBalance);
        accountRepository.saveAndFlush(account);
        entityManager.clear();

        assertThat(entityManager.contains(account), is(false));
        assertThat(accountRepository.getOne(account.getUuid()).getBalance(), is(newBalance));
    }
}
