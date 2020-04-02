package com.github.tehArchitecht.springbankingapp.presentation.controller;

import com.github.tehArchitecht.springbankingapp.logic.dto.request.*;
import com.github.tehArchitecht.springbankingapp.logic.manager.AccountManager;
import com.github.tehArchitecht.springbankingapp.logic.manager.OperationManager;
import com.github.tehArchitecht.springbankingapp.logic.manager.UserManager;
import com.github.tehArchitecht.springbankingapp.presentation.service.StatusMapperService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.annotations.ApiOperation;

@RestController
public class BankController {
    private final UserManager userManager;
    private final AccountManager accountManager;
    private final OperationManager operationManager;
    private final StatusMapperService statusMapperService;

    public BankController(UserManager userManager, AccountManager accountManager, OperationManager operationManager,
                          StatusMapperService statusMapperService) {
        this.userManager = userManager;
        this.accountManager = accountManager;
        this.operationManager = operationManager;
        this.statusMapperService = statusMapperService;
    }

    // -------------------------------------------------------------------------------------------------------------- //
    // Registration and authorisation operations                                                                      //
    // -------------------------------------------------------------------------------------------------------------- //

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

    // -------------------------------------------------------------------------------------------------------------- //
    // User related operations                                                                                        //
    // -------------------------------------------------------------------------------------------------------------- //

    @GetMapping("/accounts")
    @ApiOperation("get all accounts for the current user")
    public ResponseEntity<?> getUserAccounts() {
        return statusMapperService.generateResponse(accountManager.getUserAccounts());
    }

    @GetMapping("/operations")
    @ApiOperation("get all account operations for the current user")
    public ResponseEntity<?> getUserOperations() {
        return statusMapperService.generateResponse(operationManager.getUserOperations());
    }

    // -------------------------------------------------------------------------------------------------------------- //
    // Account related operations                                                                                     //
    // -------------------------------------------------------------------------------------------------------------- //

    @PostMapping("/create-account")
    @ApiOperation("create a new account")
    public ResponseEntity<?> createAccount(@RequestBody CreateAccountRequest request) {
        return statusMapperService.generateResponse(accountManager.createAccount(request));
    }

    @PostMapping("/set-primary-account")
    @ApiOperation("set the primary account")
    public ResponseEntity<?> setPrimaryAccount(@RequestBody SetPrimaryAccountRequest request) {
        return statusMapperService.generateResponse(accountManager.setPrimaryAccount(request));
    }

    // -------------------------------------------------------------------------------------------------------------- //
    // Operations with funds                                                                                          //
    // -------------------------------------------------------------------------------------------------------------- //

    @PostMapping("/deposit-funds")
    @ApiOperation("deposit funds")
    public ResponseEntity<?> depositFunds(@RequestBody DepositFundsRequest request) {
        return statusMapperService.generateResponse(operationManager.depositFunds(request));
    }

    @PostMapping("/transfer-funds")
    @ApiOperation("transfer funds")
    public ResponseEntity<?> transferFunds(@RequestBody TransferFundsRequest request) {
        return statusMapperService.generateResponse(operationManager.transferFunds(request));
    }
}