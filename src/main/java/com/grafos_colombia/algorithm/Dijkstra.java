package com.grafos_colombia.algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import com.grafos_colombia.graph.Node;

import javafx.util.Pair;

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

        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingDouble(n -> n.weight));
        pq.add(new Node(start, 0.0));

        while (!pq.isEmpty()) {
            Node current = pq.poll();
            String u = current.value;
            double d = current.weight;

            if (visited.contains(u)) continue;
            visited.add(u);

            if (u.equals(end)) break; // ya llegamos al destino

            if (d > dist.get(u)) continue;

            for (Node neighbor : adjList.getOrDefault(u, new ArrayList<>())) {
                double newDist = dist.get(u) + neighbor.weight;
                if (newDist < dist.get(neighbor.value)) {
                    dist.put(neighbor.value, newDist);
                    prev.put(neighbor.value, u);
                    pq.add(new Node(neighbor.value, newDist));
                }
            }
        }

         // reconstruir el camino
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

    public PathResult dijkstra(String start, String end) {
        return dijkstra(start, end, null);
    }
}
