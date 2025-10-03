package com.grafos_colombia.graph;

import java.util.*;

/**
 * Utility class to convert between different graph representations
 */
public class GraphConverter {

    /**
     * Convert adjacency list and edge list to GraphView format
     * @param adjList
     * @param edges
     * @return 
     */
    public static GraphViewData convertToGraphView(Map<String, List<Edge>> adjList, List<Edge> edges) {
        List<GraphNode> nodes = new ArrayList<>();
        List<GraphEdge> graphEdges = new ArrayList<>();
        Map<String, GraphNode> nodeMap = new HashMap<>();

        // Create nodes from adjacency list
        for (String nodeId : adjList.keySet()) {
            GraphNode node = new GraphNode(nodeId, nodeId);
            nodes.add(node);
            nodeMap.put(nodeId, node);
        }

        // Create edges
        for (Edge edge : edges) {
            GraphNode source = nodeMap.get(edge.getSrc());
            GraphNode target = nodeMap.get(edge.getDest());

            if (source != null && target != null) {
                GraphEdge graphEdge = new GraphEdge(source, target, edge.getWeight());
                graphEdges.add(graphEdge);
            }
        }

        return new GraphViewData(nodes, graphEdges);
    }

    /**
     * Convert adjacency list with Node objects to GraphView format
     *
     * @param adjList
     * @param edges
     * @return
     */
    public static GraphViewData convertNodeAdjList(Map<String, List<Node>> adjList, List<Edge> edges) {
        List<GraphNode> nodes = new ArrayList<>();
        List<GraphEdge> graphEdges = new ArrayList<>();
        Map<String, GraphNode> nodeMap = new HashMap<>();

        // Create nodes from adjacency list
        for (String nodeId : adjList.keySet()) {
            GraphNode node = new GraphNode(nodeId, nodeId);
            nodes.add(node);
            nodeMap.put(nodeId, node);
        }

        // Create edges
        for (Edge edge : edges) {
            GraphNode source = nodeMap.get(edge.getSrc());
            GraphNode target = nodeMap.get(edge.getDest());

            if (source != null && target != null) {
                GraphEdge graphEdge = new GraphEdge(source, target, edge.getWeight());
                graphEdges.add(graphEdge);
            }
        }

        return new GraphViewData(nodes, graphEdges);
    }

    /**
     * Convert Colombian locations to GraphView format
     *
     * @param adjList
     * @param edges
     * @return
     */
    public static GraphViewData convertColombianGraph(Map<String, List<Edge>> adjList, List<Edge> edges) {
        List<GraphNode> nodes = new ArrayList<>();
        List<GraphEdge> graphEdges = new ArrayList<>();
        Map<String, GraphNode> nodeMap = new HashMap<>();

        // Create nodes with Colombian location names
        for (String locationName : adjList.keySet()) {
            GraphNode node = new GraphNode(locationName, locationName);
            nodes.add(node);
            nodeMap.put(locationName, node);
        }

        // Create edges with distances
        for (Edge edge : edges) {
            GraphNode source = nodeMap.get(edge.getSrc());
            GraphNode target = nodeMap.get(edge.getDest());

            if (source != null && target != null) {
                GraphEdge graphEdge = new GraphEdge(source, target, edge.getWeight());
                graphEdges.add(graphEdge);
            }
        }

        return new GraphViewData(nodes, graphEdges);
    }

    /**
     * Data container for GraphView
     */
    /**
     * Convert adjacency list with geographic coordinates to GraphView format
     *
     * @param adjList
     * @param edges
     * @param geoNodes
     * @return
     */
    public static GraphViewData convertWithGeographicCoordinates(Map<String, List<Node>> adjList, List<Edge> edges, Map<String, GeoNode> geoNodes) {
        List<GraphNode> nodes = new ArrayList<>();
        List<GraphEdge> graphEdges = new ArrayList<>();
        Map<String, GraphNode> nodeMap = new HashMap<>();

        // Create nodes from adjacency list with geographic coordinates
        for (String nodeId : adjList.keySet()) {
            GeoNode geoNode = geoNodes.get(nodeId);
            GraphNode node;

            if (geoNode != null) {
                node = new GraphNode(nodeId, nodeId, 0, 0, geoNode.getLatitude(), geoNode.getLongitude());
            } else {
                node = new GraphNode(nodeId, nodeId);
            }

            nodes.add(node);
            nodeMap.put(nodeId, node);
        }

        // Create edges
        for (Edge edge : edges) {
            GraphNode source = nodeMap.get(edge.getSrc());
            GraphNode target = nodeMap.get(edge.getDest());

            if (source != null && target != null) {
                GraphEdge graphEdge = new GraphEdge(source, target, edge.getWeight());
                graphEdges.add(graphEdge);
            }
        }

        return new GraphViewData(nodes, graphEdges);
    }

    public static class GraphViewData {

        private final List<GraphNode> nodes;
        private final List<GraphEdge> edges;

        public GraphViewData(List<GraphNode> nodes, List<GraphEdge> edges) {
            this.nodes = nodes;
            this.edges = edges;
        }

        public List<GraphNode> getNodes() {
            return nodes;
        }

        public List<GraphEdge> getEdges() {
            return edges;
        }
    }
}
