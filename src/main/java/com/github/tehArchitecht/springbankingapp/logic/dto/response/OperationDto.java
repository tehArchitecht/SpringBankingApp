package com.github.tehArchitecht.springbankingapp.logic.dto.response;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

import com.github.tehArchitecht.springbankingapp.data.model.Currency;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
public class OperationDto {
    @ApiModelProperty("operation date and time")
    private Timestamp date;
    @ApiModelProperty("operation currency")
    private Currency currency;
    @ApiModelProperty("sender account ID")
    private UUID senderAccountId;
    @ApiModelProperty("receiver account ID")
    private UUID receiverAccountId;
    @ApiModelProperty("transaction amount")
    private BigDecimal amount;
    @ApiModelProperty("initial balance")
    private BigDecimal initialBalance;
    @ApiModelProperty("resulting balance")
    private BigDecimal resultingBalance;
}