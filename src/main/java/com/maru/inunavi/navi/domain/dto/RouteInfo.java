package com.maru.inunavi.navi.domain.dto;

import lombok.Data;

@Data
public class RouteInfo {

    private String startPlaceCode;
    private String endPlaceCode;
    private String startLocation;
    private String endLocation;

    public String getQuery(){
        return startPlaceCode + "," + startLocation + "," + endPlaceCode + "," + endLocation;
    }
}
