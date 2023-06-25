package com.maru.inunavi.lecture.domain.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TimeTableInfoDto {

    private static final String response = "response";
    private List<TimeTableInfo> timeTableInfoList;

    public TimeTableInfoDto(TimeTableInfo timeTableInfo) {
        this.timeTableInfoList = List.of(timeTableInfo);
    }
}
