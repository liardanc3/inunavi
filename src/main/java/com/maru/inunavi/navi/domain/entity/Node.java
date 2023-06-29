package com.maru.inunavi.navi.domain.entity;

import lombok.*;

import javax.persistence.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
public class Node {

    @Id
    @GeneratedValue
    @Column(name = "navi_id")
    private Integer id;

    // 주변노드
    @Column(length = 100, nullable = false)
    private String nearNode;

    // epsg4326 lng
    @Column(length = 70, nullable = false)
    private String lng4326;

    // epsg4326 lat
    @Column(length = 70, nullable = false)
    private String lat4326;

    // placeCode
    @Column(length = 250, nullable = false)
    private String placeCode;

    public Node(String nearNode, String s, String s1, String s2) {
    }

    public void addNearNode(int nearNode){
        this.nearNode+=","+nearNode;
    }
}
