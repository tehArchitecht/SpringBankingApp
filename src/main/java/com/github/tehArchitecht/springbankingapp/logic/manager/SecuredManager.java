package com.github.tehArchitecht.springbankingapp.logic.manager;

import com.github.tehArchitecht.springbankingapp.data.model.User;
import com.github.tehArchitecht.springbankingapp.security.CustomUserDetails;
import com.github.tehArchitecht.springbankingapp.security.service.JwtTokenService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

abstract class SecuredManager {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;

    protected SecuredManager(AuthenticationManager authenticationManager, JwtTokenService jwtTokenService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenService = jwtTokenService;
    }

    protected String generateToken(String userName, String password) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userName, password);
        CustomUserDetails details = (CustomUserDetails) authenticationManager.authenticate(token).getPrincipal();
        return "Bearer " + jwtTokenService.generateToken(details);
    }

    protected boolean isTokenInvalid() {
        return !SecurityContextHolder.getContext().getAuthentication().isAuthenticated();
    }

    protected Long getUserId() {
        return getUser().getId();
    }

    protected User getUser() {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        return userDetails.getUser();
    }
}
