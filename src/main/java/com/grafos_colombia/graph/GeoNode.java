package com.grafos_colombia.graph;

/**
 * Clase que representa un nodo con coordenadas geográficas reales
 */
public class GeoNode {

    private String name;
    private double latitude;
    private double longitude;

    public GeoNode(String name, double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * Calcula la distancia en kilómetros entre este nodo y otro
     *
     * @param other
     * @return
     */
    public double distanceTo(GeoNode other) {
        return calculateDistance(this.latitude, this.longitude,
                other.latitude, other.longitude);
    }

    /**
     * Fórmula de Haversine para calcular distancia entre dos puntos geográficos
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radio de la Tierra en kilómetros

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }

    @Override
    public String toString() {
        return name + " (" + latitude + ", " + longitude + ")";
    }
}
