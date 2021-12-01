package com.maru.inunavi.service;

import com.maru.inunavi.entity.Constant;
import com.maru.inunavi.entity.navi.Graph;
import com.maru.inunavi.entity.navi.Node;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NaviServiceImpl implements NaviService{

    @Autowired
    private Graph graph;

    public Map<String, Object> showLoad(int start, int end){

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
