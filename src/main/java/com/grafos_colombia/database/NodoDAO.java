package com.grafos_colombia.database;

import com.grafos_colombia.graph.GeoNode;
import java.sql.*;
import java.util.*;

/**
 * Data Access Object para la gestión de nodos en la base de datos
 */
public class NodoDAO {
    private DatabaseConnection dbConnection;
    
    public NodoDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    /**
     * Obtener todos los nodos activos
     */
    public List<GeoNode> obtenerTodosLosNodos() {
        List<GeoNode> nodos = new ArrayList<>();
        String sql = "SELECT * FROM nodos WHERE activo = TRUE ORDER BY nombre";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = dbConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                GeoNode nodo = new GeoNode(
                    rs.getString("nombre"),
                    rs.getDouble("latitud"),
                    rs.getDouble("longitud")
                );
                nodos.add(nodo);
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener nodos: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("❌ Error cerrando recursos: " + e.getMessage());
            }
        }
        
        return nodos;
    }
    
    /**
     * Obtener todos los nodos activos ordenados por coordenadas geográficas
     */
    public List<GeoNode> obtenerTodosLosNodosOrdenadosGeograficamente() {
        List<GeoNode> nodos = new ArrayList<>();
        String sql = "SELECT * FROM nodos WHERE activo = TRUE ORDER BY latitud, longitud";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = dbConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                GeoNode nodo = new GeoNode(
                    rs.getString("nombre"),
                    rs.getDouble("latitud"),
                    rs.getDouble("longitud")
                );
                nodos.add(nodo);
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener nodos ordenados geográficamente: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("❌ Error cerrando recursos: " + e.getMessage());
            }
        }
        
        return nodos;
    }
    
    /**
     * Obtener todos los nodos activos como mapa para acceso rápido
     */
    public Map<String, GeoNode> obtenerMapaNodos() {
        Map<String, GeoNode> nodoMap = new HashMap<>();
        String sql = "SELECT * FROM nodos WHERE activo = TRUE";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = dbConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                String nombre = rs.getString("nombre");
                GeoNode nodo = new GeoNode(
                    nombre,
                    rs.getDouble("latitud"),
                    rs.getDouble("longitud")
                );
                nodoMap.put(nombre, nodo);
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener mapa de nodos: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("❌ Error cerrando recursos: " + e.getMessage());
            }
        }
        
        return nodoMap;
    }
    
    /**
     * Obtener un nodo por nombre
     */
    public GeoNode obtenerNodoPorNombre(String nombre) {
        String sql = "SELECT * FROM nodos WHERE nombre = ? AND activo = TRUE";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = dbConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, nombre);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new GeoNode(
                    rs.getString("nombre"),
                    rs.getDouble("latitud"),
                    rs.getDouble("longitud")
                );
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener nodo por nombre: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("❌ Error cerrando recursos: " + e.getMessage());
            }
        }
        
        return null;
    }
    
    /**
     * Obtener nodos por tipo
     */
    public List<GeoNode> obtenerNodosPorTipo(String tipo) {
        List<GeoNode> nodos = new ArrayList<>();
        String sql = "SELECT * FROM nodos WHERE tipo = ? AND activo = TRUE ORDER BY nombre";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = dbConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, tipo);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                GeoNode nodo = new GeoNode(
                    rs.getString("nombre"),
                    rs.getDouble("latitud"),
                    rs.getDouble("longitud")
                );
                nodos.add(nodo);
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener nodos por tipo: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("❌ Error cerrando recursos: " + e.getMessage());
            }
        }
        
        return nodos;
    }
    
    /**
     * Obtener nodos en un rango geográfico
     */
    public List<GeoNode> obtenerNodosEnRango(double latMin, double latMax, double lonMin, double lonMax) {
        List<GeoNode> nodos = new ArrayList<>();
        String sql = "SELECT * FROM nodos WHERE latitud BETWEEN ? AND ? AND longitud BETWEEN ? AND ? AND activo = TRUE ORDER BY nombre";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = dbConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setDouble(1, latMin);
            stmt.setDouble(2, latMax);
            stmt.setDouble(3, lonMin);
            stmt.setDouble(4, lonMax);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                GeoNode nodo = new GeoNode(
                    rs.getString("nombre"),
                    rs.getDouble("latitud"),
                    rs.getDouble("longitud")
                );
                nodos.add(nodo);
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener nodos en rango: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("❌ Error cerrando recursos: " + e.getMessage());
            }
        }
        
        return nodos;
    }
    
    /**
     * Insertar un nuevo nodo
     */
    public boolean insertarNodo(GeoNode nodo, String tipo, String descripcion) {
        String sql = "INSERT INTO nodos (nombre, latitud, longitud, tipo, descripcion) VALUES (?, ?, ?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = dbConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, nodo.getName());
            stmt.setDouble(2, nodo.getLatitude());
            stmt.setDouble(3, nodo.getLongitude());
            stmt.setString(4, tipo);
            stmt.setString(5, descripcion);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("❌ Error al insertar nodo: " + e.getMessage());
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
     * Actualizar un nodo existente
     */
    public boolean actualizarNodo(GeoNode nodo, String tipo, String descripcion) {
        String sql = "UPDATE nodos SET latitud = ?, longitud = ?, tipo = ?, descripcion = ? WHERE nombre = ? AND activo = TRUE";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = dbConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setDouble(1, nodo.getLatitude());
            stmt.setDouble(2, nodo.getLongitude());
            stmt.setString(3, tipo);
            stmt.setString(4, descripcion);
            stmt.setString(5, nodo.getName());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar nodo: " + e.getMessage());
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
     * Desactivar un nodo (soft delete)
     */
    public boolean desactivarNodo(String nombre) {
        String sql = "UPDATE nodos SET activo = FALSE WHERE nombre = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = dbConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, nombre);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("❌ Error al desactivar nodo: " + e.getMessage());
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
     * Verificar si un nodo existe
     */
    public boolean existeNodo(String nombre) {
        String sql = "SELECT COUNT(*) FROM nodos WHERE nombre = ? AND activo = TRUE";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = dbConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, nombre);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error al verificar existencia de nodo: " + e.getMessage());
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
     * Obtener estadísticas de nodos
     */
    public Map<String, Object> obtenerEstadisticas() {
        Map<String, Object> stats = new HashMap<>();
        String sql = "SELECT COUNT(*) as total, COUNT(DISTINCT tipo) as tipos FROM nodos WHERE activo = TRUE";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = dbConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                stats.put("total_nodos", rs.getInt("total"));
                stats.put("tipos_diferentes", rs.getInt("tipos"));
            }
            
            // Obtener conteo por tipo
            String sqlTipos = "SELECT tipo, COUNT(*) as cantidad FROM nodos WHERE activo = TRUE GROUP BY tipo";
            PreparedStatement stmtTipos = null;
            ResultSet rsTipos = null;
            
            try {
                stmtTipos = conn.prepareStatement(sqlTipos);
                rsTipos = stmtTipos.executeQuery();
                
                Map<String, Integer> tipos = new HashMap<>();
                while (rsTipos.next()) {
                    tipos.put(rsTipos.getString("tipo"), rsTipos.getInt("cantidad"));
                }
                stats.put("conteo_por_tipo", tipos);
                
            } finally {
                if (rsTipos != null) rsTipos.close();
                if (stmtTipos != null) stmtTipos.close();
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener estadísticas: " + e.getMessage());
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
     * Buscar nodos por nombre (búsqueda parcial)
     */
    public List<GeoNode> buscarNodos(String termino) {
        List<GeoNode> nodos = new ArrayList<>();
        String sql = "SELECT * FROM nodos WHERE nombre LIKE ? AND activo = TRUE ORDER BY nombre";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = dbConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, "%" + termino + "%");
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                GeoNode nodo = new GeoNode(
                    rs.getString("nombre"),
                    rs.getDouble("latitud"),
                    rs.getDouble("longitud")
                );
                nodos.add(nodo);
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error al buscar nodos: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("❌ Error cerrando recursos: " + e.getMessage());
            }
        }
        
        return nodos;
    }
    
    /**
     * Obtener el ID de un nodo por nombre
     */
    public Integer obtenerIdNodo(String nombre) {
        String sql = "SELECT id FROM nodos WHERE nombre = ? AND activo = TRUE";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = dbConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, nombre);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("id");
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener ID de nodo: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("❌ Error cerrando recursos: " + e.getMessage());
            }
        }
        
        return null;
    }
}
