module com.grafos_colombia {

    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    
    // Correct module names for dependencies
    requires flyway.core;
    
    requires com.zaxxer.hikari;
    requires org.slf4j;
    requires com.fasterxml.jackson.databind;
    requires org.xerial.sqlitejdbc;
    
    // Open resource packages to required modules
    opens db.migration to flyway.core;
    
    opens com.grafos_colombia to javafx.fxml;
    opens com.grafos_colombia.controller to javafx.fxml;
    
    opens com.grafos_colombia.database;
    opens com.grafos_colombia.graph to javafx.fxml;
    
    exports com.grafos_colombia;
}