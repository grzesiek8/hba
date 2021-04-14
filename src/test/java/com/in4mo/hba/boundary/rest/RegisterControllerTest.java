package com.in4mo.hba.boundary.rest;

import com.in4mo.hba.boundary.rest.exception.RegisterNotFoundException;
import com.in4mo.hba.boundary.rest.exception.RichUserException;
import com.in4mo.hba.boundary.rest.exception.WrongTransferAmountException;
import com.in4mo.hba.control.RegisterService;
import com.in4mo.hba.entity.Register;
import javafx.util.Pair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import static org.mockito.Mockito.when;
import java.util.HashMap;
import java.util.Map;

@ExtendWith(SpringExtension.class)
@WebMvcTest
public class RegisterControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    private RegisterService registerService;

    @Test
    void getRegistersBalanceWithSuccess() throws Exception {
        //given
        Map<String, Integer> balances = new HashMap<>();
        balances.put("Wallet", 1000);
        balances.put("Savings", 5500);
        balances.put("Insurance policy", 500);
        balances.put("Food expenses", 1500);

        //when
        when(registerService.getBalanceOfRegisters()).thenReturn(balances);

        //then
        mockMvc.perform(MockMvcRequestBuilders.get("/registersBalance")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("{\"Insurance policy\":500,\"Savings\":5500,\"Wallet\":1000,\"Food expenses\":1500}"));
    }

    @Test
    void rechargeRegisterWithSuccess() throws Exception {
        //given
        Register register = new Register("Wallet", 2000);

        //when
        when(registerService.rechargeRegister("Wallet", 1000)).thenReturn(register);

        //then
        mockMvc.perform(MockMvcRequestBuilders.post("/recharge")
                .param("register", "Wallet")
                .param("amount", "1000")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("{\"name\":\"Wallet\",\"amount\":2000}"));
    }

    @Test
    void rechargeRegisterWithWrongRegisterName() throws Exception {
        //given
        String registerName = "Wallett";

        //when
        when(registerService.rechargeRegister(registerName, 1000)).thenThrow(new RegisterNotFoundException(registerName));

        //then
        mockMvc.perform(MockMvcRequestBuilders.post("/recharge")
                .param("register", "Wallett")
                .param("amount", "1000")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("{\"type\":\"error\",\"code\":404,\"message\":\"Register with name Wallett not found\"}"));
    }

    @Test
    void rechargeRegisterWithTooSmallAmountValue() throws Exception {
        //given
        int amount = -1;

        //when
        when(registerService.rechargeRegister("Wallet", amount)).thenThrow(new RuntimeException());

        //then
        mockMvc.perform(MockMvcRequestBuilders.post("/recharge")
                .param("register", "Wallet")
                .param("amount", "-1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("{\"type\":\"error\",\"code\":400,\"message\":\"rechargeRegister.amount: must be greater than or equal to 0\"}"));
    }

    @Test
    void rechargeRegisterWithTooBigAmountValue() throws Exception {
        //given
        int amount = 300000000;

        //when
        when(registerService.rechargeRegister("Wallet", amount))
                .thenThrow(new RichUserException(0, amount));

        //then
        mockMvc.perform(MockMvcRequestBuilders.post("/recharge")
                .param("register", "Wallet")
                .param("amount", "300000000")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("{\"type\":\"error\",\"code\":400,\"message\":\"You have already 0 on this registry and you are trying to add 300000000 which is out of the registry capacity. You are too rich, you do not need this app! ;)\"}"));
    }

    @Test
    void transferAmountWithSuccess() throws Exception {
        //given
        Register registerFrom = new Register("Wallet", 2000);
        Register registerTo = new Register("Savings", 3000);

        //when
        when(registerService.transferAmount("Wallet", "Savings", 1000))
                .thenReturn(new Pair<>(registerFrom, registerTo));

        //then
        mockMvc.perform(MockMvcRequestBuilders.post("/transfer")
                .param("registerFrom", "Wallet")
                .param("registerTo", "Savings")
                .param("amount", "1000")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("{\"registerFrom\":{\"name\":\"Wallet\",\"amount\":2000},\"registerTo\":{\"name\":\"Savings\",\"amount\":3000}}"));
    }

    @Test
    void transferAmountWithWrongRegisterFromName() throws Exception {
        //given
        String registerFromName = "Wallett";

        //when
        when(registerService.transferAmount(registerFromName, "Savings", 1000)).thenThrow(new RegisterNotFoundException(registerFromName));

        //then
        mockMvc.perform(MockMvcRequestBuilders.post("/transfer")
                .param("registerFrom", "Wallett")
                .param("registerTo", "Savings")
                .param("amount", "1000")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("{\"type\":\"error\",\"code\":404,\"message\":\"Register with name Wallett not found\"}"));
    }

    @Test
    void transferAmountWithWrongRegisterToName() throws Exception {
        //given
        String registerTo = "Saving";

        //when
        when(registerService.transferAmount("Wallet", registerTo, 1000))
                .thenThrow(new RegisterNotFoundException(registerTo));

        //then
        mockMvc.perform(MockMvcRequestBuilders.post("/transfer")
                .param("registerFrom", "Wallet")
                .param("registerTo", "Saving")
                .param("amount", "1000")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("{\"type\":\"error\",\"code\":404,\"message\":\"Register with name Saving not found\"}"));
    }

    @Test
    void transferAmountWithNotEnoughBalanceOnRegisterFrom() throws Exception {
        //given
        String registerName = "Wallet";
        int amount = 10;

        //when
        when(registerService.transferAmount(registerName, "Savings", amount))
                .thenThrow(new WrongTransferAmountException(amount, registerName));

        //then
        mockMvc.perform(MockMvcRequestBuilders.post("/transfer")
                .param("registerFrom", "Wallet")
                .param("registerTo", "Savings")
                .param("amount", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("{\"type\":\"error\",\"code\":400,\"message\":\"Given amount: 10 cannot be transferred because of too small balance on the register: Wallet\"}"));
    }

    @Test
    void transferAmountWithNotEnoughCapacityOnRegisterTo() throws Exception {
        //given
        String registerTo = "Savings";
        int amount = 100000000;

        //when
        when(registerService.transferAmount("Wallet", registerTo, amount))
                .thenThrow(new RichUserException(20000000, amount));

        //then
        mockMvc.perform(MockMvcRequestBuilders.post("/transfer")
                .param("registerFrom", "Wallet")
                .param("registerTo", "Savings")
                .param("amount", "100000000")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("{\"type\":\"error\",\"code\":400,\"message\":\"You have already 20000000 on this registry and you are trying to add 100000000 which is out of the registry capacity. You are too rich, you do not need this app! ;)\"}"));
    }

}