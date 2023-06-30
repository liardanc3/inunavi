package com.maru.inunavi.navi.domain.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class NextPlaceDto {

    private String success;
    private String nextPlaceCode;
    private String nextPlaceLocationString;
    private String nextPlaceTitle;
}
