package com.github.tehArchitecht.springbankingapp.logic.service;

import com.github.tehArchitecht.springbankingapp.data.model.Account;
import com.github.tehArchitecht.springbankingapp.data.model.Currency;
import com.github.tehArchitecht.springbankingapp.data.model.Operation;
import com.github.tehArchitecht.springbankingapp.data.model.User;
import com.github.tehArchitecht.springbankingapp.logic.CurrencyConverter;
import com.github.tehArchitecht.springbankingapp.logic.Result;
import com.github.tehArchitecht.springbankingapp.logic.Status;

import com.github.tehArchitecht.springbankingapp.logic.dto.request.*;
import com.github.tehArchitecht.springbankingapp.logic.dto.response.AccountDto;
import com.github.tehArchitecht.springbankingapp.logic.dto.response.OperationDto;
import com.github.tehArchitecht.springbankingapp.security.UserDetailsImpl;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class BankService {
    private final AccountService accountService;
    private final OperationService operationService;
    private final UserService userService;

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public BankService(AccountService accountService, OperationService operationService,
                       UserService userService, AuthenticationManager authenticationManager,
                       PasswordEncoder passwordEncoder) {
        this.accountService = accountService;
        this.operationService = operationService;
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
    }

    // -------------------------------------------------------------------------------------------------------------- //
    // Registration and authorisation operations                                                                      //
    // -------------------------------------------------------------------------------------------------------------- //

    public Status signUp (SignUpRequest request) {
        String userName = request.getUserName();
        String phoneNumber = request.getPhoneNumber();

        try {
            if (userService.isNameInUse(userName) || userService.isPhoneNumberInUse(phoneNumber))
                return Status.SING_UP_FAILURE_NAME_OR_PHONE_NUMBER_TAKEN;

            User user = extractUser(request);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userService.add(user);
            return Status.SIGN_UP_SUCCESS;
        } catch (DataAccessException e) {
            return Status.FAILURE_INTERNAL_ERROR;
        }
    }

    public Result<UserDetails> signInWithName(SignInWithNameRequest request) {
        String userName = request.getUserName();
        String password = request.getPassword();

        try {
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userName, password);
            UserDetailsImpl details = (UserDetailsImpl) authenticationManager.authenticate(token).getPrincipal();
            return Result.ofSuccess(Status.SIGN_IN_WITH_NAME_SUCCESS, details);
        } catch (DisabledException | BadCredentialsException e) {
            return Result.ofFailure(Status.SIGN_IN_WITH_NAME_FAILURE_WRONG_DATA);
        }
    }

    public Result<UserDetails> signWithPhoneNumber(SignInWithPhoneNumberRequest request) {
        String phoneNumber = request.getPhoneNumber();
        String password = request.getPassword();

        try {
            Optional<User> optional = userService.getByPhoneNumber(phoneNumber);
            if (!optional.isPresent())
                return Result.ofFailure(Status.SIGN_IN_WITH_NAME_FAILURE_WRONG_DATA);

            User user = optional.get();
            String userName = user.getName();

            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userName, password);
            UserDetailsImpl details = (UserDetailsImpl) authenticationManager.authenticate(token).getPrincipal();
            return Result.ofSuccess(Status.SIGN_IN_WITH_NAME_SUCCESS, details);
        } catch (DisabledException | BadCredentialsException e) {
            return Result.ofFailure(Status.SIGN_IN_WITH_NAME_FAILURE_WRONG_DATA);
        } catch (DataAccessException e) {
            return Result.ofFailure(Status.FAILURE_INTERNAL_ERROR);
        }
    }

    // -------------------------------------------------------------------------------------------------------------- //
    // User related operations                                                                                        //
    // -------------------------------------------------------------------------------------------------------------- //

    public Result<List<AccountDto>> getUserAccounts() {
        try {
            if (isTokenInvalid())
                return Result.ofFailure(Status.BAD_TOKEN);
            Long userId = getUserId();

            List<Account> accounts = accountService.getUserAccounts(userId);
            List<AccountDto> accountDtos = accounts.stream().map(this::convertAccount).collect(Collectors.toList());
            return Result.ofSuccess(Status.GET_USER_ACCOUNTS_SUCCESS, accountDtos);
        } catch (DataAccessException e) {
            return Result.ofFailure(Status.FAILURE_INTERNAL_ERROR);
        }
    }

    public Result<List<OperationDto>> getUserOperations() {
        try {
            if (isTokenInvalid())
                return Result.ofFailure(Status.BAD_TOKEN);
            Long userId = getUserId();

            List<Account> accounts = accountService.getUserAccounts(userId);
            Stream<OperationDto> stream = Stream.empty();
            for (Account account : accounts) {
                UUID accountId = account.getId();
                List<Operation> operations = operationService.findAllByAccountId(accountId);
                stream = Stream.concat(
                        stream,
                        operations.stream().map(op -> convertOperation(op, accountId))
                );
            }

            return Result.ofSuccess(
                    Status.GET_USER_OPERATIONS_SUCCESS,
                    stream.sorted(Comparator.comparing(OperationDto::getDate)).collect(Collectors.toList())
            );
        } catch (DataAccessException e) {
            return Result.ofFailure(Status.FAILURE_INTERNAL_ERROR);
        }
    }

    // -------------------------------------------------------------------------------------------------------------- //
    // Account related operations                                                                                     //
    // -------------------------------------------------------------------------------------------------------------- //

    public Status createAccount(CreateAccountRequest request) {
        Currency currency = request.getCurrency();

        try {
            if (isTokenInvalid())
                return Status.BAD_TOKEN;
            User user = getUser();

            accountService.add(new Account(user, currency));
            return Status.CREATE_ACCOUNT_SUCCESS;
        } catch (DataAccessException e) {
            return Status.FAILURE_INTERNAL_ERROR;
        }
    }

    public Status setPrimaryAccount(SetPrimaryAccountRequest request) {
        UUID accountId = request.getAccountId();

        try {
            if (isTokenInvalid())
                return Status.BAD_TOKEN;
            Long userId = getUserId();

            userService.setPrimaryAccountId(userId, accountId);
            return Status.SET_PRIMARY_ACCOUNT_SUCCESS;
        } catch (DataAccessException e) {
            return Status.FAILURE_INTERNAL_ERROR;
        }
    }

    // -------------------------------------------------------------------------------------------------------------- //
    // Operations with funds                                                                                          //
    // -------------------------------------------------------------------------------------------------------------- //

    public Status depositFunds(DepositFundsRequest request) {
        UUID accountId = request.getAccountId();
        BigDecimal amount = request.getAmount();
        Currency currency = request.getCurrency();

        try {
            Result<Account> result = getAccountEntity(accountId);
            if (result.failure())
                return result.getStatus();
            Account account = result.getData();

            BigDecimal converted = CurrencyConverter.convert(amount, currency, account.getCurrency());
            BigDecimal newBalance = account.getBalance().add(converted);
            accountService.setBalance(account.getId(), newBalance);

            return Status.DEPOSIT_FUNDS_SUCCESS;
        } catch (DataAccessException e) {
            return Status.FAILURE_INTERNAL_ERROR;
        }
    }

    public Status transferFunds(TransferFundsRequest request) {
        UUID senderAccountId = request.getSenderAccountId();
        String receiverPhoneNumber = request.getReceiverPhoneNumber();
        BigDecimal amount = request.getAmount();
        Currency currency = request.getCurrency();

        try {
            Result<Account> result = getAccountEntity(senderAccountId);
            if (result.failure())
                return result.getStatus();
            Account senderAccount = result.getData();

            Optional<User> userOptional = userService.getByPhoneNumber(receiverPhoneNumber);
            if (!userOptional.isPresent())
                return Status.TRANSFER_FUNDS_FAILURE_INVALID_PHONE_NUMBER;
            User receiver = userOptional.get();

            int count = accountService.countUserAccounts(receiver.getId());
            if (count == 0)
                return Status.TRANSFER_FUNDS_FAILURE_RECEIVER_HAS_NO_ACCOUNTS;

            Optional<Account> accountOptional = accountService.getUserPrimaryAccount(receiver.getId());
            if (!accountOptional.isPresent())
                return Status.TRANSFER_FUNDS_FAILURE_RECEIVER_HAS_NO_PRIMARY_ACCOUNT;
            Account receiverAccount = accountOptional.get();

            if (senderAccountId.equals(receiverAccount.getId()))
                return Status.TRANSFER_FUNDS_FAILURE_SAME_ACCOUNT;

            BigDecimal senderInitialBalance = senderAccount.getBalance();
            BigDecimal receiverInitialBalance = receiverAccount.getBalance();
            Currency senderCurrency = senderAccount.getCurrency();
            Currency receiverCurrency = receiverAccount.getCurrency();

            BigDecimal senderAmount = CurrencyConverter.convert(amount, currency, senderCurrency);
            BigDecimal receiverAmount = CurrencyConverter.convert(amount, currency, receiverCurrency);

            if (senderAccount.getBalance().compareTo(senderAmount) < 0)
                return Status.TRANSFER_FUNDS_FAILURE_INSUFFICIENT_FUNDS;

            BigDecimal senderResultingBalance = senderInitialBalance.subtract(senderAmount);
            BigDecimal receiverResultingBalance = receiverInitialBalance.add(receiverAmount);

            accountService.setBalance(senderAccount.getId(), senderResultingBalance);
            accountService.setBalance(receiverAccount.getId(), receiverResultingBalance);

            logTransfer(
                    senderAccount,
                    receiverAccount,
                    currency,
                    amount,
                    senderInitialBalance,
                    senderResultingBalance,
                    receiverInitialBalance,
                    receiverResultingBalance
            );

            return Status.TRANSFER_FUNDS_SUCCESS;
        } catch (DataAccessException e) {
            return Status.FAILURE_INTERNAL_ERROR;
        }
    }

    // -------------------------------------------------------------------------------------------------------------- //
    // Operation logging                                                                                              //
    // -------------------------------------------------------------------------------------------------------------- //

    private void logTransfer(Account sender, Account receiver, Currency currency, BigDecimal amount,
                                    BigDecimal senderInitialBalance, BigDecimal senderResultingBalance,
                                    BigDecimal receiverInitialBalance, BigDecimal receiverResultingBalance) {
        operationService.add(new Operation(
                new Timestamp(System.currentTimeMillis()),
                currency,
                sender,
                receiver,
                amount,
                senderInitialBalance,
                senderResultingBalance,
                receiverInitialBalance,
                receiverResultingBalance
        ));
    }

    // -------------------------------------------------------------------------------------------------------------- //
    // Helper methods                                                                                                 //
    // -------------------------------------------------------------------------------------------------------------- //

    private boolean isTokenInvalid() {
        return !SecurityContextHolder.getContext().getAuthentication().isAuthenticated();
    }

    private Long getUserId() {
        return getUser().getId();
    }

    private User getUser() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        return userDetails.getUser();
    }

    private Result<Account> getAccountEntity(UUID accountId) {
        try {
            if (isTokenInvalid())
                return Result.ofFailure(Status.BAD_TOKEN);
            User user = getUser();

            Optional<Account> optional = accountService.get(accountId);
            if (!optional.isPresent())
                return Result.ofFailure(Status.FAILURE_INVALID_ACCOUNT_ID);

            Account account = optional.get();
            if (!account.getUser().equals(user))
                return Result.ofFailure(Status.FAILURE_UNAUTHORIZED_ACCESS);

            return Result.ofSuccess(Status.SUCCESS, account);
        } catch (DataAccessException e) {
            return Result.ofFailure(Status.FAILURE_INTERNAL_ERROR);
        }
    }

    private User extractUser(SignUpRequest request) {
        return new User(
                request.getUserName(),
                request.getPassword(),
                request.getAddress(),
                request.getPhoneNumber()
        );
    }

    private AccountDto convertAccount(Account account) {
        return new AccountDto(
                account.getId(),
                account.getBalance(),
                account.getCurrency()
        );
    }

    private OperationDto convertOperation(Operation operation, UUID accountId) {
        UUID senderAccountId = operation.getSenderAccount().getId();
        UUID receiverAccountId = operation.getReceiverAccount().getId();
        if (accountId.equals(senderAccountId)) {
            return new OperationDto(
                    operation.getDate(),
                    operation.getCurrency(),
                    senderAccountId,
                    receiverAccountId,
                    operation.getAmount(),
                    operation.getSenderInitialBalance(),
                    operation.getSenderResultingBalance()
            );
        } else {
            return new OperationDto(
                    operation.getDate(),
                    operation.getCurrency(),
                    senderAccountId,
                    receiverAccountId,
                    operation.getAmount(),
                    operation.getReceiverInitialBalance(),
                    operation.getReceiverResultingBalance()
            );
        }
    }
}
