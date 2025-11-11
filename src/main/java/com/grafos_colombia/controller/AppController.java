package com.grafos_colombia.controller;

import com.grafos_colombia.algorithm.Bfs;
import com.grafos_colombia.algorithm.Dijkstra;
import com.grafos_colombia.algorithm.Eccentricity;
import com.grafos_colombia.algorithm.GraphMetrics;
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
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.StageStyle;

/**
 * Controlador principal simplificado - Solo carga CSV->BD->Grafo
 */
public class AppController implements Initializable {

    // === FXML Components ===
    @FXML private TextField originFilterField;
    @FXML private ComboBox<String> originComboBox;
    @FXML private TextField destinationFilterField;
    @FXML private ComboBox<String> destinationComboBox;

    @FXML private ToggleGroup calculationTypeGroup;
    @FXML private RadioButton shortestPathRadio;
    @FXML private RadioButton simpleCycleRadio;
    @FXML private RadioButton eccentricityRadio;
    @FXML private RadioButton graphMetricsRadio;

    @FXML private VBox originContainer;
    @FXML private VBox destinationContainer;

    @FXML private Button forceLayoutButton;
    @FXML private Button calculateButton;
    @FXML private Button clearButton;
    @FXML private Button loadDatabaseButton;
    @FXML private Button toggleLabelsButton;
    @FXML private Button toggleRouteButton;
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
    
    // Rutas almacenadas para alternar visualización
    private PathResult currentPrimaryPath;
    private PathResult currentAlternativePath;
    private boolean showingAlternative = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        db = DatabaseConnection.getInstance();
        loader = new GraphDataLoader();

        initializeUI();
        // No cargar datos automáticamente
    }

    private void initializeUI() {
        originFilterField.setPromptText("Filtrar origen...");
        destinationFilterField.setPromptText("Filtrar destino...");
        originComboBox.setPromptText("Origen...");
        destinationComboBox.setPromptText("Destino...");

        pathResultArea.setEditable(false);
        pathResultArea.setText("Haz clic en 'Cargar CSV → BD → Grafo' para comenzar.");

        if (graphCanvas != null && graphScrollPane != null) {
            graphCanvas.widthProperty().bind(graphScrollPane.widthProperty());
            graphCanvas.heightProperty().bind(graphScrollPane.heightProperty());
        }

        updateDatabaseStatus();
    }



    @FXML
    private void loadDatabaseGraph() {
        // Conectar a la base de datos
        if (!db.connect()) {
            pathResultArea.setText("Error: No se pudo conectar a la base de datos.");
            return;
        }
        

        // Primero cargar CSV a BD si no hay datos
        if (!db.tieneDatos()) {
            boolean loaded = loader.cargarDesdeCSV();
            if (!loaded) {
                pathResultArea.setText("Error: No se pudieron cargar los datos del CSV.");
                return;
            }
        }
        
        // Cargar grafo desde BD
        Graph graph = loader.cargarGrafoCompleto();
        if (graph != null) {
            setupGraph(graph);
            pathResultArea.setText("Grafo cargado correctamente desde BD.");
        } else {
            pathResultArea.setText("Error: No se pudo cargar el grafo desde BD.");
        }
    }

    private void setupGraph(Graph graph) {
        clearResults();
        currentGraph = graph;
        adjList = graph.getAdjList();

        initializeGraphView();
        populateComboBoxes();
        updateDatabaseStatus();
    }

    private void initializeGraphView() {
        if (graphCanvas == null) return;

        graphView = new GraphView(graphCanvas);
        List<Edge> edges = extractEdges();

        var data = GraphConverter.convertNodeAdjList(adjList, edges);
        graphView.initializeGraph(data.getNodes(), data.getEdges());
        graphView.setLayoutType(GraphView.LayoutType.FORCE_DIRECTED);
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
        } else if (selected == graphMetricsRadio) {
            handleGraphMetrics();
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

        // Calcular ruta principal
        PathResult primaryResult = Dijkstra.dijkstra(o, d, adjList);
        if (primaryResult == null || primaryResult.path == null || primaryResult.path.isEmpty()) {
            pathResultArea.setText("No hay ruta entre " + o + " y " + d);
            return;
        }

        // Calcular ruta alternativa
        PathResult alternativeResult = Dijkstra.findAlternativePath(o, d, adjList, primaryResult.path);

        // Guardar las rutas para poder alternar
        currentPrimaryPath = primaryResult;
        currentAlternativePath = alternativeResult;
        showingAlternative = false;

        // Mostrar resultados
        displayResultWithAlternative(primaryResult, alternativeResult);
        
        // Mostrar/ocultar botón de alternar ruta
        if (toggleRouteButton != null) {
            if (alternativeResult != null && alternativeResult.path != null && !alternativeResult.path.isEmpty()) {
                toggleRouteButton.setVisible(true);
                toggleRouteButton.setManaged(true);
                toggleRouteButton.setText("Resaltar Ruta Alternativa");
                showingAlternative = false;
            } else {
                toggleRouteButton.setVisible(false);
                toggleRouteButton.setManaged(false);
            }
        }

        updateRouteHighlights();
    }
    
    @FXML
    private void toggleRouteView() {
        if (currentPrimaryPath == null) {
            return;
        }
        if (currentAlternativePath == null || currentAlternativePath.path == null || currentAlternativePath.path.isEmpty()) {
            return;
        }
        
        showingAlternative = !showingAlternative;

        updateRouteHighlights();

        if (toggleRouteButton != null) {
            toggleRouteButton.setText(showingAlternative ? "Resaltar Ruta Principal" : "Resaltar Ruta Alternativa");
        }
    }

    private void updateRouteHighlights() {
        if (graphView == null) {
            return;
        }

        if (currentPrimaryPath == null || currentPrimaryPath.path == null || currentPrimaryPath.path.isEmpty()) {
            graphView.clearHighlights();
            return;
        }

        List<String> primaryNodes = currentPrimaryPath.path;
        List<String> alternativeNodes = (currentAlternativePath != null && currentAlternativePath.path != null
                && !currentAlternativePath.path.isEmpty()) ? currentAlternativePath.path : null;

        graphView.highlightRoutes(primaryNodes, alternativeNodes, showingAlternative);
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

    private void handleGraphMetrics() {
        if (currentGraph == null || adjList == null || adjList.isEmpty()) {
            showAlert("Error", "El grafo no está cargado", 
                    "Por favor, carga el grafo primero haciendo clic en 'Cargar CSV → BD → Grafo'.", 
                    Alert.AlertType.ERROR);
            return;
        }

        GraphMetrics.GraphMetricsResult metricas = currentGraph.getMetricas();
        
        if (metricas == null) {
            showAlert("Error", "No se pudieron calcular las métricas", 
                    "El grafo podría no ser conexo. Asegúrate de que todos los nodos estén conectados.", 
                    Alert.AlertType.ERROR);
            return;
        }

        // Actualizar labels en la interfaz principal
        distanceLabel.setText(String.format("%.2f km", metricas.radio));
        statsLabel.setText(String.format("Radio: %.2f km | Diámetro: %.2f km", 
                metricas.radio, metricas.diametro));
        
        // Resaltar los nodos del centro en el grafo (solo nodos, sin aristas)
        // Hacer esto ANTES de mostrar el modal para que se vea inmediatamente
        if (graphView != null && !metricas.centro.isEmpty()) {
            graphView.highlightCenterNodes(metricas.centro);
        }
        
        // Mostrar el modal con las métricas
        showMetricsDialog(metricas);
        
        // Asegurar que el resaltado persista después de cerrar el modal
        // (El resaltado ya debería estar activo, pero lo reaplicamos por si acaso)
        if (graphView != null && !metricas.centro.isEmpty()) {
            graphView.highlightCenterNodes(metricas.centro);
        }
    }
    
    private void showMetricsDialog(GraphMetrics.GraphMetricsResult metricas) {
        // Crear el diálogo
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Métricas del Grafo");
        dialog.setHeaderText("Información detallada del grafo");
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initStyle(StageStyle.DECORATED);
        
        // Crear el contenido principal
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: white;");
        
        // Título
        Label titleLabel = new Label("Métricas del Grafo");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        // Panel de métricas principales
        VBox metricsPanel = new VBox(10);
        metricsPanel.setPadding(new Insets(15));
        metricsPanel.setStyle("-fx-background-color: #ecf0f1; -fx-background-radius: 5;");
        
        // Radio
        HBox radioBox = new HBox(10);
        Label radioLabel = new Label("📏 Radio:");
        radioLabel.setStyle("-fx-font-weight: bold; -fx-min-width: 120;");
        Label radioValue = new Label(String.format("%.2f km", metricas.radio));
        radioValue.setStyle("-fx-text-fill: #3498db; -fx-font-weight: bold; -fx-font-size: 14px;");
        radioBox.getChildren().addAll(radioLabel, radioValue);
        
        // Diámetro
        HBox diametroBox = new HBox(10);
        Label diametroLabel = new Label("📐 Diámetro:");
        diametroLabel.setStyle("-fx-font-weight: bold; -fx-min-width: 120;");
        Label diametroValue = new Label(String.format("%.2f km", metricas.diametro));
        diametroValue.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 14px;");
        diametroBox.getChildren().addAll(diametroLabel, diametroValue);
        
        // Centro
        HBox centroBox = new HBox(10);
        Label centroLabel = new Label("🎯 Centro:");
        centroLabel.setStyle("-fx-font-weight: bold; -fx-min-width: 120;");
        String centroText = metricas.centro.isEmpty() ? "Ninguno" : 
                String.join(", ", metricas.centro) + 
                String.format(" (%d nodo%s)", metricas.centro.size(), 
                        metricas.centro.size() != 1 ? "s" : "");
        Label centroValue = new Label(centroText);
        centroValue.setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold; -fx-font-size: 14px;");
        centroBox.getChildren().addAll(centroLabel, centroValue);
        
        metricsPanel.getChildren().addAll(radioBox, diametroBox, centroBox);

        HBox metricHighlightButtons = new HBox(10);
        metricHighlightButtons.setPadding(new Insets(5, 0, 0, 0));

        Button radioHighlightButton = new Button("Resaltar Radio");
        radioHighlightButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");
        radioHighlightButton.setOnAction(e -> {
            if (graphView == null) return;
            if (metricas.rutaRadio != null && !metricas.rutaRadio.isEmpty()) {
                graphView.highlightRadioPath(metricas.rutaRadio);
            } else if (!metricas.centro.isEmpty()) {
                // Si no tenemos ruta específica, resaltar nodos del centro
                graphView.highlightCenterNodes(metricas.centro);
            }
        });

        Button diametroHighlightButton = new Button("Resaltar Diámetro");
        diametroHighlightButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
        diametroHighlightButton.setOnAction(e -> {
            if (graphView == null) return;
            if (metricas.rutaDiametro != null && !metricas.rutaDiametro.isEmpty()) {
                graphView.highlightDiameterPath(metricas.rutaDiametro);
            }
        });

        Button centroHighlightButton = new Button("Resaltar Centro");
        centroHighlightButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;");
        centroHighlightButton.setOnAction(e -> {
            if (graphView == null) return;
            if (!metricas.centro.isEmpty()) {
                graphView.highlightCenterNodes(metricas.centro);
            }
        });

        metricHighlightButtons.getChildren().addAll(radioHighlightButton, diametroHighlightButton, centroHighlightButton);
        
        // Tabla de excentricidades
        Label tableTitle = new Label("Excentricidades de los Nodos");
        tableTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        TableView<EccentricityData> table = new TableView<>();
        table.setPrefHeight(380);
        table.setMinHeight(300);
        table.setMaxHeight(500);
        table.setPrefWidth(600);
        
        // Columna de nodo - usando Callback directamente para mayor compatibilidad
        TableColumn<EccentricityData, String> nodoColumn = new TableColumn<>("Nodo");
        nodoColumn.setCellValueFactory(cellData -> {
            EccentricityData data = cellData.getValue();
            return data != null ? data.nodoProperty() : new SimpleStringProperty("");
        });
        nodoColumn.setPrefWidth(280);
        nodoColumn.setResizable(true);
        nodoColumn.setSortable(true);
        
        // Columna de excentricidad
        TableColumn<EccentricityData, String> excentricidadColumn = new TableColumn<>("Excentricidad (km)");
        excentricidadColumn.setCellValueFactory(cellData -> {
            EccentricityData data = cellData.getValue();
            return data != null ? data.excentricidadProperty() : new SimpleStringProperty("");
        });
        excentricidadColumn.setPrefWidth(180);
        excentricidadColumn.setResizable(true);
        excentricidadColumn.setSortable(true);
        
        // Columna de centro
        TableColumn<EccentricityData, String> centroColumn = new TableColumn<>("Es Centro");
        centroColumn.setCellValueFactory(cellData -> {
            EccentricityData data = cellData.getValue();
            return data != null ? data.esCentroProperty() : new SimpleStringProperty("");
        });
        centroColumn.setPrefWidth(120);
        centroColumn.setResizable(true);
        centroColumn.setSortable(false);
        
        table.getColumns().add(nodoColumn);
        table.getColumns().add(excentricidadColumn);
        table.getColumns().add(centroColumn);
        table.setPlaceholder(new Label("No hay datos de excentricidad disponibles"));
        
        // Preparar datos para la tabla
        ObservableList<EccentricityData> tableData = FXCollections.observableArrayList();
        List<Map.Entry<String, Double>> excentricidadesOrdenadas = new ArrayList<>(
                metricas.excentricidades.entrySet());
        excentricidadesOrdenadas.sort((a, b) -> {
            double excA = a.getValue();
            double excB = b.getValue();
            // Manejar infinitos
            if (Double.isInfinite(excA) && Double.isInfinite(excB)) return 0;
            if (Double.isInfinite(excA)) return 1;
            if (Double.isInfinite(excB)) return -1;
            return Double.compare(excA, excB);
        });
        
        int validCount = 0;
        for (Map.Entry<String, Double> entry : excentricidadesOrdenadas) {
            double exc = entry.getValue();
            if (exc != Double.POSITIVE_INFINITY && 
                exc != Double.NEGATIVE_INFINITY && 
                !Double.isNaN(exc)) {
                boolean esCentro = metricas.centro.contains(entry.getKey());
                EccentricityData data = new EccentricityData(entry.getKey(), exc, esCentro);
                tableData.add(data);
                validCount++;
            }
        }
        
        // Verificar que tenemos datos antes de asignarlos a la tabla
        System.out.println("Datos preparados para la tabla: " + validCount + " nodos válidos");
        System.out.println("Total de datos en tableData: " + tableData.size());
        
        table.setItems(tableData);
        
        // Forzar actualización de la tabla
        if (tableData.isEmpty()) {
            System.out.println("ADVERTENCIA: No hay datos válidos para mostrar en la tabla");
        } else {
            table.refresh();
        }

        TextArea detailArea = new TextArea();
        detailArea.setEditable(false);
        detailArea.setWrapText(true);
        detailArea.setPrefHeight(140);
        detailArea.setStyle("-fx-font-family: 'Consolas'; -fx-font-size: 13px;");
        detailArea.setPromptText("Selecciona un nodo en la tabla para ver el recorrido de su excentricidad.");

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue == null) {
                detailArea.clear();
                if (graphView != null && metricas.centro != null && !metricas.centro.isEmpty()) {
                    graphView.highlightCenterNodes(metricas.centro);
                }
                return;
            }

            String nodo = newValue.getNodo();
            Eccentricity.EccentricityResult detalle = metricas.detallesExcentricidad.get(nodo);
            if (detalle == null) {
                detailArea.setText("No hay información disponible para el nodo " + nodo);
                return;
            }

            String destino = detalle.farthestNode != null ? detalle.farthestNode : "No disponible";
            double distancia = detalle.eccentricity;
            List<String> ruta = detalle.path;
            String rutaTexto = (ruta != null && !ruta.isEmpty())
                    ? String.join(" → ", ruta)
                    : "Sin ruta disponible";

            detailArea.setText(String.format(
                    "Nodo origen: %s%nNodo más lejano: %s%nExcentricidad: %.2f km%n%nRuta vinculada:%n%s",
                    nodo, destino, distancia, rutaTexto));

            if (graphView != null && ruta != null && !ruta.isEmpty()) {
                graphView.highlightRadioPath(ruta);
            }
        });
        
        // Aplicar estilo a las filas del centro
        table.setRowFactory(tv -> {
            TableRow<EccentricityData> row = new TableRow<EccentricityData>() {
                @Override
                protected void updateItem(EccentricityData item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setStyle("");
                    } else if (item.isCentro()) {
                        setStyle("-fx-background-color: #d5f4e6; -fx-font-weight: bold;");
                    } else {
                        setStyle("");
                    }
                }
            };
            return row;
        });
        
        // Configurar estilo de la tabla
        table.setStyle("-fx-font-size: 12px;");
        table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        
        // Agregar todo al contenido
        Label detailLabel = new Label("Detalle de la excentricidad");
        detailLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        content.getChildren().addAll(titleLabel, metricsPanel, metricHighlightButtons, tableTitle, table, detailLabel, detailArea);
        
        // Configurar el diálogo
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.getDialogPane().setPrefSize(680, 650);
        dialog.setResizable(true);
        
        // Estilo del diálogo
        dialog.getDialogPane().setStyle("-fx-background-color: white;");
        
        // Configurar el botón de cerrar
        Button closeButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.CLOSE);
        if (closeButton != null) {
            closeButton.setText("Cerrar");
            closeButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16 8 16;");
        }
        
        // Verificar que la tabla tenga datos antes de mostrar
        System.out.println("Estado final de la tabla - Items: " + table.getItems().size());
        System.out.println("Columnas en la tabla: " + table.getColumns().size());
        
        // Forzar actualización visual de la tabla
        Platform.runLater(() -> {
            table.refresh();
            if (table.getItems().isEmpty()) {
                System.out.println("ADVERTENCIA: La tabla está vacía después de configurarla");
            } else {
                System.out.println("Tabla configurada correctamente con " + table.getItems().size() + " elementos");
            }
        });
        
        // Mostrar el diálogo
        dialog.showAndWait();
    }
    
    private void showAlert(String title, String header, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void displayResult(PathResult r) {
        distanceLabel.setText(String.format("%.1f km", r.distance));
        statsLabel.setText("Nodos: " + r.path.size() + " | Aristas: " + (r.path.size() - 1));
        pathResultArea.setText(String.join(" → ", r.path));
    }

    private void displayResultWithAlternative(PathResult primary, PathResult alternative) {
        // Mostrar distancia de la ruta principal
        distanceLabel.setText(String.format("%.1f km", primary.distance));
        
        // Construir texto con ambas rutas
        StringBuilder resultText = new StringBuilder();
        resultText.append("═══ RUTA PRINCIPAL ═══\n");
        resultText.append(String.format("Distancia: %.2f km\n", primary.distance));
        resultText.append(String.format("Nodos: %d | Aristas: %d\n", primary.path.size(), primary.path.size() - 1));
        resultText.append("Ruta: ").append(String.join(" → ", primary.path));
        
        if (alternative != null && alternative.path != null && !alternative.path.isEmpty()) {
            resultText.append("\n\n");
            resultText.append("═══ RUTA ALTERNATIVA ═══\n");
            resultText.append(String.format("Distancia: %.2f km\n", alternative.distance));
            resultText.append(String.format("Nodos: %d | Aristas: %d\n", alternative.path.size(), alternative.path.size() - 1));
            resultText.append("Ruta: ").append(String.join(" → ", alternative.path));
            
            // Actualizar estadísticas para mostrar información de ambas rutas
            statsLabel.setText(String.format("Principal: %.1f km | Alternativa: %.1f km", 
                    primary.distance, alternative.distance));
        } else {
            resultText.append("\n\n");
            resultText.append("═══ RUTA ALTERNATIVA ═══\n");
            resultText.append("No se encontró una ruta alternativa.");
            statsLabel.setText(String.format("Nodos: %d | Aristas: %d", 
                    primary.path.size(), primary.path.size() - 1));
        }
        
        pathResultArea.setText(resultText.toString());
    }

    @FXML private void handleCalculationTypeChange() {
        RadioButton sel = (RadioButton) calculationTypeGroup.getSelectedToggle();
        boolean showDest = sel == shortestPathRadio;
        boolean showOrigin = sel != graphMetricsRadio;
        
        destinationContainer.setVisible(showDest);
        destinationContainer.setManaged(showDest);
        originContainer.setVisible(showOrigin);
        originContainer.setManaged(showOrigin);
        
        if (sel == graphMetricsRadio) {
            calculateButton.setText("Calcular Métricas");
        } else {
            calculateButton.setText("Calcular " + sel.getText().split(" ")[0]);
        }
    }

    @FXML private void clearResults() {
        pathResultArea.setText("Selecciona nodos...");
        originComboBox.setValue(null);
        destinationComboBox.setValue(null);
        originFilterField.clear();
        destinationFilterField.clear();
        if (originFilteredList != null) originFilteredList.setPredicate(s -> true);
        if (destinationFilteredList != null) destinationFilteredList.setPredicate(s -> true);
        if (graphView != null) graphView.clearHighlights();
        
        // Limpiar rutas almacenadas
        currentPrimaryPath = null;
        currentAlternativePath = null;
        showingAlternative = false;
        
        // Ocultar botón de alternar ruta
        if (toggleRouteButton != null) {
            toggleRouteButton.setVisible(false);
            toggleRouteButton.setManaged(false);
        }
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
            updateLayoutButtons();
        }
    }

    private void updateLayoutButtons() {
        String active = "-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;";
        forceLayoutButton.setStyle(active);
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
}