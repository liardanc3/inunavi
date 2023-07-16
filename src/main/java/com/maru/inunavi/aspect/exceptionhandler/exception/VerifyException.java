package com.maru.inunavi.aspect.exceptionhandler.exception;

import lombok.Getter;

@Getter
public class VerifyException extends RuntimeException{

    public VerifyException(String message) {
        super(message);
    }
}
