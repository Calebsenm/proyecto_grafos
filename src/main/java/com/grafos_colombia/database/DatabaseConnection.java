package com.grafos_colombia.database;

import java.sql.*;

public class DatabaseConnection {

    private static DatabaseConnection instance;
    private Connection connection;
    private boolean isConnected = false;
    private static final String DB_URL = "jdbc:sqlite:grafos_colombia.db";

    private DatabaseConnection() {
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null)
            instance = new DatabaseConnection();
        return instance;
    }

    public boolean connect() {
        try {
            if (isConnected && connection != null && !connection.isClosed()) {
                return true;
            }

            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(DB_URL);

            if (!isConnected) {
                System.out.println("✅ Conectado a SQLite");
            }

            isConnected = true;
            return true;

        } catch (Exception e) {
            System.err.println("❌ Error al conectar a la base de datos: " + e.getMessage());
            return false;
        }
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed() || !connection.isValid(5)) {
            connect();
        }
        return connection;
    }

    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                isConnected = false;
                System.out.println("🔌 Conexión cerrada.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al cerrar conexión: " + e.getMessage());
        }
    }

    public String getDbUrl() {
        return DB_URL;
    }

    public boolean tieneDatos() {
        if (!isConnected()) {
            return false;
        }
        String sql = "SELECT " +
                "(SELECT COUNT(*) FROM nodo) as nodos_count, " +
                "(SELECT COUNT(*) FROM arista) as aristas_count";

        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                int nodosCount = rs.getInt("nodos_count");
                int aristasCount = rs.getInt("aristas_count");
                return nodosCount > 0 && aristasCount > 0;
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al verificar si la base de datos tiene datos: " + e.getMessage());
        }
        return false;
    }

    public boolean isConnected() {
        try {
            return isConnected && connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }



}
