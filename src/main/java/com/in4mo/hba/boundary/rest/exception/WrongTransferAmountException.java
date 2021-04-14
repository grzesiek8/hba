package com.in4mo.hba.boundary.rest.exception;

public class WrongTransferAmountException extends Exception {
    public WrongTransferAmountException(int amount, String registerName) {
        super("Given amount: " + amount + " cannot be transferred because of too small balance on the register: " + registerName);
    }
}