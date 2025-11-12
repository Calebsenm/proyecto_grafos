package com.grafos_colombia.controller;

import com.grafos_colombia.algorithm.Eccentricity;
import com.grafos_colombia.algorithm.GraphMetrics;
import com.grafos_colombia.graph.GraphView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.SelectionMode;

public class MetricsDialogController {

    @FXML private DialogPane dialogPane;
    @FXML private Label radioValueLabel;
    @FXML private Label diametroValueLabel;
    @FXML private Label centroValueLabel;
    @FXML private Button radioHighlightButton;
    @FXML private Button diametroHighlightButton;
    @FXML private Button centroHighlightButton;
    @FXML private ComboBox<FilterMode> filterComboBox;
    @FXML private TextField filterField;
    @FXML private TableView<EccentricityData> excentricidadTable;
    @FXML private TableColumn<EccentricityData, String> nodoColumn;
    @FXML private TableColumn<EccentricityData, String> excentricidadColumn;
    @FXML private TableColumn<EccentricityData, String> centroColumn;
    @FXML private TextArea detailArea;

    private GraphMetrics.GraphMetricsResult metricas;
    private GraphView graphView;
    private ObservableList<EccentricityData> masterData;
    private FilteredList<EccentricityData> filteredData;

    private enum FilterMode {
        TODOS("Todos"),
        CENTRO("Centro"),
        RADIO("Radio"),
        DIAMETRO("Diámetro");

        private final String label;

        FilterMode(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    public void initialize() {
        nodoColumn.setCellValueFactory(cellData ->
                cellData.getValue() != null ? cellData.getValue().nodoProperty() : new SimpleStringProperty(""));
        excentricidadColumn.setCellValueFactory(cellData ->
                cellData.getValue() != null ? cellData.getValue().excentricidadProperty() : new SimpleStringProperty(""));
        centroColumn.setCellValueFactory(cellData ->
                cellData.getValue() != null ? cellData.getValue().esCentroProperty() : new SimpleStringProperty(""));

        excentricidadTable.setPlaceholder(new Label("No hay datos de excentricidad disponibles"));
        excentricidadTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        excentricidadTable.setRowFactory(tv -> new TableRow<>() {
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
        });
    }

    public void setData(GraphMetrics.GraphMetricsResult metricas, GraphView graphView) {
        this.metricas = metricas;
        this.graphView = graphView;

        radioValueLabel.setText(String.format("%.2f km", metricas.radio));
        diametroValueLabel.setText(String.format("%.2f km", metricas.diametro));
        String centroText = metricas.centro.isEmpty()
                ? "Ninguno"
                : String.join(", ", metricas.centro) + String.format(" (%d nodo%s)",
                metricas.centro.size(), metricas.centro.size() != 1 ? "s" : "");
        centroValueLabel.setText(centroText);

        masterData = buildTableData(metricas);
        filteredData = new FilteredList<>(masterData, s -> true);
        excentricidadTable.setItems(filteredData);
        excentricidadTable.getSelectionModel().clearSelection();

        configureHighlightButtons();
        configureSelectionListener();
        configureFilters();
        configureCloseButton();

        if (graphView != null && metricas.centro != null && !metricas.centro.isEmpty()) {
            graphView.highlightCenterNodes(metricas.centro);
        }
    }

    private ObservableList<EccentricityData> buildTableData(GraphMetrics.GraphMetricsResult metricas) {
        List<Map.Entry<String, Double>> excentricidadesOrdenadas =
                new ArrayList<>(metricas.excentricidades.entrySet());
        excentricidadesOrdenadas.sort((a, b) -> Double.compare(a.getValue(), b.getValue()));

        ObservableList<EccentricityData> tableData = FXCollections.observableArrayList();
        for (Map.Entry<String, Double> entry : excentricidadesOrdenadas) {
            double exc = entry.getValue();
            if (Double.isInfinite(exc) || Double.isNaN(exc)) {
                continue;
            }
            boolean esCentro = metricas.centro.contains(entry.getKey());
            tableData.add(new EccentricityData(entry.getKey(), exc, esCentro));
        }
        return tableData;
    }

    private void configureHighlightButtons() {
        radioHighlightButton.setOnAction(e -> {
            if (graphView == null) return;
            if (metricas.rutaRadio != null && !metricas.rutaRadio.isEmpty()) {
                graphView.highlightRadioPath(metricas.rutaRadio);
            } else if (!metricas.centro.isEmpty()) {
                graphView.highlightCenterNodes(metricas.centro);
            }
        });

        diametroHighlightButton.setOnAction(e -> {
            if (graphView == null) return;
            if (metricas.rutaDiametro != null && !metricas.rutaDiametro.isEmpty()) {
                graphView.highlightDiameterPath(metricas.rutaDiametro);
            }
        });

        centroHighlightButton.setOnAction(e -> {
            if (graphView == null) return;
            if (!metricas.centro.isEmpty()) {
                graphView.highlightCenterNodes(metricas.centro);
            }
        });
    }

    private void configureFilters() {
        if (filterComboBox != null) {
            filterComboBox.setItems(FXCollections.observableArrayList(FilterMode.values()));
            filterComboBox.getSelectionModel().select(FilterMode.TODOS);
            filterComboBox.valueProperty().addListener((obs, oldMode, newMode) -> applyFilters());
        }

        if (filterField != null) {
            filterField.textProperty().addListener((obs, oldText, newText) -> applyFilters());
        }
    }

    private void applyFilters() {
        FilterMode mode = filterComboBox != null ? filterComboBox.getValue() : FilterMode.TODOS;
        String text = filterField != null ? filterField.getText() : null;
        String lowerText = text == null ? "" : text.trim().toLowerCase();

        filteredData.setPredicate(item -> {
            if (item == null) return false;

            boolean matchesText = lowerText.isEmpty()
                    || item.getNodo().toLowerCase().contains(lowerText);

            if (!matchesText) return false;

            switch (mode) {
                case CENTRO:
                    return item.isCentro();
                case RADIO:
                    return metricas.nodosRadio != null && metricas.nodosRadio.contains(item.getNodo());
                case DIAMETRO:
                    return metricas.nodosDiametro != null && metricas.nodosDiametro.contains(item.getNodo());
                case TODOS:
                default:
                    return true;
            }
        });

        if (filteredData.isEmpty()) {
            detailArea.clear();
        }
    }

    private void configureSelectionListener() {
        excentricidadTable.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
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
            List<String> ruta = detalle.path != null ? new ArrayList<>(detalle.path) : Collections.emptyList();

            String rutaTexto = ruta.isEmpty()
                    ? "Sin ruta disponible"
                    : String.join(" → ", ruta);

            detailArea.setText(String.format(
                    "Nodo origen: %s%nNodo más lejano: %s%nExcentricidad: %.2f km%n%nRuta vinculada:%n%s",
                    nodo, destino, distancia, rutaTexto));

            if (graphView != null && !ruta.isEmpty()) {
                graphView.highlightRadioPath(ruta);
            }
        });
    }

    private void configureCloseButton() {
        if (dialogPane == null) {
            return;
        }
        Button closeButton = (Button) dialogPane.lookupButton(ButtonType.CLOSE);
        if (closeButton != null) {
            closeButton.setText("Cerrar");
            closeButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; "
                    + "-fx-font-weight: bold; -fx-padding: 8 16 8 16;");
        }
    }
}

