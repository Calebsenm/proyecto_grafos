-- =====================================================
-- MIGRACIÓN 8: Vistas para facilitar consultas
-- =====================================================

-- Vista de nodos activos con información completa
CREATE VIEW IF NOT EXISTS vista_nodos_activos AS
SELECT 
    n.id,
    n.nombre,
    n.latitud,
    n.longitud,
    n.tipo,
    n.descripcion,
    n.fecha_creacion,
    (SELECT COUNT(*) FROM aristas a1 WHERE n.id = a1.nodo_origen_id AND a1.activo = 1) as conexiones_salientes,
    (SELECT COUNT(*) FROM aristas a2 WHERE n.id = a2.nodo_destino_id AND a2.activo = 1) as conexiones_entrantes
FROM nodos n
WHERE n.activo = 1;

-- Vista de aristas activas con información de nodos
CREATE VIEW IF NOT EXISTS vista_aristas_activas AS
SELECT 
    a.id,
    a.nodo_origen_id,
    n1.nombre as origen_nombre,
    n1.latitud as origen_latitud,
    n1.longitud as origen_longitud,
    a.nodo_destino_id,
    n2.nombre as destino_nombre,
    n2.latitud as destino_latitud,
    n2.longitud as destino_longitud,
    a.distancia,
    a.tipo_ruta,
    a.tiempo_estimado,
    a.costo,
    a.descripcion,
    a.fecha_creacion
FROM aristas a
JOIN nodos n1 ON a.nodo_origen_id = n1.id
JOIN nodos n2 ON a.nodo_destino_id = n2.id
WHERE a.activo = 1 AND n1.activo = 1 AND n2.activo = 1;

-- Vista de estadísticas del sistema
CREATE VIEW IF NOT EXISTS vista_estadisticas AS
SELECT 
    (SELECT COUNT(*) FROM nodos WHERE activo = 1) as total_nodos,
    (SELECT COUNT(*) FROM aristas WHERE activo = 1) as total_aristas,
    (SELECT COUNT(*) FROM rutas_calculadas) as total_rutas_calculadas,
    (SELECT AVG(distancia) FROM aristas WHERE activo = 1) as distancia_promedio,
    (SELECT MIN(distancia) FROM aristas WHERE activo = 1) as distancia_minima,
    (SELECT MAX(distancia) FROM aristas WHERE activo = 1) as distancia_maxima;