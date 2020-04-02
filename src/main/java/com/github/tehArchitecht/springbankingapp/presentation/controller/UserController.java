package com.github.tehArchitecht.springbankingapp.presentation.controller;

import com.github.tehArchitecht.springbankingapp.logic.dto.request.SignInWithNameRequest;
import com.github.tehArchitecht.springbankingapp.logic.dto.request.SignInWithPhoneNumberRequest;
import com.github.tehArchitecht.springbankingapp.logic.dto.request.SignUpRequest;
import com.github.tehArchitecht.springbankingapp.logic.manager.UserManager;
import com.github.tehArchitecht.springbankingapp.presentation.service.StatusMapperService;

import io.swagger.annotations.ApiOperation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    private final UserManager userManager;
    private final StatusMapperService statusMapperService;

    public UserController(UserManager userManager, StatusMapperService statusMapperService) {
        this.userManager = userManager;
        this.statusMapperService = statusMapperService;
    }

    @PostMapping("/signup")
    @ApiOperation("sign up")
    public ResponseEntity<?> signUp(@RequestBody SignUpRequest request) {
        return statusMapperService.generateResponse(userManager.signUp(request));
    }

    @PostMapping("/signin-with-name")
    @ApiOperation("sign in with name")
    public ResponseEntity<?> signInWithName(@RequestBody SignInWithNameRequest request) {
        return statusMapperService.generateResponse(userManager.signInWithName(request));
    }

    @PostMapping("/signin-with-phone-number")
    @ApiOperation("sign in with phone number")
    public ResponseEntity<?> signInWithPhoneNumber(@RequestBody SignInWithPhoneNumberRequest request) {
        return statusMapperService.generateResponse(userManager.signWithPhoneNumber(request));
    }
}
