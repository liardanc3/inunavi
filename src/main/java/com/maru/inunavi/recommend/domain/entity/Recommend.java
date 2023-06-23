package com.maru.inunavi.recommend.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jbosslog.JBossLog;

import javax.persistence.*;
import java.sql.Blob;

@Builder
@Entity(name="recommend")
@AllArgsConstructor
@Getter
public class Recommend {
    public Recommend(){}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Lob
    @Column(length = 50000, nullable = false)
    private String similarityString;

    public Recommend(String similarityString){
        this.similarityString=similarityString;
    }
}
