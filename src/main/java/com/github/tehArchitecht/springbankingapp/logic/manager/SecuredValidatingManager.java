package com.github.tehArchitecht.springbankingapp.logic.manager;

import com.github.tehArchitecht.springbankingapp.data.model.User;
import com.github.tehArchitecht.springbankingapp.security.CustomUserDetails;
import com.github.tehArchitecht.springbankingapp.security.service.JwtTokenService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

abstract class SecuredValidatingManager {
    private final static Logger logger = LoggerFactory.getLogger(SecuredValidatingManager.class);

    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;
    private final Validator validator;

    protected SecuredValidatingManager(AuthenticationManager authenticationManager, JwtTokenService jwtTokenService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenService = jwtTokenService;
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    protected <T> boolean hasConstraintViolations(T object) {
        Set<ConstraintViolation<T>> violations = validator.validate(object);
        for (ConstraintViolation<T> violation : violations)
            logger.warn(violation.getPropertyPath() + " " + violation.getMessage());
        return !violations.isEmpty();
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
