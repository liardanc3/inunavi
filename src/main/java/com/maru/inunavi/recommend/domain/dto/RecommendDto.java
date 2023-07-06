package com.maru.inunavi.recommend.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.maru.inunavi.lecture.domain.entity.Lecture;
import lombok.Builder;

import java.util.StringTokenizer;

public class RecommendDto {

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

    public RecommendDto(Lecture lecture) {
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

        if (lecture.getClassTime().equals("-")) {
            this.formattedTime = "-";
        }
        else {
            StringBuilder classTime = new StringBuilder();

            StringTokenizer classTimeToken = new StringTokenizer(lecture.getClassTime().replaceAll(",", "-"));
            while (classTimeToken.hasMoreTokens()) {
                int start = Integer.parseInt(classTimeToken.nextToken("-"));
                int end = Integer.parseInt(classTimeToken.nextToken("-"));

                int dayOfWeek = start / 48;
                int startHour = (start % 48) / 2;
                int startHalf = (start % 2);
                int endHour = (end % 48) / 2 + 1;
                int endHalf = ~(end % 2);

                switch (dayOfWeek) {
                    case 0: classTime.append("월 "); break;
                    case 1: classTime.append("화 "); break;
                    case 2: classTime.append("수 "); break;
                    case 3: classTime.append("목 "); break;
                    case 4: classTime.append("금 "); break;
                    case 5: classTime.append("토 "); break;
                    case 6: classTime.append("일 "); break;
                }

                classTime.append(startHour);
                classTime.append(":");
                classTime.append(startHalf == 1 ? "30 - " : "00 - ");
                classTime.append(endHour);
                classTime.append(":");
                classTime.append(endHalf == 1 ? "30, " : "00, ");
            }
            this.formattedTime = classTime.substring(0, classTime.length() - 2);
        }
    }
}
