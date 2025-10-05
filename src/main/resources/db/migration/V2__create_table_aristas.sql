-- =====================================================
-- MIGRACIÓN 2: Tabla de aristas (conexiones entre nodos)
-- =====================================================
CREATE TABLE IF NOT EXISTS aristas (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nodo_origen_id INTEGER NOT NULL,
    nodo_destino_id INTEGER NOT NULL,
    distancia REAL NOT NULL,
    tipo_ruta TEXT DEFAULT 'carretera',
    tiempo_estimado INTEGER DEFAULT NULL,
    costo REAL DEFAULT NULL,
    descripcion TEXT,
    activo BOOLEAN DEFAULT 1,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (nodo_origen_id) REFERENCES nodos(id) ON DELETE CASCADE,
    FOREIGN KEY (nodo_destino_id) REFERENCES nodos(id) ON DELETE CASCADE,
    
    UNIQUE (nodo_origen_id, nodo_destino_id),
    
    CHECK (nodo_origen_id != nodo_destino_id),
    CHECK (distancia > 0),
    CHECK (tiempo_estimado IS NULL OR tiempo_estimado > 0),
    CHECK (costo IS NULL OR costo >= 0)
);