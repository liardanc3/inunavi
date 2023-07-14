package com.maru.inunavi.aspect.exceptionhandler;

import com.maru.inunavi.aspect.exceptionhandler.exception.SelectClassException;
import com.maru.inunavi.aspect.exceptionhandler.exception.UpdateException;
import com.maru.inunavi.lecture.domain.dto.FormattedTimeDto;
import com.maru.inunavi.user.domain.dto.UpdateDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class ResponseExceptionHandler {

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(UpdateException.class)
    public UpdateDto handleUpdateException(UpdateException e){
        return UpdateDto.builder()
                .email(e.getEmail())
                .success("false")
                .build();
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(SelectClassException.class)
    public Map<String, List<FormattedTimeDto>> handleSelectClassException(){
        return Map.of("response", List.of());
    }
}
