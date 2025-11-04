package com.grafos_colombia.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * NodoDAO simplificado: solo nombre, sin coordenadas, sin tipo, sin activo
 */
public class NodoDAO {
    private final DatabaseConnection db = DatabaseConnection.getInstance();

    /** Insertar nodo por nombre (evita duplicados) */
    public boolean insertarNodo(String nombre) {
        String sql = "INSERT INTO nodo (nombre) VALUES (?) ON CONFLICT(nombre) DO NOTHING";
        try (Connection c = db.getConnection(); 
             PreparedStatement s = c.prepareStatement(sql)) {
            s.setString(1, nombre.trim());
            return s.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error insertando nodo '" + nombre + "': " + e.getMessage());
            return false;
        }
    }

    /** Verificar si existe */
    public boolean existeNodo(String nombre) {
        String sql = "SELECT 1 FROM nodo WHERE nombre = ?";
        try (Connection c = db.getConnection(); 
             PreparedStatement s = c.prepareStatement(sql)) {
            s.setString(1, nombre.trim());
            try (ResultSet rs = s.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            return false;
        }
    }

    /** Obtener todos los nombres de nodos */
    public List<String> obtenerTodosLosNombres() {
        List<String> nombres = new ArrayList<>();
        String sql = "SELECT nombre FROM nodo ORDER BY nombre";
        try (Connection c = db.getConnection(); 
             Statement s = c.createStatement(); 
             ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) {
                nombres.add(rs.getString("nombre"));
            }
        } catch (SQLException e) {
            System.err.println("Error leyendo nodos: " + e.getMessage());
        }
        return nombres;
    }

    /** Obtener ID por nombre */
    public Integer obtenerIdNodo(String nombre) {
        String sql = "SELECT id FROM nodo WHERE nombre = ?";
        try (Connection c = db.getConnection(); 
             PreparedStatement s = c.prepareStatement(sql)) {
            s.setString(1, nombre.trim());
            try (ResultSet rs = s.executeQuery()) {
                return rs.next() ? rs.getInt("id") : null;
            }
        } catch (SQLException e) {
            return null;
        }
    }

    /** Guardar nodo si no existe */
    public void guardarSiNoExiste(String nombre) {
        if (!existeNodo(nombre)) {
            insertarNodo(nombre);
        }
    }
}