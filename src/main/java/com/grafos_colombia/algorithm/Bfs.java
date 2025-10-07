package com.grafos_colombia.algorithm;

import com.grafos_colombia.graph.Node;

import java.util.*;

/**
 * Implementation of the Breadth-First Search (BFS) algorithm to find the
 * shortest simple cycle passing through a given start node.
 */
public class Bfs {

    /**
     * Finds the shortest simple cycle containing the startNode.
     *
     * @param adjList   The adjacency list of the graph.
     * @param startNode The node through which the cycle must pass.
     * @return A list of nodes representing the shortest cycle, or null if no cycle is found.
     */
    public static List<String> findShortestCycle(Map<String, List<Node>> adjList, String startNode) {
        if (!adjList.containsKey(startNode) || adjList.get(startNode).size() < 2) {
            // A cycle through startNode requires at least two neighbors.
            return null;
        }

        List<String> shortestCycle = null;
        List<Node> neighbors = adjList.get(startNode);

        // Iterate through all pairs of neighbors of the startNode
        for (int i = 0; i < neighbors.size(); i++) {
            for (int j = i + 1; j < neighbors.size(); j++) {
                String neighbor1 = neighbors.get(i).getValue();
                String neighbor2 = neighbors.get(j).getValue();

                // Find the shortest path between neighbor1 and neighbor2, avoiding startNode
                List<String> pathBetweenNeighbors = findShortestPath(adjList, neighbor1, neighbor2, startNode);

                if (pathBetweenNeighbors != null) {
                    // A cycle is formed: startNode -> neighbor1 -> ... -> neighbor2 -> startNode
                    List<String> currentCycle = new ArrayList<>();
                    currentCycle.add(startNode);
                    currentCycle.addAll(pathBetweenNeighbors);
                    currentCycle.add(startNode);

                    if (shortestCycle == null || currentCycle.size() < shortestCycle.size()) {
                        shortestCycle = currentCycle;
                    }
                }
            }
        }
        return shortestCycle;
    }

    /**
     * Finds the shortest path between two nodes using BFS, avoiding a specific node.
     *
     * @param adjList      The adjacency list of the graph.
     * @param start        The starting node of the path.
     * @param end          The ending node of the path.
     * @param excludedNode The node to exclude from the path.
     * @return The list of nodes in the shortest path, or null if no path exists.
     */
    private static List<String> findShortestPath(Map<String, List<Node>> adjList, String start, String end, String excludedNode) {
        Queue<List<String>> queue = new LinkedList<>();
        queue.add(Collections.singletonList(start));

        Set<String> visited = new HashSet<>();
        visited.add(start);
        visited.add(excludedNode); // Immediately exclude the main start node

        while (!queue.isEmpty()) {
            List<String> path = queue.poll();
            String lastNode = path.get(path.size() - 1);

            if (lastNode.equals(end)) {
                return path; // Found the shortest path
            }

            for (Node neighbor : adjList.get(lastNode)) {
                if (!visited.contains(neighbor.getValue())) {
                    visited.add(neighbor.getValue());
                    List<String> newPath = new ArrayList<>(path);
                    newPath.add(neighbor.getValue());
                    queue.add(newPath);
                }
            }
        }
        return null; // No path found
    }
}