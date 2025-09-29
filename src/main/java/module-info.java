module com.grafos_colombia {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.grafos_colombia.controller to javafx.fxml;
    exports com.grafos_colombia;
}
