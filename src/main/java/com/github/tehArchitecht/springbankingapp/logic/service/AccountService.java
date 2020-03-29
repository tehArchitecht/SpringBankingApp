package com.github.tehArchitecht.springbankingapp.logic.service;

import com.github.tehArchitecht.springbankingapp.data.model.Account;
import com.github.tehArchitecht.springbankingapp.data.model.User;
import com.github.tehArchitecht.springbankingapp.data.repository.AccountRepository;
import com.github.tehArchitecht.springbankingapp.data.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    AccountService(AccountRepository accountRepository, UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    void add(Account account) {
        Account saved = accountRepository.save(account);

        User user = account.getUser();
        if (user.getPrimaryAccount() == null) {
            userRepository.setPrimaryAccountIdById(user.getId(), saved.getId());
        }
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
        Optional<User> optional = userRepository.findById(userId);
        return optional.isPresent()
                ? Optional.of(optional.get().getPrimaryAccount())
                : Optional.empty();
    }
}
