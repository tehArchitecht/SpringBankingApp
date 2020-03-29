package com.github.tehArchitecht.springbankingapp.presentation;

import com.github.tehArchitecht.springbankingapp.data.model.Currency;
import com.github.tehArchitecht.springbankingapp.logic.Result;
import com.github.tehArchitecht.springbankingapp.logic.Status;
import com.github.tehArchitecht.springbankingapp.logic.dto.AccountDto;
import com.github.tehArchitecht.springbankingapp.logic.dto.OperationDto;
import com.github.tehArchitecht.springbankingapp.logic.security.JwtUtil;
import com.github.tehArchitecht.springbankingapp.logic.service.BankService;
import com.github.tehArchitecht.springbankingapp.logic.service.UserDetailsServiceImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
public class BankController {
    private BankService bankService;
    private AuthenticationManager authenticationManager;
    private UserDetailsService userDetailsService;
    private JwtUtil jwtUtil;

    public BankController(BankService bankService, AuthenticationManager authenticationManager, UserDetailsServiceImpl userDetailsService, JwtUtil jwtUtil) {
        this.bankService = bankService;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }

    // -------------------------------------------------------------------------------------------------------------- //
    // Registration and authorisation operations                                                                      //
    // -------------------------------------------------------------------------------------------------------------- //

    @PostMapping("/signup")
    public Status signUp (@RequestParam String name, @RequestParam String password,
                          @RequestParam String address, @RequestParam String phoneNumber) {
        return bankService.signUp(name, password, address, phoneNumber);
    }

    @PostMapping("/signin-with-name")
    public String signInWithName(@RequestParam String userName, @RequestParam String password) {
        Result<UserDetails> result = bankService.signInWithName(userName, password);
        if (result.failure()) return "";

        String token = jwtUtil.generateToken(result.getData());
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
    public Status createAccount(@RequestParam Currency currency) {
        return bankService.createAccount(currency);
    }

    @PostMapping("/set-primary-account")
    public Status createAccount(@RequestParam UUID accountId) {
        return bankService.setPrimaryAccount(accountId);
    }

    // -------------------------------------------------------------------------------------------------------------- //
    // Operations with funds                                                                                          //
    // -------------------------------------------------------------------------------------------------------------- //

    @PostMapping("/deposit-funds")
    public Status depositFunds(@RequestParam UUID accountId, @RequestParam Currency currency, @RequestParam BigDecimal amount) {
        return bankService.depositFunds(accountId, currency, amount);
    }

    @PostMapping("/transfer-funds")
    public Status transferFunds(@RequestParam UUID senderAccountId, @RequestParam String receiverPhoneNumber,
                                @RequestParam BigDecimal amount, @RequestParam Currency currency) {
        return bankService.transferFunds(senderAccountId, receiverPhoneNumber, amount, currency);
    }


}