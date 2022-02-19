package com.maru.inunavi.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Builder
@AllArgsConstructor
@Entity(name="Navi")
@Getter
public class Navi {
    public Navi() {}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // 주변노드
    @Column(length = 100, nullable=false)
    private String nearNode;

    // epsg3847
    @Column(length = 70, nullable=false)
    private String epsg3857;

    // epsg4326
    @Column(length = 70, nullable = false)
    private String epsg4326;

    // placeCode
    @Column(length = 250, nullable = false)
    private String placeCode;

    // creator
    public Navi(String nearNode, String epsg3857, String epsg4326, String placeCode) {
        this.nearNode = nearNode;
        this.epsg3857 = epsg3857;
        this.epsg4326 = epsg4326;
        this.placeCode = placeCode;
    }

    public void addNearNode(int nearNode){
        this.nearNode+=","+nearNode;
    }
}
