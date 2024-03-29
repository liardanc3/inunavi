package com.maru.inunavi.navi.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Place {

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
    private String location;

    //time
    @Column(length = 500, nullable = false)
    private String time;

    //callnum
    @Column(length = 13, nullable = false)
    private String callNum;

    //비고
    @Column(length = 50, nullable = false)
    private String etc;

}
