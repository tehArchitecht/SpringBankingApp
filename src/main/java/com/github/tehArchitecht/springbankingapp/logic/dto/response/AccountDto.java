package com.github.tehArchitecht.springbankingapp.logic.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

import com.github.tehArchitecht.springbankingapp.data.model.Currency;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
public class AccountDto {
    private UUID id;
    private BigDecimal balance;
    private Currency currency;
    private boolean isPrimary;
}


