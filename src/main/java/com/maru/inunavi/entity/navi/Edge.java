package com.maru.inunavi.entity.navi;

public class Edge {
    private int now;
    private int dist;

    public Edge(int now, int dist){
        this.now = now;
        this.dist = dist;
    }

    public int getDist() {
        return dist;
    }

    public void setDist(int dist) {
        this.dist = dist;
    }

    public int getNow() {
        return now;
    }

    public void setNow(int now) {
        this.now = now;
    }

}
