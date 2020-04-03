package com.github.tehArchitecht.springbankingapp.security.config;

import com.github.tehArchitecht.springbankingapp.security.JwtAuthenticationEntryPoint;
import com.github.tehArchitecht.springbankingapp.security.JwtRequestFilter;
import com.github.tehArchitecht.springbankingapp.security.service.CustomUserDetailsService;

import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    final CustomUserDetailsService userDetailsService;
    final JwtRequestFilter jwtRequestFilter;
    final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    // -------------------------------------------------------------------------------------------------------------- //
    // Constructor (used for constructor injection)                                                                   //
    // -------------------------------------------------------------------------------------------------------------- //

    WebSecurityConfig(CustomUserDetailsService userDetailsService, JwtRequestFilter jwtRequestFilter,
                      JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint) {
        this.userDetailsService = userDetailsService;
        this.jwtRequestFilter = jwtRequestFilter;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
    }

    // -------------------------------------------------------------------------------------------------------------- //
    // Configuration                                                                                                  //
    // -------------------------------------------------------------------------------------------------------------- //

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        final String[] PERMITTED_PATTERNS = {
                "/signup",
                "/signin-with-name",
                "/signin-with-phone-number"
        };

        // Disable CSRF (cross site request forgery).
        http.csrf().disable();
        // No session will be created or used by spring security
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        // Add JwtRequestFilter into the filter chain.
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        // Use JwtAuthenticationEntryPoint as the authentication entry point.
        http.exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint);
        // Entry points.
        http.authorizeRequests()
                .antMatchers(PERMITTED_PATTERNS)
                .permitAll()
                .anyRequest()
                .authenticated();
    }

    @Override
    public void configure(WebSecurity web) {
        // Allow swagger to be accessed without authentication.
        web.ignoring()
                .antMatchers("/v2/api-docs")
                .antMatchers("/swagger-resources/**")
                .antMatchers("/configuration/**")
                .antMatchers("/webjars/**")
                .antMatchers("/swagger-ui.html");
    }

    // -------------------------------------------------------------------------------------------------------------- //
    // Beans                                                                                                          //
    // -------------------------------------------------------------------------------------------------------------- //

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception{
        return super.authenticationManagerBean();
    }
}
