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
        String sql = "SELECT a.distancia, n1.nombre AS origen_nombre, n2.nombre AS destino_nombre " +
                     "FROM arista a " +
                     "JOIN nodo n1 ON a.origen_id = n1.id " +
                     "JOIN nodo n2 ON a.destino_id = n2.id " +
                     "ORDER BY n1.nombre, n2.nombre";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Edge arista = new Edge(
                    rs.getString("origen_nombre"),
                    rs.getString("destino_nombre"),
                    rs.getDouble("distancia")
                );
                aristas.add(arista);
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
                     "WHERE n1.nombre = ? AND n2.nombre = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nodoOrigen);
            stmt.setString(2, nodoDestino);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.err.println("Error al verificar arista: " + e.getMessage());
            return false;
        }
    }

    /** Guardar arista si no existe (bidireccional) */
    public void guardarSiNoExiste(String nodo1, String nodo2, double distancia) {
        // Insertar en ambas direcciones si no existen
        if (!existeArista(nodo1, nodo2)) {
            insertarArista(nodo1, nodo2, distancia);
        }
        if (!existeArista(nodo2, nodo1)) {
            insertarArista(nodo2, nodo1, distancia);
        }
    }
}