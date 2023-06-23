package com.maru.inunavi.user.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;

@Builder
@Entity(name="userinfo")
@Getter
public class UserInfo {

    // email
    @Id
    @Column(length=100, nullable = false)
    private String email;

    // password
    @Column(length = 100, nullable=false)
    private String password;

    // major
    @Column(length = 50, nullable=false)
    private String major;


    // creator
    public UserInfo(){}
    public UserInfo(String email, String password, String major) {
        this.email=email;
        this.password=password;
        this.major = major;
    }
}
