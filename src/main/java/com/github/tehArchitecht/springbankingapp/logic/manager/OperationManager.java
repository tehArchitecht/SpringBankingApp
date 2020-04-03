package com.github.tehArchitecht.springbankingapp.logic.manager;

import com.github.tehArchitecht.springbankingapp.data.model.Account;
import com.github.tehArchitecht.springbankingapp.data.model.Currency;
import com.github.tehArchitecht.springbankingapp.data.model.Operation;
import com.github.tehArchitecht.springbankingapp.data.model.User;
import com.github.tehArchitecht.springbankingapp.logic.CurrencyConverter;
import com.github.tehArchitecht.springbankingapp.logic.Result;
import com.github.tehArchitecht.springbankingapp.logic.Status;
import com.github.tehArchitecht.springbankingapp.logic.dto.request.DepositFundsRequest;
import com.github.tehArchitecht.springbankingapp.logic.dto.request.TransferFundsRequest;
import com.github.tehArchitecht.springbankingapp.logic.dto.response.OperationDto;
import com.github.tehArchitecht.springbankingapp.logic.service.EntityMapperService;
import com.github.tehArchitecht.springbankingapp.security.service.JwtTokenService;
import com.github.tehArchitecht.springbankingapp.service.AccountService;
import com.github.tehArchitecht.springbankingapp.service.OperationService;
import com.github.tehArchitecht.springbankingapp.service.UserService;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.AuthenticationManager;
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
public class OperationManager extends SecuredValidatingManager {
    private final EntityMapperService entityMapperService;
    private final AccountService accountService;
    private final OperationService operationService;
    private final UserService userService;

    public OperationManager(AuthenticationManager authenticationManager, JwtTokenService jwtTokenService,
                            EntityMapperService entityMapperService, AccountService accountService,
                            OperationService operationService, UserService userService) {
        super(authenticationManager, jwtTokenService);
        this.entityMapperService = entityMapperService;
        this.accountService = accountService;
        this.operationService = operationService;
        this.userService = userService;
    }

    public Result<List<OperationDto>> getUserOperations() {
        try {
            if (isTokenInvalid())
                return Result.ofFailure(Status.FAILURE_BAD_TOKEN);
            Long userId = getUserId();

            List<Account> accounts = accountService.getUserAccounts(userId);
            Stream<OperationDto> stream = Stream.empty();
            for (Account account : accounts) {
                UUID accountId = account.getId();
                List<Operation> operations = operationService.findAllByAccountId(accountId);
                stream = Stream.concat(
                        stream,
                        operations.stream().map(op -> entityMapperService.convertOperation(op, accountId))
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

    public Status depositFunds(DepositFundsRequest request) {
        if (request == null || hasConstraintViolations(request))
            return Status.FAILURE_VALIDATION_ERROR;

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
        if (request == null || hasConstraintViolations(request))
            return Status.FAILURE_VALIDATION_ERROR;

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
    // Helper methods                                                                                                 //
    // -------------------------------------------------------------------------------------------------------------- //

    private Result<Account> getAccountEntity(UUID accountId) {
        try {
            if (isTokenInvalid())
                return Result.ofFailure(Status.FAILURE_BAD_TOKEN);
            User user = getUser();

            Optional<Account> optional = accountService.get(accountId);
            if (!optional.isPresent())
                return Result.ofFailure(Status.FAILURE_INVALID_ACCOUNT_ID);

            Account account = optional.get();
            if (!account.getUser().equals(user))
                return Result.ofFailure(Status.FAILURE_UNAUTHORIZED_ACCESS);

            return Result.ofSuccess(null, account);
        } catch (DataAccessException e) {
            return Result.ofFailure(Status.FAILURE_INTERNAL_ERROR);
        }
    }

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
}
