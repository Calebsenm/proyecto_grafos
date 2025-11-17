package com.grafos_colombia.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class EccentricityData {
    private final StringProperty nodo;
    private final StringProperty excentricidad;
    private final StringProperty esCentro;
    private final boolean isCentro;

    public EccentricityData(String nodo, double excentricidad, boolean esCentro) {
        this.nodo = new SimpleStringProperty(nodo);
        this.excentricidad = new SimpleStringProperty(String.format("%.2f km", excentricidad));
        this.esCentro = new SimpleStringProperty(esCentro ? "⭐ Sí" : "No");
        this.isCentro = esCentro;
    }

    public StringProperty nodoProperty() {
        return nodo;
    }

    public StringProperty excentricidadProperty() {
        return excentricidad;
    }

    public StringProperty esCentroProperty() {
        return esCentro;
    }

    public String getNodo() {
        return nodo.get();
    }

    public String getExcentricidad() {
        return excentricidad.get();
    }

    public String getEsCentro() {
        return esCentro.get();
    }

    public boolean isCentro() {
        return isCentro;
    }
}

