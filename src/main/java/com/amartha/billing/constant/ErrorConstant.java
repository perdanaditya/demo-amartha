package com.amartha.billing.constant;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorConstant {

    ANNUAL_INTEREST_RATES_NOT_YET_AVAILABLE(HttpStatus.NOT_FOUND, "BLG404001", "Annual Interest Rate not yet available, please set first."),
    CUSTOMER_NOT_FOUND(HttpStatus.NOT_FOUND, "BLG404002", "Customer not found."),
    LOAN_NOT_FOUND(HttpStatus.NOT_FOUND, "BLG404003", "Loan not found."),
    PAID_AMOUNT_NOT_EQUAL_WITH_CURRENT_REPAYMENT_AMOUNT(HttpStatus.BAD_REQUEST, "BLG400001", "Amount not equal with current repayment amount, paid amount %s, repayment amount %s"),
    NO_REPAYMENT_SCHEDULES_FOUND(HttpStatus.BAD_REQUEST, "BLG400002", "No repayment schedules found"),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    ErrorConstant(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
