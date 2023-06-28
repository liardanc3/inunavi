package com.maru.inunavi.navi.domain.entity;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Navi {

    @Id
    @GeneratedValue
    @Column(name = "navi_id")
    private Long id;

    // 주변노드
    @Column(length = 100, nullable = false)
    private String nearNode;

    // epsg3847
    @Column(length = 70, nullable = false)
    private String epsg3857;

    // epsg4326
    @Column(length = 70, nullable = false)
    private String epsg4326;

    // placeCode
    @Column(length = 250, nullable = false)
    private String placeCode;

    public Navi(String nearNode, String s, String s1, String s2) {
    }

    public void addNearNode(int nearNode){
        this.nearNode+=","+nearNode;
    }
}
