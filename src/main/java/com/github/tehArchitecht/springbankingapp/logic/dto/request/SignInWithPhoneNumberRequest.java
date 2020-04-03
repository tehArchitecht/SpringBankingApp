package com.github.tehArchitecht.springbankingapp.logic.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class SignInWithPhoneNumberRequest {
    @NotNull
    @NotEmpty
    @ApiModelProperty(value = "phone number", example = "01234567890")
    private String phoneNumber;
    @NotNull
    @NotEmpty
    @ApiModelProperty(value = "password", example = "password")
    private String password;
}
