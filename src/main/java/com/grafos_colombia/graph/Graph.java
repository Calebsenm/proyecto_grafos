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

    /**
     * Calcula el radio del grafo (excentricidad mínima entre todos los nodos).
     * 
     * @return El radio del grafo, o Double.POSITIVE_INFINITY si el grafo no es conexo.
     */
    public double getRadio() {
        return GraphMetrics.calcularRadio(adjList);
    }

    /**
     * Calcula el diámetro del grafo (excentricidad máxima entre todos los nodos).
     * 
     * @return El diámetro del grafo, o 0.0 si el grafo no es conexo o está vacío.
     */
    public double getDiametro() {
        return GraphMetrics.calcularDiametro(adjList);
    }

    /**
     * Calcula el centro del grafo (nodos con excentricidad igual al radio).
     * 
     * @return Una lista con los nodos del centro del grafo.
     */
    public List<String> getCentro() {
        return GraphMetrics.calcularCentro(adjList);
    }

    /**
     * Obtiene todas las métricas del grafo (radio, diámetro y centro).
     * 
     * @return Un objeto GraphMetricsResult con todas las métricas, o null si el grafo no es conexo.
     */
    public GraphMetrics.GraphMetricsResult getMetricas() {
        return GraphMetrics.calcularMetricas(adjList);
    }
}
