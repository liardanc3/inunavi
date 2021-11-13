package com.maru.inunavi.entity.navi;

import java.util.ArrayList;

public class Node {

    private int num;
    private String name;
    private ArrayList<ArrayList<Integer>> edges;

    public Node(int num, String name){
        this.num = num;
        this.name = name;
        this.edges = new ArrayList<ArrayList<Integer>>();
    }

    public void addEdge(int v, int d){
        ArrayList<Integer> edge = new ArrayList<Integer>();
        edge.add(v);
        edge.add(d);
        this.edges.add(edge);
    }

    public ArrayList<ArrayList<Integer>> getEdges() {
        return edges;
    }

    public void setEdges(ArrayList<ArrayList<Integer>> edges) {
        this.edges = edges;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
