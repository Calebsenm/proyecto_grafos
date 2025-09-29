package com.grafos_colombia.controller;

import java.util.Arrays;
import java.util.List;

import com.grafos_colombia.algorithm.Dijkstra;
import com.grafos_colombia.algorithm.PathResult;
import com.grafos_colombia.graph.Edge;
import com.grafos_colombia.graph.Graph;
import javafx.fxml.FXML;

public class mainController {

    @FXML
    private void run_distra() {
        System.out.println("Algoritmo de Dijkstra ejecutado!");

        List<Edge> edges = Arrays.asList(
                new Edge("San antero", "Nuevo agrado", 11),
                new Edge("Nuevo agrado", "Lorica", 9.1),
                new Edge("Lorica", "La doctrina", 12),
                new Edge("Lorica", "Juan de dios garis", 4.2),
                new Edge("Lorica", "Mata de caña", 20),
                new Edge("La doctrina", "Trementino", 7.9),
                new Edge("Trementino", "San bernardo del viento", 5.5),
                new Edge("San bernardo del viento", "Paso nuevo", 16),
                new Edge("Paso nuevo", "Moñitos", 15),
                new Edge("Moñitos", "Cristo rey", 37),
                new Edge("Cristo rey", "Puerto escondido", 9.6),
                new Edge("Puerto escondido", "El palmar", 6.7),
                new Edge("El palmar", "Los cordobas", 27.9),
                new Edge("El palmar", "El ebano", 15.7),
                new Edge("Los cordobas", "El ebano", 21),
                new Edge("El ebano", "Los cedros", 29),
                new Edge("El ebano", "Canalete", 11),
                new Edge("Canalete", "La seba", 14),
                new Edge("La seba", "Los cedros", 25),
                new Edge("Los cedros", "Monteria", 21),
                new Edge("Monteria", "El amparo", 16),
                new Edge("Monteria", "Los garzones", 13),
                new Edge("Monteria", "El sabanal", 10),
                new Edge("El amparo", "Pueblo vaca", 28),
                new Edge("Pueblo vaca", "La ceiba", 17),
                new Edge("La ceiba", "Los morales", 16),
                new Edge("Los morales", "Tierralta", 8.2),
                new Edge("Los morales", "Valencia", 26),
                new Edge("El ebano", "San pelayo", 47),
                new Edge("San pelayo", "Carrillo", 2.8),
                new Edge("Carrillo", "Cotorra", 11),
                new Edge("Cotorra", "Mata de caña", 7.7),
                new Edge("Cotorra", "Rabolargo", 15),
                new Edge("Rabolargo", "Punta de yanez", 23),
                new Edge("Punta de yanez", "Chima", 13),
                new Edge("Punta de yanez", "Cienaga de oro", 18),
                new Edge("Juan de dios garis", "Los corrales", 4.2),
                new Edge("Los corrales", "Purisima", 4.5),
                new Edge("Purisima", "Momil", 6.2),
                new Edge("Momil", "Sabanacosta", 9.1),
                new Edge("Sabanacosta", "Tuchin", 7.6),
                new Edge("Tuchin", "Campo bello", 5.5),
                new Edge("Campo bello", "Chima", 18),
                new Edge("Tuchin", "San andres de sotavento", 7.4),
                new Edge("San andres de sotavento", "Cacaotal", 10),
                new Edge("Cacaotal", "Chinu", 8.5),
                new Edge("Chinu", "Tierra grata", 7.7),
                new Edge("Tierra grata", "Sahagun", 16),
                new Edge("Sahagun", "La mesa", 8.7),
                new Edge("La mesa", "Cienaga de oro", 16),
                new Edge("Cienaga de oro", "Berastegui", 12),
                new Edge("Berastegui", "Rabolargo", 13),
                new Edge("San pelayo", "Pelayito", 3.4),
                new Edge("Pelayito", "Cerete", 6.7),
                new Edge("Cerete", "Los garzones", 10),
                new Edge("Cerete", "Cabuya", 14),
                new Edge("Cerete", "Berastegui", 11),
                new Edge("Cabuya", "San carlos", 5.4),
                new Edge("San carlos", "El sabanal", 14),
                new Edge("San carlos", "El amparo", 22),
                new Edge("San carlos", "Patio bonito", 24),
                new Edge("El amparo", "Patio bonito", 6.7),
                new Edge("Patio bonito", "Planeta rica", 31),
                new Edge("Planeta rica", "Pueblo nuevo", 15),
                new Edge("Pueblo nuevo", "La ye", 41),
                new Edge("La ye", "Sahagun", 18),
                new Edge("La ye", "Cienaga de oro", 17),
                new Edge("Planeta rica", "Plaza bonita", 15),
                new Edge("Plaza bonita", "Buenavista", 9.3),
                new Edge("Buenavista", "Rusia", 18),
                new Edge("Rusia", "Puerto cordoba", 5.3),
                new Edge("Puerto cordoba", "La apartada", 7.9),
                new Edge("La apartada", "Palotal", 27),
                new Edge("Palotal", "Las delicias", 5.3),
                new Edge("Las delicias", "Ayapel", 8.9),
                new Edge("La apartada", "La balsa", 6.9),
                new Edge("La balsa", "Montelibano", 8.8),
                new Edge("Montelibano", "Buenos aires", 35),
                new Edge("Buenos aires", "Puerto libertador", 3.9),
                new Edge("Buenos aires", "San jose de ure", 40)

        );

        Graph graph = new Graph(edges);
        Graph.printGraph(graph);

        PathResult result = Dijkstra.dijkstra("Lorica", "Buenavista", graph.getAdjList());

        if (result != null) { // Check if a result was found
            System.out.println("Distancia: " + result.distance);
            System.out.println("Camino: " + result.path);
        } else {
            System.out.println("No se encontró un camino entre A y B.");
        }

    }
}
