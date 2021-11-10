package com.maru.inunavi.service;

import com.maru.inunavi.entity.navi.Graph;
import com.maru.inunavi.entity.navi.Node;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NaviServiceImpl implements Service{
    public Object execute(HttpServletRequest request){
        Graph graph = new Graph();
        // 밑의 부분 대신 xml에서 객체참조받아야함
        Node node = new Node(0, "정보대");
        node.addEdge(1, 2);
        node.addEdge(2, 3);
        Node node2 = new Node(1, "유아교육과");
        node2.addEdge(2, 3);
        node2.addEdge(3, 5);
        Node node3 = new Node(2, "컨벤션센터");
        node3.addEdge(3, 6);
        Node node4 = new Node(3, "영어영문학과");
        Node node5 = new Node(4, "여자휴게소");
        node5.addEdge(0, 1);
        graph.addNode(node);
        graph.addNode(node2);
        graph.addNode(node3);
        graph.addNode(node4);
        graph.addNode(node5);
        // ~
        int start = Integer.parseInt(request.getParameter("start"));
        int end = Integer.parseInt(request.getParameter("end"));
        ArrayList<Object> res = graph.dijkstra(start, end);
        int dist = (int) res.get(res.size()-1);
        res.remove(res.size()-1);
        Map<String, Object> json = new HashMap<>();
        if(res.size() < 2){
            json.put("impossible", "갈 수 없음");
        }else{
            json.put("load", res);
            json.put("dist", dist);
        }
        return json;
    }
}
