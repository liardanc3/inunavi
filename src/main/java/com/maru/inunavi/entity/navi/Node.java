package com.maru.inunavi.entity.navi;

import java.util.ArrayList;

public class Node {

    private int num;
    private String name;
    private ArrayList<ArrayList<Integer>> edge;

    public Node(int num, String name){
        this.num = num;
        this.name = name;
        this.edge = new ArrayList<ArrayList<Integer>>();
    }

    public void addEdge(int v, int d){
        ArrayList<Integer> edge = new ArrayList<Integer>();
        edge.add(v);
        edge.add(d);
        this.edge.add(edge);
    }

    public ArrayList<ArrayList<Integer>> getEdge() {
        return edge;
    }

    public void setEdge(ArrayList<ArrayList<Integer>> edge) {
        this.edge = edge;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
