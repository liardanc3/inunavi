package com.maru.inunavi.lecture.domain.entity;

import lombok.*;
import javax.persistence.*;

@Builder
@AllArgsConstructor
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Lecture {

    // A_ 순번
    @Id
    @GeneratedValue
    private int id;

    // C_학과(부)
    @Column(length = 45, nullable = false)
    private String department;

    // D_학년
    @Column(length = 45, nullable = false)
    private String grade;

    // E_이수구분
    @Column(length = 45, nullable = false)
    private String category;

    // F_학수번호
    @Column(length = 45)
    private String number;

    // G_교과목명
    @Column(length = 45)
    private String lectureName;

    // H_담당교수
    @Column(length = 100)
    private String professor;

    // I_강의실(raw)
    @Column(length = 200)
    private String classRoomRaw;

    // J_시간표(raw)
    @Column(length = 200)
    private String classTimeRaw;

    // J_시간표
    @Column(length = 200)
    private String classRoom;
    @Column(length = 200)
    private String classTime;

    // K_수업방법
    @Column(length = 100)
    private String how;

    // L_학점
    @Column(length = 45)
    private String point;


}



