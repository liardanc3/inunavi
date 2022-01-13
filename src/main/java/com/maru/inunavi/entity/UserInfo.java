package com.maru.inunavi.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;

@Builder
@Entity(name="UserInfo")
@Getter
public class UserInfo {

    // id
    @Id
    @Column(length = 45, nullable=false)
    private String UserID;

    // name
    @Column(length = 45, nullable=false)
    private String UserName;

    // password
    @Column(length = 100, nullable=false)
    private String UserPW;

    // email
    @Column(length=100, nullable = false)
    private String UserEmail;

    // creator
    public UserInfo(){}
    public UserInfo(String UserID, String UserName, String UserPW, String UserEmail) {
        this.UserID = UserID;
        this.UserName=UserName;
        this.UserPW=UserPW;
        this.UserEmail=UserEmail;
    }
}
