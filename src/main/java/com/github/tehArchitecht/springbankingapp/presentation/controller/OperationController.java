package com.github.tehArchitecht.springbankingapp.presentation.controller;

import com.github.tehArchitecht.springbankingapp.logic.dto.request.DepositFundsRequest;
import com.github.tehArchitecht.springbankingapp.logic.dto.request.TransferFundsRequest;
import com.github.tehArchitecht.springbankingapp.logic.manager.OperationManager;
import com.github.tehArchitecht.springbankingapp.presentation.service.StatusMapperService;

import io.swagger.annotations.ApiOperation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/operations")
public class OperationController {
    private final OperationManager operationManager;
    private final StatusMapperService statusMapperService;

    public OperationController(OperationManager operationManager, StatusMapperService statusMapperService) {
        this.operationManager = operationManager;
        this.statusMapperService = statusMapperService;
    }

    @PostMapping("/deposit")
    @ApiOperation("deposit funds")
    public ResponseEntity<?> depositFunds(@RequestBody DepositFundsRequest request) {
        return statusMapperService.generateResponse(operationManager.depositFunds(request));
    }

    @PostMapping("/transfer")
    @ApiOperation("transfer funds")
    public ResponseEntity<?> transferFunds(@RequestBody TransferFundsRequest request) {
        return statusMapperService.generateResponse(operationManager.transferFunds(request));
    }

    @GetMapping("/")
    @ApiOperation("get all account operations for the current user")
    public ResponseEntity<?> getUserOperations() {
        return statusMapperService.generateResponse(operationManager.getUserOperations());
    }
}
