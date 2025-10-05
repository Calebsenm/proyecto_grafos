-- =====================================================
-- MIGRACIÓN 1: Tabla de nodos (ubicaciones)
-- =====================================================
CREATE TABLE IF NOT EXISTS nodos (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL UNIQUE,
    latitud REAL NOT NULL,
    longitud REAL NOT NULL,
    tipo TEXT DEFAULT 'ciudad',
    descripcion TEXT,
    activo BOOLEAN DEFAULT 1,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);