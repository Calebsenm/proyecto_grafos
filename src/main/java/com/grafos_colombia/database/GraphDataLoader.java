package com.grafos_colombia.database;

import com.grafos_colombia.graph.Edge;
import com.grafos_colombia.graph.Graph;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Carga datos del grafo desde CSV y DB (versión mínima)
 */
public class GraphDataLoader {
    private final DatabaseConnection db = DatabaseConnection.getInstance();
    private final NodoDAO nodoDAO = new NodoDAO();
    private final AristaDAO aristaDAO = new AristaDAO();

    /** Cargar grafo completo desde la base de datos */
    public Graph cargarGrafoCompleto() {
        System.out.println("Cargando grafo desde la base de datos...");

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
        System.out.println("Grafo cargado: " + aristas.size() + " aristas, " + graph.getAdjList().size() + " nodos");
        return graph;
    }

    /** Cargar desde CSV solo si la DB está vacía */
    public void cargarGrafoDesdeCSVSiEsNecesario() {
        if (db.tieneDatos()) {
            System.out.println("Base de datos ya tiene datos. Saltando CSV.");
            return;
        }

        System.out.println("Base de datos vacía. Cargando desde Grafos.csv...");

        List<Edge> edges = new ArrayList<>();
        Set<String> nodos = new HashSet<>();

        try (InputStream is = getClass().getResourceAsStream("/Grafos.csv");
             BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {

            if (is == null) {
                System.err.println("Grafos.csv no encontrado en resources");
                return;
            }

            br.readLine(); // saltar cabecera
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",", 3);
                if (p.length == 3) {
                    String o = p[0].trim(), d = p[1].trim();
                    double dist = Double.parseDouble(p[2].trim());
                    edges.add(new Edge(o, d, dist));
                    nodos.add(o);
                    nodos.add(d);
                }
            }

            // Insertar nodos
            for (String n : nodos) {
                nodoDAO.insertarNodo(n);
            }

            // Insertar aristas
            for (Edge e : edges) {
                aristaDAO.insertarArista(e.getSrc(), e.getDest(), e.getWeight());
            }

            System.out.println("CSV cargado: " + edges.size() + " aristas, " + nodos.size() + " nodos");

        } catch (Exception e) {
            System.err.println("Error leyendo CSV: " + e.getMessage());
        }
    }

    /** Verificar que no haya aristas huérfanas */
    public boolean verificarIntegridadDatos() {
        System.out.println("Verificando integridad...");

        List<Edge> aristas = aristaDAO.obtenerTodasLasAristas();
        List<String> nombresNodos = nodoDAO.obtenerTodosLosNombres();

        boolean ok = true;
        for (Edge e : aristas) {
            if (!nombresNodos.contains(e.getSrc())) {
                System.err.println("Nodo origen no existe: " + e.getSrc());
                ok = false;
            }
            if (!nombresNodos.contains(e.getDest())) {
                System.err.println("Nodo destino no existe: " + e.getDest());
                ok = false;
            }
        }

        System.out.println(ok ? "Integridad OK" : "Hay errores de integridad");
        return ok;
    }

    /** Mostrar estadísticas básicas */
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