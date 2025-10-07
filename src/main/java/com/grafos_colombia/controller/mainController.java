package com.grafos_colombia.controller;

import com.grafos_colombia.algorithm.Bfs;
import com.grafos_colombia.algorithm.Dijkstra;
import com.grafos_colombia.algorithm.Eccentricity;
import com.grafos_colombia.algorithm.PathResult;
import com.grafos_colombia.database.AristaDAO;
import com.grafos_colombia.database.DatabaseConnection;
import com.grafos_colombia.database.GraphDataLoader;
import com.grafos_colombia.database.NodoDAO;
import com.grafos_colombia.database.RutaDAO;
import com.grafos_colombia.graph.Edge;
import com.grafos_colombia.graph.GeoNode;
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
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/**
 * Controlador principal de la aplicación de grafos de Colombia
 */
public class MainController implements Initializable {

    // FXML Components
    @FXML
    private TextField originFilterField;
    @FXML
    private ComboBox<String> originComboBox;
    @FXML
    private TextField destinationFilterField;
    @FXML
    private ComboBox<String> destinationComboBox;

    // Calculation Type Components
    @FXML
    private ToggleGroup calculationTypeGroup;
    @FXML
    private RadioButton shortestPathRadio;
    @FXML
    private RadioButton simpleCycleRadio;
    @FXML
    private RadioButton eccentricityRadio;

    @FXML
    private VBox originContainer;
    @FXML
    private VBox destinationContainer;

    // Layout buttons
    @FXML
    private Button geographicLayoutButton;
    @FXML
    private Button forceLayoutButton;

    @FXML
    private Button calculateButton;
    @FXML
    private Button clearButton;
    @FXML
    private Button loadExampleButton;
    @FXML
    private Button loadDatabaseButton;
    @FXML
    private Button toggleLabelsButton;
    @FXML
    private Button showHistoryButton;
    @FXML
    private Button clearCacheButton;
    @FXML
    private Button zoomInButton;
    @FXML
    private Button zoomOutButton;
    @FXML
    private Button resetZoomButton;

    @FXML
    private TextArea pathResultArea;
    @FXML
    private VBox leftControls;
    @FXML
    private VBox rightControls;

    // Status labels
    @FXML
    private Label databaseStatusLabel;
    @FXML
    private Label distanceLabel;
    @FXML
    private Label statsLabel;
    @FXML
    private Label cacheStatusLabel;

    // Canvas for graph visualization
    @FXML
    private Canvas graphCanvas;
    @FXML
    private ScrollPane graphScrollPane;

    // Graph and database components
    private GraphView graphView;
    private Graph currentGraph;
    private Map<String, List<Node>> adjList;
    private DatabaseConnection databaseConnection;
    private GraphDataLoader graphDataLoader;
    private NodoDAO nodoDAO;
    private AristaDAO aristaDAO;
    private RutaDAO rutaDAO;

    // Filtered lists for ComboBoxes
    private FilteredList<String> originFilteredList;
    private FilteredList<String> destinationFilteredList;

    // Application state
    private boolean useDatabase = false;
    private boolean showEdgeLabels = true;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("🚀 Inicializando MainController...");

        // Initialize database connection
        initializeDatabase();

        // Initialize UI components
        initializeUIComponents();

        // Load default graph
        loadGraphData();

        System.out.println("✅ MainController inicializado correctamente");
    }

    /**
     * Initialize database connection and loader
     */
    private void initializeDatabase() {
        databaseConnection = DatabaseConnection.getInstance();
        graphDataLoader = new GraphDataLoader();
        nodoDAO = new NodoDAO();
        aristaDAO = new AristaDAO();
        rutaDAO = new RutaDAO();

        // Try to connect to database
        if (databaseConnection.connect()) {
            System.out.println("✅ Conexión a base de datos establecida");
            useDatabase = true;

            // Verify tables exist
            if (databaseConnection.verifyTables()) {
                System.out.println("✅ Tablas de base de datos verificadas");
            } else {
                System.out.println("⚠️ Algunas tablas no existen en la base de datos");
            }
        } else {
            System.out.println("❌ No se pudo conectar a la base de datos, usando datos hardcoded");
            useDatabase = false;
        }
    }

    /**
     * Initialize UI components
     */
    private void initializeUIComponents() {
        // Initialize filter fields
        if (originFilterField != null) {
            originFilterField.setPromptText("Filtrar origen...");
        }
        if (destinationFilterField != null) {
            destinationFilterField.setPromptText("Filtrar destino...");
        }

        // Initialize ComboBoxes
        if (originComboBox != null) {
            originComboBox.setPromptText("Selecciona origen...");
        }
        if (destinationComboBox != null) {
            destinationComboBox.setPromptText("Selecciona destino...");
        }

        // Initialize result text area
        if (pathResultArea != null) {
            pathResultArea.setEditable(false);
            pathResultArea.setText("Selecciona origen y destino para calcular la ruta más corta.");
        }

        // Bind canvas size to its container (ScrollPane) to make it responsive
        if (graphCanvas != null && graphScrollPane != null) {
            graphCanvas.widthProperty().bind(graphScrollPane.widthProperty());
            graphCanvas.heightProperty().bind(graphScrollPane.heightProperty());
            System.out.println("✅ Canvas enlazado al tamaño del ScrollPane para ser responsivo.");
        } else {
            System.out.println("❌ Canvas no encontrado en initializeUIComponents()");
        }

        // Update database status
        updateDatabaseStatus();
    }

    /**
     * Load graph data (example or from database)
     */
    private void loadGraphData() {
        if (useDatabase) {
            // Try to load from database first
            loadDatabaseGraph();
            return;
        }

        // Fallback to example graph
        loadExampleGraph();
    }

    /**
     * Load example graph with hardcoded data
     */
    @FXML
    private void loadExampleGraph() {
        System.out.println("📊 Cargando grafo ejemplo...");

        List<Edge> edges = Arrays.asList(
                new Edge("San antero", "Nuevo agrado", 11),
                new Edge("Nuevo agrado", "Lorica", 9.1),
                new Edge("Lorica", "La doctrina", 12),
                new Edge("Lorica", "Juan de dios garis", 4.2),
                new Edge("Lorica", "Mata de caña", 20),
                new Edge("La doctrina", "Trementino", 7.9),
                new Edge("Trementino", "San bernardo del viento", 5.5),
                new Edge("San bernardo del viento", "Paso nuevo", 16),
                new Edge("Paso nuevo", "Moñitos", 15),
                new Edge("Moñitos", "Cristo rey", 37),
                new Edge("Cristo rey", "Puerto escondido", 9.6),
                new Edge("Puerto escondido", "El palmar", 6.7),
                new Edge("El palmar", "Los cordobas", 27.9),
                new Edge("El palmar", "El ebano", 15.7),
                new Edge("Los cordobas", "El ebano", 21),
                new Edge("El ebano", "Los cedros", 29),
                new Edge("El ebano", "Canalete", 11),
                new Edge("Canalete", "La seba", 14),
                new Edge("La seba", "Los cedros", 25),
                new Edge("Los cedros", "Monteria", 21),
                new Edge("Monteria", "El amparo", 16),
                new Edge("Monteria", "Los garzones", 13),
                new Edge("Monteria", "El sabanal", 10),
                new Edge("El amparo", "Pueblo vaca", 28),
                new Edge("Pueblo vaca", "La ceiba", 17),
                new Edge("La ceiba", "Los morales", 16),
                new Edge("Los morales", "Tierralta", 8.2),
                new Edge("Los morales", "Valencia", 26),
                new Edge("El ebano", "San pelayo", 47),
                new Edge("San pelayo", "Carrillo", 2.8),
                new Edge("Carrillo", "Cotorra", 11),
                new Edge("Cotorra", "Mata de caña", 7.7),
                new Edge("Cotorra", "Rabolargo", 15),
                new Edge("Rabolargo", "Punta de yanez", 23),
                new Edge("Punta de yanez", "Chima", 13),
                new Edge("Punta de yanez", "Cienaga de oro", 18),
                new Edge("Juan de dios garis", "Los corrales", 4.2),
                new Edge("Los corrales", "Purisima", 4.5),
                new Edge("Purisima", "Momil", 6.2),
                new Edge("Momil", "Sabanacosta", 9.1),
                new Edge("Sabanacosta", "Tuchin", 7.6),
                new Edge("Tuchin", "Campo bello", 5.5),
                new Edge("Campo bello", "Chima", 18),
                new Edge("Tuchin", "San andres de sotavento", 7.4),
                new Edge("San andres de sotavento", "Cacaotal", 10),
                new Edge("Cacaotal", "Chinu", 8.5),
                new Edge("Chinu", "Tierra grata", 7.7),
                new Edge("Tierra grata", "Sahagun", 16),
                new Edge("Sahagun", "La mesa", 8.7),
                new Edge("La mesa", "Cienaga de oro", 16),
                new Edge("Cienaga de oro", "Berastegui", 12),
                new Edge("Berastegui", "Rabolargo", 13),
                new Edge("San pelayo", "Pelayito", 3.4),
                new Edge("Pelayito", "Cerete", 6.7),
                new Edge("Cerete", "Los garzones", 10),
                new Edge("Cerete", "Cabuya", 14),
                new Edge("Cerete", "Berastegui", 11),
                new Edge("Cabuya", "San carlos", 5.4),
                new Edge("San carlos", "El sabanal", 14),
                new Edge("San carlos", "El amparo", 22),
                new Edge("San carlos", "Patio bonito", 24),
                new Edge("El amparo", "Patio bonito", 6.7),
                new Edge("Patio bonito", "Planeta rica", 31),
                new Edge("Planeta rica", "Pueblo nuevo", 15),
                new Edge("Pueblo nuevo", "La ye", 41),
                new Edge("La ye", "Sahagun", 18),
                new Edge("La ye", "Cienaga de oro", 17),
                new Edge("Planeta rica", "Plaza bonita", 15),
                new Edge("Plaza bonita", "Buenavista", 9.3),
                new Edge("Buenavista", "Rusia", 18),
                new Edge("Rusia", "Puerto cordoba", 5.3),
                new Edge("Puerto cordoba", "La apartada", 7.9),
                new Edge("La apartada", "Palotal", 27),
                new Edge("Palotal", "Las delicias", 5.3),
                new Edge("Las delicias", "Ayapel", 8.9),
                new Edge("La apartada", "La balsa", 6.9),
                new Edge("La balsa", "Montelibano", 8.8),
                new Edge("Montelibano", "Buenos aires", 35),
                new Edge("Buenos aires", "Puerto libertador", 3.9),
                new Edge("Buenos aires", "San jose de ure", 40)
        );

        currentGraph = new Graph(edges);
        adjList = currentGraph.getAdjList();

        // Initialize GraphView
        initializeGraphView();

        // Populate ComboBoxes
        populateComboBoxes();

        // Set force-directed layout by default for the example graph
        if (graphView != null) {
            graphView.setLayoutType(GraphView.LayoutType.FORCE_DIRECTED);
            updateLayoutButtonStyles(GraphView.LayoutType.FORCE_DIRECTED);
        }

        System.out.println("✅ Grafo ejemplo cargado con " + edges.size() + " aristas");
    }

    /**
     * Load graph from database
     */
    @FXML
    private void loadDatabaseGraph() {
        System.out.println("🗄️ Cargando grafo desde base de datos...");

        if (!useDatabase) {
            if (pathResultArea != null) {
                pathResultArea.setText("❌ No hay conexión a la base de datos disponible.");
            }
            return;
        }

        try {
            // Load graph from database
            currentGraph = graphDataLoader.cargarGrafoCompleto();

            if (currentGraph != null) {
                adjList = currentGraph.getAdjList();

                // Initialize GraphView with geographic coordinates
                initializeGraphView();

                // Populate ComboBoxes
                populateComboBoxes();

                // Update database status
                updateDatabaseStatus();

                if (pathResultArea != null) {
                    pathResultArea.setText("✅ Grafo cargado desde la base de datos exitosamente.");
                }
                System.out.println("✅ Grafo cargado desde BD con " + adjList.size() + " nodos");
            } else {
                if (pathResultArea != null) {
                    pathResultArea.setText("❌ No se pudo cargar el grafo desde la base de datos.");
                }
                System.out.println("❌ Error al cargar grafo desde BD");
            }

        } catch (Exception e) {
            if (pathResultArea != null) {
                pathResultArea.setText("❌ Error al cargar desde la base de datos: " + e.getMessage());
            }
            System.out.println("❌ Excepción al cargar desde BD: " + e.getMessage());
        }
    }

    /**
     * Initialize GraphView with current graph
     */
    private void initializeGraphView() {
        System.out.println("🔍 Iniciando initializeGraphView()...");

        if (graphCanvas == null) {
            System.out.println("❌ Canvas no está disponible - verificar FXML");
            return;
        }

        System.out.println("✅ Canvas encontrado: " + graphCanvas.getWidth() + "x" + graphCanvas.getHeight());

        if (adjList == null || adjList.isEmpty()) {
            System.out.println("❌ AdjList está vacía o es null");
            return;
        }

        System.out.println("✅ AdjList tiene " + adjList.size() + " nodos");

        try {
            // Create GraphView with the canvas
            System.out.println("🔧 Creando GraphView...");
            graphView = new GraphView(graphCanvas);

            // Convert graph to GraphView format
            System.out.println("🔧 Extrayendo aristas...");
            List<Edge> edges = extractEdgesFromAdjList();
            System.out.println("✅ Extraídas " + edges.size() + " aristas");

            // Try to use geographic coordinates if available
            if (useDatabase) {
                System.out.println("🗄️ Usando coordenadas geográficas de BD...");
                Map<String, GeoNode> geoNodes = nodoDAO.obtenerMapaNodos();
                System.out.println("✅ Obtenidos " + geoNodes.size() + " nodos geográficos");

                GraphConverter.GraphViewData graphData = GraphConverter.convertWithGeographicCoordinates(
                        adjList, edges, geoNodes);
                System.out.println("✅ Convertidos " + graphData.getNodes().size() + " nodos y " + graphData.getEdges().size() + " aristas");

                graphView.initializeGraph(graphData.getNodes(), graphData.getEdges());
            } else {
                System.out.println("📊 Usando conversión estándar...");
                GraphConverter.GraphViewData graphData = GraphConverter.convertNodeAdjList(adjList, edges);
                System.out.println("✅ Convertidos " + graphData.getNodes().size() + " nodos y " + graphData.getEdges().size() + " aristas");

                graphView.initializeGraph(graphData.getNodes(), graphData.getEdges());
            }

            System.out.println("✅ GraphView inicializado correctamente con " + edges.size() + " aristas");

            // Force a render to see if it works
            if (graphView != null) {
                System.out.println("🎨 Forzando render...");
                graphView.render();
                System.out.println("✅ Render completado");
            }

        } catch (Exception e) {
            System.err.println("❌ Error al inicializar GraphView: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Extract edges from adjacency list
     */
    private List<Edge> extractEdgesFromAdjList() {
        List<Edge> edges = new ArrayList<>();
        Set<String> processed = new HashSet<>();

        for (Map.Entry<String, List<Node>> entry : adjList.entrySet()) {
            String source = entry.getKey();
            for (Node neighbor : entry.getValue()) {
                String target = neighbor.getValue();
                String edgeKey = Math.min(source.hashCode(), target.hashCode()) + "-"
                        + Math.max(source.hashCode(), target.hashCode());

                if (!processed.contains(edgeKey)) {
                    edges.add(new Edge(source, target, neighbor.getWeight()));
                    processed.add(edgeKey);
                }
            }
        }

        return edges;
    }

    /**
     * Populate ComboBoxes with node names
     */
    private void populateComboBoxes() {
        if (adjList == null || adjList.isEmpty()) {
            System.out.println("⚠️ No hay datos para poblar ComboBoxes");
            return;
        }

        // Get sorted list of node names
        List<String> nodeNames = new ArrayList<>(adjList.keySet());
        nodeNames.sort(String.CASE_INSENSITIVE_ORDER);

        ObservableList<String> nodeList = FXCollections.observableArrayList(nodeNames);

        // Create filtered lists
        originFilteredList = new FilteredList<>(nodeList, s -> true);
        destinationFilteredList = new FilteredList<>(nodeList, s -> true);

        // Set items in ComboBoxes
        if (originComboBox != null) {
            originComboBox.setItems(originFilteredList);
        }
        if (destinationComboBox != null) {
            destinationComboBox.setItems(destinationFilteredList);
        }

        // Set default values
        if (!nodeNames.isEmpty()) {
            if (originComboBox != null) {
                originComboBox.setValue(nodeNames.get(0));
            }
            if (destinationComboBox != null && nodeNames.size() > 1) {
                destinationComboBox.setValue(nodeNames.get(1));
            }
        }

        System.out.println("✅ ComboBoxes poblados con " + nodeNames.size() + " nodos (con filtros)");
    }

    /**
     * Calculate shortest path between selected nodes
     */
    @FXML
    private void calculateShortestPath() {
        RadioButton selectedRadio = (RadioButton) calculationTypeGroup.getSelectedToggle();

        if (selectedRadio.equals(simpleCycleRadio)) {
            handleCycleDetection();
            return;
        }
        if (selectedRadio.equals(eccentricityRadio)) {
            handleEccentricityCalculation();
            return;
        }
        // Si no es ninguno de los nuevos, ejecuta el cálculo de ruta corta
        handleShortestPathCalculation();
    }

    private void handleShortestPathCalculation() {
        String origin = originComboBox != null ? originComboBox.getValue() : null;
        String destination = destinationComboBox != null ? destinationComboBox.getValue() : null;

        if (origin == null || origin.trim().isEmpty()) {
            if (pathResultArea != null) {
                pathResultArea.setText("❌ Por favor selecciona un nodo de origen.");
            }
            return;
        }

        if (destination == null || destination.trim().isEmpty()) {
            if (pathResultArea != null) {
                pathResultArea.setText("❌ Por favor selecciona un nodo de destino.");
            }
            return;
        }

        if (origin.equals(destination)) {
            if (pathResultArea != null) {
                pathResultArea.setText("❌ El origen y destino deben ser diferentes.");
            }
            return;
        }

        if (adjList == null || adjList.isEmpty()) {
            if (pathResultArea != null) {
                pathResultArea.setText("❌ No hay grafo cargado para calcular rutas.");
            }
            return;
        }

        System.out.println("🔍 Calculando ruta de " + origin + " a " + destination + "...");

        try {
            // Check cache first if using database
            PathResult cachedResult = null;
            if (useDatabase) {
                cachedResult = rutaDAO.buscarRutaExistente(origin, destination);
                if (cachedResult != null) {
                    System.out.println("📋 Ruta encontrada en cache");
                    displayPathResult(cachedResult, origin, destination, true);
                    return;
                }
            }

            // Calculate using Dijkstra's algorithm
            PathResult result = Dijkstra.dijkstra(origin, destination, adjList);

            if (result != null) {
                // Cache the result if using database
                if (useDatabase) {
                    rutaDAO.guardarRuta(result, origin, destination);
                    System.out.println("💾 Ruta guardada en cache");
                }

                displayPathResult(result, origin, destination, false);

                // Highlight path in GraphView
                if (graphView != null) {
                    graphView.highlightPath(result.path);
                }

            } else {
                if (pathResultArea != null) {
                    pathResultArea.setText("❌ No se encontró una ruta entre " + origin + " y " + destination + ".");
                }
            }

        } catch (Exception e) {
            if (pathResultArea != null) {
                pathResultArea.setText("❌ Error al calcular la ruta: " + e.getMessage());
            }
            System.err.println("❌ Error en cálculo de ruta: " + e.getMessage());
        }
    }

    private void handleCycleDetection() {
        if (adjList == null || adjList.isEmpty()) {
            pathResultArea.setText("❌ No hay grafo cargado para detectar ciclos.");
            return;
        }

        String startNode = originComboBox.getValue();
        if (startNode == null || startNode.trim().isEmpty()) {
            pathResultArea.setText("❌ Por favor selecciona un nodo de origen para buscar el ciclo.");
            return;
        }

        System.out.println("🔄 Buscando un ciclo desde el nodo: " + startNode + "...");
        List<String> cyclePath = Bfs.findShortestCycle(adjList, startNode);

        if (cyclePath != null && !cyclePath.isEmpty()) {
            String cycleString = String.join(" → ", cyclePath);
            
            // Calculate total distance of the cycle
            double totalDistance = 0.0;
            for (int i = 0; i < cyclePath.size() - 1; i++) {
                String u = cyclePath.get(i);
                String v = cyclePath.get(i + 1);
                for (Node neighbor : adjList.get(u)) {
                    if (neighbor.getValue().equals(v)) {
                        totalDistance += neighbor.getWeight();
                        break;
                    }
                }
            }

            pathResultArea.setText("✅ Ciclo encontrado desde " + startNode + ":\n" + cycleString);
            // The number of unique nodes/edges in a simple cycle is path.size() - 1
            statsLabel.setText("Nodos: " + (cyclePath.size() - 1) + " | Aristas: " + (cyclePath.size() - 1));
            distanceLabel.setText(String.format("%.2f km", totalDistance));

            if (graphView != null) {
                graphView.highlightPath(cyclePath);
            }
        } else {
            pathResultArea.setText("✅ No se encontraron ciclos que involucren al nodo " + startNode + ".");
        }
    }

    private void handleEccentricityCalculation() {
        if (adjList == null || adjList.isEmpty()) {
            pathResultArea.setText("❌ No hay grafo cargado para calcular la excentricidad.");
            return;
        }

        String startNode = originComboBox.getValue();
        if (startNode == null || startNode.trim().isEmpty()) {
            pathResultArea.setText("❌ Por favor selecciona un nodo de origen.");
            return;
        }

        System.out.println("🔄 Calculando excentricidad para el nodo: " + startNode + "...");
        Eccentricity.EccentricityResult result = Eccentricity.calculate(startNode, adjList);

        if (result != null && result.farthestNode != null) {
            // Display results
            distanceLabel.setText(String.format("%.2f km", result.eccentricity));
            if (result.path != null) {
                statsLabel.setText("Nodos: " + result.path.size() + " | Aristas: " + (result.path.size() - 1));
            } else {
                statsLabel.setText("Excentricidad");
            }
            pathResultArea.setText("El nodo más lejano desde " + startNode + " es " + result.farthestNode + ".");

            // Highlight the longest shortest path
            if (graphView != null && result.path != null) {
                graphView.highlightPath(result.path);
            }
            System.out.println("✅ Excentricidad de " + startNode + " es " + result.eccentricity + " (hacia " + result.farthestNode + ")");
        } else {
            pathResultArea.setText("❌ No se pudo calcular la excentricidad para " + startNode + ". El nodo podría estar aislado.");
        }
    }

    /**
     * Display path result in text area
     */
    private void displayPathResult(PathResult result, String origin, String destination, boolean fromCache) {
        if (pathResultArea == null) {
            return;
        }

        // Update the main stats labels directly
        distanceLabel.setText(String.format("%.2f km", result.distance));
        statsLabel.setText("Nodos: " + result.path.size() + " | Aristas: " + (result.path.size() - 1));

        // Build the continuous path string in the format A -> B -> C
        String detailedPath = String.join("  →  ", result.path);
        pathResultArea.setText(detailedPath);

        // Ensure the text area starts scrolled to the beginning
        pathResultArea.positionCaret(0);
        System.out.println("✅ Ruta mostrada: " + detailedPath);
    }

    /**
     * Handles changes in the calculation type radio buttons.
     * Shows or hides the destination input fields based on the selection.
     */
    @FXML
    private void handleCalculationTypeChange() {
        RadioButton selectedRadio = (RadioButton) calculationTypeGroup.getSelectedToggle();
        if (selectedRadio == null) {
            return;
        }

        boolean needsDestination = selectedRadio.equals(shortestPathRadio);
        destinationContainer.setVisible(needsDestination);
        destinationContainer.setManaged(needsDestination);

        // Update the main button text
        calculateButton.setText("Calcular " + selectedRadio.getText().split(" ")[0]);

        System.out.println("🔄 Tipo de cálculo cambiado a: " + selectedRadio.getText());
    }

    /**
     * Clear results and selections
     */
    @FXML
    private void clearResults() {
        if (pathResultArea != null) {
            pathResultArea.setText("Selecciona origen y destino para calcular la ruta más corta.");
        }

        // Clear ComboBox selections
        if (originComboBox != null) {
            originComboBox.getSelectionModel().clearSelection();
        }
        if (destinationComboBox != null) {
            destinationComboBox.getSelectionModel().clearSelection();
        }

        // Clear filters
        clearFilters();

        // Clear GraphView highlighting
        if (graphView != null) {
            graphView.highlightPath(new ArrayList<>());
        }

        System.out.println("🧹 Resultados y selecciones limpiadas");
    }

    /**
     * Clear filter fields and reset ComboBoxes
     */
    private void clearFilters() {
        // Clear filter text fields
        if (originFilterField != null) {
            originFilterField.clear();
        }
        if (destinationFilterField != null) {
            destinationFilterField.clear();
        }

        // Reset ComboBox selections
        if (originComboBox != null) {
            originComboBox.setValue(null);
        }
        if (destinationComboBox != null) {
            destinationComboBox.setValue(null);
        }

        // Reset filter predicates to show all items
        if (originFilteredList != null) {
            originFilteredList.setPredicate(item -> true);
        }
        if (destinationFilteredList != null) {
            destinationFilteredList.setPredicate(item -> true);
        }

        // Update prompt texts
        updateComboBoxPrompt(originComboBox, "Selecciona origen...");
        updateComboBoxPrompt(destinationComboBox, "Selecciona destino...");
    }

    /**
     * Filter origin ComboBox based on text input
     */
    @FXML
    private void filterOriginComboBox() {
        if (originFilteredList == null) {
            return;
        }

        String filterText = originFilterField != null ? originFilterField.getText() : "";
        if (filterText == null || filterText.trim().isEmpty()) {
            originFilteredList.setPredicate(item -> true);
        } else {
            String lowerCaseFilter = filterText.toLowerCase();
            originFilteredList.setPredicate(item
                    -> item.toLowerCase().contains(lowerCaseFilter));
        }

        updateComboBoxPrompt(originComboBox, "Selecciona origen...");
    }

    /**
     * Filter destination ComboBox based on text input
     */
    @FXML
    private void filterDestinationComboBox() {
        if (destinationFilteredList == null) {
            return;
        }

        String filterText = destinationFilterField != null ? destinationFilterField.getText() : "";
        if (filterText == null || filterText.trim().isEmpty()) {
            destinationFilteredList.setPredicate(item -> true);
        } else {
            String lowerCaseFilter = filterText.toLowerCase();
            destinationFilteredList.setPredicate(item
                    -> item.toLowerCase().contains(lowerCaseFilter));
        }

        updateComboBoxPrompt(destinationComboBox, "Selecciona destino...");
    }

    /**
     * Update ComboBox prompt text based on filter results
     */
    private void updateComboBoxPrompt(ComboBox<String> comboBox, String defaultPrompt) {
        if (comboBox != null) {
            if (comboBox.getItems().isEmpty()) {
                comboBox.setPromptText("Sin resultados...");
            } else {
                comboBox.setPromptText(defaultPrompt);
            }
        }
    }

    /**
     * Toggle edge labels visibility
     */
    @FXML
    private void toggleEdgeLabels() {
        showEdgeLabels = !showEdgeLabels;

        if (graphView != null) {
            graphView.setShowEdgeWeights(showEdgeLabels);
        }

        if (toggleLabelsButton != null) {
            toggleLabelsButton.setText(showEdgeLabels ? "Ocultar Etiquetas" : "Mostrar Etiquetas");
        }
        System.out.println("🏷️ Etiquetas de aristas: " + (showEdgeLabels ? "Mostradas" : "Ocultas"));
    }

    /**
     * Update database and cache status labels
     */
    private void updateDatabaseStatus() {
        if (databaseStatusLabel != null) {
            if (useDatabase && databaseConnection.isConnected()) {
                databaseStatusLabel.setText("🟢 BD: Conectada");
                databaseStatusLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
            } else {
                databaseStatusLabel.setText("🔴 BD: Desconectada");
                databaseStatusLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            }
        }

        if (cacheStatusLabel != null) {
            if (useDatabase) {
                cacheStatusLabel.setText("💾 Cache: Disponible");
                cacheStatusLabel.setStyle("-fx-text-fill: blue; -fx-font-weight: bold;");
            } else {
                cacheStatusLabel.setText("💾 Cache: No disponible");
                cacheStatusLabel.setStyle("-fx-text-fill: gray; -fx-font-weight: bold;");
            }
        }
    }

    // Layout control methods
    @FXML
    private void setGeographicLayout() {
        if (graphView != null) {
            graphView.setLayoutType(GraphView.LayoutType.GEOGRAPHIC);
            updateLayoutButtonStyles(GraphView.LayoutType.GEOGRAPHIC);
            System.out.println("🗺️ Layout cambiado a: Geográfico");
        }
    }

    @FXML
    private void setForceLayout() {
        if (graphView != null) {
            graphView.setLayoutType(GraphView.LayoutType.FORCE_DIRECTED);
            updateLayoutButtonStyles(GraphView.LayoutType.FORCE_DIRECTED);
            System.out.println("🌀 Layout cambiado a: Por Fuerzas");
        }
    }

    /**
     * Update layout button styles to highlight active layout
     */
    private void updateLayoutButtonStyles(GraphView.LayoutType activeLayout) {
        if (geographicLayoutButton == null) {
            return;
        }

        // Reset all button styles
        geographicLayoutButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");
        forceLayoutButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");

        // Highlight active layout
        switch (activeLayout) {
            case GEOGRAPHIC:
                geographicLayoutButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold; -fx-border-color: #ffffff; -fx-border-width: 2px;");
                break;
            case FORCE_DIRECTED:
                forceLayoutButton.setStyle("-fx-background-color: #ec7063; -fx-text-fill: white; -fx-font-weight: bold; -fx-border-color: #ffffff; -fx-border-width: 2px;");
                break;
        }
    }

    /**
     * Show route history dialog
     */
    @FXML
    private void showRouteHistory() {
        if (!useDatabase) {
            if (pathResultArea != null) {
                pathResultArea.setText("❌ El historial de rutas solo está disponible cuando se usa la base de datos.");
            }
            return;
        }

        try {
            // Get route history from database (last 10 routes)
            List<String> history = rutaDAO.obtenerHistorialRutas(10);

            if (history.isEmpty()) {
                if (pathResultArea != null) {
                    pathResultArea.setText("📋 No hay rutas en el historial.");
                }
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("📋 Historial de Rutas Calculadas (últimas 10):\n\n");

                for (String route : history) {
                    sb.append("📍 ").append(route).append("\n");
                    sb.append("---\n");
                }

                if (pathResultArea != null) {
                    pathResultArea.setText(sb.toString());
                }
            }

            System.out.println("📋 Historial de rutas mostrado: " + history.size() + " rutas");

        } catch (Exception e) {
            if (pathResultArea != null) {
                pathResultArea.setText("❌ Error al obtener historial de rutas: " + e.getMessage());
            }
            System.err.println("❌ Error al obtener historial: " + e.getMessage());
        }
    }

    /**
     * Clear route cache
     */
    @FXML
    private void clearRouteCache() {
        if (!useDatabase) {
            if (pathResultArea != null) {
                pathResultArea.setText("❌ El cache de rutas solo está disponible cuando se usa la base de datos.");
            }
            return;
        }

        try {
            // Clear old routes (older than 7 days)
            int deletedCount = rutaDAO.limpiarRutasAntiguas(7);

            if (pathResultArea != null) {
                pathResultArea.setText("✅ Cache de rutas limpiado exitosamente. Se eliminaron " + deletedCount + " rutas antiguas.");
            }
            System.out.println("✅ Cache de rutas limpiado: " + deletedCount + " rutas eliminadas");

            // Update cache status
            updateDatabaseStatus();

        } catch (Exception e) {
            if (pathResultArea != null) {
                pathResultArea.setText("❌ Error al limpiar cache: " + e.getMessage());
            }
            System.err.println("❌ Error al limpiar cache: " + e.getMessage());
        }
    }

    // Zoom control methods
    @FXML
    private void zoomIn() {
        if (graphView != null) {
            graphView.zoomIn();
            System.out.println("🔍 Zoom in aplicado");
        }
    }

    @FXML
    private void zoomOut() {
        if (graphView != null) {
            graphView.zoomOut();
            System.out.println("🔍 Zoom out aplicado");
        }
    }

    @FXML
    private void resetZoom() {
        if (graphView != null) {
            graphView.resetView();
            System.out.println("🔄 Zoom reseteado");
        }
    }

    // Setter for GraphView (to be called from FXML or main application)
    public void setGraphView(GraphView graphView) {
        this.graphView = graphView;
        System.out.println("✅ GraphView establecido en MainController");
    }

    // Getters for testing and external access
    public Graph getCurrentGraph() {
        return currentGraph;
    }

    public Map<String, List<Node>> getAdjList() {
        return adjList;
    }

    public boolean isUsingDatabase() {
        return useDatabase;
    }

    public DatabaseConnection getDatabaseConnection() {
        return databaseConnection;
    }
}
