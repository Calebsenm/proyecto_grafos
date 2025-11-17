package com.grafos_colombia.graph;


public class GraphEdge {

    private GraphNode source;
    private GraphNode target;
    private double weight;
    private int highlightLevel;
    private double highlightWidth;

    public GraphEdge(GraphNode source, GraphNode target, double weight) {
        this.source = source;
        this.target = target;
        this.weight = weight;
        this.highlightLevel = 0;
        this.highlightWidth = 0;
    }


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
        return highlightLevel > 0;
    }

    public void setHighlighted(boolean highlighted) {
        this.highlightLevel = highlighted ? 1 : 0;
        this.highlightWidth = highlighted ? 3.0 : 0.0;
    }

    public int getHighlightLevel() {
        return highlightLevel;
    }

    public void setHighlightLevel(int highlightLevel) {
        this.highlightLevel = Math.max(0, highlightLevel);
    }

    public double getHighlightWidth() {
        return highlightWidth;
    }

    public void setHighlightWidth(double highlightWidth) {
        this.highlightWidth = Math.max(0.0, highlightWidth);
    }


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
