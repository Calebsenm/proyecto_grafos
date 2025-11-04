package com.grafos_colombia.database;

import com.grafos_colombia.graph.Edge;
import com.grafos_colombia.graph.Graph;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Cargador simple de grafo desde CSV y base de datos
 */
public class GraphDataLoader {
    private final DatabaseConnection db = DatabaseConnection.getInstance();
    private final NodoDAO nodoDAO = new NodoDAO();
    private final AristaDAO aristaDAO = new AristaDAO();

    /**
     * Cargar datos desde CSV a la base de datos
     */
    public boolean cargarDesdeCSV() {
        try (InputStream is = getClass().getResourceAsStream("/csv/Grafos.csv");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            
            if (is == null) {
                System.err.println("No se encontró el archivo CSV");
                return false;
            }
            
            String line;
            boolean isFirstLine = true;
            int count = 0;
            
            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Saltar encabezado
                }
                
                line = line.trim();
                if (line.isEmpty()) continue;
                
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    String nodo1 = parts[0].trim();
                    String nodo2 = parts[1].trim();
                    double distancia = Double.parseDouble(parts[2].trim());
                    
                    // Guardar nodos si no existen
                    nodoDAO.guardarSiNoExiste(nodo1);
                    nodoDAO.guardarSiNoExiste(nodo2);
                    
                    // Guardar arista
                    aristaDAO.guardarSiNoExiste(nodo1, nodo2, distancia);
                    count++;
                }
            }
            
            System.out.println("CSV cargado: " + count + " aristas");
            return true;
            
        } catch (Exception e) {
            System.err.println("Error cargando CSV: " + e.getMessage());
            return false;
        }
    }

    /**
     * Cargar grafo completo desde la base de datos
     */
    public Graph cargarGrafoCompleto() {
        if (!db.isConnected() && !db.connect()) {
            System.err.println("No se pudo conectar a la base de datos");
            return null;
        }

        List<Edge> aristas = aristaDAO.obtenerTodasLasAristas();
        if (aristas.isEmpty()) {
            System.err.println("No hay aristas en la base de datos");
            return null;
        }

        Graph graph = new Graph(aristas);
        return graph;
    }

    /**
     * Mostrar estadísticas básicas del grafo
     */
    public void mostrarEstadisticas() {
        System.out.println("\nESTADÍSTICAS:");
        List<String> nodos = nodoDAO.obtenerTodosLosNombres();
        List<Edge> aristas = aristaDAO.obtenerTodasLasAristas();

        double totalDist = aristas.stream().mapToDouble(Edge::getWeight).sum();
        double prom = aristas.isEmpty() ? 0 : totalDist / aristas.size();

        System.out.println("   • Nodos: " + nodos.size());
        System.out.println("   • Aristas: " + aristas.size());
        System.out.println("   • Distancia total: " + String.format("%.1f", totalDist) + " km");
        System.out.println("   • Distancia promedio: " + String.format("%.1f", prom) + " km");
    }
}