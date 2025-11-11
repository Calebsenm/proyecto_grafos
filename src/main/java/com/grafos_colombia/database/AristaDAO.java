package com.grafos_colombia.database;

import com.grafos_colombia.graph.Edge;
import java.sql.*;
import java.util.*;

public class AristaDAO {
    private DatabaseConnection dbConnection;

    public AristaDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    public List<Edge> obtenerTodasLasAristas() {
        List<Edge> aristas = new ArrayList<>();
        Set<String> aristasVistas = new HashSet<>(); // Para evitar duplicados
        String sql = "SELECT a.distancia, n1.nombre AS origen_nombre, n2.nombre AS destino_nombre " +
                     "FROM arista a " +
                     "JOIN nodo n1 ON a.origen_id = n1.id " +
                     "JOIN nodo n2 ON a.destino_id = n2.id " +
                     "ORDER BY n1.nombre, n2.nombre";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String origen = rs.getString("origen_nombre");
                String destino = rs.getString("destino_nombre");
                double distancia = rs.getDouble("distancia");
                
                // Normalizar: crear una clave única para la arista sin importar la dirección
                String claveArista;
                if (origen.compareTo(destino) <= 0) {
                    claveArista = origen + "|" + destino;
                } else {
                    claveArista = destino + "|" + origen;
                }
                
                // Solo agregar si no hemos visto esta arista antes
                if (!aristasVistas.contains(claveArista)) {
                    // Siempre crear la arista con el nodo menor como origen para consistencia
                    if (origen.compareTo(destino) <= 0) {
                        aristas.add(new Edge(origen, destino, distancia));
                    } else {
                        aristas.add(new Edge(destino, origen, distancia));
                    }
                    aristasVistas.add(claveArista);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener aristas: " + e.getMessage());
        }

        return aristas;
    }

    public List<Edge> obtenerAristasEntreNodos(String nodoOrigen, String nodoDestino) {
        List<Edge> aristas = new ArrayList<>();
        String sql = "SELECT a.distancia, n1.nombre AS origen_nombre, n2.nombre AS destino_nombre " +
                     "FROM arista a " +
                     "JOIN nodo n1 ON a.origen_id = n1.id " +
                     "JOIN nodo n2 ON a.destino_id = n2.id " +
                     "WHERE n1.nombre = ? AND n2.nombre = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nodoOrigen);
            stmt.setString(2, nodoDestino);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Edge arista = new Edge(
                        rs.getString("origen_nombre"),
                        rs.getString("destino_nombre"),
                        rs.getDouble("distancia")
                    );
                    aristas.add(arista);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener aristas entre nodos: " + e.getMessage());
        }

        return aristas;
    }

    public boolean insertarArista(String nodoOrigen, String nodoDestino, double distancia) {
        // Primero: obtener IDs
        NodoDAO nodoDAO = new NodoDAO();
        Integer origenId = nodoDAO.obtenerIdNodo(nodoOrigen);
        Integer destinoId = nodoDAO.obtenerIdNodo(nodoDestino);

        if (origenId == null || destinoId == null) {
            System.err.println("Uno o ambos nodos no existen: " + nodoOrigen + " -> " + nodoDestino);
            return false;
        }

        String sql = "INSERT INTO arista (origen_id, destino_id, distancia) VALUES (?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, origenId);
            stmt.setInt(2, destinoId);
            stmt.setDouble(3, distancia);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al insertar arista: " + e.getMessage());
            return false;
        }
    }

    public boolean existeArista(String nodoOrigen, String nodoDestino) {
        String sql = "SELECT COUNT(*) FROM arista a " +
                     "JOIN nodo n1 ON a.origen_id = n1.id " +
                     "JOIN nodo n2 ON a.destino_id = n2.id " +
                     "WHERE (n1.nombre = ? AND n2.nombre = ?) OR (n1.nombre = ? AND n2.nombre = ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nodoOrigen);
            stmt.setString(2, nodoDestino);
            stmt.setString(3, nodoDestino);
            stmt.setString(4, nodoOrigen);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.err.println("Error al verificar arista: " + e.getMessage());
            return false;
        }
    }

    /** Guardar arista si no existe (solo una dirección, normalizada) */
    public void guardarSiNoExiste(String nodo1, String nodo2, double distancia) {
        // Normalizar: siempre guardar con el nodo lexicográficamente menor como origen
        // Esto evita duplicados cuando la misma arista viene en direcciones opuestas
        String origen, destino;
        if (nodo1.compareTo(nodo2) <= 0) {
            origen = nodo1;
            destino = nodo2;
        } else {
            origen = nodo2;
            destino = nodo1;
        }
        
        // Verificar si ya existe en cualquier dirección y guardar solo si no existe
        if (!existeArista(origen, destino)) {
            insertarArista(origen, destino, distancia);
        }
    }

    /**
     * Limpia aristas duplicadas de la base de datos, dejando solo una dirección
     * (normalizada con el nodo menor como origen).
     * Elimina aristas donde el nombre del origen es mayor que el destino (lexicográficamente).
     */
    public int limpiarDuplicados() {
        int eliminadas = 0;
        // Eliminar aristas donde existe una arista inversa y el origen > destino
        // Esto mantiene solo las aristas normalizadas (origen < destino)
        String sql = "DELETE FROM arista WHERE id IN (" +
                     "  SELECT a1.id FROM arista a1 " +
                     "  JOIN nodo n1 ON a1.origen_id = n1.id " +
                     "  JOIN nodo n2 ON a1.destino_id = n2.id " +
                     "  WHERE EXISTS (" +
                     "    SELECT 1 FROM arista a2 " +
                     "    JOIN nodo n3 ON a2.origen_id = n3.id " +
                     "    JOIN nodo n4 ON a2.destino_id = n4.id " +
                     "    WHERE a2.origen_id = a1.destino_id " +
                     "    AND a2.destino_id = a1.origen_id" +
                     "  ) " +
                     "  AND n1.nombre > n2.nombre" +
                     ")";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            eliminadas = stmt.executeUpdate();
            if (eliminadas > 0) {
                System.out.println("Se eliminaron " + eliminadas + " aristas duplicadas");
            }
            
        } catch (SQLException e) {
            System.err.println("Error al limpiar duplicados: " + e.getMessage());
        }
        
        return eliminadas;
    }
}