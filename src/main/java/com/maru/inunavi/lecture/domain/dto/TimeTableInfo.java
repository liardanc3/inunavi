package com.maru.inunavi.lecture.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public class TimeTableInfo {

    private String year;
    private String semester;

    @JsonProperty("majorArrayString")
    private String majors;

    @JsonProperty("CSEArrayString")
    private String basicGenerals;

    @JsonProperty("categoryListString")
    private String categories;

}
