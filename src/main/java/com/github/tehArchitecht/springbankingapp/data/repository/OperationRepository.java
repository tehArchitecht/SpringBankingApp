package com.github.tehArchitecht.springbankingapp.data.repository;

import com.github.tehArchitecht.springbankingapp.data.model.Operation;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface OperationRepository extends CrudRepository<Operation, Long> {
    List<Operation> findBySenderAccount_IdOrReceiverAccount_Id(UUID senderId, UUID receiverId);
}