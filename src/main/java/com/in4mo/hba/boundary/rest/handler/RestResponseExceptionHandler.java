package com.in4mo.hba.boundary.rest.handler;

import com.google.gson.Gson;
import com.in4mo.hba.boundary.dto.ErrorMessageResponse;
import com.in4mo.hba.boundary.rest.exception.RegisterNotFoundException;
import com.in4mo.hba.boundary.rest.exception.RichUserException;
import com.in4mo.hba.boundary.rest.exception.WrongTransferAmountException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;

@ControllerAdvice
public class RestResponseExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(value = {
            ConstraintViolationException.class,
            WrongTransferAmountException.class,
            RichUserException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<Object> handleArgumentValidationException(Exception exception, WebRequest request) {
        ErrorMessageResponse response = new ErrorMessageResponse(HttpStatus.BAD_REQUEST.value(), exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Gson().toJson(response));
    }

    @ExceptionHandler(value = {RegisterNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected ResponseEntity<Object> handleArgumentNotFoundException(Exception exception, WebRequest request) {
        ErrorMessageResponse response = new ErrorMessageResponse(HttpStatus.NOT_FOUND.value(), exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Gson().toJson(response));
    }
}