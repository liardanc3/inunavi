package com.maru.inunavi.service;

import com.maru.inunavi.entity.Constant;
import com.maru.inunavi.entity.navi.Graph;
import com.maru.inunavi.entity.navi.Node;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NaviServiceImpl implements Service{

    private Graph graph;

    public NaviServiceImpl(){
        this.graph = Constant.graph;
    }

    public Object execute(HttpServletRequest request){


        /*
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
        // ~
        */
        int start = Integer.parseInt(request.getParameter("start"));
        int end = Integer.parseInt(request.getParameter("end"));
        ArrayList<Object> res = this.graph.dijkstra(start, end);
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
