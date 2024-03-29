package com.maru.inunavi.lecture.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TimeTableInfoDto {

    private String year;
    private String semester;

    @JsonProperty("majorArrayString")
    private String majors;

    @JsonProperty("CSEArrayString")
    private String basicGenerals;

    @JsonProperty("categoryListString")
    private String categories;

}
