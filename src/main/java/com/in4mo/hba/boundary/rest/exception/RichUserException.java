package com.in4mo.hba.boundary.rest.exception;

public class RichUserException extends Exception {
    public RichUserException(int currentAmount, int amountToAdd) {
        super("You have already " + currentAmount + " on this registry and you are trying to add " + amountToAdd +
                " which is out of the registry capacity. You are too rich, you do not need this app! ;)");
    }
}
