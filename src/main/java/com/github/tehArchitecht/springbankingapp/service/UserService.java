package com.github.tehArchitecht.springbankingapp.service;

import com.github.tehArchitecht.springbankingapp.data.model.User;
import com.github.tehArchitecht.springbankingapp.data.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void add(User user) {
        userRepository.save(user);
    }

    public boolean isNameInUse(String name) {
        return userRepository.existsByName(name);
    }

    public boolean isPhoneNumberInUse(String phoneNumber) {
        return userRepository.existsByPhoneNumber(phoneNumber);
    }

    public Optional<User> getByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber);
    }

    public void setPrimaryAccountId(Long userId, UUID primaryAccountId) {
        userRepository.setPrimaryAccountIdById(userId, primaryAccountId);
    }
}
