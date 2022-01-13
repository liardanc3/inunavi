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

    // name
    @Column(length = 45, nullable=false)
    private String BuildingName;

    // location
    @Column(length = 45, nullable=false)
    private String Location;


}
