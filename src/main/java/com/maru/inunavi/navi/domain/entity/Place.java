package com.maru.inunavi.navi.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;
import java.util.List;

@Builder
@Entity(name="place")
@AllArgsConstructor
@Getter
public class Place implements Comparable<Place>{
    public Place(){};

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    //creator
    public Place(List<String> csv) {
        this.placeCode = csv.get(0);
        this.title = csv.get(1);
        this.sort = csv.get(2);
        this.distance = csv.get(3);
        this.epsg4326 = csv.get(4);
        this.time = csv.get(5);
        this.callNum = csv.get(6);
        this.etc = csv.get(7);
    }

    //setter
    public void setDistance(String distance){
        this.distance=distance;
    }

    @Override
    public int compareTo(Place target){
        return (int)(Double.parseDouble(this.distance) - Double.parseDouble(target.distance));
    }
}
