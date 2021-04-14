package com.in4mo.hba.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Register {
    @Id
    private String name;
    private int amount;

    public Register(String name, int amount) {
        this.name = name;
        this.amount = amount;
    }

    public Register() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void rechargeAmount(int amount) {
        this.amount += amount;
    }

    public void reduceAmount(int amount) {
        this.amount -= amount;
    }

    public boolean hasEnoughAmountForTransfer(int amount) {
        return this.amount >= amount;
    }

    public boolean notEnoughCapacityForTransfer(int amount) {
        long sum = ((long) this.amount) + ((long) amount);
        return sum > Integer.MAX_VALUE;
    }
}