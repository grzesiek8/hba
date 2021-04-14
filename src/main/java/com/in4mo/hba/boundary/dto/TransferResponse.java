package com.in4mo.hba.boundary.dto;

import com.in4mo.hba.entity.Register;
import javafx.util.Pair;

public class TransferResponse {
    Register registerFrom;
    Register registerTo;

    public TransferResponse(Pair<Register, Register> registers) {
        this.registerFrom = registers.getKey();
        this.registerTo = registers.getValue();
    }
}
