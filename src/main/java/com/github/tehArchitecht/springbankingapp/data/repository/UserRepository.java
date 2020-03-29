package com.github.tehArchitecht.springbankingapp.data.repository;

import com.github.tehArchitecht.springbankingapp.data.model.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByName(String name);
    Optional<User> findByPhoneNumber(String PhoneNumber);

    boolean existsByName(String name);
    boolean existsByPhoneNumber(String phoneNumber);

    @Modifying
    @Transactional
    @Query("UPDATE User SET primary_account_id=?2 where id=?1")
    void setPrimaryAccountIdById(Long id, UUID primaryAccountId);
}
