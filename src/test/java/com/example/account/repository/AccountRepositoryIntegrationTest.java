package com.example.account.repository;

import com.example.DemoApplication;
import com.example.SqlDataAccount;
import com.example.account.model.Account;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DemoApplication.class)

@SqlDataAccount
public class AccountRepositoryIntegrationTest {

    @Autowired AccountRepository accountRepository;
    @Autowired EntityManager entityManager;

    @Test
    public void should_find_all_accounts() {
        assertThat(accountRepository.findAll(), hasSize(3));
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
        final int size = 2;

        final Page<Account> firstPage = accountRepository.findAllBy(new PageRequest(page, size));
        final Page<Account> secondPage = accountRepository.findAllBy(firstPage.nextPageable());

        assertThat(firstPage.getTotalElements(), is(3L));
        assertThat(firstPage.getTotalPages(), is(2));

        assertThat(firstPage.getContent().get(0).getBalance(), is(30));
        assertThat(firstPage.hasNext(), is(true));
        assertThat(secondPage.getContent().get(0).getBalance(), is(90));
        assertThat(secondPage.hasNext(), is(false));
    }

    @Test
    public void should_async_find_by_uuid() throws ExecutionException, InterruptedException {
        accountRepository.findByUuid("abc-523")

         .thenAccept(account -> assertThat(account.getBalance(), is(50))).get();
    }

    @Test
    public void should_create_new_account() {
        accountRepository.saveAndFlush(Account.builder().balance(100).build());

        final List<Account> accounts = accountRepository.findAll();

        assertThat(accounts.size(), is(4));
        accounts.forEach(account -> assertThat(account.getUuid(), notNullValue()));
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
