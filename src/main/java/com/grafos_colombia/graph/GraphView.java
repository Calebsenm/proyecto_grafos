package com.grafos_colombia.graph;

import java.util.*;
import javafx.animation.AnimationTimer;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.geometry.VPos;
import javafx.scene.text.FontWeight;

/**
 * Advanced graph visualization component with force-directed layout
 */
public class GraphView {

    private Canvas canvas;
    private GraphicsContext gc;

    // Graph data
    private List<GraphNode> nodes;
    private List<GraphEdge> edges;
    private Map<String, GraphNode> nodeMap;

    // Viewport properties
    private double viewportX = 0.0;
    private double viewportY = 0.0;
    private double viewportZoom = 1.0;
    private double minZoom = 0.01;
    private double maxZoom = 10.0;


    // Interaction properties
    private boolean isPanning = false;
    private double lastMouseX = 0.0;
    private double lastMouseY = 0.0;
    private GraphNode draggedNode = null;
    private double dragOffsetX = 0.0;
    private double dragOffsetY = 0.0;

    // Rendering properties
    private boolean showEdgeWeights = true;
    private boolean showNodeLabels = true;
    private boolean isAnimating = false;

    // Layout options
    public enum LayoutType {
        FORCE_DIRECTED,
        GEOGRAPHIC,
    }
    
    private LayoutType currentLayout = LayoutType.GEOGRAPHIC;
    private AnimationTimer animationTimer;

    // New force-directed layout properties from GraphPanel
    private double temperature = 0;
    private static final int NODE_DIAMETER = 16;

    // Colors
    private static final Color NODE_COLOR = Color.LIGHTBLUE;
    private static final Color HIGHLIGHTED_NODE_COLOR = Color.RED;
    private static final Color DRAGGING_NODE_COLOR = Color.ORANGE;
    private static final Color EDGE_COLOR = Color.GRAY;
    private static final Color HIGHLIGHTED_EDGE_COLOR = Color.BLUE;
    private static final Color TEXT_COLOR = Color.BLACK;
    private static final Color BACKGROUND_COLOR = Color.web("#ecf0f1");

    public GraphView(Canvas canvas) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
        this.nodes = new ArrayList<>();
        this.edges = new ArrayList<>();
        this.nodeMap = new HashMap<>();

        setupEventHandlers();
    }

    /**
     * Initialize graph with nodes and edges
     *
     * @param nodes
     * @param edges
     */
    public void initializeGraph(List<GraphNode> nodes, List<GraphEdge> edges) {
        this.nodes = new ArrayList<>(nodes);
        this.edges = new ArrayList<>(edges);
        this.nodeMap.clear();

        // Build node map for quick lookup
        for (GraphNode node : nodes) {
            nodeMap.put(node.getId(), node);
        }

        // Initialize positions based on current layout type
        applyLayout();
    }

    /**
     * Initialize node positions randomly
     */
    /**
     * Apply layout based on current layout type
     */
    private void applyLayout() {
        // Apply appropriate sorting before layout
        applyOptimalSorting();

        switch (currentLayout) {
            case GEOGRAPHIC:
                applyGeographicLayout();
                break;
            case FORCE_DIRECTED:
                initializeRandomPositions();
                startLayoutAnimation();
                break;
        }
    }

    /**
     * Apply optimal sorting based on current layout type
     */
    private void applyOptimalSorting() {
        if (nodes.isEmpty()) {
            return;
        }

        switch (currentLayout) {
            case GEOGRAPHIC:
                sortNodesByGeographicCoordinates();
                break;
            case FORCE_DIRECTED:
                // Keep original order for force-directed
                break;
        }
    }

    /**
     * Sort nodes alphabetically
     */
    private void sortNodesAlphabetically() {
    }

    /**
     * Apply geographic layout using real coordinates
     */
    private void applyGeographicLayout() {
        if (nodes.isEmpty()) {
            return;
        }

        // Find bounds of coordinates
        double minLat = Double.MAX_VALUE, maxLat = Double.MIN_VALUE;
        double minLon = Double.MAX_VALUE, maxLon = Double.MIN_VALUE;

        for (GraphNode node : nodes) {
            if (node.getLatitude() != 0 || node.getLongitude() != 0) {
                minLat = Math.min(minLat, node.getLatitude());
                maxLat = Math.max(maxLat, node.getLatitude());
                minLon = Math.min(minLon, node.getLongitude());
                maxLon = Math.max(maxLon, node.getLongitude());
            }
        }

        // If no geographic data, fallback to circular layout
        if (minLat == Double.MAX_VALUE) {
            // If no geo data, maybe default to force-directed? For now, we do nothing.
            return;
        }

        // Calculate scaling factors
        double canvasWidth = canvas.getWidth();
        double canvasHeight = canvas.getHeight();
        double padding = 50; // Padding from edges

        double latRange = maxLat - minLat;
        double lonRange = maxLon - minLon;

        double scaleX = (canvasWidth - 2 * padding) / lonRange;
        double scaleY = (canvasHeight - 2 * padding) / latRange;

        // Apply geographic positions
        for (GraphNode node : nodes) {
            if (node.getLatitude() != 0 || node.getLongitude() != 0) {
                double x = padding + (node.getLongitude() - minLon) * scaleX;
                double y = padding + (maxLat - node.getLatitude()) * scaleY; // Invert Y for screen coordinates
                node.setPosition(x, y);
            } else {
                // Fallback to center for nodes without coordinates
                node.setPosition(canvasWidth / 2, canvasHeight / 2);
            }
        }

        // Render once without animation
        render();
    }

    /**
     * Initialize node positions randomly (for force-directed layout)
     */
    private void initializeRandomPositions() {
        Random random = new Random();
        double centerX = canvas.getWidth() / 2.0;
        double centerY = canvas.getHeight() / 2.0;
        double spread = Math.min(canvas.getWidth(), canvas.getHeight()) * 0.3;

        for (GraphNode node : nodes) {
            double x = centerX + (random.nextDouble() - 0.5) * spread;
            double y = centerY + (random.nextDouble() - 0.5) * spread;
            node.setPosition(x, y);
            node.setVelocity(0, 0);
        }
    }

    /**
     * Set the layout type and apply it
     * @param layoutType
     */
    public void setLayoutType(LayoutType layoutType) {
        this.currentLayout = layoutType;
        if (nodes != null && !nodes.isEmpty()) {
            applyLayout();
        }
    }

    /**
     * Get current layout type
     * @return 
     */
    public LayoutType getCurrentLayout() {
        return currentLayout;
    }

    /**
     * Sort nodes by geographic coordinates for better visualization
     */
    private void sortNodesByGeographicCoordinates() {
        nodes.sort((node1, node2) -> {
            // First sort by latitude (north to south)
            int latCompare = Double.compare(node1.getLatitude(), node2.getLatitude());
            if (latCompare != 0) {
                return latCompare;
            }
            // Then sort by longitude (west to east)
            return Double.compare(node1.getLongitude(), node2.getLongitude());
        });
    }

    /**
     * Start the force-directed layout animation
     */
    private void startLayoutAnimation() {
        // Initialize temperature for simulated annealing
        if (canvas != null) {
            this.temperature = canvas.getWidth() / 10.0;
        }

        if (animationTimer != null) {
            animationTimer.stop();
        }

        isAnimating = true;
        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (temperature > 0.1) {
                    updateForces();
                    // Cool down
                    temperature *= 0.99;
                } else {
                    stopLayoutAnimation();
                }
                render();
            }
        };
        animationTimer.start();
    }

    /**
     * Stop the layout animation
     */
    public void stopLayoutAnimation() {
        isAnimating = false;
        if (animationTimer != null) {
            animationTimer.stop();
        }
    }

    /**
     * Update forces for all nodes
     */
    private void updateForces() {
        if (nodes.isEmpty() || canvas.getWidth() == 0 || canvas.getHeight() == 0) {
            return;
        }

        double area = canvas.getWidth() * canvas.getHeight();
        double k = 1.2 * Math.sqrt(area / nodes.size());

        // Create a map for forces for this iteration
        Map<GraphNode, Point2D> forces = new HashMap<>();
        for (GraphNode node : nodes) {
            forces.put(node, new Point2D(0, 0));
        }

        // a. Calculate repulsion forces
        for (int i = 0; i < nodes.size(); i++) {
            for (int j = i + 1; j < nodes.size(); j++) {
                GraphNode node1 = nodes.get(i);
                GraphNode node2 = nodes.get(j);

                Point2D delta = node1.getPosition().subtract(node2.getPosition());
                double distance = Math.max(0.1, delta.magnitude());
                double repulsiveForce = (k * k) / distance;

                // "Personal space bubble" to prevent overlaps
                if (distance < NODE_DIAMETER * 2.5) {
                    repulsiveForce *= 20;
                }

                Point2D force = delta.normalize().multiply(repulsiveForce);
                forces.put(node1, forces.get(node1).add(force));
                forces.put(node2, forces.get(node2).subtract(force));
            }
        }

        // b. Calculate attraction forces
        for (GraphEdge edge : edges) {
            GraphNode source = edge.getSource();
            GraphNode target = edge.getTarget();

            Point2D delta = source.getPosition().subtract(target.getPosition());
            double distance = Math.max(0.1, delta.magnitude());
            double attractiveForce = (distance * distance) / k;

            Point2D force = delta.normalize().multiply(attractiveForce);
            forces.put(source, forces.get(source).subtract(force));
            forces.put(target, forces.get(target).add(force));
        }

        // c. Move nodes according to forces and temperature
        for (GraphNode node : nodes) {
            if (!node.isFixed() && !node.isDragging()) {
                Point2D force = forces.get(node);
                double displacement = Math.min(force.magnitude(), temperature);
                if (displacement > 0) {
                    Point2D newPos = node.getPosition().add(force.normalize().multiply(displacement));
                    node.setPosition(newPos);
                }
            }
        }
    }

    /**
     * Render the graph
     */
    public void render() {
        // Clear canvas
        gc.setFill(BACKGROUND_COLOR);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Apply viewport transformations
        gc.save();
        gc.translate(-viewportX * viewportZoom, -viewportY * viewportZoom);
        gc.scale(viewportZoom, viewportZoom);

        // Draw edges first (so they appear behind nodes)
        drawEdges();

        // Draw nodes
        drawNodes();

        // Restore transformations
        gc.restore();

        // Draw UI elements (not affected by viewport)
        drawUI();
    }

    /**
     * Draw edges with smooth curves
     */
    private void drawEdges() {
        gc.setStroke(EDGE_COLOR);
        gc.setLineWidth(1.0 / viewportZoom);

        for (GraphEdge edge : edges) {
            GraphNode source = edge.getSource();
            GraphNode target = edge.getTarget();

            if (edge.isHighlighted()) {
                gc.setStroke(HIGHLIGHTED_EDGE_COLOR);
                gc.setLineWidth(3.0 / viewportZoom);
            } else {
                gc.setStroke(EDGE_COLOR);
                gc.setLineWidth(1.0 / viewportZoom);
            }

            // Draw smooth curve
            double x1 = source.getX();
            double y1 = source.getY();
            double x2 = target.getX();
            double y2 = target.getY();

            // Draw a straight line
            gc.strokeLine(x1, y1, x2, y2);

            // Draw edge weight
            if (showEdgeWeights && viewportZoom > 0.5) {
                double midX = (x1 + x2) / 2;
                double midY = (y1 + y2) / 2;

                // Background circle for weight
                gc.setFill(Color.WHITE);
                gc.fillOval(midX - 12, midY - 8, 24, 16);
                gc.setStroke(Color.BLACK);
                gc.setLineWidth(0.5 / viewportZoom);
                gc.strokeOval(midX - 12, midY - 8, 24, 16);

                // Weight text
                gc.setFill(TEXT_COLOR);
                gc.setFont(Font.font("Arial", 8 / viewportZoom));
                String weightText = String.format("%.1f", edge.getWeight());
                gc.fillText(weightText, midX - 8, midY + 2);
            }
        }
    }

    /**
     * Draw nodes
     */
    private void drawNodes() {
        for (GraphNode node : nodes) {
            double x = node.getX();
            double y = node.getY();
            double radius = node.getRadius() / viewportZoom;

            // Choose node color
            Color nodeColor;
            if (node.isDragging()) {
                nodeColor = DRAGGING_NODE_COLOR;
            } else if (node.isFixed()) {
                nodeColor = HIGHLIGHTED_NODE_COLOR;
            } else {
                nodeColor = NODE_COLOR;
            }

            // Draw node circle
            gc.setFill(nodeColor);
            gc.fillOval(x - radius, y - radius, radius * 2, radius * 2);

            // Draw node border
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(1.0 / viewportZoom);
            gc.strokeOval(x - radius, y - radius, radius * 2, radius * 2);

            // Draw node label
            if (showNodeLabels && viewportZoom > 0.3) {
                gc.setFill(Color.BLACK); // Use black text for better contrast on light blue
                gc.setFont(Font.font("Arial", FontWeight.BOLD, 10 / viewportZoom));
                gc.setTextAlign(TextAlignment.CENTER);
                gc.setTextBaseline(VPos.CENTER);

                String label = node.getName();
                gc.fillText(label, x, y);
            }
        }
    }

    /**
     * Draw UI elements
     */
    private void drawUI() {
        // Zoom indicator
        gc.setFill(Color.rgb(0, 0, 0, 0.8));
        gc.fillRoundRect(canvas.getWidth() - 160, 10, 150, 80, 8, 8);

        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        gc.fillText(String.format("Zoom: %.1fx", viewportZoom), canvas.getWidth() - 155, 28);

        gc.setFont(Font.font("Arial", 10));
        gc.fillText("Rueda: Zoom | Arrastra: Mover", canvas.getWidth() - 155, 42);
        gc.fillText(String.format("Viewport: %.0f,%.0f", viewportX, viewportY), canvas.getWidth() - 155, 56);
        gc.fillText("Click + Arrastra nodo: Mover", canvas.getWidth() - 155, 70);
    }

    /**
     * Setup event handlers
     */
    private void setupEventHandlers() {
        canvas.setOnMousePressed(this::handleMousePressed);
        canvas.setOnMouseDragged(this::handleMouseDragged);
        canvas.setOnMouseReleased(this::handleMouseReleased);
        canvas.setOnScroll(this::handleScroll);
    }

    /**
     * Handle mouse press
     */
    private void handleMousePressed(MouseEvent event) {
        double mouseX = event.getX();
        double mouseY = event.getY();

        // Convert to world coordinates
        double worldX = (mouseX / viewportZoom) + viewportX;
        double worldY = (mouseY / viewportZoom) + viewportY;

        // Check if clicking on a node
        GraphNode clickedNode = getNodeAt(worldX, worldY);
        if (clickedNode != null) {
            draggedNode = clickedNode;
            clickedNode.setDragging(true);
            dragOffsetX = worldX - clickedNode.getX();
            dragOffsetY = worldY - clickedNode.getY();
        } else {
            // Start panning
            isPanning = true;
            lastMouseX = mouseX;
            lastMouseY = mouseY;
        }
    }

    /**
     * Handle mouse drag
     */
    private void handleMouseDragged(MouseEvent event) {
        double mouseX = event.getX();
        double mouseY = event.getY();

        if (draggedNode != null) {
            // Drag node
            double worldX = (mouseX / viewportZoom) + viewportX;
            double worldY = (mouseY / viewportZoom) + viewportY;
            draggedNode.setPosition(worldX - dragOffsetX, worldY - dragOffsetY);
            draggedNode.setVelocity(0, 0); // Stop node movement
        } else if (isPanning) {
            // Pan viewport
            double deltaX = mouseX - lastMouseX;
            double deltaY = mouseY - lastMouseY;
            viewportX -= deltaX / viewportZoom;
            viewportY -= deltaY / viewportZoom;
            lastMouseX = mouseX;
            lastMouseY = mouseY;
        }

        render();
    }

    /**
     * Handle mouse release
     */
    private void handleMouseReleased(MouseEvent event) {
        if (draggedNode != null) {
            draggedNode.setDragging(false);
            draggedNode = null;
        }
        isPanning = false;
    }

    /**
     * Handle mouse scroll (zoom)
     */
    private void handleScroll(ScrollEvent event) {
        double zoomFactor = 1.1;
        double deltaY = event.getDeltaY();

        double oldZoom = viewportZoom;

        if (deltaY > 0) {
            viewportZoom = Math.min(maxZoom, viewportZoom * zoomFactor);
        } else {
            viewportZoom = Math.max(minZoom, viewportZoom / zoomFactor);
        }

        if (oldZoom != viewportZoom) {
            // Zoom towards mouse position
            double mouseX = event.getX();
            double mouseY = event.getY();

            double worldMouseX = (mouseX / oldZoom) + viewportX;
            double worldMouseY = (mouseY / oldZoom) + viewportY;

            viewportX = worldMouseX - (mouseX / viewportZoom);
            viewportY = worldMouseY - (mouseY / viewportZoom);

            render();
        }

        event.consume();
    }

    /**
     * Get node at specific world coordinates
     */
    private GraphNode getNodeAt(double worldX, double worldY) {
        for (GraphNode node : nodes) {
            double dx = worldX - node.getX();
            double dy = worldY - node.getY();
            double distance = Math.sqrt(dx * dx + dy * dy);

            if (distance <= node.getRadius() * 2) {
                return node;
            }
        }
        return null;
    }

    /**
     * Highlight path nodes and edges
     * @param pathNodeIds
     */
    public void highlightPath(List<String> pathNodeIds) {
        // Clear previous highlights
        for (GraphNode node : nodes) {
            node.setFixed(false);
        }
        for (GraphEdge edge : edges) {
            edge.setHighlighted(false);
        }

        // Highlight path nodes
        for (String nodeId : pathNodeIds) {
            GraphNode node = nodeMap.get(nodeId);
            if (node != null) {
                node.setFixed(true);
            }
        }

        // Highlight path edges
        for (int i = 0; i < pathNodeIds.size() - 1; i++) {
            String sourceId = pathNodeIds.get(i);
            String targetId = pathNodeIds.get(i + 1);

            GraphNode source = nodeMap.get(sourceId);
            GraphNode target = nodeMap.get(targetId);

            if (source != null && target != null) {
                for (GraphEdge edge : edges) {
                    if (edge.connects(source, target)) {
                        edge.setHighlighted(true);
                        break;
                    }
                }
            }
        }
    }

    /**
     * Center viewport on graph
     */
    public void centerView() {
        if (nodes.isEmpty()) {
            return;
        }

        double centerX = 0, centerY = 0;
        for (GraphNode node : nodes) {
            centerX += node.getX();
            centerY += node.getY();
        }
        centerX /= nodes.size();
        centerY /= nodes.size();

        viewportX = centerX - (canvas.getWidth() / 2) / viewportZoom;
        viewportY = centerY - (canvas.getHeight() / 2) / viewportZoom;

        render();
    }

    /**
     * Reset zoom and pan
     */
    public void resetView() {
        viewportZoom = 1.0;
        centerView();
    }

    /**
     * Zoom in towards center of canvas
     */
    public void zoomIn() {
        double oldZoom = viewportZoom;
        viewportZoom = Math.min(maxZoom, viewportZoom * 1.2);

        if (oldZoom != viewportZoom) {
            // Zoom towards center of canvas
            double centerX = canvas.getWidth() / 2.0;
            double centerY = canvas.getHeight() / 2.0;

            double worldCenterX = (centerX / oldZoom) + viewportX;
            double worldCenterY = (centerY / oldZoom) + viewportY;

            viewportX = worldCenterX - (centerX / viewportZoom);
            viewportY = worldCenterY - (centerY / viewportZoom);

            render();
        }
    }

    /**
     * Zoom out from center of canvas
     */
    public void zoomOut() {
        double oldZoom = viewportZoom;
        viewportZoom = Math.max(minZoom, viewportZoom / 1.2);

        if (oldZoom != viewportZoom) {
            // Zoom from center of canvas
            double centerX = canvas.getWidth() / 2.0;
            double centerY = canvas.getHeight() / 2.0;

            double worldCenterX = (centerX / oldZoom) + viewportX;
            double worldCenterY = (centerY / oldZoom) + viewportY;

            viewportX = worldCenterX - (centerX / viewportZoom);
            viewportY = worldCenterY - (centerY / viewportZoom);

            render();
        }
    }

    // Getters and Setters
    public List<GraphNode> getNodes() {
        return nodes;
    }

    public List<GraphEdge> getEdges() {
        return edges;
    }

    public boolean isShowEdgeWeights() {
        return showEdgeWeights;
    }

    public void setShowEdgeWeights(boolean showEdgeWeights) {
        this.showEdgeWeights = showEdgeWeights;
    }

    public boolean isShowNodeLabels() {
        return showNodeLabels;
    }

    public void setShowNodeLabels(boolean showNodeLabels) {
        this.showNodeLabels = showNodeLabels;
    }

    public boolean isAnimating() {
        return isAnimating;
    }
}
