package com.github.tehArchitecht.springbankingapp.security.exception;

import org.springframework.security.core.AuthenticationException;

public class InvalidJwtTokenException extends AuthenticationException {
    public InvalidJwtTokenException(Exception e) {
        super("Provided JWT token is not valid", e);
    }
}
