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


public class GraphView {

    private Canvas canvas;
    private GraphicsContext gc;
    private List<GraphNode> nodes;
    private List<GraphEdge> edges;
    private Map<String, GraphNode> nodeMap;
    private double viewportX = 0.0;
    private double viewportY = 0.0;
    private double viewportZoom = 1.0;
    private double minZoom = 0.01;
    private double maxZoom = 10.0;
    private boolean isPanning = false;
    private double lastMouseX = 0.0;
    private double lastMouseY = 0.0;
    private GraphNode draggedNode = null;
    private double dragOffsetX = 0.0;
    private double dragOffsetY = 0.0;
    private boolean showEdgeWeights = true;
    private boolean showNodeLabels = true;
    private boolean isAnimating = false;

    public enum LayoutType {
        FORCE_DIRECTED,
        GEOGRAPHIC,
    }
    
    private LayoutType currentLayout = LayoutType.GEOGRAPHIC;
    private AnimationTimer animationTimer;


    private double temperature = 0;
    private static final int NODE_DIAMETER = 16;

    private static final Color NODE_COLOR = Color.LIGHTBLUE;
    private static final Color PRIMARY_NODE_HIGHLIGHT_COLOR = Color.web("#e74c3c");
    private static final Color SECONDARY_NODE_HIGHLIGHT_COLOR = Color.web("#9b59b6");
    private static final Color RADIO_NODE_HIGHLIGHT_COLOR = Color.web("#3498db");
    private static final Color CENTER_NODE_HIGHLIGHT_COLOR = Color.web("#2ecc71");
    private static final Color DIAMETER_NODE_HIGHLIGHT_COLOR = Color.web("#e74c3c");
    private static final Color DRAGGING_NODE_COLOR = Color.ORANGE;
    private static final Color EDGE_COLOR = Color.GRAY;
    private static final Color PRIMARY_EDGE_HIGHLIGHT_COLOR = Color.web("#e74c3c");
    private static final Color SECONDARY_EDGE_HIGHLIGHT_COLOR = Color.web("#9b59b6");
    private static final Color RADIO_EDGE_HIGHLIGHT_COLOR = Color.web("#3498db");
    private static final Color CENTER_EDGE_HIGHLIGHT_COLOR = Color.web("#2ecc71");
    private static final Color DIAMETER_EDGE_HIGHLIGHT_COLOR = Color.web("#e74c3c");
    private static final Color TEXT_COLOR = Color.BLACK;
    private static final Color BACKGROUND_COLOR = Color.web("#ecf0f1");

    private static final int HIGHLIGHT_LEVEL_PRIMARY_ROUTE = 1;
    private static final int HIGHLIGHT_LEVEL_ALTERNATE_ROUTE = 2;
    private static final int HIGHLIGHT_LEVEL_RADIO = 3;
    private static final int HIGHLIGHT_LEVEL_DIAMETER = 4;
    private static final int HIGHLIGHT_LEVEL_CENTER = 5;

    public GraphView(Canvas canvas) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
        this.nodes = new ArrayList<>();
        this.edges = new ArrayList<>();
        this.nodeMap = new HashMap<>();

        setupEventHandlers();
    }


    public void initializeGraph(List<GraphNode> nodes, List<GraphEdge> edges) {
        this.nodes = new ArrayList<>(nodes);
        this.edges = new ArrayList<>(edges);
        this.nodeMap.clear();

     
        for (GraphNode node : nodes) {
            nodeMap.put(node.getId(), node);
        }

       
        applyLayout();
    }

 
    private void applyLayout() {
    
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

  
    private void applyOptimalSorting() {
        if (nodes.isEmpty()) {
            return;
        }

        switch (currentLayout) {
            case GEOGRAPHIC:
                sortNodesByGeographicCoordinates();
                break;
            case FORCE_DIRECTED:
               
                break;
        }
    }

  
    private void sortNodesAlphabetically() {
    }


    private void applyGeographicLayout() {
        if (nodes.isEmpty()) {
            return;
        }

    
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

        if (minLat == Double.MAX_VALUE) {
       
            return;
        }


        double canvasWidth = canvas.getWidth();
        double canvasHeight = canvas.getHeight();
        double padding = 50; 

        double latRange = maxLat - minLat;
        double lonRange = maxLon - minLon;

        double scaleX = (canvasWidth - 2 * padding) / lonRange;
        double scaleY = (canvasHeight - 2 * padding) / latRange;


        for (GraphNode node : nodes) {
            if (node.getLatitude() != 0 || node.getLongitude() != 0) {
                double x = padding + (node.getLongitude() - minLon) * scaleX;
                double y = padding + (maxLat - node.getLatitude()) * scaleY; 
                node.setPosition(x, y);
            } else {

                node.setPosition(canvasWidth / 2, canvasHeight / 2);
            }
        }


        render();
    }

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

    public void setLayoutType(LayoutType layoutType) {
        this.currentLayout = layoutType;
        if (nodes != null && !nodes.isEmpty()) {
            applyLayout();
        }
    }

 
    public LayoutType getCurrentLayout() {
        return currentLayout;
    }


    private void sortNodesByGeographicCoordinates() {
        nodes.sort((node1, node2) -> {
           
            int latCompare = Double.compare(node1.getLatitude(), node2.getLatitude());
            if (latCompare != 0) {
                return latCompare;
            }
            return Double.compare(node1.getLongitude(), node2.getLongitude());
        });
    }


    private void startLayoutAnimation() {
      
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
                
                    temperature *= 0.99;
                } else {
                    stopLayoutAnimation();
                }
                render();
            }
        };
        animationTimer.start();
    }

    public void stopLayoutAnimation() {
        isAnimating = false;
        if (animationTimer != null) {
            animationTimer.stop();
        }
    }


    private void updateForces() {
        if (nodes.isEmpty() || canvas.getWidth() == 0 || canvas.getHeight() == 0) {
            return;
        }

        double area = canvas.getWidth() * canvas.getHeight();
        double k = 1.2 * Math.sqrt(area / nodes.size());


        Map<GraphNode, Point2D> forces = new HashMap<>();
        for (GraphNode node : nodes) {
            forces.put(node, new Point2D(0, 0));
        }


        for (int i = 0; i < nodes.size(); i++) {
            for (int j = i + 1; j < nodes.size(); j++) {
                GraphNode node1 = nodes.get(i);
                GraphNode node2 = nodes.get(j);

                Point2D delta = node1.getPosition().subtract(node2.getPosition());
                double distance = Math.max(0.1, delta.magnitude());
                double repulsiveForce = (k * k) / distance;

                if (distance < NODE_DIAMETER * 2.5) {
                    repulsiveForce *= 20;
                }

                Point2D force = delta.normalize().multiply(repulsiveForce);
                forces.put(node1, forces.get(node1).add(force));
                forces.put(node2, forces.get(node2).subtract(force));
            }
        }

    
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

    public void render() {
    
        gc.setFill(BACKGROUND_COLOR);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gc.save();
        gc.translate(-viewportX * viewportZoom, -viewportY * viewportZoom);
        gc.scale(viewportZoom, viewportZoom);

        drawEdges();
        drawNodes();
        gc.restore();

      
        drawUI();
    }


    private void drawEdges() {
        for (GraphEdge edge : edges) {
            GraphNode source = edge.getSource();
            GraphNode target = edge.getTarget();

            int highlightLevel = edge.getHighlightLevel();
            Color edgeColor = EDGE_COLOR;
            switch (highlightLevel) {
                case HIGHLIGHT_LEVEL_PRIMARY_ROUTE:
                    edgeColor = PRIMARY_EDGE_HIGHLIGHT_COLOR;
                    break;
                case HIGHLIGHT_LEVEL_ALTERNATE_ROUTE:
                    edgeColor = SECONDARY_EDGE_HIGHLIGHT_COLOR;
                    break;
                case HIGHLIGHT_LEVEL_RADIO:
                    edgeColor = RADIO_EDGE_HIGHLIGHT_COLOR;
                    break;
                case HIGHLIGHT_LEVEL_DIAMETER:
                    edgeColor = DIAMETER_EDGE_HIGHLIGHT_COLOR;
                    break;
                case HIGHLIGHT_LEVEL_CENTER:
                    edgeColor = CENTER_EDGE_HIGHLIGHT_COLOR;
                    break;
                default:
                    edgeColor = EDGE_COLOR;
            }

            double lineWidth = edge.getHighlightWidth() > 0 ? edge.getHighlightWidth() : 1.0;

            gc.setStroke(edgeColor);
            gc.setLineWidth(lineWidth / viewportZoom);

       
            double x1 = source.getX();
            double y1 = source.getY();
            double x2 = target.getX();
            double y2 = target.getY();

     
            gc.strokeLine(x1, y1, x2, y2);

        
            if (showEdgeWeights && viewportZoom > 0.5) {
                double midX = (x1 + x2) / 2;
                double midY = (y1 + y2) / 2;

                gc.setFill(Color.WHITE);
                gc.fillOval(midX - 12, midY - 8, 24, 16);
                gc.setStroke(Color.BLACK);
                gc.setLineWidth(0.5 / viewportZoom);
                gc.strokeOval(midX - 12, midY - 8, 24, 16);

            
                gc.setFill(TEXT_COLOR);
                gc.setFont(Font.font("Arial", 8 / viewportZoom));
                String weightText = String.format("%.1f", edge.getWeight());
                gc.fillText(weightText, midX - 8, midY + 2);
            }
        }
    }

    private void drawNodes() {
        for (GraphNode node : nodes) {
            double x = node.getX();
            double y = node.getY();
            double radius = node.getRadius(); 

      
            Color nodeColor = NODE_COLOR;
            int highlightLevel = node.getHighlightLevel();
            if (node.isDragging()) {
                nodeColor = DRAGGING_NODE_COLOR;
            } else {
                switch (highlightLevel) {
                    case HIGHLIGHT_LEVEL_PRIMARY_ROUTE:
                        nodeColor = PRIMARY_NODE_HIGHLIGHT_COLOR;
                        break;
                    case HIGHLIGHT_LEVEL_ALTERNATE_ROUTE:
                        nodeColor = SECONDARY_NODE_HIGHLIGHT_COLOR;
                        break;
                    case HIGHLIGHT_LEVEL_RADIO:
                        nodeColor = RADIO_NODE_HIGHLIGHT_COLOR;
                        break;
                    case HIGHLIGHT_LEVEL_DIAMETER:
                        nodeColor = DIAMETER_NODE_HIGHLIGHT_COLOR;
                        break;
                    case HIGHLIGHT_LEVEL_CENTER:
                        nodeColor = CENTER_NODE_HIGHLIGHT_COLOR;
                        break;
                    default:
                        nodeColor = NODE_COLOR;
                }
            }

            gc.setFill(nodeColor);
            gc.fillOval(x - radius, y - radius, radius * 2, radius * 2);

       
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(1.0 / viewportZoom);
            gc.strokeOval(x - radius, y - radius, radius * 2, radius * 2);

     
            if (showNodeLabels && viewportZoom > 0.3) {
                gc.setFill(Color.BLACK); 
                gc.setFont(Font.font("Arial", FontWeight.BOLD, 10 / viewportZoom));
                gc.setTextAlign(TextAlignment.CENTER);
                gc.setTextBaseline(VPos.CENTER);

                String label = node.getName();
                gc.fillText(label, x, y);
            }
        }
    }

  
    private void drawUI() {
      
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


    private void handleMousePressed(MouseEvent event) {
        double mouseX = event.getX();
        double mouseY = event.getY();

        double worldX = (mouseX / viewportZoom) + viewportX;
        double worldY = (mouseY / viewportZoom) + viewportY;

        GraphNode clickedNode = getNodeAt(worldX, worldY);
        if (clickedNode != null) {
            draggedNode = clickedNode;
            clickedNode.setDragging(true);
            dragOffsetX = worldX - clickedNode.getX();
            dragOffsetY = worldY - clickedNode.getY();
        } else {
    
            isPanning = true;
            lastMouseX = mouseX;
            lastMouseY = mouseY;
        }
    }


    private void handleMouseDragged(MouseEvent event) {
        double mouseX = event.getX();
        double mouseY = event.getY();

        if (draggedNode != null) {
        
            double worldX = (mouseX / viewportZoom) + viewportX;
            double worldY = (mouseY / viewportZoom) + viewportY;
            draggedNode.setPosition(worldX - dragOffsetX, worldY - dragOffsetY);
            draggedNode.setVelocity(0, 0); 
        } else if (isPanning) {
        
            double deltaX = mouseX - lastMouseX;
            double deltaY = mouseY - lastMouseY;
            viewportX -= deltaX / viewportZoom;
            viewportY -= deltaY / viewportZoom;
            lastMouseX = mouseX;
            lastMouseY = mouseY;
        }

        render();
    }


    private void handleMouseReleased(MouseEvent event) {
        if (draggedNode != null) {
            draggedNode.setDragging(false);
            draggedNode = null;
        }
        isPanning = false;
    }

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

  
    private void clearHighlightState() {
        for (GraphNode node : nodes) {
            node.setHighlightLevel(0);
            node.setFixed(false);
        }
        for (GraphEdge edge : edges) {
            edge.setHighlightLevel(0);
            edge.setHighlightWidth(0.0);
        }
    }

    private GraphEdge findEdgeBetween(GraphNode source, GraphNode target) {
        for (GraphEdge edge : edges) {
            if (edge.connects(source, target)) {
                return edge;
            }
        }
        return null;
    }

    private void applyPathHighlight(List<String> pathNodeIds, int highlightLevel, double lineWidth) {
        if (pathNodeIds == null || pathNodeIds.isEmpty()) {
            return;
        }

        for (String nodeId : pathNodeIds) {
            GraphNode node = nodeMap.get(nodeId);
            if (node != null) {
                node.setHighlightLevel(highlightLevel);
                node.setFixed(true);
            }
        }

        for (int i = 0; i < pathNodeIds.size() - 1; i++) {
            GraphNode source = nodeMap.get(pathNodeIds.get(i));
            GraphNode target = nodeMap.get(pathNodeIds.get(i + 1));
            if (source != null && target != null) {
                GraphEdge edge = findEdgeBetween(source, target);
                if (edge != null) {
                    edge.setHighlightLevel(highlightLevel);
                    edge.setHighlightWidth(lineWidth);
                }
            }
        }
    }

    public void highlightRoutes(List<String> primaryPathNodeIds,
                                List<String> alternativePathNodeIds,
                                boolean invertColors) {
        clearHighlightState();

        boolean hasPrimary = primaryPathNodeIds != null && !primaryPathNodeIds.isEmpty();
        boolean hasAlternative = alternativePathNodeIds != null && !alternativePathNodeIds.isEmpty();

        if (!hasPrimary && !hasAlternative) {
            render();
            return;
        }

        int primaryLevel = invertColors ? HIGHLIGHT_LEVEL_ALTERNATE_ROUTE : HIGHLIGHT_LEVEL_PRIMARY_ROUTE;
        int alternativeLevel = invertColors ? HIGHLIGHT_LEVEL_PRIMARY_ROUTE : HIGHLIGHT_LEVEL_ALTERNATE_ROUTE;

        if (hasPrimary && hasAlternative) {
            if (invertColors) {
                applyPathHighlight(primaryPathNodeIds, primaryLevel, 3.0);
                applyPathHighlight(alternativePathNodeIds, alternativeLevel, 4.0);
            } else {
                applyPathHighlight(alternativePathNodeIds, alternativeLevel, 3.0);
                applyPathHighlight(primaryPathNodeIds, primaryLevel, 4.0);
            }
        } else {
            if (hasPrimary) {
                applyPathHighlight(primaryPathNodeIds, primaryLevel, 4.0);
            }
            if (hasAlternative) {
                applyPathHighlight(alternativePathNodeIds, alternativeLevel, 3.0);
            }
        }

        render();
    }

    public void highlightPath(List<String> pathNodeIds) {
        highlightRoutes(pathNodeIds, null, false);
    }

    public void highlightRadioPath(List<String> pathNodeIds) {
        clearHighlightState();
        if (pathNodeIds != null && !pathNodeIds.isEmpty()) {
            applyPathHighlight(pathNodeIds, HIGHLIGHT_LEVEL_RADIO, 4.0);
        }
        render();
    }

    public void highlightDiameterPath(List<String> pathNodeIds) {
        clearHighlightState();
        if (pathNodeIds != null && !pathNodeIds.isEmpty()) {
            applyPathHighlight(pathNodeIds, HIGHLIGHT_LEVEL_DIAMETER, 4.5);
        }
        render();
    }

    public void highlightCenterNodes(List<String> nodeIds) {
        highlightNodesInternal(nodeIds, HIGHLIGHT_LEVEL_CENTER);
    }

    private void highlightNodesInternal(List<String> nodeIds, int highlightLevel) {
        clearHighlightState();

        if (nodeIds != null) {
            for (String nodeId : nodeIds) {
                GraphNode node = nodeMap.get(nodeId);
                if (node != null) {
                    node.setHighlightLevel(highlightLevel);
                    node.setFixed(true);
                }
            }
        }

        render();
    }


    public void highlightNodes(List<String> nodeIds) {
        highlightNodesInternal(nodeIds, HIGHLIGHT_LEVEL_PRIMARY_ROUTE);
    }

    public void highlightNodesWithLevel(List<String> nodeIds, int highlightLevel) {
        highlightNodesInternal(nodeIds, highlightLevel);
    }

    public void clearHighlights() {
        clearHighlightState();
        render();
    }


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

    public void resetView() {
        viewportZoom = 1.0;
        centerView();
    }


    public void zoomIn() {
        double oldZoom = viewportZoom;
        viewportZoom = Math.min(maxZoom, viewportZoom * 1.2);

        if (oldZoom != viewportZoom) {
            double centerX = canvas.getWidth() / 2.0;
            double centerY = canvas.getHeight() / 2.0;

            double worldCenterX = (centerX / oldZoom) + viewportX;
            double worldCenterY = (centerY / oldZoom) + viewportY;

            viewportX = worldCenterX - (centerX / viewportZoom);
            viewportY = worldCenterY - (centerY / viewportZoom);

            render();
        }
    }

    public void zoomOut() {
        double oldZoom = viewportZoom;
        viewportZoom = Math.max(minZoom, viewportZoom / 1.2);

        if (oldZoom != viewportZoom) {
          
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
