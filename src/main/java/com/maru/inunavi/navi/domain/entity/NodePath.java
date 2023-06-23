package com.maru.inunavi.navi.domain.entity;

import lombok.*;

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

    @Column(length=1000, nullable = false)
    private String Query;

    @Column(length=10, nullable = false)
    private String isArrived;

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
    public NodePath(String Query, String isArrived,Double dist, String route) {
        this.Query=Query;
        this.isArrived=isArrived;
        this.Dist=dist;
        this.Route=route;
        this.Steps= (int) (1.65*dist);
        this.Time=Steps/100;
    }

}
