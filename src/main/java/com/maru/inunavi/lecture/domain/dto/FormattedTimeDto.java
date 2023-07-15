package com.maru.inunavi.lecture.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.maru.inunavi.lecture.domain.entity.Lecture;
import lombok.Getter;

@Getter
public class FormattedTimeDto {

    private String id;
    private String department;
    private String grade;
    private String category;
    private String number;

    @JsonProperty(namespace = "lecturename")
    private String lectureName;

    private String professor;

    @JsonProperty(namespace = "classroom_raw")
    private String classRoomRaw;

    @JsonProperty(namespace = "classtime_raw")
    private String classTimeRaw;

    @JsonProperty(namespace = "classroom")
    private String classRoom;

    @JsonProperty(namespace = "classtime")
    private String classTime;

    @JsonProperty(namespace = "how")
    private String how;

    private String point;

    @JsonProperty(namespace = "realTime")
    private String formattedTime;

    public FormattedTimeDto(Lecture lecture) {
        this.id = String.valueOf(lecture.getId());
        this.department = lecture.getDepartment();
        this.grade = lecture.getGrade();
        this.category = lecture.getCategory();
        this.number = lecture.getNumber();
        this.lectureName = lecture.getLectureName();
        this.professor = lecture.getProfessor();
        this.classRoomRaw = lecture.getClassRoomRaw();
        this.classTimeRaw = lecture.getClassTimeRaw();
        this.classRoom = lecture.getClassRoom();
        this.classTime = lecture.getClassTime();
        this.how = lecture.getHow();
        this.point = lecture.getPoint();
        this.formattedTime = lecture.getFormattedTime();
    }
}
