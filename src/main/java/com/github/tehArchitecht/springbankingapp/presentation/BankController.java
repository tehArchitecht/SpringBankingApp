package com.github.tehArchitecht.springbankingapp.presentation;

import com.github.tehArchitecht.springbankingapp.logic.Result;
import com.github.tehArchitecht.springbankingapp.logic.Status;
import com.github.tehArchitecht.springbankingapp.logic.dto.request.*;
import com.github.tehArchitecht.springbankingapp.logic.dto.response.AccountDto;
import com.github.tehArchitecht.springbankingapp.logic.dto.response.OperationDto;
import com.github.tehArchitecht.springbankingapp.security.service.JwtTokenService;
import com.github.tehArchitecht.springbankingapp.logic.service.BankService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BankController {
    private final BankService bankService;
    private final JwtTokenService jwtTokenService;

    public BankController(BankService bankService, JwtTokenService jwtTokenService) {
        this.bankService = bankService;
        this.jwtTokenService = jwtTokenService;
    }

    // -------------------------------------------------------------------------------------------------------------- //
    // Registration and authorisation operations                                                                      //
    // -------------------------------------------------------------------------------------------------------------- //

    @PostMapping("/signup")
    public Status signUp(@RequestBody SignUpRequest request) {
        return bankService.signUp(request);
    }

    @PostMapping("/signin-with-name")
    public String signInWithName(@RequestBody SignInWithNameRequest request) {
        Result<UserDetails> result = bankService.signInWithName(request);
        if (result.failure()) return "";

        String token = jwtTokenService.generateToken(result.getData());
        return "Bearer " + token;
    }

    @PostMapping("/signin-with-phone-number")
    public String signInWithName(@RequestBody SignInWithPhoneNumberRequest request) {
        Result<UserDetails> result = bankService.signWithPhoneNumber(request);
        if (result.failure()) return "";

        String token = jwtTokenService.generateToken(result.getData());
        return "Bearer " + token;
    }

    // -------------------------------------------------------------------------------------------------------------- //
    // User related operations                                                                                        //
    // -------------------------------------------------------------------------------------------------------------- //

    @GetMapping("/accounts")
    public Result<List<AccountDto>> getUserAccounts() {
        return bankService.getUserAccounts();
    }

    @GetMapping("/operations")
    public Result<List<OperationDto>> getUserOperations() {
        return bankService.getUserOperations();
    }

    // -------------------------------------------------------------------------------------------------------------- //
    // Account related operations                                                                                     //
    // -------------------------------------------------------------------------------------------------------------- //

    @PostMapping("/create-account")
    public Status createAccount(@RequestBody CreateAccountRequest request) {
        return bankService.createAccount(request);
    }

    @PostMapping("/set-primary-account")
    public Status setPrimaryAccount(@RequestBody SetPrimaryAccountRequest request) {
        return bankService.setPrimaryAccount(request);
    }

    // -------------------------------------------------------------------------------------------------------------- //
    // Operations with funds                                                                                          //
    // -------------------------------------------------------------------------------------------------------------- //

    @PostMapping("/deposit-funds")
    public Status depositFunds(@RequestBody DepositFundsRequest request) {
        return bankService.depositFunds(request);
    }

    @PostMapping("/transfer-funds")
    public Status transferFunds(@RequestBody TransferFundsRequest request) {
        return bankService.transferFunds(request);
    }


}