package com.grafos_colombia.database;

import com.grafos_colombia.graph.ColombianLocations;
import com.grafos_colombia.graph.Edge;
import com.grafos_colombia.graph.GeoNode;
import com.grafos_colombia.graph.Graph;
import com.grafos_colombia.graph.GraphConverter;
import com.grafos_colombia.graph.GraphView;
import com.grafos_colombia.graph.Node;
import java.util.*;

/**
 * Clase para cargar datos del grafo desde la base de datos
 */
public class GraphDataLoader {
    private DatabaseConnection dbConnection;
    private NodoDAO nodoDAO;
    private AristaDAO aristaDAO;
    
    public GraphDataLoader() {
        this.dbConnection = DatabaseConnection.getInstance();
        this.nodoDAO = new NodoDAO();
        this.aristaDAO = new AristaDAO();
    }

    public DatabaseConnection getDbConnection() {
        return dbConnection;
    }

    public void setDbConnection(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    public NodoDAO getNodoDAO() {
        return nodoDAO;
    }

    public void setNodoDAO(NodoDAO nodoDAO) {
        this.nodoDAO = nodoDAO;
    }

    public AristaDAO getAristaDAO() {
        return aristaDAO;
    }

    public void setAristaDAO(AristaDAO aristaDAO) {
        this.aristaDAO = aristaDAO;
    }
      
    /**
     * Cargar grafo completo desde la base de datos
     * @return 
     */
    public Graph cargarGrafoCompleto() {
        System.out.println("🔄 Cargando grafo desde la base de datos...");
        
        // Verificar conexión
        if (!dbConnection.isConnected()) {
            if (!dbConnection.connect()) {
                System.err.println("❌ No se pudo conectar a la base de datos");
                return null;
            }
        }
        
        // Obtener todas las aristas
        List<Edge> aristas = aristaDAO.obtenerTodasLasAristas();
        
        if (aristas.isEmpty()) {
            System.err.println("⚠️ No se encontraron aristas en la base de datos");
            return null;
        }
        
        // Crear grafo
        Graph graph = new Graph(aristas);
        
        System.out.println("✅ Grafo cargado exitosamente:");
        System.out.println("   • Aristas cargadas: " + aristas.size());
        System.out.println("   • Nodos únicos: " + graph.getAdjList().size());
        
        return graph;
    }
    
    /**
     * Cargar grafo y inicializar GraphView
     * @param graphView
     * @return 
     */
    public boolean cargarGrafoEnGraphView(GraphView graphView) {
        Graph graph = cargarGrafoCompleto();
        
        if (graph == null) {
            return false;
        }
        
        try {
            // Obtener mapa de nodos con coordenadas geográficas
            Map<String, GeoNode> geoNodes = nodoDAO.obtenerMapaNodos();
            
            // Convertir a formato GraphView con coordenadas geográficas
            GraphConverter.GraphViewData graphData = GraphConverter.convertWithGeographicCoordinates(
                graph.getAdjList(), 
                obtenerAristasDelGrafo(graph),
                geoNodes
            );
            
            // Inicializar GraphView
            graphView.initializeGraph(graphData.getNodes(), graphData.getEdges());
            
            System.out.println("✅ GraphView inicializado con datos de la base de datos");
            return true;
            
        } catch (Exception e) {
            System.err.println("❌ Error al inicializar GraphView: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Obtener aristas del grafo como lista
     */
    private List<Edge> obtenerAristasDelGrafo(Graph graph) {
        List<Edge> aristas = new ArrayList<>();
        Map<String, List<Node>> adjList = graph.getAdjList();
        
        for (Map.Entry<String, List<Node>> entry : adjList.entrySet()) {
            String origen = entry.getKey();
            for (Node destino : entry.getValue()) {
                // Evitar duplicados
                boolean existe = aristas.stream().anyMatch(arista -> 
                    (arista.getSrc().equals(origen) && arista.getDest().equals(destino.getValue())) ||
                    (arista.getSrc().equals(destino.getValue()) && arista.getDest().equals(origen))
                );
                
                if (!existe) {
                    aristas.add(new Edge(origen, destino.getValue(), destino.getWeight()));
                }
            }
        }
        
        return aristas;
    }
    
    /**
     * Cargar grafo por región geográfica
     * @param latMin
     * @param latMax
     * @param lonMin
     * @param lonMax
     * @return 
     */
    public Graph cargarGrafoPorRegion(double latMin, double latMax, double lonMin, double lonMax) {
        System.out.println("🔄 Cargando grafo por región geográfica...");
        System.out.println("   • Latitud: " + latMin + " a " + latMax);
        System.out.println("   • Longitud: " + lonMin + " a " + lonMax);
        
        // Obtener nodos en la región
        List<com.grafos_colombia.graph.GeoNode> nodosRegion = nodoDAO.obtenerNodosEnRango(latMin, latMax, lonMin, lonMax);
        
        if (nodosRegion.isEmpty()) {
            System.err.println("⚠️ No se encontraron nodos en la región especificada");
            return null;
        }
        
        // Obtener nombres de nodos en la región
        Set<String> nombresNodos = new HashSet<>();
        for (com.grafos_colombia.graph.GeoNode nodo : nodosRegion) {
            nombresNodos.add(nodo.getName());
        }
        
        // Obtener aristas que conecten nodos de la región
        List<Edge> aristasRegion = new ArrayList<>();
        List<Edge> todasAristas = aristaDAO.obtenerTodasLasAristas();
        
        for (Edge arista : todasAristas) {
            if (nombresNodos.contains(arista.getSrc()) && nombresNodos.contains(arista.getDest())) {
                aristasRegion.add(arista);
            }
        }
        
        if (aristasRegion.isEmpty()) {
            System.err.println("⚠️ No se encontraron conexiones entre nodos de la región");
            return null;
        }
        
        Graph graph = new Graph(aristasRegion);
        
        System.out.println("✅ Grafo regional cargado:");
        System.out.println("   • Nodos en región: " + nodosRegion.size());
        System.out.println("   • Aristas en región: " + aristasRegion.size());
        
        return graph;
    }
    
    /**
     * Cargar grafo por tipo de nodo
     * @param tipo
     * @return 
     */
    public Graph cargarGrafoPorTipoNodo(String tipo) {
        System.out.println("🔄 Cargando grafo por tipo de nodo: " + tipo);
        
        // Obtener nodos del tipo especificado
        List<com.grafos_colombia.graph.GeoNode> nodosTipo = nodoDAO.obtenerNodosPorTipo(tipo);
        
        if (nodosTipo.isEmpty()) {
            System.err.println("⚠️ No se encontraron nodos del tipo: " + tipo);
            return null;
        }
        
        // Obtener nombres de nodos del tipo
        Set<String> nombresNodos = new HashSet<>();
        for (com.grafos_colombia.graph.GeoNode nodo : nodosTipo) {
            nombresNodos.add(nodo.getName());
        }
        
        // Obtener aristas que conecten nodos del tipo
        List<Edge> aristasTipo = new ArrayList<>();
        List<Edge> todasAristas = aristaDAO.obtenerTodasLasAristas();
        
        for (Edge arista : todasAristas) {
            if (nombresNodos.contains(arista.getSrc()) && nombresNodos.contains(arista.getDest())) {
                aristasTipo.add(arista);
            }
        }
        
        Graph graph = new Graph(aristasTipo);
        
        System.out.println("✅ Grafo por tipo cargado:");
        System.out.println("   • Nodos de tipo '" + tipo + "': " + nodosTipo.size());
        System.out.println("   • Aristas entre nodos del tipo: " + aristasTipo.size());
        
        return graph;
    }
    
    /**
     * Obtener estadísticas de la base de datos
     */
    public void mostrarEstadisticas() {
        System.out.println("\n📊 ESTADÍSTICAS DE LA BASE DE DATOS:");
        
        // Estadísticas de nodos
        Map<String, Object> statsNodos = nodoDAO.obtenerEstadisticas();
        System.out.println("   📍 Nodos:");
        System.out.println("      • Total: " + statsNodos.get("total_nodos"));
        System.out.println("      • Tipos diferentes: " + statsNodos.get("tipos_diferentes"));
        
        @SuppressWarnings("unchecked")
        Map<String, Integer> tiposNodos = (Map<String, Integer>) statsNodos.get("conteo_por_tipo");
        if (tiposNodos != null) {
            for (Map.Entry<String, Integer> entry : tiposNodos.entrySet()) {
                System.out.println("      • " + entry.getKey() + ": " + entry.getValue());
            }
        }
        
        // Estadísticas de aristas
        Map<String, Object> statsAristas = aristaDAO.obtenerEstadisticas();
        System.out.println("   🛣️ Aristas:");
        System.out.println("      • Total: " + statsAristas.get("total_aristas"));
        System.out.println("      • Distancia promedio: " + String.format("%.2f", statsAristas.get("distancia_promedio")) + " km");
        System.out.println("      • Distancia mínima: " + statsAristas.get("distancia_minima") + " km");
        System.out.println("      • Distancia máxima: " + statsAristas.get("distancia_maxima") + " km");
        
        @SuppressWarnings("unchecked")
        Map<String, Integer> tiposRuta = (Map<String, Integer>) statsAristas.get("conteo_por_tipo_ruta");
        if (tiposRuta != null) {
            System.out.println("      • Tipos de ruta:");
            for (Map.Entry<String, Integer> entry : tiposRuta.entrySet()) {
                System.out.println("        - " + entry.getKey() + ": " + entry.getValue());
            }
        }
    }
    
    /**
     * Verificar integridad de los datos
     * @return 
     */
    public boolean verificarIntegridadDatos() {
        System.out.println("🔍 Verificando integridad de los datos...");
        
        boolean integridadOk = true;
        
        // Verificar que todos los nodos en aristas existan
        List<Edge> aristas = aristaDAO.obtenerTodasLasAristas();
        Set<String> nombresNodosExistentes = new HashSet<>();
        
        // Obtener todos los nombres de nodos existentes
        List<com.grafos_colombia.graph.GeoNode> todosNodos = nodoDAO.obtenerTodosLosNodos();
        for (com.grafos_colombia.graph.GeoNode nodo : todosNodos) {
            nombresNodosExistentes.add(nodo.getName());
        }
        
        // Verificar nodos en aristas
        for (Edge arista : aristas) {
            if (!nombresNodosExistentes.contains(arista.getSrc())) {
                System.err.println("❌ Nodo origen no existe en arista: " + arista.getSrc());
                integridadOk = false;
            }
            if (!nombresNodosExistentes.contains(arista.getDest())) {
                System.err.println("❌ Nodo destino no existe en arista: " + arista.getDest());
                integridadOk = false;
            }
        }
        
        if (integridadOk) {
            System.out.println("✅ Integridad de datos verificada correctamente");
        } else {
            System.err.println("❌ Se encontraron problemas de integridad en los datos");
        }
        
        return integridadOk;
    }
    
    /**
     * Sincronizar datos de ColombianLocations con la base de datos
     */
    public void sincronizarConColombianLocations() {
        System.out.println("🔄 Sincronizando con ColombianLocations...");
        
        // Lista de nombres conocidos de ColombianLocations
        String[] locationNames = {
            "Montería", "Lorica", "Sahagún", "Cereté", "San Pelayo", "Ciénaga de Oro",
            "Tierralta", "Montelíbano", "Puerto Escondido", "San Bernardo del Viento",
            "San Antero", "Nuevo Agrado", "La Doctrina", "Juan de Dios Garis", "Mata de Caña",
            "Trementino", "Paso Nuevo", "Moñitos", "Cristo Rey", "El Palmar",
            "Los Córdobas", "El Ébano", "Los Cedros", "Canalete", "La Seba",
            "El Amparo", "Los Garzones", "El Sabanal", "Pueblo Vaca", "La Ceiba",
            "Los Morales", "Valencia", "Pelayito", "Cabuya", "San Carlos",
            "Patio Bonito", "Planeta Rica", "Pueblo Nuevo", "La Ye", "Plaza Bonita",
            "Buenavista", "Rusia", "Puerto Córdoba", "La Apartada", "Palotal",
            "Las Delicias", "Ayapel", "La Balsa", "Buenos Aires", "Puerto Libertador",
            "San José de Uré", "Momil", "Sabanacosta", "Tuchín", "Campo Bello",
            "Chimá", "San Andrés de Sotavento", "Cacaotal", "Chinú", "Tierra Grata",
            "La Mesa", "Berástegui", "Rabolargo", "Punta de Yánez", "Los Corrales",
            "Purísima", "Carrillo", "Cotorra"
        };
        
        int nodosSincronizados = 0;
        int nodosFaltantes = 0;
        
        for (String nombre : locationNames) {
            com.grafos_colombia.graph.GeoNode nodoColombia = ColombianLocations.getLocation(nombre);
            
            if (nodoColombia != null) {
                // Verificar si existe en la base de datos
                if (nodoDAO.existeNodo(nombre)) {
                    nodosSincronizados++;
                } else {
                    // Insertar nodo faltante
                    if (nodoDAO.insertarNodo(nodoColombia, "ciudad", "Ubicación de Córdoba, Colombia")) {
                        nodosSincronizados++;
                        System.out.println("   ✅ Nodo agregado: " + nombre);
                    } else {
                        nodosFaltantes++;
                        System.err.println("   ❌ Error al agregar nodo: " + nombre);
                    }
                }
            }
        }
        
        System.out.println("✅ Sincronización completada:");
        System.out.println("   • Nodos sincronizados: " + nodosSincronizados);
        if (nodosFaltantes > 0) {
            System.out.println("   • Nodos con errores: " + nodosFaltantes);
        }
    }
}
