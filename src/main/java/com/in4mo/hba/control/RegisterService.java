package com.in4mo.hba.control;

import com.in4mo.hba.boundary.rest.exception.RegisterNotFoundException;
import com.in4mo.hba.boundary.rest.exception.RichUserException;
import com.in4mo.hba.boundary.rest.exception.WrongTransferAmountException;
import com.in4mo.hba.entity.Register;
import com.in4mo.hba.entity.RegisterRepository;
import com.in4mo.hba.entity.RegisterType;
import javafx.util.Pair;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RegisterService {
    private final RegisterRepository registerRepository;

    public RegisterService(RegisterRepository registerRepository) {
        this.registerRepository = registerRepository;
    }

    public Map<String, Integer> getBalanceOfRegisters() {
        List<Register> registers = registerRepository.findAll();
        Map<String, Integer> balances = new HashMap<>();
        for (Register register : registers) {
            balances.put(register.getName(), register.getAmount());
        }
        return balances;
    }

    public Register rechargeRegister(String registerName, int amount) throws RichUserException, RegisterNotFoundException {
        if (ifRegisterNotExist(registerName)) throw new RegisterNotFoundException(registerName);

        Register register = registerRepository.getOne(registerName);
        if (register.notEnoughCapacityForTransfer(amount))
            throw new RichUserException(register.getAmount(), amount);
        register.rechargeAmount(amount);
        return registerRepository.save(register);
    }

    public Pair<Register, Register> transferAmount(String registerFromName, String registerToName, int amount) throws WrongTransferAmountException, RichUserException, RegisterNotFoundException {
        if (ifRegisterNotExist(registerFromName)) throw new RegisterNotFoundException(registerFromName);
        if (ifRegisterNotExist(registerToName)) throw new RegisterNotFoundException(registerToName);

        Register registerFrom = registerRepository.getOne(registerFromName);
        Register registerTo = registerRepository.getOne(registerToName);

        if (!registerFrom.hasEnoughAmountForTransfer(amount))
            throw new WrongTransferAmountException(amount, registerFromName);
        if (registerFrom.notEnoughCapacityForTransfer(amount))
            throw new RichUserException(registerTo.getAmount(), amount);

        registerFrom.reduceAmount(amount);
        registerTo.rechargeAmount(amount);

        return new Pair<>(registerRepository.save(registerFrom), registerRepository.save(registerTo));
    }

    private boolean ifRegisterNotExist(String registerName) {
        RegisterType registerType = null;

        for (RegisterType rt : RegisterType.values()) {
            if (rt.name.equals(registerName)) {
                registerType = rt;
            }
        }

        return registerType == null;
    }
}
