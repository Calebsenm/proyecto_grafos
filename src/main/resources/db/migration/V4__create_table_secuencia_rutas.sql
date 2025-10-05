-- =====================================================
-- MIGRACIÓN 4: Tabla de secuencia de rutas (detalle)
-- =====================================================
CREATE TABLE IF NOT EXISTS secuencia_rutas (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    ruta_id INTEGER NOT NULL,
    orden INTEGER NOT NULL,
    nodo_id INTEGER NOT NULL,
    
    FOREIGN KEY (ruta_id) REFERENCES rutas_calculadas(id) ON DELETE CASCADE,
    FOREIGN KEY (nodo_id) REFERENCES nodos(id),
    
    UNIQUE (ruta_id, orden)
);