package com.maru.inunavi.navi.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaceDto implements Comparable<PlaceDto>{

    private String placeCode;
    private String title;
    private String sort;
    private Double distance;
    private String location;
    private String time;
    private String callNum;

    @Override
    public int compareTo(PlaceDto target){
        return (int)(this.distance - target.distance);
    }
}
