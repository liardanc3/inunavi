package com.maru.inunavi.user.domain.dto;

import com.maru.inunavi.user.domain.entity.User;
import lombok.Data;

@Data
public class SignUpDto {

    private String success;
    private String email;

    public SignUpDto(User user) {
        this.success = "success";
        this.email = user.getEmail();
    }

    public SignUpDto(String email){
        this.success = "false";
        this.email = email;
    }
}
