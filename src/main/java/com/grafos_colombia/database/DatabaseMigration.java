package com.grafos_colombia.database;

import org.flywaydb.core.Flyway;
import java.sql.Connection;
import java.sql.Statement;

public class DatabaseMigration {

    private static String dbUrl;

    public static void init() {
        try {
            // 1. Get database URL from the central connection manager
            dbUrl = DatabaseConnection.getInstance().getDbUrl();
            if (dbUrl == null || dbUrl.trim().isEmpty()) {
                throw new IllegalStateException("Database URL is not configured.");
            }

            // 2. Run Flyway migrations
            Flyway flyway = Flyway.configure()
                    .dataSource(dbUrl, null, null)
                    .locations("classpath:db/migration")  // Usar classpath para JAR
                    .baselineOnMigrate(true)
                    .load();

            flyway.migrate();

            System.out.println("✅ Migraciones aplicadas correctamente");

        } catch (Exception e) {
            System.err.println("❌ Error en migraciones: " + e.getMessage());
            // Intentar crear tablas manualmente como fallback
            createTablesManually();
        }
    }
    
    /**
     * Fallback: crear tablas manualmente si Flyway falla
     */
    private static void createTablesManually() {
        try {
            DatabaseConnection db = DatabaseConnection.getInstance();
            if (!db.connect()) {
                throw new RuntimeException("No se pudo conectar a la BD");
            }
            
            Connection conn = db.getConnection();
            Statement stmt = conn.createStatement();
            
            // Crear tabla nodo
            String createNodo = "CREATE TABLE IF NOT EXISTS nodo (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "nombre TEXT NOT NULL UNIQUE" +
                    ")";
            stmt.executeUpdate(createNodo);
            
            // Crear tabla arista
            String createArista = "CREATE TABLE IF NOT EXISTS arista (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "origen_id INTEGER NOT NULL REFERENCES nodo(id) ON DELETE CASCADE, " +
                    "destino_id INTEGER NOT NULL REFERENCES nodo(id) ON DELETE CASCADE, " +
                    "distancia REAL NOT NULL CHECK (distancia > 0), " +
                    "UNIQUE (origen_id, destino_id), " +
                    "CHECK (origen_id != destino_id)" +
                    ")";
            stmt.executeUpdate(createArista);
            
            stmt.close();
            System.out.println("✅ Tablas creadas manualmente (fallback)");
            
        } catch (Exception e) {
            System.err.println("❌ Error crítico creando tablas: " + e.getMessage());
        }
    }

    public static String getDbUrl() {
        return dbUrl;
    }
}
