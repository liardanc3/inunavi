package com.maru.inunavi.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import javax.persistence.*;

@Builder
@AllArgsConstructor
@Entity(name="UserLecture")
@Getter
public class UserLecture {

    // 아이디
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //이메일
    @Column(length = 100, nullable=false)
    private String email;

    // 과목번호
    @Column(length = 45, nullable=false)
    private String lectureId;

    public UserLecture(){}
    public UserLecture(String email, String lectureId) {
        this.email=email;
        this.lectureId=lectureId;
    }

}
