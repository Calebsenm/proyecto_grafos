package com.grafos_colombia.algorithm;

import java.util.List;

public class PathResult {
    public double distance;
    public List<String> path;
    PathResult(double distance, List<String> path) {
        this.distance = distance;
        this.path = path;
    }
}