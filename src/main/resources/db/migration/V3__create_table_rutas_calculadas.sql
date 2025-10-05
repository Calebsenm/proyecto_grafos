-- =====================================================
-- MIGRACIÓN 3: Tabla de rutas calculadas (historial)
-- =====================================================
CREATE TABLE IF NOT EXISTS rutas_calculadas (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nodo_origen_id INTEGER NOT NULL,
    nodo_destino_id INTEGER NOT NULL,
    distancia_total REAL NOT NULL,
    tiempo_total INTEGER DEFAULT NULL,
    costo_total REAL DEFAULT NULL,
    fecha_calculo TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (nodo_origen_id) REFERENCES nodos(id),
    FOREIGN KEY (nodo_destino_id) REFERENCES nodos(id),
    
    CHECK (distancia_total > 0)
);