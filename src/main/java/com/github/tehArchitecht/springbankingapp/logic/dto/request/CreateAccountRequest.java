package com.github.tehArchitecht.springbankingapp.logic.dto.request;

import com.github.tehArchitecht.springbankingapp.data.model.Currency;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class CreateAccountRequest {
    @NotNull
    private Currency currency;
}
