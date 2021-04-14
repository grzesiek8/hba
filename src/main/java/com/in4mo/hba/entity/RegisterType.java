package com.in4mo.hba.entity;

public enum RegisterType {
    WALLET("Wallet"),
    SAVINGS("Savings"),
    INSURANCE_POLICY("Insurance policy"),
    FOOD_EXPENSES("Food expenses");

    public final String name;

    RegisterType(String name) {
        this.name = name;
    }
}