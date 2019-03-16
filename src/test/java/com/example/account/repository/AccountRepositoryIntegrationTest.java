package com.example.account.repository;

import com.example.account.model.Account;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringRunner.class)
@SpringBootTest
@SqlDataAccount
@Transactional
public class AccountRepositoryIntegrationTest {

    @Autowired AccountRepository accountRepository;
    @Autowired EntityManager entityManager;

    @Test
    public void should_find_all_accounts() {
        assertThat( accountRepository.findAll() ).hasSize(3);
    }

    @Test
    public void should_find_by_uuid() {
        Account account = accountRepository.getOne("abc-123");

        assertThat(account).isNotNull();
        assertThat(account.getBalance()).isEqualTo(30);
    }

    @Test
    public void should_find_by_balance() {
        final List<Account> accounts = accountRepository.findByBalance(50);

        assertThat(accounts).hasSize(1);
        assertThat(accounts.get(0).getUuid()).isEqualTo("abc-523");
    }

    @Test
    public void should_find_from_balance() {
        final List<Account> accounts = accountRepository.findFromBalance(50);

        assertThat(accounts).hasSize(1);
        assertThat(accounts.get(0).getUuid()).isEqualTo("abc-523");
    }

    @Test
    public void should_find_page_accounts() {
        final int page = 0;
        final int size = 2;

        final Page<Account> firstPage = accountRepository.findAllBy(new PageRequest(page, size));
        final Page<Account> secondPage = accountRepository.findAllBy(firstPage.nextPageable());

        assertThat(firstPage.getTotalElements()).isEqualTo(3L);
        assertThat(firstPage.getTotalPages()).isEqualTo(2);

        assertThat(firstPage.getContent().get(0).getBalance()).isEqualTo(30);
        assertThat(firstPage.hasNext()).isTrue();
        assertThat(secondPage.getContent().get(0).getBalance()).isEqualTo(90);
        assertThat(secondPage.hasNext()).isFalse();
    }

    @Test
    public void should_async_find_by_uuid() throws ExecutionException, InterruptedException {
        accountRepository.findByUuid("abc-523")

         .thenAccept(account -> assertThat(account.getBalance()).isEqualTo(50)).get();
    }

    @Test
    public void should_create_new_account() {
        accountRepository.saveAndFlush(Account.builder().balance(100).build());

        final List<Account> accounts = accountRepository.findAll();

        assertThat(accounts).hasSize(4);
        accounts.forEach(account -> assertThat(account.getUuid()).isNotNull());
    }

    @Test
    public void should_update_account() {
        final Account account = accountRepository.findAll().get(0);
        final int newBalance = 1000;

        account.setBalance(newBalance);
        accountRepository.saveAndFlush(account);
        entityManager.clear();

        assertThat(entityManager.contains(account)).isFalse();
        assertThat(accountRepository.getOne(account.getUuid()).getBalance()).isEqualTo(newBalance);
    }
}
