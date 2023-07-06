package com.maru.inunavi.recommend.domain.entity;

import com.maru.inunavi.lecture.domain.entity.Lecture;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.jbosslog.JBossLog;

import javax.persistence.*;
import java.sql.Blob;

@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Recommend {

    @Id
    @GeneratedValue
    @Column(name = "recommend_id")
    private Long id;

    @OneToOne(mappedBy = "recommend", fetch = FetchType.LAZY)
    private Lecture lecture;

    @Lob
    @Column(length = 50000, nullable = false)
    private String similarity;

}
