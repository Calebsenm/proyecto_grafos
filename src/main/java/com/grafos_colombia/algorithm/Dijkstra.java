package com.grafos_colombia.algorithm;

import com.grafos_colombia.graph.Node;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class Dijkstra {

    public static PathResult dijkstra(String start, String end, Map<String, List<Node>> adjList) {

        Map<String, Double> dist = new HashMap<>();
        Map<String, String> prev = new HashMap<>();
        Set<String> visited = new HashSet<>();

        for (String v : adjList.keySet()) {
            dist.put(v, Double.POSITIVE_INFINITY);
            prev.put(v, null);
        }

        dist.put(start, 0.0);

        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingDouble(n -> n.getWeight()));
        pq.add(new Node(start, 0.0));

        while (!pq.isEmpty()) {
            Node current = pq.poll();
            String u = current.getValue();
            double d = current.getWeight();

            if (visited.contains(u)) {
                continue;
            }
            visited.add(u);

            if (u.equals(end)) {
                break; 
            }
            if (d > dist.get(u)) {
                continue;
            }

            for (Node neighbor : adjList.getOrDefault(u, new ArrayList<>())) {
                double newDist = dist.get(u) + neighbor.getWeight();
                if (newDist < dist.get(neighbor.getValue())) {
                    dist.put(neighbor.getValue(), newDist);
                    prev.put(neighbor.getValue(), u);
                    pq.add(new Node(neighbor.getValue(), newDist));
                }
            }
        }

     
        List<String> path = new ArrayList<>();
        String at = end;
        if (dist.get(end) != Double.POSITIVE_INFINITY) {
            while (at != null) {
                path.add(at);
                at = prev.get(at);
            }
            Collections.reverse(path);
        }

        return new PathResult(dist.get(end), path);
    }

    private static double calculatePathDistance(List<String> path, Map<String, List<Node>> adjList) {
        if (path == null || path.size() < 2) {
            return Double.POSITIVE_INFINITY;
        }

        double distance = 0.0;
        for (int i = 0; i < path.size() - 1; i++) {
            String current = path.get(i);
            String next = path.get(i + 1);
            boolean found = false;
            for (Node neighbor : adjList.getOrDefault(current, Collections.emptyList())) {
                if (neighbor.getValue().equals(next)) {
                    distance += neighbor.getWeight();
                    found = true;
                    break;
                }
            }
            if (!found) {
                return Double.POSITIVE_INFINITY;
            }
        }
        return distance;
    }

    private static Map<String, List<Node>> cloneGraph(Map<String, List<Node>> adjList) {
        Map<String, List<Node>> clone = new HashMap<>();
        for (Map.Entry<String, List<Node>> entry : adjList.entrySet()) {
            List<Node> neighbors = new ArrayList<>();
            for (Node neighbor : entry.getValue()) {
                neighbors.add(new Node(neighbor.getValue(), neighbor.getWeight()));
            }
            clone.put(entry.getKey(), neighbors);
        }
        return clone;
    }

    private static void removeEdge(Map<String, List<Node>> graph, String from, String to) {
        if (graph.containsKey(from)) {
            graph.get(from).removeIf(node -> node.getValue().equals(to));
        }
        if (graph.containsKey(to)) {
            graph.get(to).removeIf(node -> node.getValue().equals(from));
        }
    }

    private static void removeNode(Map<String, List<Node>> graph, String node) {
        graph.remove(node);
        for (List<Node> neighbors : graph.values()) {
            neighbors.removeIf(n -> n.getValue().equals(node));
        }
    }

    private static class PathCandidate {
        final List<String> path;
        final double distance;

        PathCandidate(List<String> path, double distance) {
            this.path = path;
            this.distance = distance;
        }
    }

    public static PathResult findAlternativePath(String start, String end,
                                                  Map<String, List<Node>> adjList,
                                                  List<String> primaryPath) {
        if (primaryPath == null || primaryPath.size() < 2) {
            return null;
        }

        List<PathResult> shortestPaths = new ArrayList<>();
        shortestPaths.add(new PathResult(calculatePathDistance(primaryPath, adjList), primaryPath));

        PriorityQueue<PathCandidate> candidates = new PriorityQueue<>(Comparator.comparingDouble(c -> c.distance));

        for (int i = 0; i < primaryPath.size() - 1; i++) {
            String spurNode = primaryPath.get(i);
            List<String> rootPath = new ArrayList<>(primaryPath.subList(0, i + 1));

            Map<String, List<Node>> modifiedGraph = cloneGraph(adjList);

            for (PathResult pathResult : shortestPaths) {
                List<String> path = pathResult.path;
                if (path.size() > i && rootPath.equals(path.subList(0, i + 1))) {
                    String u = path.get(i);
                    String v = path.get(i + 1);
                    removeEdge(modifiedGraph, u, v);
                }
            }

            for (int j = 0; j < rootPath.size() - 1; j++) {
                String nodeToRemove = rootPath.get(j);
                removeNode(modifiedGraph, nodeToRemove);
            }

            PathResult spurResult = dijkstra(spurNode, end, modifiedGraph);
            if (spurResult == null || spurResult.path == null || spurResult.path.isEmpty() ||
                    spurResult.distance == Double.POSITIVE_INFINITY) {
                continue;
            }

            List<String> totalPath = new ArrayList<>(rootPath);
            totalPath.remove(totalPath.size() - 1);
            totalPath.addAll(spurResult.path);

            double totalDistance = calculatePathDistance(totalPath, adjList);
            if (totalDistance == Double.POSITIVE_INFINITY) {
                continue;
            }

            PathCandidate candidate = new PathCandidate(totalPath, totalDistance);

            boolean duplicate = false;
            for (PathResult result : shortestPaths) {
                if (result.path.equals(candidate.path)) {
                    duplicate = true;
                    break;
                }
            }
            if (!duplicate) {
                candidates.add(candidate);
            }
        }

        while (!candidates.isEmpty()) {
            PathCandidate candidate = candidates.poll();
            if (!candidate.path.equals(primaryPath)) {
                return new PathResult(candidate.distance, candidate.path);
            }
        }

        return null;
    }
}
