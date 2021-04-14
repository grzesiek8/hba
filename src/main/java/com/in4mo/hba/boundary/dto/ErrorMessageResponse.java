package com.in4mo.hba.boundary.dto;

public class ErrorMessageResponse {
    final String type = "error";
    int code;
    String message;

    public ErrorMessageResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
