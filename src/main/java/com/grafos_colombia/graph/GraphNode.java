package com.grafos_colombia.graph;

import javafx.geometry.Point2D;

/**
 * Represents a node in the graph visualization
 */
public class GraphNode {

    private String id;
    private String name;
    private Point2D position;
    private Point2D velocity;
    private boolean isFixed;
    private boolean isDragging;
    private int highlightLevel;
    private double radius;

    // Geographic coordinates
    private double latitude;
    private double longitude;

    public GraphNode(String id, String name) {
        this.id = id;
        this.name = name;
        this.position = new Point2D(0, 0);
        this.velocity = new Point2D(0, 0);
        this.isFixed = false;
        this.isDragging = false;
        this.radius = 8.0;
        this.latitude = 0.0;
        this.longitude = 0.0;
        this.highlightLevel = 0;
    }

    public GraphNode(String id, String name, double x, double y) {
        this(id, name);
        this.position = new Point2D(x, y);
    }

    public GraphNode(String id, String name, double x, double y, double latitude, double longitude) {
        this(id, name, x, y);
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Point2D getPosition() {
        return position;
    }

    public void setPosition(Point2D position) {
        this.position = position;
    }

    public void setPosition(double x, double y) {
        this.position = new Point2D(x, y);
    }

    public Point2D getVelocity() {
        return velocity;
    }

    public void setVelocity(Point2D velocity) {
        this.velocity = velocity;
    }

    public void setVelocity(double vx, double vy) {
        this.velocity = new Point2D(vx, vy);
    }

    public boolean isFixed() {
        return isFixed;
    }

    public void setFixed(boolean fixed) {
        this.isFixed = fixed;
    }

    public boolean isDragging() {
        return isDragging;
    }

    public void setDragging(boolean dragging) {
        this.isDragging = dragging;
    }

    public int getHighlightLevel() {
        return highlightLevel;
    }

    public void setHighlightLevel(int highlightLevel) {
        this.highlightLevel = Math.max(0, highlightLevel);
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    // Utility methods
    public double getX() {
        return position.getX();
    }

    public double getY() {
        return position.getY();
    }

    public double getVX() {
        return velocity.getX();
    }

    public double getVY() {
        return velocity.getY();
    }

    public void setX(double x) {
        this.position = new Point2D(x, position.getY());
    }

    public void setY(double y) {
        this.position = new Point2D(position.getX(), y);
    }

    public double distanceTo(GraphNode other) {
        return position.distance(other.getPosition());
    }

    public void addForce(double fx, double fy) {
        if (!isFixed && !isDragging) {
            velocity = new Point2D(velocity.getX() + fx, velocity.getY() + fy);
        }
    }

    public void updatePosition(double damping, double maxVelocity) {
        if (!isFixed && !isDragging) {
            // Apply damping
            velocity = new Point2D(velocity.getX() * damping, velocity.getY() * damping);

            // Limit velocity
            double speed = Math.sqrt(velocity.getX() * velocity.getX() + velocity.getY() * velocity.getY());
            if (speed > maxVelocity) {
                double factor = maxVelocity / speed;
                velocity = new Point2D(velocity.getX() * factor, velocity.getY() * factor);
            }

            // Update position
            position = new Point2D(position.getX() + velocity.getX(), position.getY() + velocity.getY());
        }
    }

    @Override
    public String toString() {
        return "GraphNode{" + "id='" + id + "', name='" + name + "', position=" + position + "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        GraphNode graphNode = (GraphNode) obj;
        return id != null ? id.equals(graphNode.id) : graphNode.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
