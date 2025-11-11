module com.grafos_colombia {

    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires org.xerial.sqlitejdbc;
    
    opens com.grafos_colombia to javafx.fxml;
    opens com.grafos_colombia.controller to javafx.fxml;
    opens com.grafos_colombia.database;
    opens com.grafos_colombia.graph to javafx.fxml;
    
    exports com.grafos_colombia;
}