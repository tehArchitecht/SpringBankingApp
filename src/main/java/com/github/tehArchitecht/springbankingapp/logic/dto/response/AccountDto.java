package com.github.tehArchitecht.springbankingapp.logic.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

import com.github.tehArchitecht.springbankingapp.data.model.Currency;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
public class AccountDto {
    @ApiModelProperty("account id")
    private UUID id;
    @ApiModelProperty("account balance")
    private BigDecimal balance;
    @ApiModelProperty("account currency")
    private Currency currency;
    @ApiModelProperty("flag that indicates whether the account is used for receiving transactions or not")
    private boolean isPrimary;
}


