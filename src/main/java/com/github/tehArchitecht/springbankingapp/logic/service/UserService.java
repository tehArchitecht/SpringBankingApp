package com.github.tehArchitecht.springbankingapp.logic.service;

import com.github.tehArchitecht.springbankingapp.data.model.User;
import com.github.tehArchitecht.springbankingapp.data.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;

    UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    void add(User user) {
        userRepository.save(user);
    }

    boolean isNameInUse(String name) {
        return userRepository.existsByName(name);
    }

    boolean isPhoneNumberInUse(String phoneNumber) {
        return userRepository.existsByPhoneNumber(phoneNumber);
    }

    Optional<User> getByName(String name) {
        return userRepository.findByName(name);
    }

    Optional<User> getByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber);
    }

    void setPrimaryAccountId(Long userId, UUID primaryAccountId) {
        userRepository.setPrimaryAccountIdById(userId, primaryAccountId);
    }
}
