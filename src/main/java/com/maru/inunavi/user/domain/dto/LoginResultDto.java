package com.maru.inunavi.user.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@Builder
public class LoginResultDto {
    private String success;
    private String email;
    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String major;
}
