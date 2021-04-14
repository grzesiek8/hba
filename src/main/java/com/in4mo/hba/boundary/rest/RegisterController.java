package com.in4mo.hba.boundary.rest;

import com.google.gson.Gson;
import com.in4mo.hba.boundary.dto.TransferResponse;
import com.in4mo.hba.boundary.rest.exception.RegisterNotFoundException;
import com.in4mo.hba.boundary.rest.exception.RichUserException;
import com.in4mo.hba.boundary.rest.exception.WrongTransferAmountException;
import com.in4mo.hba.control.RegisterService;
import com.in4mo.hba.entity.Register;
import com.in4mo.hba.entity.RegisterType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Min;
import java.util.Map;

@RestController
@Validated
public class RegisterController {
    @Autowired
    private RegisterService registerService;

    @GetMapping("/registersBalance")
    ResponseEntity<Map<String, Integer>> getBalances() {
        return new ResponseEntity<>(registerService.getBalanceOfRegisters(), HttpStatus.OK);
    }

    @PostMapping("/recharge")
    ResponseEntity<Register> rechargeRegister(@RequestParam String register, @RequestParam @Min(0) int amount) throws RegisterNotFoundException, RichUserException {
        return new ResponseEntity<>(registerService.rechargeRegister(register, amount), HttpStatus.OK);
    }

    @PostMapping("/transfer")
    ResponseEntity<String> transfer(
            @RequestParam String registerFrom,
            @RequestParam String registerTo,
            @RequestParam @Min(0) int amount) throws RegisterNotFoundException, WrongTransferAmountException, RichUserException {
        TransferResponse response = new TransferResponse(registerService.transferAmount(registerFrom, registerTo, amount));
        return new ResponseEntity<>(new Gson().toJson(response), HttpStatus.OK);
    }
}