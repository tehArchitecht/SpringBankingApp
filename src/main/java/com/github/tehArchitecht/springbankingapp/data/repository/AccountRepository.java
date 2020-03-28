package com.github.tehArchitecht.springbankingapp.data.repository;

import com.github.tehArchitecht.springbankingapp.data.model.Account;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface AccountRepository extends CrudRepository<Account, UUID> {
    List<Account> findAllByUserId(Long userId);
    Integer countByUserId(Long userId);

    @Modifying
    @Transactional
    @Query("UPDATE Account SET balance=?2 where id=?1")
    void setAccountBalanceById(UUID accountId, BigDecimal balance);
}
