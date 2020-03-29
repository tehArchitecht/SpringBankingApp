package com.github.tehArchitecht.springbankingapp.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        // By default a 403 (Forbidden) error would be returned, with the message "Access Denied".
        // This returns a 401 (Unauthorized) error with the message "Unauthorized".
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
    }
}