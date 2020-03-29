package com.github.tehArchitecht.springbankingapp.logic.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class SignInWithNameRequest {
    private String userName;
    private String password;
}
