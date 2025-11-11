package com.grafos_colombia.database;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/**
 * Clase para gestionar la conexión a la base de datos
 */
public class DatabaseConnection {

    private static DatabaseConnection instance;
    private Connection connection;
    private boolean isConnected = false;

    private String dbType;
    private String dbUrl;
    private String dbUser;
    private String dbPassword;

    private static final String CONFIG_FILE = "/database.properties";

    private DatabaseConnection() {
        loadConfiguration();
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null)
            instance = new DatabaseConnection();
        return instance;
    }

    private void loadConfiguration() {
        Properties props = new Properties();

        // ✅ Usar getResourceAsStream para cargar desde classpath
        try (InputStream input = getClass().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                throw new RuntimeException("No se pudo encontrar " + CONFIG_FILE + " en el classpath. " +
                        "Asegúrate de que esté en src/main/resources/");
            }

            props.load(input);

            // El resto de tu código sigue igual...
            dbType = props.getProperty("db.type", "sqlite").trim().toLowerCase();

            if (dbType.equals("sqlite")) {
                String dbFile = props.getProperty("db.file", "grafos_colombia.db");
                dbUrl = "jdbc:sqlite:" + dbFile;
                dbUser = "";
                dbPassword = "";
            } else if (dbType.equals("mysql")) {
                String host = props.getProperty("db.host", "localhost");
                String port = props.getProperty("db.port", "3306");
                String name = props.getProperty("db.name", "grafos_colombia");
                dbUser = props.getProperty("db.user", "root");
                dbPassword = props.getProperty("db.password", "");
                dbUrl = "jdbc:mysql://" + host + ":" + port + "/" + name + "?useSSL=false&serverTimezone=UTC";
            } else {
                throw new IllegalArgumentException("Tipo de base no reconocido: " + dbType);
            }

            System.out.println("✅ Configuración cargada: " + dbType + " - " + dbUrl);

        } catch (IOException e) {
            throw new RuntimeException("No se pudo leer " + CONFIG_FILE, e);
        }
    }

    public boolean connect() {
        try {
            if (isConnected && connection != null && !connection.isClosed()) {
                return true;
            }

            if (dbType.equals("sqlite")) {
                Class.forName("org.sqlite.JDBC");
            } else {
                Class.forName("com.mysql.cj.jdbc.Driver");
            }

            connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            isConnected = true;
            System.out.println("✅ Conectado a " + dbType.toUpperCase());
            return true;

        } catch (Exception e) {
            System.err.println("❌ Error al conectar a la base de datos: " + e.getMessage());
            return false;
        }
    }

    public Connection getConnection() throws SQLException {
        if (!isConnected || connection == null || connection.isClosed())
            connect();
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

    public String getDbType() {
        return dbType;
    }

    public String getDbUrl() {
        return dbUrl;
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

            String[] requiredTables = { "nodos", "aristas", "rutas_calculadas", "secuencia_rutas", "configuraciones" };

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

    public boolean isConnected() {
        try {
            return isConnected && connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Método para limpiar recursos
     * 
     * @deprecated Use try-with-resources or explicit disconnect() instead
     */
    @Deprecated(since = "9")
    @Override
    protected void finalize() throws Throwable {
        disconnect();
        super.finalize();
    }
}
