package com.grafos_colombia.algorithm;

import com.grafos_colombia.graph.Node;

import java.util.*;


public class Bfs {

  
    public static List<String> findShortestCycle(Map<String, List<Node>> adjList, String startNode) {
        if (!adjList.containsKey(startNode) || adjList.get(startNode).size() < 2) {
            return null;
        }

        List<String> shortestCycle = null;
        List<Node> neighbors = adjList.get(startNode);

        for (int i = 0; i < neighbors.size(); i++) {
            for (int j = i + 1; j < neighbors.size(); j++) {
                String neighbor1 = neighbors.get(i).getValue();
                String neighbor2 = neighbors.get(j).getValue();

                List<String> pathBetweenNeighbors = findShortestPath(adjList, neighbor1, neighbor2, startNode);

                if (pathBetweenNeighbors != null) {
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

    private static List<String> findShortestPath(Map<String, List<Node>> adjList, String start, String end, String excludedNode) {
        Queue<List<String>> queue = new LinkedList<>();
        queue.add(Collections.singletonList(start));

        Set<String> visited = new HashSet<>();
        visited.add(start);
        visited.add(excludedNode); 

        while (!queue.isEmpty()) {
            List<String> path = queue.poll();
            String lastNode = path.get(path.size() - 1);

            if (lastNode.equals(end)) {
                return path; 
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
        return null; 
    }
}