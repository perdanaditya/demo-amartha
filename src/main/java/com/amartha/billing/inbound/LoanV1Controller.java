package com.amartha.billing.inbound;

import com.amartha.billing.config.MapStructConverter;
import com.amartha.billing.inbound.model.CreateLoanRequest;
import com.amartha.billing.inbound.model.MakePaymentRequest;
import com.amartha.billing.service.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/loan/v1")
public class LoanV1Controller {

    private final LoanService loanService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UUID> createLoan(@RequestBody @Valid CreateLoanRequest request) {
        UUID result = loanService.createLoan(MapStructConverter.MAPPER.toLoanServiceRequest(request));
        return ResponseEntity.ok(result);
    }

    @PutMapping(value = "/make-payment/{loanId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void makePayment(@PathVariable UUID loanId, @RequestBody MakePaymentRequest request) {
        loanService.makePayment(loanId, request.getPaidAmount());
    }

    @GetMapping(value = "/is-delinquent/{loanId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> isDelinquent(@PathVariable("loanId") UUID loanId) {
        return ResponseEntity.ok(loanService.isDelinquent(loanId));
    }

    @GetMapping(value = "/get-outstanding/{loanId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BigDecimal> getOutstanding(@PathVariable("loanId") UUID id) {
        return ResponseEntity.ok(loanService.getOutstanding(id));
    }
}
