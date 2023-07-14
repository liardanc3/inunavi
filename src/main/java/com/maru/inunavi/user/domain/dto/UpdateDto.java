package com.maru.inunavi.user.domain.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UpdateDto {

    private String success;
    private String email;

}
