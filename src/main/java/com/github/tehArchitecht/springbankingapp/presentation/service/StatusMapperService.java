package com.github.tehArchitecht.springbankingapp.presentation.service;

import com.github.tehArchitecht.springbankingapp.logic.Result;
import com.github.tehArchitecht.springbankingapp.logic.Status;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class StatusMapperService {
    public ResponseEntity<Status> generateResponse(Status status) {
        return new ResponseEntity<>(status, getHttpStatus(status));
    }
    public ResponseEntity<Result<?>> generateResponse(Result<?> result) {
        return new ResponseEntity<>(result, getHttpStatus(result.getStatus()));
    }

    private HttpStatus getHttpStatus(Status status) {
        switch (status) {
            // status values shared by methods
            case FAILURE_BAD_TOKEN:
                return HttpStatus.UNAUTHORIZED;
            case FAILURE_INTERNAL_ERROR:
                return HttpStatus.INTERNAL_SERVER_ERROR;
            case FAILURE_INVALID_ACCOUNT_ID:
                return HttpStatus.UNPROCESSABLE_ENTITY;
            case FAILURE_UNAUTHORIZED_ACCESS:
                return HttpStatus.FORBIDDEN;
            case FAILURE_VALIDATION_ERROR:
                return HttpStatus.UNPROCESSABLE_ENTITY;
            // signUp
            case SIGN_UP_SUCCESS:
                return HttpStatus.CREATED;
            case SING_UP_FAILURE_NAME_OR_PHONE_NUMBER_TAKEN:
                return HttpStatus.CONFLICT;
            // signInWithName
            case SIGN_IN_WITH_NAME_SUCCESS:
                return HttpStatus.OK;
            case SIGN_IN_WITH_NAME_FAILURE_WRONG_DATA:
                return HttpStatus.UNAUTHORIZED;
            // signWithPhoneNumber
            case SIGN_IN_WITH_PHONE_NUMBER_SUCCESS:
                return HttpStatus.OK;
            case SIGN_IN_WITH_PHONE_NUMBER_FAILURE_WRONG_DATA:
                return HttpStatus.UNAUTHORIZED;
            // getUserOperations
            case GET_USER_OPERATIONS_SUCCESS:
                return HttpStatus.OK;
            // getUserAccounts
            case GET_USER_ACCOUNTS_SUCCESS:
                return HttpStatus.OK;
            // createAccount
            case CREATE_ACCOUNT_SUCCESS:
                return HttpStatus.CREATED;
            // setPrimaryAccount
            case SET_PRIMARY_ACCOUNT_SUCCESS:
                return HttpStatus.OK;
            // depositFunds
            case DEPOSIT_FUNDS_SUCCESS:
                return HttpStatus.OK;
            // transferFunds
            case TRANSFER_FUNDS_SUCCESS:
                return HttpStatus.OK;
            case TRANSFER_FUNDS_FAILURE_INVALID_PHONE_NUMBER:
                return HttpStatus.UNPROCESSABLE_ENTITY;
            case TRANSFER_FUNDS_FAILURE_RECEIVER_HAS_NO_PRIMARY_ACCOUNT:
                return HttpStatus.UNPROCESSABLE_ENTITY;
            case TRANSFER_FUNDS_FAILURE_SAME_ACCOUNT:
                return HttpStatus.UNPROCESSABLE_ENTITY;
            case TRANSFER_FUNDS_FAILURE_INSUFFICIENT_FUNDS:
                return HttpStatus.UNPROCESSABLE_ENTITY;
            // default
            default: throw new IllegalArgumentException(status.toString());
        }
    }
}
