package com.maru.inunavi.aspect.exceptionhandler;

import com.maru.inunavi.aspect.exceptionhandler.exception.LoginProcessException;
import com.maru.inunavi.aspect.exceptionhandler.exception.SelectClassException;
import com.maru.inunavi.aspect.exceptionhandler.exception.UpdateException;
import com.maru.inunavi.aspect.exceptionhandler.exception.VerifyException;
import com.maru.inunavi.lecture.domain.dto.FormattedTimeDto;
import com.maru.inunavi.user.domain.dto.LoginResultDto;
import com.maru.inunavi.user.domain.dto.UpdateDto;
import com.maru.inunavi.user.domain.dto.VerifyDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;

@RestControllerAdvice
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ResponseExceptionHandler {

    @ExceptionHandler(UpdateException.class)
    public UpdateDto handleUpdateException(UpdateException e){
        return UpdateDto.builder()
                .email(e.getEmail())
                .success("false")
                .build();
    }

    @ExceptionHandler(SelectClassException.class)
    public Map<String, List<FormattedTimeDto>> handleSelectClassException(){
        return Map.of("response", List.of());
    }

    @ExceptionHandler(LoginProcessException.class)
    public LoginResultDto handleLoginException(LoginProcessException e){
        return LoginResultDto.builder()
                .email(e.getEmail())
                .success("false")
                .message("로그인 실패")
                .build();
    }

    @ExceptionHandler(VerifyException.class)
    public VerifyDto handleVerifyException(VerifyException e){
        return VerifyDto.builder()
                .success("false")
                .message(e.getMessage())
                .build();
    }
}
