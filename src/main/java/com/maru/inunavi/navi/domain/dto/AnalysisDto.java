package com.maru.inunavi.navi.domain.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AnalysisDto {
    private String success;
    private String distancePercentage;
    private String tightnessPercentage;
    private String totalDistance;
}
