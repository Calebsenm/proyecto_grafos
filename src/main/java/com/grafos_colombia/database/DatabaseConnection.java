package com.grafos_colombia.database;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

/**
 * Clase para gestionar la conexión a la base de datos
 */
public class DatabaseConnection {
    private static final String DB_URL_BASE = "jdbc:mysql://localhost:3306/grafos_colombia";
    private static final String DB_URL_PARAMS = "?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=UTC";
    private static final String DB_URL = DB_URL_BASE + DB_URL_PARAMS;
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    private static final String CONFIG_FILE = "database.properties";
    
    private static DatabaseConnection instance;
    private Connection connection;
    private boolean isConnected = false;
    
    /**
     * Constructor privado para implementar Singleton
     */
    private DatabaseConnection() {
        loadConfiguration();
    }
    
    /**
     * Obtener instancia única de la conexión
     */
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }
    
    /**
     * Cargar configuración desde archivo properties
     */
    private void loadConfiguration() {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
            props.load(fis);
            
            // Aquí se podrían cargar configuraciones personalizadas
            // Por ahora usamos los valores por defecto
        } catch (IOException e) {
            System.out.println("No se encontró archivo de configuración, usando valores por defecto");
        }
    }
    
    /**
     * Establecer conexión a la base de datos
     */
    public boolean connect() {
        try {
            if (isConnected && connection != null && !connection.isClosed()) {
                return true;
            }
            
            // Cargar el driver de MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Intentar conexión con parámetros UTF-8
            try {
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                System.out.println("✅ Conexión a la base de datos establecida correctamente (con UTF-8)");
            } catch (SQLException e) {
                // Si falla por encoding, intentar sin parámetros específicos
                if (e.getMessage().contains("character encoding") || e.getMessage().contains("utf8")) {
                    System.out.println("⚠️ Intentando conexión con configuración alternativa...");
                    String alternativeUrl = DB_URL_BASE + "?useUnicode=true&useSSL=false";
                    connection = DriverManager.getConnection(alternativeUrl, DB_USER, DB_PASSWORD);
                    System.out.println("✅ Conexión a la base de datos establecida correctamente (configuración alternativa)");
                } else {
                    throw e; // Re-lanzar si no es un error de encoding
                }
            }
            
            isConnected = true;
            return true;
            
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Error: Driver de MySQL no encontrado");
            System.err.println("   Asegúrate de tener MySQL Connector/J en el classpath");
            return false;
        } catch (SQLException e) {
            System.err.println("❌ Error al conectar con la base de datos:");
            System.err.println("   " + e.getMessage());
            System.err.println("   Verifica que MySQL esté ejecutándose y la base de datos exista");
            return false;
        }
    }
    
    /**
     * Cerrar conexión a la base de datos
     */
    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                isConnected = false;
                System.out.println("✅ Conexión a la base de datos cerrada");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al cerrar la conexión: " + e.getMessage());
        }
    }
    
    /**
     * Obtener la conexión activa
     */
    public Connection getConnection() throws SQLException {
        if (!isConnected || connection == null || connection.isClosed()) {
            if (!connect()) {
                throw new SQLException("No se pudo establecer conexión con la base de datos");
            }
        }
        return connection;
    }
    
    /**
     * Verificar si hay conexión activa
     */
    public boolean isConnected() {
        try {
            return isConnected && connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
    
    /**
     * Ejecutar una consulta de prueba
     */
    public boolean testConnection() {
        try {
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT 1");
            rs.close();
            stmt.close();
            return true;
        } catch (SQLException e) {
            System.err.println("❌ Error en prueba de conexión: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Obtener información de la base de datos
     */
    public void printDatabaseInfo() {
        try {
            Connection conn = getConnection();
            DatabaseMetaData metaData = conn.getMetaData();
            
            System.out.println("\n📊 INFORMACIÓN DE LA BASE DE DATOS:");
            System.out.println("   • URL: " + metaData.getURL());
            System.out.println("   • Usuario: " + metaData.getUserName());
            System.out.println("   • Driver: " + metaData.getDriverName());
            System.out.println("   • Versión Driver: " + metaData.getDriverVersion());
            System.out.println("   • Versión BD: " + metaData.getDatabaseProductVersion());
            
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener información: " + e.getMessage());
        }
    }
    
    /**
     * Verificar que las tablas existan
     */
    public boolean verifyTables() {
        try {
            Connection conn = getConnection();
            DatabaseMetaData metaData = conn.getMetaData();
            
            String[] requiredTables = {"nodos", "aristas", "rutas_calculadas", "secuencia_rutas", "configuraciones"};
            
            for (String tableName : requiredTables) {
                ResultSet tables = metaData.getTables(null, null, tableName, null);
                if (!tables.next()) {
                    System.err.println("❌ Tabla faltante: " + tableName);
                    return false;
                }
                tables.close();
            }
            
            System.out.println("✅ Todas las tablas requeridas están presentes");
            return true;
            
        } catch (SQLException e) {
            System.err.println("❌ Error al verificar tablas: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Obtener estadísticas de la base de datos
     */
    public void printStatistics() {
        try {
            Connection conn = getConnection();
            
            System.out.println("\n📈 ESTADÍSTICAS DE LA BASE DE DATOS:");
            
            // Contar nodos
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as total FROM nodos WHERE activo = TRUE");
            if (rs.next()) {
                System.out.println("   • Nodos activos: " + rs.getInt("total"));
            }
            rs.close();
            
            // Contar aristas
            rs = stmt.executeQuery("SELECT COUNT(*) as total FROM aristas WHERE activo = TRUE");
            if (rs.next()) {
                System.out.println("   • Aristas activas: " + rs.getInt("total"));
            }
            rs.close();
            
            // Contar rutas calculadas
            rs = stmt.executeQuery("SELECT COUNT(*) as total FROM rutas_calculadas");
            if (rs.next()) {
                System.out.println("   • Rutas calculadas: " + rs.getInt("total"));
            }
            rs.close();
            
            stmt.close();
            
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener estadísticas: " + e.getMessage());
        }
    }
    
    /**
     * Método para limpiar recursos
     * @deprecated Use try-with-resources or explicit disconnect() instead
     */
    @Deprecated(since = "9")
    @Override
    protected void finalize() throws Throwable {
        disconnect();
        super.finalize();
    }
}
