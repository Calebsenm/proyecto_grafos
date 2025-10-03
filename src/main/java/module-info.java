module com.grafos_colombia {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    
    // MySQL Connector
    requires mysql.connector.j;
    
    // Additional dependencies
    requires com.zaxxer.hikari;
    requires org.slf4j;
    requires ch.qos.logback.classic;
    requires com.fasterxml.jackson.databind;
    requires org.hibernate.validator;
    requires org.glassfish.expressly;

    opens com.grafos_colombia.controller to javafx.fxml;
    opens com.grafos_colombia.database;
    opens com.grafos_colombia.graph;
    exports com.grafos_colombia;
    exports com.grafos_colombia.graph;
    exports com.grafos_colombia.database;
}
