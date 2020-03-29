package com.github.tehArchitecht.springbankingapp.security.service;

import com.github.tehArchitecht.springbankingapp.data.model.User;
import com.github.tehArchitecht.springbankingapp.data.repository.UserRepository;
import com.github.tehArchitecht.springbankingapp.security.UserDetailsImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        Optional<User> optional = userRepository.findByName(name);
        if (!optional.isPresent()) {
            throw new UsernameNotFoundException("No user found with username " + name);
        }

        return new UserDetailsImpl(optional.get());
    }
}
