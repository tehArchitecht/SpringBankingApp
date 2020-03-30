package com.github.tehArchitecht.springbankingapp.security;

import com.github.tehArchitecht.springbankingapp.security.exception.InvalidJwtTokenException;
import com.github.tehArchitecht.springbankingapp.security.service.CustomUserDetailsService;
import com.github.tehArchitecht.springbankingapp.security.service.JwtTokenService;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    private CustomUserDetailsService userDetailsService;
    private JwtTokenService jwtTokenService;

    JwtRequestFilter(CustomUserDetailsService userDetailsService, JwtTokenService jwtTokenService) {
        this.userDetailsService = userDetailsService;
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        try {
            String jwtToken = null;
            if (SecurityContextHolder.getContext().getAuthentication() == null)
                jwtToken = jwtTokenService.resolveToken(request);

            if (jwtToken != null && jwtTokenService.validateToken(jwtToken)) {
                String userName = jwtTokenService.extractUsername(jwtToken);
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userName);
                setAuthentication(userDetails, request);
            }
        } catch (InvalidJwtTokenException | UsernameNotFoundException e) {
            logger.error(e);
        }

        chain.doFilter(request, response);
    }

    private void setAuthentication(UserDetails userDetails, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }
}
