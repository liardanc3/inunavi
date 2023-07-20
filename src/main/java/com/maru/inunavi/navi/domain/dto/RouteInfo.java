package com.maru.inunavi.navi.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouteInfo {

    private String startPlaceCode;
    private String endPlaceCode;
    private String startLocation;
    private String endLocation;

    public String getQuery(){
        return startPlaceCode + "," + startLocation + "," + endPlaceCode + "," + endLocation;
    }
}
