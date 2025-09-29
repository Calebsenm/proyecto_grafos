package com.grafos_colombia.graph;

// A class to store a graph edge
public class Edge {

    String src, dest;
    double weight;

    public Edge(String src, String dest, double weight) {
        this.src = src;
        this.dest = dest;
        this.weight = weight;
    }
}
