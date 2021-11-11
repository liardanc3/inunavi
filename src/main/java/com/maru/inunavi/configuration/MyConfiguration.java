package com.maru.inunavi.configuration;

import com.maru.inunavi.entity.navi.Graph;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyConfiguration {

    @Bean
    public Graph graph() {
        Graph graph = new Graph();

        graph.addNode("정보대");
        graph.addEdge(0, 1, 3);
        graph.addEdge(0, 2, 7);

        graph.addNode("유아교육과");
        graph.addEdge(1, 2, 3);
        graph.addEdge(1, 3, 5);

        graph.addNode("컨벤션센터");
        graph.addEdge(2, 3, 6);

        graph.addNode("영어영문학과");

        graph.addNode("여자휴게소");
        graph.addEdge(4, 0, 1);

        return graph;
    }
}
