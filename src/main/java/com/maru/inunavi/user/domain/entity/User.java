package com.maru.inunavi.user.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue
    @Column(name = "user_id")
    private Long id;

    // email
    @Column(length = 100, nullable = false)
    private String email;

    // password
    @Column(length = 100, nullable = false)
    private String password;

    // major
    @Column(length = 50, nullable = false)
    private String major;


}
