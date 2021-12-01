package com.maru.inunavi.configuration;

import com.maru.inunavi.entity.Constant;
import com.maru.inunavi.entity.navi.Graph;
import com.maru.inunavi.service.NaviService;
import com.maru.inunavi.service.NaviServiceImpl;
import com.maru.inunavi.service.UserService;
import com.maru.inunavi.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class MyConfiguration {

    @Autowired
    public void setTemplate(DataSource dataSource){ Constant.template = new JdbcTemplate(dataSource); }

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

    @Bean
    public UserService userService() {
        UserService userService = new UserServiceImpl();
        return userService;
    }

    @Bean
    public NaviService naviService() {
        NaviService naviService = new NaviServiceImpl();
        return naviService;
    }
}
