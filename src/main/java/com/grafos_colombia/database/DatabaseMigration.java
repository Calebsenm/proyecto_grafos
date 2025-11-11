package com.grafos_colombia.database;

import java.sql.Connection;
import java.sql.Statement;

public class DatabaseMigration {

    public static void init() {
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
            System.out.println("✅ Tablas creadas correctamente");

        } catch (Exception e) {
            System.err.println("❌ Error creando tablas: " + e.getMessage());
        }
    }
}
