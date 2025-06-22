package com.amartha.billing.exception;

import com.amartha.billing.constant.ErrorConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AppException extends RuntimeException{

    private ErrorConstant error;

    private String message;

    public AppException(ErrorConstant error) {
        super(error.getMessage());
        this.error = error;
        this.message = error.getMessage();
    }

    public AppException(ErrorConstant error, String message) {
        super(error.getMessage());
        this.error = error;
        this.message = message;
    }
}
