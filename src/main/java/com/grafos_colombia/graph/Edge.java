package com.grafos_colombia.graph;

// A class to store a graph edge
public class Edge {

    private String src, dest;
    private double weight;

    public Edge(String src, String dest, double weight) {
        this.src = src;
        this.dest = dest;
        this.weight = weight;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getDest() {
        return dest;
    }

    public void setDest(String dest) {
        this.dest = dest;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}
