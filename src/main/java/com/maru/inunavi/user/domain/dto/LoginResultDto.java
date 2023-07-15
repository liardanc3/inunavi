package com.maru.inunavi.user.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Builder
@Getter
public class LoginResultDto {
    private String success;
    private String email;
    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String major;
}
