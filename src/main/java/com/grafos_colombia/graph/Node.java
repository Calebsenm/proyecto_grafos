package com.grafos_colombia.graph;

// A class to store adjacency list nodes
public class Node {
    public String value;
    public double  weight;

    public Node(String value, double weight) {
        this.value = value;
        this.weight = weight;
    }

    @Override
    public String toString() {
        return this.value + " (" + this.weight + ")";
    }
}
