package com.in4mo.hba.control;

import com.in4mo.hba.boundary.rest.exception.RegisterNotFoundException;
import com.in4mo.hba.boundary.rest.exception.RichUserException;
import com.in4mo.hba.boundary.rest.exception.WrongTransferAmountException;
import com.in4mo.hba.entity.Register;
import com.in4mo.hba.entity.RegisterRepository;
import javafx.util.Pair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class RegisterServiceTest {
    @MockBean
    private RegisterRepository registerRepository;

    @Test
    void getBalanceOfRegisters() {
        //given
        List<Register> registers = new ArrayList<>();
        registers.add(new Register("Wallet", 10000));
        registers.add(new Register("Savings", 500));
        RegisterService registerService = new RegisterService(registerRepository);

        //when
        when(registerRepository.findAll()).thenReturn(registers);

        //then
        Map<String, Integer> registersBalance = registerService.getBalanceOfRegisters();
        assertEquals(registersBalance.size(), 2);
        assertTrue(registersBalance.containsKey("Wallet"));
        assertTrue(registersBalance.containsKey("Savings"));
        assertTrue(registersBalance.containsValue(10000));
        assertTrue(registersBalance.containsValue(500));
        assertEquals(registersBalance.get("Wallet"), new Integer(10000));
        assertEquals(registersBalance.get("Savings"), new Integer(500));
    }

    @Test
    void rechargeRegister() throws RegisterNotFoundException, RichUserException {
        //given
        String registerName = "Wallet";
        Register register = new Register(registerName, 1000);
        RegisterService registerService = new RegisterService(registerRepository);

        //when
        when(registerRepository.getOne(registerName)).thenReturn(register);
        when(registerRepository.save(register)).thenReturn(register);

        //then
        Register registerRecharged = registerService.rechargeRegister("Wallet", 500);
        assertEquals(registerRecharged.getName(), "Wallet");
        assertEquals(registerRecharged.getAmount(), 1500);
    }

    @Test
    void transfer() throws RichUserException, WrongTransferAmountException, RegisterNotFoundException {
        //given
        String registerNameFrom = "Wallet";
        String registerNameTo = "Savings";
        Register registerFrom = new Register(registerNameFrom, 1000);
        Register registerTo = new Register(registerNameTo, 200);
        RegisterService registerService = new RegisterService(registerRepository);

        //when
        when(registerRepository.getOne(registerNameFrom)).thenReturn(registerFrom);
        when(registerRepository.getOne(registerNameTo)).thenReturn(registerTo);
        when(registerRepository.save(registerFrom)).thenReturn(registerFrom);
        when(registerRepository.save(registerTo)).thenReturn(registerTo);

        //then
        Pair<Register, Register> updatedRegisters = registerService.transferAmount("Wallet", "Savings", 500);
        assertEquals(updatedRegisters.getKey().getName(), "Wallet");
        assertEquals(updatedRegisters.getKey().getAmount(), 500);
        assertEquals(updatedRegisters.getValue().getName(), "Savings");
        assertEquals(updatedRegisters.getValue().getAmount(), 700);
    }
}
