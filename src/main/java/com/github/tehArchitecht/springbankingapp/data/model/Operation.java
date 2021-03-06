package com.github.tehArchitecht.springbankingapp.data.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
public class Operation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Timestamp date;
    private Currency currency;
    private BigDecimal amount;
    private BigDecimal senderInitialBalance;
    private BigDecimal senderResultingBalance;
    private BigDecimal receiverInitialBalance;
    private BigDecimal receiverResultingBalance;

    @ManyToOne
    @JoinColumn(name = "sender_account_id")
    private Account senderAccount;
    @ManyToOne
    @JoinColumn(name = "receiver_account_id")
    private Account receiverAccount;

    public Operation(Timestamp date, Currency currency, Account senderAccount, Account receiverAccount,
                     BigDecimal amount, BigDecimal senderInitialBalance, BigDecimal senderResultingBalance,
                     BigDecimal receiverInitialBalance, BigDecimal receiverResultingBalance) {
        this.date = date;
        this.currency = currency;
        this.senderAccount = senderAccount;
        this.receiverAccount = receiverAccount;
        this.amount = amount;
        this.senderInitialBalance = senderInitialBalance;
        this.senderResultingBalance = senderResultingBalance;
        this.receiverInitialBalance = receiverInitialBalance;
        this.receiverResultingBalance = receiverResultingBalance;
    }
}
