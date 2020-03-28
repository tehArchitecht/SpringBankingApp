package com.github.tehArchitecht.springbankingapp.data.model;

import javax.persistence.*;
import lombok.*;

@Entity
@Data
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

    public User(String name, String password, String address, String phoneNumber) {
        this.name = name;
        this.password = password;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }
}
