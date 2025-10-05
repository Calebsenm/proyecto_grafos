-- =====================================================
-- MIGRACIÓN 6: Insertar configuraciones por defecto
-- =====================================================
INSERT OR IGNORE INTO configuraciones (clave, valor, descripcion) VALUES
('algoritmo_por_defecto', 'dijkstra', 'Algoritmo de cálculo de rutas por defecto'),
('unidad_distancia', 'km', 'Unidad de medida para distancias'),
('velocidad_promedio', '60', 'Velocidad promedio en km/h para cálculo de tiempo'),
('max_nodos_visualizacion', '1000', 'Máximo número de nodos para visualización'),
('zoom_inicial', '6.0', 'Nivel de zoom inicial del mapa'),
('centro_latitud', '4.570868', 'Latitud del centro del mapa por defecto (Colombia)'),
('centro_longitud', '-74.297333', 'Longitud del centro del mapa por defecto (Colombia)');