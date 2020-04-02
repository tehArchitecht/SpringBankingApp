package com.github.tehArchitecht.springbankingapp.logic.service;

import com.github.tehArchitecht.springbankingapp.data.model.Account;
import com.github.tehArchitecht.springbankingapp.data.model.Operation;
import com.github.tehArchitecht.springbankingapp.data.model.User;
import com.github.tehArchitecht.springbankingapp.logic.dto.request.SignUpRequest;
import com.github.tehArchitecht.springbankingapp.logic.dto.response.AccountDto;
import com.github.tehArchitecht.springbankingapp.logic.dto.response.OperationDto;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class EntityMapperService {
    public User extractUser(SignUpRequest request) {
        return new User(
                request.getUserName(),
                request.getPassword(),
                request.getAddress(),
                request.getPhoneNumber()
        );
    }

    public AccountDto convertAccount(Account account, UUID primaryAccountId) {
        return new AccountDto(
                account.getId(),
                account.getBalance(),
                account.getCurrency(),
                account.getId().equals(primaryAccountId)
        );
    }

    public OperationDto convertOperation(Operation operation, UUID accountId) {
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
