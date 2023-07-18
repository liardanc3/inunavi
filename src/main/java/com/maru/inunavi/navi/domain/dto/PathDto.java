package com.maru.inunavi.navi.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import com.maru.inunavi.navi.domain.entity.Path;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PathDto {

    private Integer id;
    private String query;
    private String isArrived;

    @JsonProperty("Dist")
    private Double dist;

    @JsonProperty("Route")
    private String route;

    @JsonProperty("time")
    private int time;

    @JsonProperty("Steps")
    private int steps;

    @JsonIgnore
    private Integer srcId;

    public PathDto(Path path){
        this.id = path.getId();
        this.query = path.getQuery();
        this.isArrived = path.getIsArrived();
        this.dist = path.getDist();
        this.route = path.getRoute();

        this.steps = (int) (1.65 * this.dist);
        this.time = this.steps / 100;

        this.srcId = path.getSrcId();
    }

}
