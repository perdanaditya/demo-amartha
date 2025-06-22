package com.amartha.billing.inbound;

import com.amartha.billing.inbound.variable.LoanV1ControllerTestVariable;
import com.amartha.billing.service.LoanService;
import com.amartha.billing.service.model.LoanServiceRequest;
import com.amartha.billing.util.JSONHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class LoanV1ControllerTest extends LoanV1ControllerTestVariable {

    @InjectMocks
    private LoanV1Controller loanV1Controller;
    @Mock
    private LoanService loanService;
    private MockMvc mockMvc;
    private AutoCloseable autoCloseable;

    @BeforeEach
    public void before() {
        this.autoCloseable = org.mockito.MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(this.loanV1Controller).build();
    }

    @AfterEach
    public void after() throws Exception {
        verifyNoMoreInteractions(this.loanService);
        if (autoCloseable != null) {
            autoCloseable.close();
        }
    }

    @Test
    public void createLoan() throws Exception {
        when(loanService.createLoan(any(LoanServiceRequest.class)))
                .thenReturn(MOCK_UUID);

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .post("/loan/v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSONHelper.toJson(LOAN_REQUEST));

        this.mockMvc.perform(builder)
                .andExpect(status().isOk());
        verify(loanService, times(1))
                .createLoan(any(LoanServiceRequest.class));
    }

    @Test
    public void makePayment() throws Exception {
        doNothing().when(loanService).makePayment(eq(MOCK_UUID), any(BigDecimal.class));

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .put("/loan/v1/make-payment/" + MOCK_UUID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSONHelper.toJson(PAYMENT_REQUEST));

        this.mockMvc.perform(builder)
                .andExpect(status().isOk());
        verify(loanService, times(1))
                .makePayment(eq(MOCK_UUID), any(BigDecimal.class));
    }

    @Test
    public void isDelinquent() throws Exception {
        when(loanService.isDelinquent(MOCK_UUID)).thenReturn(true);

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .get("/loan/v1/is-delinquent/" + MOCK_UUID)
                .accept(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(builder)
                .andExpect(status().isOk());
        verify(loanService, times(1))
                .isDelinquent(MOCK_UUID);
    }

    @Test
    public void getOutstanding() throws Exception {
        when(loanService.getOutstanding(MOCK_UUID))
                .thenReturn(new BigDecimal("5500000"));

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .get("/loan/v1/get-outstanding/" + MOCK_UUID)
                .accept(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(builder)
                .andExpect(status().isOk());
        verify(loanService, times(1))
                .getOutstanding(MOCK_UUID);
    }
}