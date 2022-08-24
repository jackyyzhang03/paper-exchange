package com.example.paperexchange.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class InsufficientSharesException extends RuntimeException {
    public InsufficientSharesException() {
        super("Invalid sell order due to insufficient number of owned shares");
    }
}
