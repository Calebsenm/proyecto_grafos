package com.grafos_colombia.algorithm;

import com.grafos_colombia.graph.Node;
import java.util.List;
import java.util.Map;

/**
 * Calculates the eccentricity of a node in a graph.
 * The eccentricity of a node 'v' is the greatest distance between 'v' and any other node in the graph.
 */
public class Eccentricity {

    /**
     * A simple class to hold the result of the eccentricity calculation.
     */
    public static class EccentricityResult {
        public final double eccentricity;
        public final String farthestNode;
        public final List<String> path;

        public EccentricityResult(double eccentricity, String farthestNode, List<String> path) {
            this.eccentricity = eccentricity;
            this.farthestNode = farthestNode;
            this.path = path;
        }
    }

    /**
     * Calculates the eccentricity of the given startNode.
     *
     * @param startNode The node for which to calculate eccentricity.
     * @param adjList   The adjacency list of the graph.
     * @return An EccentricityResult object containing the eccentricity value and the farthest node.
     */
    public static EccentricityResult calculate(String startNode, Map<String, List<Node>> adjList) {
        if (!adjList.containsKey(startNode)) {
            return null;
        }

        double maxDistance = 0.0;
        String farthestNode = null;
        List<String> longestPath = null;

        for (String targetNode : adjList.keySet()) {
            if (startNode.equals(targetNode)) continue;

            PathResult result = Dijkstra.dijkstra(startNode, targetNode, adjList);

            if (result != null && result.distance > maxDistance) {
                maxDistance = result.distance;
                farthestNode = targetNode;
                longestPath = result.path;
            }
        }

        return new EccentricityResult(maxDistance, farthestNode, longestPath);
    }
}