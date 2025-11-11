package com.grafos_colombia.algorithm;

import com.grafos_colombia.graph.Node;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Calcula las métricas del grafo: radio, diámetro y centro.
 * 
 * - Radio: La excentricidad mínima entre todos los nodos del grafo.
 * - Diámetro: La excentricidad máxima entre todos los nodos del grafo.
 * - Centro: El conjunto de nodos cuya excentricidad es igual al radio.
 */
public class GraphMetrics {

    /**
     * Clase para almacenar los resultados de las métricas del grafo.
     */
    public static class GraphMetricsResult {
        public final double radio;
        public final double diametro;
        public final List<String> centro;
        public final Map<String, Double> excentricidades; // Excentricidad de cada nodo
        public final Map<String, Eccentricity.EccentricityResult> detallesExcentricidad;
        public final String nodoReferenciaRadio;
        public final String nodoReferenciaDiametro;
        public final List<String> rutaRadio;
        public final List<String> rutaDiametro;

        public GraphMetricsResult(double radio, double diametro, List<String> centro,
                                  Map<String, Double> excentricidades,
                                  Map<String, Eccentricity.EccentricityResult> detallesExcentricidad,
                                  String nodoReferenciaRadio,
                                  String nodoReferenciaDiametro,
                                  List<String> rutaRadio,
                                  List<String> rutaDiametro) {
            this.radio = radio;
            this.diametro = diametro;
            this.centro = centro;
            this.excentricidades = excentricidades;
            this.detallesExcentricidad = detallesExcentricidad;
            this.nodoReferenciaRadio = nodoReferenciaRadio;
            this.nodoReferenciaDiametro = nodoReferenciaDiametro;
            this.rutaRadio = rutaRadio;
            this.rutaDiametro = rutaDiametro;
        }

        @Override
        public String toString() {
            return String.format("Radio: %.2f, Diámetro: %.2f, Centro: %s", 
                    radio, diametro, centro.toString());
        }
    }

    /**
     * Calcula las métricas del grafo: radio, diámetro y centro.
     * 
     * @param adjList La lista de adyacencia del grafo.
     * @return Un objeto GraphMetricsResult con las métricas calculadas.
     *         Si el grafo está vacío o no es conexo, retorna null.
     */
    public static GraphMetricsResult calcularMetricas(Map<String, List<Node>> adjList) {
        if (adjList == null || adjList.isEmpty()) {
            return null;
        }

        // Caso especial: grafo con un solo nodo
        if (adjList.size() == 1) {
            String unicoNodo = adjList.keySet().iterator().next();
            Map<String, Double> excentricidades = new HashMap<>();
            excentricidades.put(unicoNodo, 0.0);
            Map<String, Eccentricity.EccentricityResult> detalles = new HashMap<>();
            detalles.put(unicoNodo, new Eccentricity.EccentricityResult(0.0, unicoNodo, List.of(unicoNodo)));
            List<String> centro = new ArrayList<>();
            centro.add(unicoNodo);
            return new GraphMetricsResult(
                    0.0,
                    0.0,
                    centro,
                    excentricidades,
                    detalles,
                    unicoNodo,
                    unicoNodo,
                    List.of(unicoNodo),
                    List.of(unicoNodo));
        }

        Map<String, Double> excentricidades = new HashMap<>();
        Map<String, Eccentricity.EccentricityResult> detalles = new HashMap<>();
        double minExcentricidad = Double.POSITIVE_INFINITY;
        double maxExcentricidad = 0.0;
        boolean tieneExcentricidadesValidas = false;
        String nodoMinExcentricidad = null;
        String nodoMaxExcentricidad = null;
        List<String> rutaRadio = null;
        List<String> rutaDiametro = null;

        // Calcular la excentricidad de cada nodo
        for (String nodo : adjList.keySet()) {
            Eccentricity.EccentricityResult resultado = Eccentricity.calculate(nodo, adjList);
            
            if (resultado == null) {
                // Nodo aislado o no alcanzable
                excentricidades.put(nodo, Double.POSITIVE_INFINITY);
                continue;
            }

            double excentricidad = resultado.eccentricity;
            excentricidades.put(nodo, excentricidad);
            detalles.put(nodo, resultado);

            // Si la excentricidad es infinita, el grafo no es conexo
            if (excentricidad == Double.POSITIVE_INFINITY || 
                excentricidad == Double.NEGATIVE_INFINITY ||
                Double.isNaN(excentricidad)) {
                // Nodo no alcanzable desde otros nodos (grafo no conexo)
                continue;
            }

            // Marcar que tenemos al menos una excentricidad válida
            tieneExcentricidadesValidas = true;

            // Actualizar mínimo y máximo
            if (excentricidad < minExcentricidad) {
                minExcentricidad = excentricidad;
                nodoMinExcentricidad = nodo;
                rutaRadio = (resultado.path != null) ? new ArrayList<>(resultado.path) : null;
            }
            if (excentricidad > maxExcentricidad) {
                maxExcentricidad = excentricidad;
                nodoMaxExcentricidad = nodo;
                rutaDiametro = (resultado.path != null) ? new ArrayList<>(resultado.path) : null;
            }
        }

        // Si no encontramos excentricidades válidas, el grafo no es conexo
        if (!tieneExcentricidadesValidas || minExcentricidad == Double.POSITIVE_INFINITY) {
            return null;
        }

        // Encontrar el centro: nodos con excentricidad igual al radio
        List<String> centro = new ArrayList<>();
        for (Map.Entry<String, Double> entrada : excentricidades.entrySet()) {
            double excentricidad = entrada.getValue();
            // Solo considerar nodos con excentricidad válida (no infinita)
            if (excentricidad != Double.POSITIVE_INFINITY && 
                excentricidad != Double.NEGATIVE_INFINITY &&
                !Double.isNaN(excentricidad)) {
                // Usar una tolerancia pequeña para comparar números de punto flotante
                if (Math.abs(excentricidad - minExcentricidad) < 0.0001) {
                    centro.add(entrada.getKey());
                }
            }
        }

        if (rutaRadio == null && nodoMinExcentricidad != null) {
            Eccentricity.EccentricityResult detalleMin = detalles.get(nodoMinExcentricidad);
            if (detalleMin != null && detalleMin.path != null) {
                rutaRadio = new ArrayList<>(detalleMin.path);
            }
        }
        if (rutaDiametro == null && nodoMaxExcentricidad != null) {
            Eccentricity.EccentricityResult detalleMax = detalles.get(nodoMaxExcentricidad);
            if (detalleMax != null && detalleMax.path != null) {
                rutaDiametro = new ArrayList<>(detalleMax.path);
            }
        }

        if (rutaRadio == null) {
            rutaRadio = Collections.emptyList();
        }
        if (rutaDiametro == null) {
            rutaDiametro = Collections.emptyList();
        }

        return new GraphMetricsResult(
                minExcentricidad,
                maxExcentricidad,
                centro,
                excentricidades,
                detalles,
                nodoMinExcentricidad,
                nodoMaxExcentricidad,
                rutaRadio,
                rutaDiametro);
    }

    /**
     * Calcula el radio del grafo (excentricidad mínima).
     * 
     * @param adjList La lista de adyacencia del grafo.
     * @return El radio del grafo, o Double.POSITIVE_INFINITY si el grafo no es conexo.
     */
    public static double calcularRadio(Map<String, List<Node>> adjList) {
        GraphMetricsResult resultado = calcularMetricas(adjList);
        return resultado != null ? resultado.radio : Double.POSITIVE_INFINITY;
    }

    /**
     * Calcula el diámetro del grafo (excentricidad máxima).
     * 
     * @param adjList La lista de adyacencia del grafo.
     * @return El diámetro del grafo, o 0.0 si el grafo no es conexo o está vacío.
     */
    public static double calcularDiametro(Map<String, List<Node>> adjList) {
        GraphMetricsResult resultado = calcularMetricas(adjList);
        return resultado != null ? resultado.diametro : 0.0;
    }

    /**
     * Calcula el centro del grafo (nodos con excentricidad igual al radio).
     * 
     * @param adjList La lista de adyacencia del grafo.
     * @return Una lista con los nodos del centro, o lista vacía si el grafo no es conexo.
     */
    public static List<String> calcularCentro(Map<String, List<Node>> adjList) {
        GraphMetricsResult resultado = calcularMetricas(adjList);
        return resultado != null ? resultado.centro : new ArrayList<>();
    }
}
