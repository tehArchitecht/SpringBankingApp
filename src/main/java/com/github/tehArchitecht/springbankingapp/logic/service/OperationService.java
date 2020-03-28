package com.github.tehArchitecht.springbankingapp.logic.service;

import com.github.tehArchitecht.springbankingapp.data.model.Operation;
import com.github.tehArchitecht.springbankingapp.data.repository.OperationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class OperationService {
    private final OperationRepository operationRepository;

    OperationService(OperationRepository operationRepository) {
        this.operationRepository = operationRepository;
    }

    void add(Operation operation) {
        operationRepository.save(operation);
    }

    List<Operation> findAllByAccountId(UUID accountId) {
        return operationRepository.findBySenderAccount_IdOrReceiverAccount_Id(accountId, accountId);
    }
}
