package com.maru.inunavi.entity.navi;

import java.util.*;

public class Graph {

    private ArrayList<Node> nodes;

    private final int INF = 1000000000;

    public Graph(){
        this.nodes = new ArrayList<Node>();
    }

    public void addNode(Node v){
        this.nodes.add(v);
    }

    public void addNode(int num, String name){
        Node v = new Node(num, name);
        this.nodes.add(v);
    }

    public void addEdge(int num, int v, int d){ this.nodes.get(num).addEdge(v, d); }

    public ArrayList<Object> dijkstra(int start, int end){
        ArrayList<Object> res = new ArrayList<Object>();
        PriorityQueue<Edge> q = new PriorityQueue<Edge>(100, Comparator.comparing((Edge e) -> e.getDist()));
        q.add(new Edge(start, 0));
        int[] distance = new int[this.nodes.size()];
        Arrays.fill(distance, this.INF);
        int[] before = new int[this.nodes.size()];
        Arrays.fill(before, -1);
        distance[start] = 0;
        while (q.size() > 0) {
            Edge e = q.poll();
            int now = e.getNow();
            int dist = e.getDist();
            if (distance[now] < dist) continue;
            for(ArrayList<Integer> edge:this.nodes.get(now).getEdge()){
                int cost = dist + edge.get(1);
                if (cost < distance[edge.get(0)]) {
                    distance[edge.get(0)] = cost;
                    before[edge.get(0)] = now;
                    q.add(new Edge(edge.get(0), cost));
                }
            }
        }
        /*
        System.out.println(start+" "+end);
        for(int i=0;i<this.nodes.size();i++) {
            System.out.println(distance[i]+" "+before[i]+" "+this.nodes.get(i).getNum());
        }
        */
        int i = end;
        while(i > -1){
            res.add(this.nodes.get(i));
            i = before[i];
        }
        //if(res.size() < 2) return new ArrayList<Object>();
        Collections.reverse(res);
        res.add(distance[end]);
        return res;
    }
}
