package com.github.tehArchitecht.springbankingapp.presentation.controller;

import com.github.tehArchitecht.springbankingapp.logic.dto.request.CreateAccountRequest;
import com.github.tehArchitecht.springbankingapp.logic.dto.request.SetPrimaryAccountRequest;
import com.github.tehArchitecht.springbankingapp.logic.manager.AccountManager;
import com.github.tehArchitecht.springbankingapp.presentation.DefaultApiResponses;
import com.github.tehArchitecht.springbankingapp.presentation.service.StatusMapperService;

import io.swagger.annotations.ApiOperation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
@DefaultApiResponses
public class AccountController {
    private final AccountManager accountManager;
    private final StatusMapperService statusMapperService;

    public AccountController(AccountManager accountManager, StatusMapperService statusMapperService) {
        this.accountManager = accountManager;
        this.statusMapperService = statusMapperService;
    }

    @GetMapping("/")
    @ApiOperation("get all accounts for the current user")
    public ResponseEntity<?> getUserAccounts() {
        return statusMapperService.generateResponse(accountManager.getUserAccounts());
    }

    @PostMapping("/")
    @ApiOperation("create a new account")
    public ResponseEntity<?> createAccount(@RequestBody CreateAccountRequest request) {
        return statusMapperService.generateResponse(accountManager.createAccount(request));
    }

    @PostMapping("/primary")
    @ApiOperation("set the primary account")
    public ResponseEntity<?> setPrimaryAccount(@RequestBody SetPrimaryAccountRequest request) {
        return statusMapperService.generateResponse(accountManager.setPrimaryAccount(request));
    }
}
