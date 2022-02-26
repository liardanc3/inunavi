package com.maru.inunavi.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;

@Builder
@Entity(name="RecommendTable")
@AllArgsConstructor
@Getter
public class RecommendTable {
    public RecommendTable(){}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(length = 20000, nullable = false)
    private String similarityString;

    public RecommendTable(String similarityString){
        this.similarityString=similarityString;
    }
}
