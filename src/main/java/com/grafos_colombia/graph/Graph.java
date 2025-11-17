package com.grafos_colombia.graph;

import com.grafos_colombia.algorithm.GraphMetrics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Graph {

    private final Map<String, List<Node>> adjList = new HashMap<>();

    public Graph(List<Edge> edges) {

        for (Edge edge : edges) {
            adjList.computeIfAbsent(edge.getSrc(), k -> new ArrayList<>()).add(new Node(edge.getDest(), edge.getWeight()));
            adjList.computeIfAbsent(edge.getDest(), k -> new ArrayList<>()).add(new Node(edge.getSrc(), edge.getWeight()));
        }
    }

    public static void printGraph(Graph graph) {
        System.out.println(graph.adjList.entrySet());
    }

    public Map<String, List<Node>> getAdjList() {
        return adjList;
    }

  
    public double getRadio() {
        return GraphMetrics.calcularRadio(adjList);
    }

   
    public double getDiametro() {
        return GraphMetrics.calcularDiametro(adjList);
    }

  
    public List<String> getCentro() {
        return GraphMetrics.calcularCentro(adjList);
    }

    public GraphMetrics.GraphMetricsResult getMetricas() {
        return GraphMetrics.calcularMetricas(adjList);
    }
}
