package com.github.tehArchitecht.springbankingapp.logic.dto.request;

import com.github.tehArchitecht.springbankingapp.data.model.Currency;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class DepositFundsRequest {
    @NotNull
    @ApiModelProperty(value = "receiver account ID", example = "123e4567-e89b-12d3-a456-426655440000")
    private UUID accountId;
    @NotNull
    @ApiModelProperty("deposit currency")
    private Currency currency;
    @NotNull
    @Positive
    @ApiModelProperty(value = "deposit amount", example = "1")
    private BigDecimal amount;
}
