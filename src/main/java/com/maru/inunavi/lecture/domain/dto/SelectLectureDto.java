package com.maru.inunavi.lecture.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

import java.util.StringTokenizer;

@Data
public class SelectLectureDto {

    private String id;
    private String department;
    private String grade;
    private String category;
    private String number;

    @JsonProperty("lecturename")
    private String lectureName;

    private String professor;
    private String classRoomRaw;
    private String classTimeRaw;
    private String classRoom;
    private String classTime;
    private String how;
    private String point;
    
    private String realTime;

    public SelectLectureDto(String id, String department, String grade, String category, String number, String lectureName, String professor, String classRoomRaw, String classTimeRaw, String classRoom, String classTime, String how, String point) {
        this.id = id;
        this.department = department;
        this.grade = grade;
        this.category = category;
        this.number = number;
        this.lectureName = lectureName;
        this.professor = professor;
        this.classRoomRaw = classRoomRaw;
        this.classTimeRaw = classTimeRaw;
        this.classRoom = classRoom.length() > 2 ? classRoom.substring(0, classRoom.length() - 1) : classRoom;
        this.classTime = classTime;
        this.how = how;
        this.point = point;
        
        if(!classTime.equals("-")){
            this.realTime = "";

            StringTokenizer classTimeToken = new StringTokenizer(classTime.replaceAll(",","-"));
            while(classTimeToken.hasMoreTokens()){

                int start = Integer.parseInt(classTimeToken.nextToken("-"));
                int end = Integer.parseInt(classTimeToken.nextToken("-"));

                int dayOfWeek = start / 48;
                int startHour = (start % 48) / 2;
                int startHalf = (start % 2);
                int endHour = (end % 48) / 2 + 1;
                int endHalf = ~(end % 2);

                switch(dayOfWeek){
                    case 0 : this.realTime += "월 "; break;
                    case 1 : this.realTime += "화 "; break;
                    case 2 : this.realTime += "수 "; break;
                    case 3 : this.realTime += "목 "; break;
                    case 4 : this.realTime += "금 "; break;
                    case 5 : this.realTime += "토 "; break;
                    case 6 : this.realTime += "일 "; break;
                }

                this.realTime += Integer.toString(startHour);
                this.realTime += ":";
                this.realTime += startHalf == 1 ? "30 - " : "00 - ";
                this.realTime += Integer.toString(endHour);
                this.realTime += ":";
                this.realTime += endHalf == 1 ? "30, " : "00, ";
            }
            this.realTime = this.realTime.substring(0, this.realTime.length() - 2);
        }
        else{
            this.realTime = "-";
        }
    }
}
