package com.github.tehArchitecht.springbankingapp.data.repository;

import com.github.tehArchitecht.springbankingapp.data.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByName(String name);
    Optional<User> findByPhoneNumber(String PhoneNumber);

    boolean existsByName(String name);
    boolean existsByPhoneNumber(String phoneNumber);
}
