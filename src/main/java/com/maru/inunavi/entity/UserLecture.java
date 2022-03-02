package com.maru.inunavi.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import javax.persistence.*;

@Builder
@AllArgsConstructor
@Entity(name="userlecture")
@Getter
public class UserLecture {

    // 아이디
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //이메일
    @Column(length = 100, nullable=false)
    private String email;

    // 과목 인덱스
    @Column(length = 45, nullable=false)
    private int lectureIdx;

    public UserLecture(){}
    public UserLecture(String email, int lectureIdx) {
        this.email=email;
        this.lectureIdx=lectureIdx;
    }

}
