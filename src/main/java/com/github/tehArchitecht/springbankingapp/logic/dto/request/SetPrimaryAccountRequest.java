package com.github.tehArchitecht.springbankingapp.logic.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class SetPrimaryAccountRequest {
    private UUID accountId;
}
