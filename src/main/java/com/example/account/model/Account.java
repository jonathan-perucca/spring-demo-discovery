package com.example.account.model;

import com.example.Model;
import lombok.*;

import javax.persistence.Entity;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)

@Entity
public class Account extends Model {

    private int balance;

    public boolean wouldNeedCredit(int amount) {
        return getBalance() + amount < 0;
    }
}
