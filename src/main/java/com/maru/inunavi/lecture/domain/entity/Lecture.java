package com.maru.inunavi.lecture.domain.entity;

import com.maru.inunavi.user.domain.entity.User;
import lombok.*;
import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Builder
@AllArgsConstructor
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Lecture {

    // 순번
    @Id
    @GeneratedValue
    @Column(name = "lecture_id")
    private Long id;

    // 학과(부)
    @Column(length = 45, nullable = false)
    private String department;

    // 학년
    @Column(length = 45, nullable = false)
    private String grade;

    // 이수구분
    @Column(length = 45, nullable = false)
    private String category;

    // 학수번호
    @Column(length = 45)
    private String number;

    // 교과목명
    @Column(length = 45)
    private String lectureName;

    // 담당교수
    @Column(length = 100)
    private String professor;

    // 강의실(raw)
    @Column(length = 200)
    private String classRoomRaw;

    // 시간표(raw)
    @Column(length = 200)
    private String classTimeRaw;

    // 시간표
    @Column(length = 200)
    private String classRoom;
    @Column(length = 200)
    private String classTime;

    // 수업방법
    @Column(length = 100)
    private String how;

    // 학점
    @Column(length = 45)
    private String point;

    @ManyToMany(mappedBy = "lectures")
    private Set<User> users = new HashSet<>();

    // ------------------------------------------------ //

    public List<Integer> getStartTimeList(){
        return classTime.equals("-") ? null :
                Arrays.stream(classTime.split(","))
                        .map(classTimeToken -> Integer.parseInt(classTimeToken.split("-")[0]))
                        .collect(Collectors.toList());
    }

}
