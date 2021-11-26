package com.maru.inunavi.entity;

import lombok.*;
import javax.persistence.*;
import java.util.Comparator;
import java.util.List;

@Builder
@Entity(name="AllLecture")
@AllArgsConstructor
@Getter
public class Lecture {

    // A_ 순번
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
    @Column(length = 45, nullable = true)
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
    @Column(length = 45, nullable = true)
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getLecturename() {
        return lecturename;
    }

    public void setLecturename(String lecturename) {
        this.lecturename = lecturename;
    }

    public String getProfessor() {
        return professor;
    }

    public void setProfessor(String professor) {
        this.professor = professor;
    }

    public String getClassroom_raw() {
        return classroom_raw;
    }

    public void setClassroom_raw(String classroom_raw) {
        this.classroom_raw = classroom_raw;
    }

    public String getClasstime_raw() {
        return classtime_raw;
    }

    public void setClasstime_raw(String classtime_raw) {
        this.classtime_raw = classtime_raw;
    }

    public String getClassroom() {
        return classroom;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }

    public String getClasstime() {
        return classtime;
    }

    public void setClasstime(String classtime) {
        this.classtime = classtime;
    }

    public String getHow() {
        return how;
    }

    public void setHow(String how) {
        this.how = how;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }
}



