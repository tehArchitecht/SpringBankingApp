package com.github.tehArchitecht.springbankingapp.service;

import com.github.tehArchitecht.springbankingapp.data.model.Operation;
import com.github.tehArchitecht.springbankingapp.data.repository.OperationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class OperationService {
    private final OperationRepository operationRepository;

    public OperationService(OperationRepository operationRepository) {
        this.operationRepository = operationRepository;
    }

    public void add(Operation operation) {
        operationRepository.save(operation);
    }

    public List<Operation> findAllByAccountId(UUID accountId) {
        return operationRepository.findBySenderAccount_IdOrReceiverAccount_Id(accountId, accountId);
    }
}
