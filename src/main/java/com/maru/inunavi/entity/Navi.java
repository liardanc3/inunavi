package com.maru.inunavi.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

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
    private Long id;

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
    @Column(length = 5, nullable = false)
    private String placeCode;

    // node
    @Column(length = 70, nullable = false)
    private String node;

    // title
    @Column(length = 70, nullable = false)
    private String title;

    // sort
    @Column(length = 20, nullable = false)
    private String sort;

    // creator
    public Navi(List<String> csv, String epsg4326) {
        this.nearNode = csv.get(1);
        this.epsg3857 = csv.get(2);
        this.epsg4326 = epsg4326;
        this.placeCode = csv.get(3);
        this.node = csv.get(4);
        this.title = csv.get(5);
        this.sort = csv.get(6);
    }
}
