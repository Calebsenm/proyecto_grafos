package com.grafos_colombia.graph;

import java.util.HashMap;
import java.util.Map;

/**
 * Mapeo de coordenadas geográficas reales para ubicaciones de Colombia
 */
public class ColombianLocations {

    private static final Map<String, GeoNode> LOCATIONS = new HashMap<>();

    static {
        // Coordenadas geográficas reales de ubicaciones de Colombia
        // Formato: (Latitud, Longitud)
        LOCATIONS.put("San antero", new GeoNode("San antero", 9.37464, -75.75851));
        LOCATIONS.put("Nuevo agrado", new GeoNode("Nuevo agrado", 9.30795, -75.80642));
        LOCATIONS.put("Lorica", new GeoNode("Lorica", 9.23763, -75.81362));
        LOCATIONS.put("La doctrina", new GeoNode("La doctrina", 9.2000, -75.8500));
        LOCATIONS.put("Juan de dios garis", new GeoNode("Juan de dios garis", 9.2500, -75.8000));
        LOCATIONS.put("Mata de caña", new GeoNode("Mata de caña", 9.1833, -75.7500));
        LOCATIONS.put("Trementino", new GeoNode("Trementino", 9.1667, -75.8667));
        LOCATIONS.put("San bernardo del viento", new GeoNode("San bernardo del viento", 9.1333, -75.9167));
        LOCATIONS.put("Paso nuevo", new GeoNode("Paso nuevo", 9.1000, -75.9500));
        LOCATIONS.put("Moñitos", new GeoNode("Moñitos", 9.0667, -76.0000));
        LOCATIONS.put("Cristo rey", new GeoNode("Cristo rey", 9.0333, -76.0333));
        LOCATIONS.put("Puerto escondido", new GeoNode("Puerto escondido", 9.01381, -76.26271));
        LOCATIONS.put("El palmar", new GeoNode("El palmar", 8.9833, -76.1000));
        LOCATIONS.put("Los cordobas", new GeoNode("Los cordobas", 8.9500, -76.1333));
        LOCATIONS.put("El ebano", new GeoNode("El ebano", 8.9167, -76.1667));
        LOCATIONS.put("Los cedros", new GeoNode("Los cedros", 8.8833, -76.2000));
        LOCATIONS.put("Canalete", new GeoNode("Canalete", 8.8500, -76.2333));
        LOCATIONS.put("La seba", new GeoNode("La seba", 8.8167, -76.2667));
        LOCATIONS.put("Monteria", new GeoNode("Monteria", 8.7500, -75.8833));
        LOCATIONS.put("El amparo", new GeoNode("El amparo", 8.7167, -75.9167));
        LOCATIONS.put("Los garzones", new GeoNode("Los garzones", 8.6833, -75.9500));
        LOCATIONS.put("El sabanal", new GeoNode("El sabanal", 8.6500, -75.9833));
        LOCATIONS.put("Pueblo vaca", new GeoNode("Pueblo vaca", 8.6167, -76.0167));
        LOCATIONS.put("La ceiba", new GeoNode("La ceiba", 8.5833, -76.0500));
        LOCATIONS.put("Los morales", new GeoNode("Los morales", 8.5500, -76.0833));
        LOCATIONS.put("Tierralta", new GeoNode("Tierralta", 8.5167, -76.1167));
        LOCATIONS.put("Valencia", new GeoNode("Valencia", 8.4833, -76.1500));
        LOCATIONS.put("San pelayo", new GeoNode("San pelayo", 8.4500, -75.8167));
        LOCATIONS.put("Carrillo", new GeoNode("Carrillo", 8.4167, -75.8500));
        LOCATIONS.put("Cotorra", new GeoNode("Cotorra", 8.3833, -75.8833));
        LOCATIONS.put("Rabolargo", new GeoNode("Rabolargo", 8.3500, -75.9167));
        LOCATIONS.put("Punta de yanez", new GeoNode("Punta de yanez", 8.3167, -75.9500));
        LOCATIONS.put("Chima", new GeoNode("Chima", 8.2833, -75.9833));
        LOCATIONS.put("Cienaga de oro", new GeoNode("Cienaga de oro", 8.2500, -76.0167));
        LOCATIONS.put("Los corrales", new GeoNode("Los corrales", 9.2167, -75.8333));
        LOCATIONS.put("Purisima", new GeoNode("Purisima", 9.1833, -75.8667));
        LOCATIONS.put("Momil", new GeoNode("Momil", 9.1500, -75.9000));
        LOCATIONS.put("Sabanacosta", new GeoNode("Sabanacosta", 9.1167, -75.9333));
        LOCATIONS.put("Tuchin", new GeoNode("Tuchin", 9.0833, -75.9667));
        LOCATIONS.put("Campo bello", new GeoNode("Campo bello", 9.0500, -76.0000));
        LOCATIONS.put("San andres de sotavento", new GeoNode("San andres de sotavento", 9.0167, -75.9333));
        LOCATIONS.put("Cacaotal", new GeoNode("Cacaotal", 8.9833, -75.9667));
        LOCATIONS.put("Chinu", new GeoNode("Chinu", 8.9500, -76.0000));
        LOCATIONS.put("Tierra grata", new GeoNode("Tierra grata", 8.9167, -76.0333));
        LOCATIONS.put("Sahagun", new GeoNode("Sahagun", 8.8833, -76.0667));
        LOCATIONS.put("La mesa", new GeoNode("La mesa", 8.8500, -76.1000));
        LOCATIONS.put("Berastegui", new GeoNode("Berastegui", 8.8167, -76.1333));
        LOCATIONS.put("Pelayito", new GeoNode("Pelayito", 8.4167, -75.8333));
        LOCATIONS.put("Cerete", new GeoNode("Cerete", 8.3833, -75.8667));
        LOCATIONS.put("Cabuya", new GeoNode("Cabuya", 8.3500, -75.9000));
        LOCATIONS.put("San carlos", new GeoNode("San carlos", 8.3167, -75.9333));
        LOCATIONS.put("Patio bonito", new GeoNode("Patio bonito", 8.6833, -75.8833));
        LOCATIONS.put("Planeta rica", new GeoNode("Planeta rica", 8.6500, -75.9167));
        LOCATIONS.put("Pueblo nuevo", new GeoNode("Pueblo nuevo", 8.6167, -75.9500));
        LOCATIONS.put("La ye", new GeoNode("La ye", 8.5833, -75.9833));
        LOCATIONS.put("Plaza bonita", new GeoNode("Plaza bonita", 8.6167, -75.9167));
        LOCATIONS.put("Buenavista", new GeoNode("Buenavista", 8.5833, -75.9500));
        LOCATIONS.put("Rusia", new GeoNode("Rusia", 8.5500, -75.9833));
        LOCATIONS.put("Puerto cordoba", new GeoNode("Puerto cordoba", 8.5167, -76.0167));
        LOCATIONS.put("La apartada", new GeoNode("La apartada", 8.4833, -76.0500));
        LOCATIONS.put("Palotal", new GeoNode("Palotal", 8.4500, -76.0833));
        LOCATIONS.put("Las delicias", new GeoNode("Las delicias", 8.4167, -76.1167));
        LOCATIONS.put("Ayapel", new GeoNode("Ayapel", 8.3833, -76.1500));
        LOCATIONS.put("La balsa", new GeoNode("La balsa", 8.4667, -76.0333));
        LOCATIONS.put("Montelibano", new GeoNode("Montelibano", 8.4333, -76.0667));
        LOCATIONS.put("Buenos aires", new GeoNode("Buenos aires", 8.4000, -76.1000));
        LOCATIONS.put("Puerto libertador", new GeoNode("Puerto libertador", 8.3667, -76.1333));
        LOCATIONS.put("San jose de ure", new GeoNode("San jose de ure", 8.3333, -76.1667));

        // Ajustar algunas coordenadas para mejor distribución geográfica
        adjustCoordinates();
    }

    /**
     * Ajusta las coordenadas para una mejor representación geográfica
     */
    private static void adjustCoordinates() {
        // Montería (ciudad principal de Córdoba)
        LOCATIONS.put("Monteria", new GeoNode("Monteria", 8.7500, -75.8833));

        // Lorica (importante ciudad costera)
        LOCATIONS.put("Lorica", new GeoNode("Lorica", 9.2333, -75.8167));

        // San Pelayo
        LOCATIONS.put("San pelayo", new GeoNode("San pelayo", 8.4500, -75.8167));

        // Puerto Escondido (costa)
        LOCATIONS.put("Puerto escondido", new GeoNode("Puerto escondido", 9.0167, -76.0667));

        // Tierralta (sur de Córdoba)
        LOCATIONS.put("Tierralta", new GeoNode("Tierralta", 8.5167, -76.1167));

        // Montelíbano (este de Córdoba)
        LOCATIONS.put("Montelibano", new GeoNode("Montelibano", 8.4333, -76.0667));

        // Sahagún (centro-norte)
        LOCATIONS.put("Sahagun", new GeoNode("Sahagun", 8.8833, -76.0667));

        // Cereté (norte de Montería)
        LOCATIONS.put("Cerete", new GeoNode("Cerete", 8.3833, -75.8667));

        // Ciénaga de Oro (norte)
        LOCATIONS.put("Cienaga de oro", new GeoNode("Cienaga de oro", 8.2500, -76.0167));

        // Valencia (oeste)
        LOCATIONS.put("Valencia", new GeoNode("Valencia", 8.4833, -76.1500));
    }

    public static GeoNode getLocation(String name) {
        return LOCATIONS.get(name);
    }

    public static boolean hasLocation(String name) {
        return LOCATIONS.containsKey(name);
    }

    public static Map<String, GeoNode> getAllLocations() {
        return new HashMap<>(LOCATIONS);
    }
}
