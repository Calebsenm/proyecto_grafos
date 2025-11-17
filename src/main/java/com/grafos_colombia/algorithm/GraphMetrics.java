package com.grafos_colombia.algorithm;

import com.grafos_colombia.graph.Node;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GraphMetrics {

   
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
        public final List<String> nodosRadio;
        public final List<String> nodosDiametro;

        public GraphMetricsResult(double radio, double diametro, List<String> centro,
                                  Map<String, Double> excentricidades,
                                  Map<String, Eccentricity.EccentricityResult> detallesExcentricidad,
                                  String nodoReferenciaRadio,
                                  String nodoReferenciaDiametro,
                                  List<String> rutaRadio,
                                  List<String> rutaDiametro,
                                  List<String> nodosRadio,
                                  List<String> nodosDiametro) {
            this.radio = radio;
            this.diametro = diametro;
            this.centro = centro;
            this.excentricidades = excentricidades;
            this.detallesExcentricidad = detallesExcentricidad;
            this.nodoReferenciaRadio = nodoReferenciaRadio;
            this.nodoReferenciaDiametro = nodoReferenciaDiametro;
            this.rutaRadio = rutaRadio;
            this.rutaDiametro = rutaDiametro;
            this.nodosRadio = nodosRadio;
            this.nodosDiametro = nodosDiametro;
        }

        @Override
        public String toString() {
            return String.format("Radio: %.2f, Diámetro: %.2f, Centro: %s", 
                    radio, diametro, centro.toString());
        }
    }

 
    public static GraphMetricsResult calcularMetricas(Map<String, List<Node>> adjList) {
        if (adjList == null || adjList.isEmpty()) {
            return null;
        }

    
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
                    List.of(unicoNodo),
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
        List<String> nodosRadio = new ArrayList<>();
        List<String> nodosDiametro = new ArrayList<>();

 
        for (String nodo : adjList.keySet()) {
            Eccentricity.EccentricityResult resultado = Eccentricity.calculate(nodo, adjList);
            
            if (resultado == null) {
  
                excentricidades.put(nodo, Double.POSITIVE_INFINITY);
                continue;
            }

            double excentricidad = resultado.eccentricity;
            excentricidades.put(nodo, excentricidad);
            detalles.put(nodo, resultado);

      
            if (excentricidad == Double.POSITIVE_INFINITY || 
                excentricidad == Double.NEGATIVE_INFINITY ||
                Double.isNaN(excentricidad)) {
        
                continue;
            }

          
            tieneExcentricidadesValidas = true;

          
            if (excentricidad < minExcentricidad) {
                minExcentricidad = excentricidad;
                nodoMinExcentricidad = nodo;
                rutaRadio = (resultado.path != null) ? new ArrayList<>(resultado.path) : null;
                nodosRadio.clear();
                if (resultado.path != null) {
                    nodosRadio.addAll(resultado.path);
                } else {
                    nodosRadio.add(nodo);
                }
            } else if (Math.abs(excentricidad - minExcentricidad) < 0.0001) {
                if (resultado.path != null && nodosRadio.isEmpty()) {
                    nodosRadio.addAll(resultado.path);
                }
            }
            if (excentricidad > maxExcentricidad) {
                maxExcentricidad = excentricidad;
                nodoMaxExcentricidad = nodo;
                rutaDiametro = (resultado.path != null) ? new ArrayList<>(resultado.path) : null;
                nodosDiametro.clear();
                if (resultado.path != null) {
                    nodosDiametro.addAll(resultado.path);
                } else {
                    nodosDiametro.add(nodo);
                }
            } else if (Math.abs(excentricidad - maxExcentricidad) < 0.0001) {
                if (resultado.path != null && nodosDiametro.isEmpty()) {
                    nodosDiametro.addAll(resultado.path);
                }
            }
        }

  
        if (!tieneExcentricidadesValidas || minExcentricidad == Double.POSITIVE_INFINITY) {
            return null;
        }

  
        List<String> centro = new ArrayList<>();
        for (Map.Entry<String, Double> entrada : excentricidades.entrySet()) {
            double excentricidad = entrada.getValue();
   
            if (excentricidad != Double.POSITIVE_INFINITY && 
                excentricidad != Double.NEGATIVE_INFINITY &&
                !Double.isNaN(excentricidad)) {
            
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
        if (nodosRadio.isEmpty() && nodoMinExcentricidad != null) {
            nodosRadio.add(nodoMinExcentricidad);
        }
        if (nodosDiametro.isEmpty() && nodoMaxExcentricidad != null) {
            nodosDiametro.add(nodoMaxExcentricidad);
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
                rutaDiametro,
                nodosRadio,
                nodosDiametro);
    }


    public static double calcularRadio(Map<String, List<Node>> adjList) {
        GraphMetricsResult resultado = calcularMetricas(adjList);
        return resultado != null ? resultado.radio : Double.POSITIVE_INFINITY;
    }


    public static double calcularDiametro(Map<String, List<Node>> adjList) {
        GraphMetricsResult resultado = calcularMetricas(adjList);
        return resultado != null ? resultado.diametro : 0.0;
    }

    public static List<String> calcularCentro(Map<String, List<Node>> adjList) {
        GraphMetricsResult resultado = calcularMetricas(adjList);
        return resultado != null ? resultado.centro : new ArrayList<>();
    }
}
