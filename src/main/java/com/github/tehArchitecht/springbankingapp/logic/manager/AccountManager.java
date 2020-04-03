package com.github.tehArchitecht.springbankingapp.logic.manager;

import com.github.tehArchitecht.springbankingapp.data.model.Account;
import com.github.tehArchitecht.springbankingapp.data.model.Currency;
import com.github.tehArchitecht.springbankingapp.data.model.User;
import com.github.tehArchitecht.springbankingapp.logic.Result;
import com.github.tehArchitecht.springbankingapp.logic.Status;
import com.github.tehArchitecht.springbankingapp.logic.dto.request.CreateAccountRequest;
import com.github.tehArchitecht.springbankingapp.logic.dto.request.SetPrimaryAccountRequest;
import com.github.tehArchitecht.springbankingapp.logic.dto.response.AccountDto;
import com.github.tehArchitecht.springbankingapp.logic.service.EntityMapperService;
import com.github.tehArchitecht.springbankingapp.security.service.JwtTokenService;
import com.github.tehArchitecht.springbankingapp.logic.service.AccountService;
import com.github.tehArchitecht.springbankingapp.logic.service.UserService;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AccountManager extends SecuredValidatingManager {
    private final EntityMapperService entityMapperService;
    private final AccountService accountService;
    private final UserService userService;

    public AccountManager(AuthenticationManager authenticationManager, JwtTokenService jwtTokenService,
                          EntityMapperService entityMapperService, AccountService accountService,
                          UserService userService) {
        super(authenticationManager, jwtTokenService);
        this.entityMapperService = entityMapperService;
        this.accountService = accountService;
        this.userService = userService;
    }

    public Result<List<AccountDto>> getUserAccounts() {
        try {
            if (isTokenInvalid())
                return Result.ofFailure(Status.FAILURE_BAD_TOKEN);
            User user = getUser();
            Long userId = user.getId();

            Account primaryAccount = user.getPrimaryAccount();
            UUID primaryAccountId = primaryAccount == null ? null : primaryAccount.getId();

            List<Account> accounts = accountService.getUserAccounts(userId);
            List<AccountDto> accountDtos = accounts.stream()
                    .map((Account account) -> entityMapperService.convertAccount(account, primaryAccountId))
                    .collect(Collectors.toList());
            return Result.ofSuccess(Status.GET_USER_ACCOUNTS_SUCCESS, accountDtos);
        } catch (DataAccessException e) {
            return Result.ofFailure(Status.FAILURE_INTERNAL_ERROR);
        }
    }
    public Result<AccountDto> createAccount(CreateAccountRequest request) {
        if (request == null || hasConstraintViolations(request))
            return Result.ofFailure(Status.FAILURE_VALIDATION_ERROR);

        Currency currency = request.getCurrency();

        try {
            if (isTokenInvalid())
                return Result.ofFailure(Status.FAILURE_BAD_TOKEN);
            User user = getUser();

            Account created = accountService.add(new Account(user, currency));
            UUID primaryAccountId = accountService.getUserPrimaryAccount(user.getId())
                    .map(Account::getId).orElse(null);

            AccountDto account = entityMapperService.convertAccount(created, primaryAccountId);
            return Result.ofSuccess(Status.CREATE_ACCOUNT_SUCCESS, account);
        } catch (DataAccessException e) {
            return Result.ofFailure(Status.FAILURE_INTERNAL_ERROR);
        }
    }

    public Status setPrimaryAccount(SetPrimaryAccountRequest request) {
        if (request == null || hasConstraintViolations(request))
            return Status.FAILURE_VALIDATION_ERROR;

        UUID accountId = request.getAccountId();

        try {
            if (isTokenInvalid())
                return Status.FAILURE_BAD_TOKEN;
            Long userId = getUserId();

            userService.setPrimaryAccountId(userId, accountId);
            return Status.SET_PRIMARY_ACCOUNT_SUCCESS;
        } catch (DataAccessException e) {
            return Status.FAILURE_INTERNAL_ERROR;
        }
    }
}
