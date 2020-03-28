package com.github.tehArchitecht.springbankingapp.logic.service;

import com.github.tehArchitecht.springbankingapp.data.model.Account;
import com.github.tehArchitecht.springbankingapp.data.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AccountService {
    private final AccountRepository accountRepository;

    AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    void add(Account account) {
        accountRepository.save(account);
    }

    Optional<Account> get(UUID accountId) {
        return accountRepository.findById(accountId);
    }

    void setBalance(UUID accountId, BigDecimal balance) {
        accountRepository.setAccountBalanceById(accountId, balance);
    }

    List<Account> getUserAccounts(Long userId) {
        return accountRepository.findAllByUserId(userId);
    }

    int countUserAccounts(Long userId) {
        return accountRepository.countByUserId(userId);
    }

    Optional<Account> getUserPrimaryAccount(Long userId) {
        List<Account> accounts = accountRepository.findAllByUserId(userId);
        return accounts.stream().min(Comparator.comparing(Account::getNumber));
    }
}
