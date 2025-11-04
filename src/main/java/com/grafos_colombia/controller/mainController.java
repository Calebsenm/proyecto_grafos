package com.grafos_colombia.controller;

import com.grafos_colombia.algorithm.Bfs;
import com.grafos_colombia.algorithm.Dijkstra;
import com.grafos_colombia.algorithm.Eccentricity;
import com.grafos_colombia.algorithm.PathResult;
import com.grafos_colombia.database.DatabaseConnection;
import com.grafos_colombia.database.GraphDataLoader;
import com.grafos_colombia.graph.Edge;
import com.grafos_colombia.graph.Graph;
import com.grafos_colombia.graph.GraphConverter;
import com.grafos_colombia.graph.GraphView;
import com.grafos_colombia.graph.Node;
import java.net.URL;
import java.util.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

/**
 * Controlador principal (versión mínima: sin coordenadas, sin cache)
 */
public class MainController implements Initializable {

    // === FXML Components ===
    @FXML private TextField originFilterField;
    @FXML private ComboBox<String> originComboBox;
    @FXML private TextField destinationFilterField;
    @FXML private ComboBox<String> destinationComboBox;

    @FXML private ToggleGroup calculationTypeGroup;
    @FXML private RadioButton shortestPathRadio;
    @FXML private RadioButton simpleCycleRadio;
    @FXML private RadioButton eccentricityRadio;

    @FXML private VBox originContainer;
    @FXML private VBox destinationContainer;

    @FXML private Button geographicLayoutButton;
    @FXML private Button forceLayoutButton;
    @FXML private Button calculateButton;
    @FXML private Button clearButton;
    @FXML private Button loadExampleButton;
    @FXML private Button loadDatabaseButton;
    @FXML private Button toggleLabelsButton;
    @FXML private Button zoomInButton;
    @FXML private Button zoomOutButton;
    @FXML private Button resetZoomButton;

    @FXML private TextArea pathResultArea;
    @FXML private Label databaseStatusLabel;
    @FXML private Label distanceLabel;
    @FXML private Label statsLabel;

    @FXML private Canvas graphCanvas;
    @FXML private ScrollPane graphScrollPane;

    // === Graph & DB ===
    private GraphView graphView;
    private Graph currentGraph;
    private Map<String, List<Node>> adjList;
    private DatabaseConnection db;
    private GraphDataLoader loader;

    private FilteredList<String> originFilteredList;
    private FilteredList<String> destinationFilteredList;
    private boolean showEdgeLabels = true;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("Inicializando MainController...");

        db = DatabaseConnection.getInstance();
        loader = new GraphDataLoader();

        initializeUI();
        loadGraphData();

        System.out.println("MainController inicializado");
    }

    private void initializeUI() {
        originFilterField.setPromptText("Filtrar origen...");
        destinationFilterField.setPromptText("Filtrar destino...");
        originComboBox.setPromptText("Origen...");
        destinationComboBox.setPromptText("Destino...");

        pathResultArea.setEditable(false);
        pathResultArea.setText("Selecciona origen y destino.");

        if (graphCanvas != null && graphScrollPane != null) {
            graphCanvas.widthProperty().bind(graphScrollPane.widthProperty());
            graphCanvas.heightProperty().bind(graphScrollPane.heightProperty());
        }

        updateDatabaseStatus();
    }

    private void loadGraphData() {
        if (db.isConnected() && db.tieneDatos()) {
            loadDatabaseGraph();
        } else {
            loadExampleGraph();
        }
    }

    @FXML
    private void loadExampleGraph() {
        System.out.println("Cargando grafo de ejemplo...");
        List<Edge> edges = getExampleEdges();
        Graph graph = new Graph(edges);
        setupGraph(graph, false);
        setForceLayout();
    }

    @FXML
    private void loadDatabaseGraph() {
        System.out.println("Cargando desde base de datos...");
        Graph graph = loader.cargarGrafoCompleto();
        if (graph != null) {
            setupGraph(graph, false); // SIN coordenadas
            setForceLayout(); // Solo fuerza
            pathResultArea.setText("Grafo cargado desde BD.");
        } else {
            pathResultArea.setText("No hay datos en BD. Usando ejemplo.");
            loadExampleGraph();
        }
    }

    private void setupGraph(Graph graph, boolean hasCoords) {
        clearResults();
        currentGraph = graph;
        adjList = graph.getAdjList();

        initializeGraphView(hasCoords);
        populateComboBoxes();
        updateDatabaseStatus();
    }

    private void initializeGraphView(boolean hasCoords) {
        if (graphCanvas == null) return;

        graphView = new GraphView(graphCanvas);
        List<Edge> edges = extractEdges();

        var data = GraphConverter.convertNodeAdjList(adjList, edges);
        graphView.initializeGraph(data.getNodes(), data.getEdges());
        graphView.render();
    }

    private List<Edge> extractEdges() {
        List<Edge> edges = new ArrayList<>();
        Set<String> seen = new HashSet<>();

        for (var entry : adjList.entrySet()) {
            String src = entry.getKey();
            for (Node n : entry.getValue()) {
                String dest = n.getValue();
                String key = src.compareTo(dest) < 0 ? src + "-" + dest : dest + "-" + src;
                if (!seen.contains(key)) {
                    edges.add(new Edge(src, dest, n.getWeight()));
                    seen.add(key);
                }
            }
        }
        return edges;
    }

    private void populateComboBoxes() {
        if (adjList.isEmpty()) return;

        List<String> names = new ArrayList<>(adjList.keySet());
        names.sort(String.CASE_INSENSITIVE_ORDER);
        ObservableList<String> list = FXCollections.observableArrayList(names);

        originFilteredList = new FilteredList<>(list, s -> true);
        destinationFilteredList = new FilteredList<>(list, s -> true);

        originComboBox.setItems(originFilteredList);
        destinationComboBox.setItems(destinationFilteredList);

        if (!names.isEmpty()) {
            originComboBox.setValue(names.get(0));
            if (names.size() > 1) destinationComboBox.setValue(names.get(1));
        }
    }

    @FXML
    private void calculateShortestPath() {
        RadioButton selected = (RadioButton) calculationTypeGroup.getSelectedToggle();
        if (selected == simpleCycleRadio) {
            handleCycle();
        } else if (selected == eccentricityRadio) {
            handleEccentricity();
        } else {
            handleShortestPath();
        }
    }

    private void handleShortestPath() {
        String o = originComboBox.getValue();
        String d = destinationComboBox.getValue();

        if (o == null || d == null || o.equals(d)) {
            pathResultArea.setText("Selecciona origen y destino diferentes.");
            return;
        }

        PathResult result = Dijkstra.dijkstra(o, d, adjList);
        if (result != null) {
            displayResult(result);
            graphView.highlightPath(result.path);
        } else {
            pathResultArea.setText("No hay ruta entre " + o + " y " + d);
        }
    }

    private void handleCycle() {
        String start = originComboBox.getValue();
        if (start == null) {
            pathResultArea.setText("Selecciona un nodo.");
            return;
        }

        List<String> cycle = Bfs.findShortestCycle(adjList, start);
        if (cycle != null && !cycle.isEmpty()) {
            double dist = 0;
            for (int i = 0; i < cycle.size() - 1; i++) {
                String u = cycle.get(i), v = cycle.get(i + 1);
                for (Node n : adjList.get(u)) {
                    if (n.getValue().equals(v)) {
                        dist += n.getWeight();
                        break;
                    }
                }
            }
            pathResultArea.setText("Ciclo: " + String.join(" → ", cycle));
            distanceLabel.setText(String.format("%.1f km", dist));
            statsLabel.setText("Nodos: " + (cycle.size() - 1));
            graphView.highlightPath(cycle);
        } else {
            pathResultArea.setText("No hay ciclo desde " + start);
        }
    }

    private void handleEccentricity() {
        String start = originComboBox.getValue();
        if (start == null) {
            pathResultArea.setText("Selecciona un nodo.");
            return;
        }

        var res = Eccentricity.calculate(start, adjList);
        if (res != null && res.farthestNode != null) {
            distanceLabel.setText(String.format("%.1f km", res.eccentricity));
            statsLabel.setText("Nodos: " + res.path.size());
            pathResultArea.setText("Más lejano: " + res.farthestNode);
            graphView.highlightPath(res.path);
        } else {
            pathResultArea.setText("Nodo aislado.");
        }
    }

    private void displayResult(PathResult r) {
        distanceLabel.setText(String.format("%.1f km", r.distance));
        statsLabel.setText("Nodos: " + r.path.size() + " | Aristas: " + (r.path.size() - 1));
        pathResultArea.setText(String.join(" → ", r.path));
    }

    @FXML private void handleCalculationTypeChange() {
        RadioButton sel = (RadioButton) calculationTypeGroup.getSelectedToggle();
        boolean showDest = sel == shortestPathRadio;
        destinationContainer.setVisible(showDest);
        destinationContainer.setManaged(showDest);
        calculateButton.setText("Calcular " + sel.getText().split(" ")[0]);
    }

    @FXML private void clearResults() {
        pathResultArea.setText("Selecciona nodos...");
        originComboBox.setValue(null);
        destinationComboBox.setValue(null);
        originFilterField.clear();
        destinationFilterField.clear();
        if (originFilteredList != null) originFilteredList.setPredicate(s -> true);
        if (destinationFilteredList != null) destinationFilteredList.setPredicate(s -> true);
        if (graphView != null) graphView.highlightPath(List.of());
    }

    @FXML private void filterOriginComboBox() { filterCombo(originFilterField, originFilteredList, originComboBox); }
    @FXML private void filterDestinationComboBox() { filterCombo(destinationFilterField, destinationFilteredList, destinationComboBox); }

    private void filterCombo(TextField field, FilteredList<String> list, ComboBox<String> box) {
        String text = field.getText();
        if (text == null || text.isBlank()) {
            list.setPredicate(s -> true);
        } else {
            String lower = text.toLowerCase();
            list.setPredicate(s -> s.toLowerCase().contains(lower));
        }
        box.setPromptText(list.isEmpty() ? "Sin resultados" : "Selecciona...");
    }

    @FXML private void toggleEdgeLabels() {
        showEdgeLabels = !showEdgeLabels;
        if (graphView != null) graphView.setShowEdgeWeights(showEdgeLabels);
        toggleLabelsButton.setText(showEdgeLabels ? "Ocultar" : "Mostrar");
    }

    @FXML private void setForceLayout() {
        if (graphView != null) {
            graphView.setLayoutType(GraphView.LayoutType.FORCE_DIRECTED);
            updateLayoutButtons(GraphView.LayoutType.FORCE_DIRECTED);
        }
    }

    // Deshabilitamos layout geográfico
    @FXML private void setGeographicLayout() {
        pathResultArea.setText("Layout geográfico no disponible (sin coordenadas).");
    }

    private void updateLayoutButtons(GraphView.LayoutType type) {
        String active = "-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;";
        String inactive = "-fx-background-color: #95a5a6; -fx-text-fill: white;";
        forceLayoutButton.setStyle(type == GraphView.LayoutType.FORCE_DIRECTED ? active : inactive);
        geographicLayoutButton.setStyle("-fx-background-color: #7f8c8d; -fx-text-fill: #bdc3c7;"); // Gris
        geographicLayoutButton.setDisable(true);
    }

    @FXML private void zoomIn() { if (graphView != null) graphView.zoomIn(); }
    @FXML private void zoomOut() { if (graphView != null) graphView.zoomOut(); }
    @FXML private void resetZoom() { if (graphView != null) graphView.resetView(); }

    private void updateDatabaseStatus() {
        if (databaseStatusLabel == null) return;
        if (db.isConnected()) {
            databaseStatusLabel.setText("BD: Conectada");
            databaseStatusLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
        } else {
            databaseStatusLabel.setText("BD: Desconectada");
            databaseStatusLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        }
    }

    // === DATOS DE EJEMPLO ===
    private List<Edge> getExampleEdges() {
        return List.of(
            new Edge("San Antero", "Lorica", 20),
            new Edge("Lorica", "Montería", 45),
            new Edge("Montería", "Cereté", 15),
            new Edge("Cereté", "San Pelayo", 18),
            new Edge("San Pelayo", "Cotorra", 12),
            new Edge("Montería", "Planeta Rica", 70),
            new Edge("Planeta Rica", "Sahagún", 55),
            new Edge("Sahagún", "Chinú", 30)
        );
    }
}