package com.maru.inunavi.aspect.exceptionhandler.exception;

import lombok.Getter;

@Getter
public class UpdateException extends RuntimeException{

    private final String email;

    public UpdateException(String email) {
        this.email = email;
    }
}
