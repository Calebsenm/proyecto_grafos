package com.grafos_colombia.algorithm;

import com.grafos_colombia.graph.Node;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of the Depth-First Search (DFS) algorithm to detect
 * cycles in an undirected graph.
 */
public class Dfs {

    /**
     * Main function that checks if a cycle exists in the graph.
     *
     * @param adjList The adjacency list representing the graph.
     * @return true if at least one cycle is found, false otherwise.
     */
    public static boolean hasCycle(Map<String, List<Node>> adjList) {
        Set<String> visited = new HashSet<>();

        for (String vertex : adjList.keySet()) {
            if (!visited.contains(vertex)) {
                if (dfs(vertex, visited, null, adjList)) {
                    return true; // A cycle was found
                }
            }
        }
        return false; // No cycles were found
    }

    /**
     * Recursive DFS function to traverse the graph.
     */
    private static boolean dfs(String current, Set<String> visited, String parent, Map<String, List<Node>> adjList) {
        visited.add(current);

        for (Node neighborNode : adjList.get(current)) {
            String neighbor = neighborNode.getValue();
            if (!visited.contains(neighbor)) {
                if (dfs(neighbor, visited, current, adjList))
                    return true;
            } else if (!neighbor.equals(parent)) {
                return true; // A cycle was found
            }
        }
        return false;
    }
}