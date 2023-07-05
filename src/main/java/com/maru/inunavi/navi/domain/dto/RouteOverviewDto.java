package com.maru.inunavi.navi.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RouteOverviewDto {

    private String startLectureName;
    private String endLectureName;

    @JsonProperty(namespace = "endLectureTime")
    private String formattedTime;

    private String totalTime;

    private String distance;

    private String directionString;

}
