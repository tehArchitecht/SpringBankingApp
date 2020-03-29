package com.github.tehArchitecht.springbankingapp.data.model;

import java.math.BigDecimal;
import java.util.UUID;

import javax.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
public class Account {
    @Id
    @GeneratedValue
    private UUID id;
    private BigDecimal balance;
    private Currency currency;

    @ManyToOne
    private User user;

    public Account(User user, Currency currency) {
        this.user = user;
        this.currency = currency;
        this.balance = new BigDecimal("0.000");
    }
}
