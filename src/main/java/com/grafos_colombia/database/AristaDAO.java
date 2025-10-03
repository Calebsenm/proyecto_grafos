package com.grafos_colombia.database;

import com.grafos_colombia.graph.Edge;
import java.sql.*;
import java.util.*;

/**
 * Data Access Object para la gestión de aristas en la base de datos
 */
public class AristaDAO {
    private DatabaseConnection dbConnection;
    
    public AristaDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    /**
     * Obtener todas las aristas activas
     */
    public List<Edge> obtenerTodasLasAristas() {
        List<Edge> aristas = new ArrayList<>();
        String sql = "SELECT a.*, n1.nombre as origen_nombre, n2.nombre as destino_nombre " +
                    "FROM aristas a " +
                    "JOIN nodos n1 ON a.nodo_origen_id = n1.id " +
                    "JOIN nodos n2 ON a.nodo_destino_id = n2.id " +
                    "WHERE a.activo = TRUE AND n1.activo = TRUE AND n2.activo = TRUE " +
                    "ORDER BY n1.nombre, n2.nombre";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = dbConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Edge arista = new Edge(
                    rs.getString("origen_nombre"),
                    rs.getString("destino_nombre"),
                    rs.getDouble("distancia")
                );
                aristas.add(arista);
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener aristas: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("❌ Error cerrando recursos: " + e.getMessage());
            }
        }
        
        return aristas;
    }
    
    /**
     * Obtener aristas desde un nodo específico
     */
    public List<Edge> obtenerAristasDesdeNodo(String nombreNodo) {
        String sql = "SELECT a.*, n1.nombre as origen_nombre, n2.nombre as destino_nombre " +
                    "FROM aristas a " +
                    "JOIN nodos n1 ON a.nodo_origen_id = n1.id " +
                    "JOIN nodos n2 ON a.nodo_destino_id = n2.id " +
                    "WHERE n1.nombre = ? AND a.activo = TRUE AND n1.activo = TRUE AND n2.activo = TRUE " +
                    "ORDER BY n2.nombre";
        
        List<Edge> aristas = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = dbConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, nombreNodo);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Edge arista = new Edge(
                    rs.getString("origen_nombre"),
                    rs.getString("destino_nombre"),
                    rs.getDouble("distancia")
                );
                aristas.add(arista);
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener aristas desde nodo: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("❌ Error cerrando recursos: " + e.getMessage());
            }
        }
        
        return aristas;
    }
    
    /**
     * Obtener aristas hacia un nodo específico
     */
    public List<Edge> obtenerAristasHaciaNodo(String nombreNodo) {
        String sql = "SELECT a.*, n1.nombre as origen_nombre, n2.nombre as destino_nombre " +
                    "FROM aristas a " +
                    "JOIN nodos n1 ON a.nodo_origen_id = n1.id " +
                    "JOIN nodos n2 ON a.nodo_destino_id = n2.id " +
                    "WHERE n2.nombre = ? AND a.activo = TRUE AND n1.activo = TRUE AND n2.activo = TRUE " +
                    "ORDER BY n1.nombre";
        
        List<Edge> aristas = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = dbConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, nombreNodo);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Edge arista = new Edge(
                    rs.getString("origen_nombre"),
                    rs.getString("destino_nombre"),
                    rs.getDouble("distancia")
                );
                aristas.add(arista);
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener aristas hacia nodo: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("❌ Error cerrando recursos: " + e.getMessage());
            }
        }
        
        return aristas;
    }
    
    /**
     * Obtener aristas entre dos nodos específicos
     */
    public List<Edge> obtenerAristasEntreNodos(String nodoOrigen, String nodoDestino) {
        String sql = "SELECT a.*, n1.nombre as origen_nombre, n2.nombre as destino_nombre " +
                    "FROM aristas a " +
                    "JOIN nodos n1 ON a.nodo_origen_id = n1.id " +
                    "JOIN nodos n2 ON a.nodo_destino_id = n2.id " +
                    "WHERE n1.nombre = ? AND n2.nombre = ? AND a.activo = TRUE AND n1.activo = TRUE AND n2.activo = TRUE";
        
        List<Edge> aristas = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = dbConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, nodoOrigen);
            stmt.setString(2, nodoDestino);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Edge arista = new Edge(
                    rs.getString("origen_nombre"),
                    rs.getString("destino_nombre"),
                    rs.getDouble("distancia")
                );
                aristas.add(arista);
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener aristas entre nodos: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("❌ Error cerrando recursos: " + e.getMessage());
            }
        }
        
        return aristas;
    }
    
    /**
     * Insertar una nueva arista
     */
    public boolean insertarArista(String nodoOrigen, String nodoDestino, double distancia, 
                                  String tipoRuta, Integer tiempoEstimado, Double costo, String descripcion) {
        String sql = "INSERT INTO aristas (nodo_origen_id, nodo_destino_id, distancia, tipo_ruta, tiempo_estimado, costo, descripcion) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            // Obtener IDs de los nodos
            NodoDAO nodoDAO = new NodoDAO();
            Integer origenId = nodoDAO.obtenerIdNodo(nodoOrigen);
            Integer destinoId = nodoDAO.obtenerIdNodo(nodoDestino);
            
            if (origenId == null || destinoId == null) {
                System.err.println("❌ Uno o ambos nodos no existen: " + nodoOrigen + " -> " + nodoDestino);
                return false;
            }
            
            conn = dbConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, origenId);
            stmt.setInt(2, destinoId);
            stmt.setDouble(3, distancia);
            stmt.setString(4, tipoRuta);
            
            if (tiempoEstimado != null) {
                stmt.setInt(5, tiempoEstimado);
            } else {
                stmt.setNull(5, Types.INTEGER);
            }
            
            if (costo != null) {
                stmt.setDouble(6, costo);
            } else {
                stmt.setNull(6, Types.DECIMAL);
            }
            
            stmt.setString(7, descripcion);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("❌ Error al insertar arista: " + e.getMessage());
            return false;
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("❌ Error cerrando recursos: " + e.getMessage());
            }
        }
    }
    
    /**
     * Actualizar una arista existente
     */
    public boolean actualizarArista(String nodoOrigen, String nodoDestino, double distancia, 
                                   String tipoRuta, Integer tiempoEstimado, Double costo, String descripcion) {
        String sql = "UPDATE aristas SET distancia = ?, tipo_ruta = ?, tiempo_estimado = ?, costo = ?, descripcion = ? " +
                    "WHERE nodo_origen_id = (SELECT id FROM nodos WHERE nombre = ? AND activo = TRUE) " +
                    "AND nodo_destino_id = (SELECT id FROM nodos WHERE nombre = ? AND activo = TRUE) " +
                    "AND activo = TRUE";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = dbConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setDouble(1, distancia);
            stmt.setString(2, tipoRuta);
            
            if (tiempoEstimado != null) {
                stmt.setInt(3, tiempoEstimado);
            } else {
                stmt.setNull(3, Types.INTEGER);
            }
            
            if (costo != null) {
                stmt.setDouble(4, costo);
            } else {
                stmt.setNull(4, Types.DECIMAL);
            }
            
            stmt.setString(5, descripcion);
            stmt.setString(6, nodoOrigen);
            stmt.setString(7, nodoDestino);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar arista: " + e.getMessage());
            return false;
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("❌ Error cerrando recursos: " + e.getMessage());
            }
        }
    }
    
    /**
     * Desactivar una arista (soft delete)
     */
    public boolean desactivarArista(String nodoOrigen, String nodoDestino) {
        String sql = "UPDATE aristas SET activo = FALSE " +
                    "WHERE nodo_origen_id = (SELECT id FROM nodos WHERE nombre = ? AND activo = TRUE) " +
                    "AND nodo_destino_id = (SELECT id FROM nodos WHERE nombre = ? AND activo = TRUE)";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = dbConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, nodoOrigen);
            stmt.setString(2, nodoDestino);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("❌ Error al desactivar arista: " + e.getMessage());
            return false;
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("❌ Error cerrando recursos: " + e.getMessage());
            }
        }
    }
    
    /**
     * Verificar si existe una arista entre dos nodos
     */
    public boolean existeArista(String nodoOrigen, String nodoDestino) {
        String sql = "SELECT COUNT(*) FROM aristas a " +
                    "JOIN nodos n1 ON a.nodo_origen_id = n1.id " +
                    "JOIN nodos n2 ON a.nodo_destino_id = n2.id " +
                    "WHERE n1.nombre = ? AND n2.nombre = ? AND a.activo = TRUE";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = dbConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, nodoOrigen);
            stmt.setString(2, nodoDestino);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error al verificar existencia de arista: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("❌ Error cerrando recursos: " + e.getMessage());
            }
        }
        
        return false;
    }
    
    /**
     * Obtener estadísticas de aristas
     */
    public Map<String, Object> obtenerEstadisticas() {
        Map<String, Object> stats = new HashMap<>();
        String sql = "SELECT COUNT(*) as total, AVG(distancia) as distancia_promedio, " +
                    "MIN(distancia) as distancia_minima, MAX(distancia) as distancia_maxima, " +
                    "COUNT(DISTINCT tipo_ruta) as tipos_ruta FROM aristas WHERE activo = TRUE";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = dbConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                stats.put("total_aristas", rs.getInt("total"));
                stats.put("distancia_promedio", rs.getDouble("distancia_promedio"));
                stats.put("distancia_minima", rs.getDouble("distancia_minima"));
                stats.put("distancia_maxima", rs.getDouble("distancia_maxima"));
                stats.put("tipos_ruta", rs.getInt("tipos_ruta"));
            }
            
            // Obtener conteo por tipo de ruta
            String sqlTipos = "SELECT tipo_ruta, COUNT(*) as cantidad FROM aristas WHERE activo = TRUE GROUP BY tipo_ruta";
            PreparedStatement stmtTipos = null;
            ResultSet rsTipos = null;
            
            try {
                stmtTipos = conn.prepareStatement(sqlTipos);
                rsTipos = stmtTipos.executeQuery();
                
                Map<String, Integer> tipos = new HashMap<>();
                while (rsTipos.next()) {
                    tipos.put(rsTipos.getString("tipo_ruta"), rsTipos.getInt("cantidad"));
                }
                stats.put("conteo_por_tipo_ruta", tipos);
                
            } finally {
                if (rsTipos != null) rsTipos.close();
                if (stmtTipos != null) stmtTipos.close();
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener estadísticas de aristas: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("❌ Error cerrando recursos: " + e.getMessage());
            }
        }
        
        return stats;
    }
    
    /**
     * Obtener aristas por tipo de ruta
     */
    public List<Edge> obtenerAristasPorTipoRuta(String tipoRuta) {
        List<Edge> aristas = new ArrayList<>();
        String sql = "SELECT a.*, n1.nombre as origen_nombre, n2.nombre as destino_nombre " +
                    "FROM aristas a " +
                    "JOIN nodos n1 ON a.nodo_origen_id = n1.id " +
                    "JOIN nodos n2 ON a.nodo_destino_id = n2.id " +
                    "WHERE a.tipo_ruta = ? AND a.activo = TRUE AND n1.activo = TRUE AND n2.activo = TRUE " +
                    "ORDER BY a.distancia";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = dbConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, tipoRuta);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Edge arista = new Edge(
                    rs.getString("origen_nombre"),
                    rs.getString("destino_nombre"),
                    rs.getDouble("distancia")
                );
                aristas.add(arista);
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener aristas por tipo: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("❌ Error cerrando recursos: " + e.getMessage());
            }
        }
        
        return aristas;
    }
}
