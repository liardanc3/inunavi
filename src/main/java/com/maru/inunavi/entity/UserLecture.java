package com.maru.inunavi.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import javax.persistence.*;

@Builder
@Entity(name="UserLecture")
@AllArgsConstructor
@Getter
public class UserLecture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 아이디
    @Column(length = 45, nullable=false)
    private String UserID;

    // 과목번호
    @Column(length = 45, nullable=false)
    private String LectureID;

    public UserLecture(){}
    public UserLecture(String UserID, String LectureID) {
        this.UserID = UserID;
        this.LectureID = LectureID;
    }
}
