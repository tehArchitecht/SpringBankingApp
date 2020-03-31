package com.github.tehArchitecht.springbankingapp.presentation.controller;

import com.github.tehArchitecht.springbankingapp.logic.dto.request.*;
import com.github.tehArchitecht.springbankingapp.presentation.service.StatusMapperService;
import com.github.tehArchitecht.springbankingapp.security.service.JwtTokenService;
import com.github.tehArchitecht.springbankingapp.logic.service.BankService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.annotations.ApiOperation;

@RestController
public class BankController {
    private final BankService bankService;
    private final StatusMapperService statusMapperService;

    public BankController(BankService bankService, StatusMapperService statusMapperService) {
        this.bankService = bankService;
        this.statusMapperService = statusMapperService;
    }

    // -------------------------------------------------------------------------------------------------------------- //
    // Registration and authorisation operations                                                                      //
    // -------------------------------------------------------------------------------------------------------------- //

    @PostMapping("/signup")
    @ApiOperation("sign up")
    public ResponseEntity<?> signUp(@RequestBody SignUpRequest request) {
        return statusMapperService.generateResponse(bankService.signUp(request));
    }

    @PostMapping("/signin-with-name")
    @ApiOperation("sign in with name")
    public ResponseEntity<?> signInWithName(@RequestBody SignInWithNameRequest request) {
        return statusMapperService.generateResponse(bankService.signInWithName(request));
    }

    @PostMapping("/signin-with-phone-number")
    @ApiOperation("sign in with phone number")
    public ResponseEntity<?> signInWithName(@RequestBody SignInWithPhoneNumberRequest request) {
        return statusMapperService.generateResponse(bankService.signWithPhoneNumber(request));
    }

    // -------------------------------------------------------------------------------------------------------------- //
    // User related operations                                                                                        //
    // -------------------------------------------------------------------------------------------------------------- //

    @GetMapping("/accounts")
    @ApiOperation("get all accounts for the current user")
    public ResponseEntity<?> getUserAccounts() {
        return statusMapperService.generateResponse(bankService.getUserAccounts());
    }

    @GetMapping("/operations")
    @ApiOperation("get all account operations for the current user")
    public ResponseEntity<?> getUserOperations() {
        return statusMapperService.generateResponse(bankService.getUserOperations());
    }

    // -------------------------------------------------------------------------------------------------------------- //
    // Account related operations                                                                                     //
    // -------------------------------------------------------------------------------------------------------------- //

    @PostMapping("/create-account")
    @ApiOperation("create a new account")
    public ResponseEntity<?> createAccount(@RequestBody CreateAccountRequest request) {
        return statusMapperService.generateResponse(bankService.createAccount(request));
    }

    @PostMapping("/set-primary-account")
    @ApiOperation("set the primary account")
    public ResponseEntity<?> setPrimaryAccount(@RequestBody SetPrimaryAccountRequest request) {
        return statusMapperService.generateResponse(bankService.setPrimaryAccount(request));
    }

    // -------------------------------------------------------------------------------------------------------------- //
    // Operations with funds                                                                                          //
    // -------------------------------------------------------------------------------------------------------------- //

    @PostMapping("/deposit-funds")
    @ApiOperation("deposit funds")
    public ResponseEntity<?> depositFunds(@RequestBody DepositFundsRequest request) {
        return statusMapperService.generateResponse(bankService.depositFunds(request));
    }

    @PostMapping("/transfer-funds")
    @ApiOperation("transfer funds")
    public ResponseEntity<?> transferFunds(@RequestBody TransferFundsRequest request) {
        return statusMapperService.generateResponse(bankService.transferFunds(request));
    }
}