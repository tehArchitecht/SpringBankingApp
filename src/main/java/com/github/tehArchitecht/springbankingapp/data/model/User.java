package com.github.tehArchitecht.springbankingapp.data.model;

import javax.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique=true)
    private String name;
    private String password;
    private String address;
    @Column(unique=true)
    private String phoneNumber;

    @OneToOne
    @JoinColumn(name = "primary_account_id")
    private Account primaryAccount;

    public User(String name, String password, String address, String phoneNumber) {
        this.name = name;
        this.password = password;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }
}
