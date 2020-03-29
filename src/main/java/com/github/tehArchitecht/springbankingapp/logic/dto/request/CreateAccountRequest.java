package com.github.tehArchitecht.springbankingapp.logic.dto.request;

import com.github.tehArchitecht.springbankingapp.data.model.Currency;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class CreateAccountRequest {
    private Currency currency;
}
