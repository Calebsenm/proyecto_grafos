-- =====================================================
-- MIGRACIÓN 7: Índices para optimización
-- =====================================================

-- Índices para tabla NODOS
CREATE INDEX IF NOT EXISTS idx_nodos_nombre ON nodos(nombre);
CREATE INDEX IF NOT EXISTS idx_nodos_activo ON nodos(activo);
CREATE INDEX IF NOT EXISTS idx_nodos_tipo ON nodos(tipo);
CREATE INDEX IF NOT EXISTS idx_nodos_coordenadas ON nodos(latitud, longitud);
CREATE INDEX IF NOT EXISTS idx_nodos_fecha_creacion ON nodos(fecha_creacion);

-- Índices para tabla ARISTAS
CREATE INDEX IF NOT EXISTS idx_aristas_origen ON aristas(nodo_origen_id);
CREATE INDEX IF NOT EXISTS idx_aristas_destino ON aristas(nodo_destino_id);
CREATE INDEX IF NOT EXISTS idx_aristas_activo ON aristas(activo);
CREATE INDEX IF NOT EXISTS idx_aristas_distancia ON aristas(distancia);
CREATE INDEX IF NOT EXISTS idx_aristas_tipo_ruta ON aristas(tipo_ruta);
CREATE INDEX IF NOT EXISTS idx_aristas_fecha_creacion ON aristas(fecha_creacion);

-- Índices para tabla RUTAS_CALCULADAS
CREATE INDEX IF NOT EXISTS idx_rutas_origen_destino ON rutas_calculadas(nodo_origen_id, nodo_destino_id);
CREATE INDEX IF NOT EXISTS idx_rutas_fecha_calculo ON rutas_calculadas(fecha_calculo);
CREATE INDEX IF NOT EXISTS idx_rutas_distancia_total ON rutas_calculadas(distancia_total);

-- Índices para tabla SECUENCIA_RUTAS
CREATE INDEX IF NOT EXISTS idx_secuencia_ruta_orden ON secuencia_rutas(ruta_id, orden);
CREATE INDEX IF NOT EXISTS idx_secuencia_nodo ON secuencia_rutas(nodo_id);

-- Índices para tabla CONFIGURACIONES
CREATE INDEX IF NOT EXISTS idx_config_clave ON configuraciones(clave);