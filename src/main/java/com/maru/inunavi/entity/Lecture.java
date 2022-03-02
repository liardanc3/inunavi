package com.maru.inunavi.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import javax.persistence.*;
import java.util.Comparator;
import java.util.List;

@Builder
@Entity(name="lecture")
@AllArgsConstructor
@Getter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Lecture {
    // A_ 순번
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    @Column(length = 45, nullable = true)
    private String number;

    // G_교과목명
    @Column(length = 45, nullable = true)
    private String lecturename;

    // H_담당교수
    @Column(length = 100, nullable = true)
    private String professor;

    // I_강의실(raw)
    @Column(length = 200, nullable = true)
    private String classroom_raw;

    // J_시간표(raw)
    @Column(length = 200, nullable = true)
    private String classtime_raw;

    // J_시간표
    @Column(length = 200, nullable = true)
    private String classroom;
    @Column(length = 200, nullable = true)
    private String classtime;

    // K_수업방법
    @Column(length = 100, nullable = true)
    private String how;

    // L_학점
    @Column(length = 45, nullable = true)
    private String point;

    // 생성자
    public Lecture() {}
    public Lecture(List<String> csv, String classroom, String classtime) {
        this.department = csv.get(0);
        this.grade = csv.get(1);
        this.category = csv.get(2);
        this.number = csv.get(3);
        this.lecturename = csv.get(4);
        this.professor = csv.get(5);
        this.classroom_raw = csv.get(6);
        this.classtime_raw = csv.get(7);
        this.how = csv.get(8);
        this.point = csv.get(9);

        this.classroom = classroom;
        this.classtime = classtime;
    }
}



