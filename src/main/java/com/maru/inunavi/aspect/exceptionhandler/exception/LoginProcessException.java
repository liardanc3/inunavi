package com.maru.inunavi.aspect.exceptionhandler.exception;

import lombok.Getter;

@Getter
public class LoginProcessException extends RuntimeException {

    private final String email;

    public LoginProcessException(String email){
        this.email = email;
    }
}
