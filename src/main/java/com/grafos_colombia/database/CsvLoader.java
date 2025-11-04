package com.grafos_colombia.database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

/**
 * Cargador simple de datos CSV a la base de datos
 */
public class CsvLoader {
    
    private final DatabaseConnection db;
    
    public CsvLoader() {
        this.db = DatabaseConnection.getInstance();
    }
    
    /**
     * Carga datos del CSV a la base de datos
     */
    public boolean cargarDatosDesdeCSV() {
        System.out.println("Iniciando carga de datos desde CSV...");
        
        try {
            if (!db.connect()) {
                System.err.println("No se pudo conectar a la base de datos");
                return false;
            }
            
            // Limpiar tablas existentes
            limpiarTablas();
            
            // Cargar datos del CSV
            return procesarArchivoCSV();
            
        } catch (Exception e) {
            System.err.println("Error al cargar datos: " + e.getMessage());
            return false;
        }
    }
    
    private void limpiarTablas() throws SQLException {
        Connection conn = db.getConnection();
        
        // Limpiar aristas primero (por foreign key)
        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM aristas")) {
            stmt.executeUpdate();
        }
        
        // Limpiar nodos
        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM nodos")) {
            stmt.executeUpdate();
        }
        
        System.out.println("Tablas limpiadas");
    }
    
    private boolean procesarArchivoCSV() {
        try (InputStream is = getClass().getResourceAsStream("/csv/Grafos.csv");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            
            if (is == null) {
                System.err.println("No se encontró el archivo CSV");
                return false;
            }
            
            String line;
            boolean isFirstLine = true;
            Set<String> nodosInsertados = new HashSet<>();
            int aristasInsertadas = 0;
            
            while ((line = reader.readLine()) != null) {
                // Saltar la primera línea (headers)
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    String nodoUno = parts[0].trim();
                    String nodoDos = parts[1].trim();
                    String distanciaStr = parts[2].trim();
                    
                    // Saltar líneas con distancia vacía
                    if (distanciaStr.isEmpty()) {
                        continue;
                    }
                    
                    try {
                        double distancia = Double.parseDouble(distanciaStr);
                        
                        // Insertar nodos si no existen
                        insertarNodoSiNoExiste(nodoUno, nodosInsertados);
                        insertarNodoSiNoExiste(nodoDos, nodosInsertados);
                        
                        // Insertar arista
                        if (insertarArista(nodoUno, nodoDos, distancia)) {
                            aristasInsertadas++;
                        }
                        
                    } catch (NumberFormatException e) {
                        System.err.println("Distancia inválida en línea: " + line);
                    }
                }
            }
            
            System.out.println("Carga completada:");
            System.out.println("  - Nodos insertados: " + nodosInsertados.size());
            System.out.println("  - Aristas insertadas: " + aristasInsertadas);
            
            return true;
            
        } catch (IOException e) {
            System.err.println("Error al leer archivo CSV: " + e.getMessage());
            return false;
        }
    }
    
    private void insertarNodoSiNoExiste(String nombre, Set<String> nodosInsertados) {
        if (nodosInsertados.contains(nombre)) {
            return;
        }
        
        try {
            Connection conn = db.getConnection();
            String sql = "INSERT INTO nodos (nombre, activo) VALUES (?, TRUE)";
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, nombre);
                stmt.executeUpdate();
                nodosInsertados.add(nombre);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al insertar nodo " + nombre + ": " + e.getMessage());
        }
    }
    
    private boolean insertarArista(String nodoUno, String nodoDos, double distancia) {
        try {
            Connection conn = db.getConnection();
            String sql = "INSERT INTO aristas (nodo_origen, nodo_destino, peso, activo) VALUES (?, ?, ?, TRUE)";
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, nodoUno);
                stmt.setString(2, nodoDos);
                stmt.setDouble(3, distancia);
                stmt.executeUpdate();
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al insertar arista " + nodoUno + " -> " + nodoDos + ": " + e.getMessage());
            return false;
        }
    }
}