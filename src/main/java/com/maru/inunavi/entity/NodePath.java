package com.maru.inunavi.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@Entity(name="Path")
@AllArgsConstructor
@Getter
public class NodePath {
    public NodePath(){};

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //from-to
    @Column(length = 10, nullable=false)
    private String src2dst;

    //dist
    @Column(length = 30, nullable = false)
    private Double dist;

    //path
    @Column(length = 10000, nullable = false)
    private String path;

    //creator
    public NodePath(String src2dst, Double dist, String path) {
        this.src2dst=src2dst;
        this.dist=dist;
        this.path=path;
    }
}
