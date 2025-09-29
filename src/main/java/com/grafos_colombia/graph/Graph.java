package com.grafos_colombia.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Graph {

    private Map<String, List<Node>> adjList = new HashMap<>();;

    public Graph(List<Edge> edges) {

        for (Edge e : edges) {
            adjList.computeIfAbsent(e.src, k -> new ArrayList<>()).add(new Node(e.dest, e.weight));
            adjList.computeIfAbsent(e.dest, k -> new ArrayList<>()).add(new Node(e.src, e.weight));
        }
    }

    public static void printGraph(Graph graph) {
       System.out.println(graph.adjList.entrySet());
    }
    
    public Map<String, List<Node>> getAdjList() {
        return adjList;
    }
}




// main class fo example 
/* 
class Main {
    public static void main(String[] args) {
        // Input: List of edges in a weighted digraph (as per the above diagram)
        // tuple `(x, y, w)` represents an edge from `x` to `y` having weight `w`
        List<Edge> edges = Arrays.asList(
                new Edge(0, 1, 6),
                new Edge(1, 2, 7), 
                new Edge(2, 0, 5),
                new Edge(2, 1, 4), 
                new Edge(3, 2, 10),
                new Edge(4, 5, 1),
                new Edge(5, 4, 3));

        // construct a graph from the given list of edges
        Graph graph = new Graph(edges);

        // print adjacency list representation of the graph
        Graph.printGraph(graph);
    }
}

*/