package com.maru.inunavi.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@Entity(name="nodepath")
@AllArgsConstructor
@Getter
public class NodePath {
    public NodePath(){};

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //from-to
    @Column(length = 10, nullable=false)
    private String Src2dst;

    //dist
    @Column(length = 30, nullable = false)
    private Double Dist;

    //route
    @Column(length = 10000, nullable = false)
    private String Route;

    //time
    @Column(length = 4, nullable=false)
    private int Time;

    //steps
    @Column(length = 5, nullable = false)
    private int Steps;

    //creator
    public NodePath(String src2dst, Double dist, String route) {
        this.Src2dst=src2dst;
        this.Dist=dist;
        this.Route=route;
        this.Steps= (int) (1.65*dist);
        this.Time=Steps/100;
    }
}
