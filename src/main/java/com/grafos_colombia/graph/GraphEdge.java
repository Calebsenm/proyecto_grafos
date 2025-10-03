package com.grafos_colombia.graph;

/**
 * Represents an edge in the graph visualization
 */
public class GraphEdge {

    private GraphNode source;
    private GraphNode target;
    private double weight;
    private boolean isHighlighted;

    public GraphEdge(GraphNode source, GraphNode target, double weight) {
        this.source = source;
        this.target = target;
        this.weight = weight;
        this.isHighlighted = false;
    }

    // Getters and Setters
    public GraphNode getSource() {
        return source;
    }

    public void setSource(GraphNode source) {
        this.source = source;
    }

    public GraphNode getTarget() {
        return target;
    }

    public void setTarget(GraphNode target) {
        this.target = target;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public boolean isHighlighted() {
        return isHighlighted;
    }

    public void setHighlighted(boolean highlighted) {
        this.isHighlighted = highlighted;
    }

    // Utility methods
    public double getLength() {
        return source.distanceTo(target);
    }

    public GraphNode getOther(GraphNode node) {
        if (node.equals(source)) {
            return target;
        } else if (node.equals(target)) {
            return source;
        }
        return null;
    }

    public boolean connects(GraphNode node1, GraphNode node2) {
        return (source.equals(node1) && target.equals(node2))
                || (source.equals(node2) && target.equals(node1));
    }

    @Override
    public String toString() {
        return "GraphEdge{" + "source=" + source.getName() + ", target=" + target.getName()
                + ", weight=" + weight + "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        GraphEdge graphEdge = (GraphEdge) obj;
        return connects(graphEdge.source, graphEdge.target);
    }

    @Override
    public int hashCode() {
        return source.hashCode() + target.hashCode();
    }
}
