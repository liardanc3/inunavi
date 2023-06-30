package com.maru.inunavi.navi.domain.entity;

import lombok.*;

import javax.persistence.*;

@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Path {

    @Id
    @GeneratedValue
    @Column(name = "path_id")
    private Integer id;

    @Column(length = 1000, nullable = false)
    private String query;

    @Column(length = 10, nullable = false)
    private String isArrived;

    @Column(length = 30, nullable = false)
    private Double dist;

    @Column(length = 10000, nullable = false)
    private String route;
}
