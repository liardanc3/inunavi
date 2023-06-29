package com.maru.inunavi.navi.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Place implements Comparable<Place>{

    @Id
    @GeneratedValue
    @Column(name = "place_id")
    private Integer id;

    //placeCode
    @Column(length = 15, nullable=false)
    private String placeCode;

    //title
    @Column(length = 30, nullable = false)
    private String title;

    //sort
    @Column(length = 10, nullable = false)
    private String sort;

    //distance
    @Column(length = 30, nullable = false)
    private String distance;

    //Location
    @Column(length = 100, nullable=false)
    private String epsg4326;

    //time
    @Column(length = 500, nullable = false)
    private String time;

    //callnum
    @Column(length = 13, nullable = false)
    private String callNum;

    //비고
    @Column(length = 50, nullable = false)
    private String etc;


    //setter
    public void setDistance(String distance){
        this.distance=distance;
    }

    @Override
    public int compareTo(Place target){
        return (int)(Double.parseDouble(this.distance) - Double.parseDouble(target.distance));
    }
}
