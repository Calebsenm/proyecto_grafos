package com.grafos_colombia.graph;

import java.util.*;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
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
    private double minZoom = 0.1;
    private double maxZoom = 10.0;

    // Force-directed layout properties
    private double springConstant = 0.1;
    private double repulsionConstant = 10000.0;
    private double damping = 0.8;
    private double maxVelocity = 50.0;
    private double minDistance = 30.0;

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
        CIRCULAR,
        HIERARCHICAL
    }
    
    private LayoutType currentLayout = LayoutType.GEOGRAPHIC;
    private AnimationTimer animationTimer;

    // Colors
    private static final Color NODE_COLOR = Color.LIGHTBLUE;
    private static final Color HIGHLIGHTED_NODE_COLOR = Color.RED;
    private static final Color DRAGGING_NODE_COLOR = Color.ORANGE;
    private static final Color EDGE_COLOR = Color.GRAY;
    private static final Color HIGHLIGHTED_EDGE_COLOR = Color.BLUE;
    private static final Color TEXT_COLOR = Color.BLACK;
    private static final Color BACKGROUND_COLOR = Color.web("#ecf0f1");

    public GraphView(Canvas canvas) {
        System.out.println("🔧 GraphView constructor llamado con canvas: " + canvas);
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
        this.nodes = new ArrayList<>();
        this.edges = new ArrayList<>();
        this.nodeMap = new HashMap<>();

        System.out.println("✅ GraphView constructor completado");
        setupEventHandlers();
        System.out.println("✅ Event handlers configurados");
    }

    /**
     * Initialize graph with nodes and edges
     *
     * @param nodes
     * @param edges
     */
    public void initializeGraph(List<GraphNode> nodes, List<GraphEdge> edges) {
        System.out.println("🔧 initializeGraph llamado con " + nodes.size() + " nodos y " + edges.size() + " aristas");
        
        this.nodes = new ArrayList<>(nodes);
        this.edges = new ArrayList<>(edges);
        this.nodeMap.clear();

        // Build node map for quick lookup
        for (GraphNode node : nodes) {
            nodeMap.put(node.getId(), node);
        }

        System.out.println("✅ NodeMap construido con " + nodeMap.size() + " entradas");

        // Initialize positions based on current layout type
        System.out.println("🔧 Aplicando layout: " + currentLayout);
        applyLayout();
        System.out.println("✅ Layout aplicado");
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
            case CIRCULAR:
                applyCircularLayout();
                break;
            case HIERARCHICAL:
                applyHierarchicalLayout();
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
            case CIRCULAR:
                sortNodesAlphabetically();
                break;
            case HIERARCHICAL:
                sortNodesByRegion();
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
        nodes.sort((node1, node2) -> node1.getId().compareToIgnoreCase(node2.getId()));
    }

    /**
     * Sort nodes by region (first letter of name)
     */
    private void sortNodesByRegion() {
        nodes.sort((node1, node2) -> {
            char region1 = Character.toUpperCase(node1.getId().charAt(0));
            char region2 = Character.toUpperCase(node2.getId().charAt(0));
            int regionCompare = Character.compare(region1, region2);
            if (regionCompare != 0) {
                return regionCompare;
            }
            // Within same region, sort alphabetically
            return node1.getId().compareToIgnoreCase(node2.getId());
        });
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
            applyCircularLayout();
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
     * Apply circular layout
     */
    private void applyCircularLayout() {
        if (nodes.isEmpty()) {
            return;
        }

        double centerX = canvas.getWidth() / 2.0;
        double centerY = canvas.getHeight() / 2.0;
        double radius = Math.min(canvas.getWidth(), canvas.getHeight()) * 0.35;

        for (int i = 0; i < nodes.size(); i++) {
            GraphNode node = nodes.get(i);
            double angle = 2 * Math.PI * i / nodes.size();
            double x = centerX + radius * Math.cos(angle);
            double y = centerY + radius * Math.sin(angle);
            node.setPosition(x, y);
        }

        render();
    }

    /**
     * Apply hierarchical layout by regions
     */
    private void applyHierarchicalLayout() {
        if (nodes.isEmpty()) {
            return;
        }

        // Group nodes by first letter of name (simple region grouping)
        Map<Character, List<GraphNode>> groups = new HashMap<>();
        for (GraphNode node : nodes) {
            char firstChar = Character.toUpperCase(node.getId().charAt(0));
            groups.computeIfAbsent(firstChar, k -> new ArrayList<>()).add(node);
        }

        double canvasWidth = canvas.getWidth();
        double canvasHeight = canvas.getHeight();
        double padding = 100;

        int numGroups = groups.size();
        int cols = (int) Math.ceil(Math.sqrt(numGroups));
        int rows = (int) Math.ceil((double) numGroups / cols);

        double groupWidth = (canvasWidth - 2 * padding) / cols;
        double groupHeight = (canvasHeight - 2 * padding) / rows;

        int groupIndex = 0;
        for (List<GraphNode> group : groups.values()) {
            int col = groupIndex % cols;
            int row = groupIndex / cols;

            double groupX = padding + col * groupWidth + groupWidth / 2;
            double groupY = padding + row * groupHeight + groupHeight / 2;

            // Arrange nodes in group in a small circle
            double groupRadius = Math.min(groupWidth, groupHeight) * 0.3;
            for (int i = 0; i < group.size(); i++) {
                GraphNode node = group.get(i);
                double angle = 2 * Math.PI * i / group.size();
                double x = groupX + groupRadius * Math.cos(angle);
                double y = groupY + groupRadius * Math.sin(angle);
                node.setPosition(x, y);
            }

            groupIndex++;
        }

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
        if (animationTimer != null) {
            animationTimer.stop();
        }

        isAnimating = true;
        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                updateForces();
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
        // Clear velocities
        for (GraphNode node : nodes) {
            node.setVelocity(0, 0);
        }

        // Apply repulsion forces between all nodes
        for (int i = 0; i < nodes.size(); i++) {
            for (int j = i + 1; j < nodes.size(); j++) {
                GraphNode node1 = nodes.get(i);
                GraphNode node2 = nodes.get(j);

                double dx = node1.getX() - node2.getX();
                double dy = node1.getY() - node2.getY();
                double distance = Math.sqrt(dx * dx + dy * dy);

                if (distance > 0) {
                    // Minimum distance enforcement
                    if (distance < minDistance) {
                        double force = (minDistance - distance) / distance * 100.0;
                        double fx = dx * force;
                        double fy = dy * force;

                        node1.addForce(fx, fy);
                        node2.addForce(-fx, -fy);
                    }

                    // Repulsion force
                    double repulsionForce = repulsionConstant / (distance * distance);
                    double fx = dx * repulsionForce;
                    double fy = dy * repulsionForce;

                    node1.addForce(fx, fy);
                    node2.addForce(-fx, -fy);
                }
            }
        }

        // Apply spring forces for connected nodes
        for (GraphEdge edge : edges) {
            GraphNode source = edge.getSource();
            GraphNode target = edge.getTarget();

            double dx = target.getX() - source.getX();
            double dy = target.getY() - source.getY();
            double distance = Math.sqrt(dx * dx + dy * dy);

            if (distance > 0) {
                double springForce = springConstant * (distance - 50.0); // Ideal distance
                double fx = dx * springForce / distance;
                double fy = dy * springForce / distance;

                source.addForce(fx, fy);
                target.addForce(-fx, -fy);
            }
        }

        // Apply gravity to keep nodes near center
        double centerX = canvas.getWidth() / 2.0;
        double centerY = canvas.getHeight() / 2.0;
        double gravity = 0.01;

        for (GraphNode node : nodes) {
            if (!node.isFixed() && !node.isDragging()) {
                double dx = centerX - node.getX();
                double dy = centerY - node.getY();
                node.addForce(dx * gravity, dy * gravity);
            }
        }

        // Update positions
        for (GraphNode node : nodes) {
            node.updatePosition(damping, maxVelocity);
        }
    }

    /**
     * Render the graph
     */
    public void render() {
        System.out.println("🎨 Render llamado - Canvas: " + canvas.getWidth() + "x" + canvas.getHeight() + 
                          ", Nodos: " + nodes.size() + ", Aristas: " + edges.size());
        
        // Clear canvas
        gc.setFill(BACKGROUND_COLOR);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Apply viewport transformations
        gc.save();
        gc.translate(-viewportX * viewportZoom, -viewportY * viewportZoom);
        gc.scale(viewportZoom, viewportZoom);

        // Draw edges first (so they appear behind nodes)
        System.out.println("🎨 Dibujando " + edges.size() + " aristas");
        drawEdges();

        // Draw nodes
        System.out.println("🎨 Dibujando " + nodes.size() + " nodos");
        drawNodes();

        // Restore transformations
        gc.restore();

        // Draw UI elements (not affected by viewport)
        drawUI();
        
        System.out.println("✅ Render completado");
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

            // Calculate control points for smooth curve
            double dx = x2 - x1;
            double dy = y2 - y1;
            double distance = Math.sqrt(dx * dx + dy * dy);

            if (distance > 0) {
                double offset = Math.min(distance * 0.2, 30);
                double cx1 = x1 + dx * 0.5 + dy * offset / distance;
                double cy1 = y1 + dy * 0.5 - dx * offset / distance;
                double cx2 = x2 - dx * 0.5 + dy * offset / distance;
                double cy2 = y2 - dy * 0.5 - dx * offset / distance;

                gc.strokePolyline(
                        new double[]{x1, cx1, cx2, x2},
                        new double[]{y1, cy1, cy2, y2},
                        4
                );
            }

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
                gc.setFill(TEXT_COLOR);
                gc.setFont(Font.font("Arial", FontWeight.BOLD, 10 / viewportZoom));

                String label = node.getName();
                double labelWidth = gc.getFont().getSize() * label.length() * 0.6;
                double labelX = x - labelWidth / 2;
                double labelY = y - radius - 5;

                // Background for label
                gc.setFill(Color.WHITE);
                gc.fillRect(labelX - 2, labelY - 12, labelWidth + 4, 14);
                gc.setStroke(Color.BLACK);
                gc.strokeRect(labelX - 2, labelY - 12, labelWidth + 4, 14);

                // Label text
                gc.setFill(TEXT_COLOR);
                gc.fillText(label, labelX, labelY);
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
