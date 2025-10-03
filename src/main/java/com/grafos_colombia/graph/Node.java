package com.grafos_colombia.graph;

// A class to store adjacency list nodes
public class Node {

    private String value;
    private double weight;

    public Node(String value, double weight) {
        this.value = value;
        this.weight = weight;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return this.value + " (" + this.weight + ")";
    }
}
