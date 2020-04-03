package com.github.tehArchitecht.springbankingapp.logic.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class SetPrimaryAccountRequest {
    @NotNull
    @ApiModelProperty(value = "ID of the new primary account", example = "123e4567-e89b-12d3-a456-426655440000")
    private UUID accountId;
}
