package com.in4mo.hba.boundary.rest.exception;

public class RegisterNotFoundException extends Exception {
    public RegisterNotFoundException(String register) {
        super("Register with name " + register + " not found");
    }
}