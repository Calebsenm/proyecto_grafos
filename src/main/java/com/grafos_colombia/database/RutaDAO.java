package com.grafos_colombia.database;

import com.grafos_colombia.algorithm.PathResult;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para manejar operaciones con la tabla rutas_calculadas
 * Permite cachear rutas, mantener historial y optimizar consultas
 */
public class RutaDAO {
    
    /**
     * Buscar una ruta previamente calculada entre dos nodos
     */
    public PathResult buscarRutaExistente(String nodoOrigen, String nodoDestino) {
        String sql = "SELECT rc.id, rc.distancia_total, rc.tiempo_total, rc.costo_total, rc.fecha_calculo " +
                    "FROM rutas_calculadas rc " +
                    "JOIN nodos n1 ON rc.nodo_origen_id = n1.id " +
                    "JOIN nodos n2 ON rc.nodo_destino_id = n2.id " +
                    "WHERE n1.nombre = ? AND n2.nombre = ? AND n1.activo = TRUE AND n2.activo = TRUE " +
                    "ORDER BY rc.fecha_calculo DESC " +
                    "LIMIT 1";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nodoOrigen);
            stmt.setString(2, nodoDestino);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int rutaId = rs.getInt("id");
                double distancia = rs.getDouble("distancia_total");
                
                // Obtener la secuencia de nodos de la ruta
                List<String> secuencia = obtenerSecuenciaRuta(rutaId);
                
                if (!secuencia.isEmpty()) {
                    System.out.println("🔄 Ruta encontrada en cache: " + nodoOrigen + " → " + nodoDestino + 
                                     " (Distancia: " + String.format("%.2f", distancia) + " km)");
                    return new PathResult(distancia, secuencia);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error al buscar ruta existente: " + e.getMessage());
        }
        
        return null;
    }
    
    
    /**
     * Obtener la secuencia de nodos de una ruta calculada
     */
    private List<String> obtenerSecuenciaRuta(int rutaId) {
        String sql = "SELECT n.nombre " +
                    "FROM secuencia_rutas sr " +
                    "JOIN nodos n ON sr.nodo_id = n.id " +
                    "WHERE sr.ruta_id = ? AND n.activo = TRUE " +
                    "ORDER BY sr.orden";
        
        List<String> secuencia = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, rutaId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                secuencia.add(rs.getString("nombre"));
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener secuencia de ruta: " + e.getMessage());
        }
        
        return secuencia;
    }
    
    /**
     * Guardar una nueva ruta calculada en la base de datos
     */
    public boolean guardarRuta(PathResult ruta, String nodoOrigen, String nodoDestino) {
        if (ruta == null || ruta.path == null || ruta.path.size() < 2) {
            return false;
        }
        
        Connection conn = null;
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            conn.setAutoCommit(false); // Iniciar transacción
            
            // 1. Obtener IDs de nodos origen y destino
            int origenId = obtenerIdNodo(nodoOrigen, conn);
            int destinoId = obtenerIdNodo(nodoDestino, conn);
            
            if (origenId == -1 || destinoId == -1) {
                System.err.println("❌ No se pudieron encontrar los nodos: " + nodoOrigen + " o " + nodoDestino);
                return false;
            }
            
            // 2. Insertar en tabla rutas_calculadas
            String insertRutaSQL = "INSERT INTO rutas_calculadas (nodo_origen_id, nodo_destino_id, distancia_total, tiempo_total, costo_total) " +
                                  "VALUES (?, ?, ?, ?, ?)";
            
            try (PreparedStatement stmt = conn.prepareStatement(insertRutaSQL, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, origenId);
                stmt.setInt(2, destinoId);
                stmt.setDouble(3, ruta.distance);
                stmt.setInt(4, calcularTiempoEstimado(ruta.distance));
                stmt.setNull(5, Types.DECIMAL); // costo_total por ahora null
                
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected == 0) {
                    throw new SQLException("No se pudo insertar la ruta");
                }
                
                // 3. Obtener el ID de la ruta insertada
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (!generatedKeys.next()) {
                    throw new SQLException("No se pudo obtener el ID de la ruta insertada");
                }
                int rutaId = generatedKeys.getInt(1);
                
                // 4. Insertar secuencia de nodos
                if (!insertarSecuenciaRuta(rutaId, ruta.path, conn)) {
                    throw new SQLException("No se pudo insertar la secuencia de la ruta");
                }
                
                conn.commit();
                System.out.println("💾 Ruta guardada en cache: " + nodoOrigen + " → " + nodoDestino + 
                                 " (ID: " + rutaId + ", Distancia: " + String.format("%.2f", ruta.distance) + " km)");
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error al guardar ruta: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    System.err.println("❌ Error al hacer rollback: " + rollbackEx.getMessage());
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("❌ Error al cerrar conexión: " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * Obtener ID de un nodo por nombre
     */
    private int obtenerIdNodo(String nombreNodo, Connection conn) throws SQLException {
        String sql = "SELECT id FROM nodos WHERE nombre = ? AND activo = TRUE";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nombreNodo);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("id");
            }
        }
        
        return -1;
    }
    
    /**
     * Insertar la secuencia de nodos de una ruta
     */
    private boolean insertarSecuenciaRuta(int rutaId, List<String> secuencia, Connection conn) throws SQLException {
        String sql = "INSERT INTO secuencia_rutas (ruta_id, orden, nodo_id) VALUES (?, ?, ?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < secuencia.size(); i++) {
                String nombreNodo = secuencia.get(i);
                int nodoId = obtenerIdNodo(nombreNodo, conn);
                
                if (nodoId == -1) {
                    System.err.println("❌ No se pudo encontrar nodo en secuencia: " + nombreNodo);
                    return false;
                }
                
                stmt.setInt(1, rutaId);
                stmt.setInt(2, i + 1); // orden empieza en 1
                stmt.setInt(3, nodoId);
                stmt.addBatch();
            }
            
            stmt.executeBatch();
            return true;
        }
    }
    
    /**
     * Calcular tiempo estimado basado en distancia
     */
    private int calcularTiempoEstimado(double distancia) {
        // Velocidad promedio de 60 km/h
        return Math.max(5, (int) (distancia * 60 / 60));
    }
    
    /**
     * Obtener estadísticas de rutas calculadas
     */
    public void mostrarEstadisticas() {
        String sql = "SELECT " +
                    "COUNT(*) as total_rutas, " +
                    "AVG(distancia_total) as distancia_promedio, " +
                    "MIN(distancia_total) as distancia_minima, " +
                    "MAX(distancia_total) as distancia_maxima, " +
                    "COUNT(DISTINCT nodo_origen_id) as nodos_origen_unicos, " +
                    "COUNT(DISTINCT nodo_destino_id) as nodos_destino_unicos " +
                    "FROM rutas_calculadas";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                System.out.println("\n📊 ESTADÍSTICAS DE RUTAS CALCULADAS:");
                System.out.println("   • Total de rutas: " + rs.getInt("total_rutas"));
                System.out.println("   • Distancia promedio: " + String.format("%.2f", rs.getDouble("distancia_promedio")) + " km");
                System.out.println("   • Distancia mínima: " + String.format("%.2f", rs.getDouble("distancia_minima")) + " km");
                System.out.println("   • Distancia máxima: " + String.format("%.2f", rs.getDouble("distancia_maxima")) + " km");
                System.out.println("   • Nodos origen únicos: " + rs.getInt("nodos_origen_unicos"));
                System.out.println("   • Nodos destino únicos: " + rs.getInt("nodos_destino_unicos"));
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener estadísticas de rutas: " + e.getMessage());
        }
    }
    
    /**
     * Limpiar rutas antiguas (más de X días)
     */
    public int limpiarRutasAntiguas(int dias) {
        String sql = "DELETE FROM rutas_calculadas WHERE fecha_calculo < DATE_SUB(NOW(), INTERVAL ? DAY)";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, dias);
            int rowsDeleted = stmt.executeUpdate();
            
            System.out.println("🗑️ Rutas eliminadas (más de " + dias + " días): " + rowsDeleted);
            return rowsDeleted;
            
        } catch (SQLException e) {
            System.err.println("❌ Error al limpiar rutas antiguas: " + e.getMessage());
            return 0;
        }
    }
    
    /**
     * Obtener historial de rutas recientes
     */
    public List<String> obtenerHistorialRutas(int limite) {
        String sql = "SELECT " +
                    "n1.nombre as origen, " +
                    "n2.nombre as destino, " +
                    "rc.distancia_total, " +
                    "rc.fecha_calculo " +
                    "FROM rutas_calculadas rc " +
                    "JOIN nodos n1 ON rc.nodo_origen_id = n1.id " +
                    "JOIN nodos n2 ON rc.nodo_destino_id = n2.id " +
                    "ORDER BY rc.fecha_calculo DESC " +
                    "LIMIT ?";
        
        List<String> historial = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, limite);
            ResultSet rs = stmt.executeQuery();
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            
            while (rs.next()) {
                String origen = rs.getString("origen");
                String destino = rs.getString("destino");
                double distancia = rs.getDouble("distancia_total");
                Timestamp fecha = rs.getTimestamp("fecha_calculo");
                
                String fechaStr = fecha.toLocalDateTime().format(formatter);
                String entrada = String.format("• %s → %s (%.2f km) - %s", 
                                             origen, destino, distancia, fechaStr);
                historial.add(entrada);
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener historial de rutas: " + e.getMessage());
        }
        
        return historial;
    }
    
    /**
     * Verificar si existe una ruta entre dos nodos (sin obtener los detalles)
     */
    public boolean existeRuta(String nodoOrigen, String nodoDestino) {
        String sql = "SELECT COUNT(*) as count " +
                    "FROM rutas_calculadas rc " +
                    "JOIN nodos n1 ON rc.nodo_origen_id = n1.id " +
                    "JOIN nodos n2 ON rc.nodo_destino_id = n2.id " +
                    "WHERE n1.nombre = ? AND n2.nombre = ? AND n1.activo = TRUE AND n2.activo = TRUE";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nodoOrigen);
            stmt.setString(2, nodoDestino);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error al verificar existencia de ruta: " + e.getMessage());
        }
        
        return false;
    }
}
