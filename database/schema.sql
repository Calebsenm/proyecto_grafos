-- =====================================================
-- SCHEMA DE BASE DE DATOS PARA PROYECTO DE GRAFOS
-- Sistema de gestión de nodos y aristas para Dijkstra
-- =====================================================

-- Crear base de datos con charset UTF-8
CREATE DATABASE IF NOT EXISTS grafos_colombia 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;
USE grafos_colombia;

-- Deshabilitar verificación de foreign keys temporalmente
SET FOREIGN_KEY_CHECKS = 0;

-- =====================================================
-- TABLA DE NODOS (LOCACIONES)
-- =====================================================
DROP TABLE IF EXISTS nodos;
CREATE TABLE nodos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL UNIQUE,
    latitud DECIMAL(10, 8) NOT NULL,
    longitud DECIMAL(11, 8) NOT NULL,
    tipo VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'ciudad',
    descripcion TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_nombre (nombre),
    INDEX idx_coordenadas (latitud, longitud),
    INDEX idx_tipo (tipo),
    INDEX idx_activo (activo)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- =====================================================
-- TABLA DE ARISTAS (CONEXIONES)
-- =====================================================
DROP TABLE IF EXISTS aristas;
CREATE TABLE aristas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nodo_origen_id INT NOT NULL,
    nodo_destino_id INT NOT NULL,
    distancia DECIMAL(10, 2) NOT NULL,
    tipo_ruta VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'carretera',
    tiempo_estimado INT DEFAULT NULL, -- en minutos
    costo DECIMAL(10, 2) DEFAULT NULL,
    descripcion TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (nodo_origen_id) REFERENCES nodos(id) ON DELETE CASCADE,
    FOREIGN KEY (nodo_destino_id) REFERENCES nodos(id) ON DELETE CASCADE,
    
    UNIQUE KEY unique_connection (nodo_origen_id, nodo_destino_id),
    INDEX idx_origen (nodo_origen_id),
    INDEX idx_destino (nodo_destino_id),
    INDEX idx_distancia (distancia),
    INDEX idx_tipo_ruta (tipo_ruta),
    INDEX idx_activo (activo),
    
    -- Constraint para evitar auto-conexiones
    CONSTRAINT chk_different_nodes CHECK (nodo_origen_id != nodo_destino_id),
    
    -- Constraint para distancia positiva
    CONSTRAINT chk_positive_distance CHECK (distancia > 0)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- =====================================================
-- TABLA DE RUTAS CALCULADAS (HISTORIAL)
-- =====================================================
DROP TABLE IF EXISTS rutas_calculadas;
CREATE TABLE rutas_calculadas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nodo_origen_id INT NOT NULL,
    nodo_destino_id INT NOT NULL,
    distancia_total DECIMAL(10, 2) NOT NULL,
    tiempo_total INT DEFAULT NULL,
    costo_total DECIMAL(10, 2) DEFAULT NULL,
    fecha_calculo TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (nodo_origen_id) REFERENCES nodos(id) ON DELETE CASCADE,
    FOREIGN KEY (nodo_destino_id) REFERENCES nodos(id) ON DELETE CASCADE,
    
    INDEX idx_origen_destino (nodo_origen_id, nodo_destino_id),
    INDEX idx_fecha_calculo (fecha_calculo),
    INDEX idx_distancia_total (distancia_total)
);

-- =====================================================
-- TABLA DE SECUENCIA DE RUTAS (DETALLE DE RUTAS)
-- =====================================================
DROP TABLE IF EXISTS secuencia_rutas;
CREATE TABLE secuencia_rutas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    ruta_id INT NOT NULL,
    orden INT NOT NULL,
    nodo_id INT NOT NULL,
    
    FOREIGN KEY (ruta_id) REFERENCES rutas_calculadas(id) ON DELETE CASCADE,
    FOREIGN KEY (nodo_id) REFERENCES nodos(id) ON DELETE CASCADE,
    
    INDEX idx_ruta_orden (ruta_id, orden),
    INDEX idx_nodo (nodo_id)
);

-- =====================================================
-- TABLA DE CONFIGURACIONES DEL SISTEMA
-- =====================================================
DROP TABLE IF EXISTS configuraciones;
CREATE TABLE configuraciones (
    id INT AUTO_INCREMENT PRIMARY KEY,
    clave VARCHAR(100) NOT NULL UNIQUE,
    valor TEXT NOT NULL,
    descripcion TEXT,
    fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- =====================================================
-- INSERTAR CONFIGURACIONES POR DEFECTO
-- =====================================================
INSERT INTO configuraciones (clave, valor, descripcion) VALUES
('algoritmo_por_defecto', 'dijkstra', 'Algoritmo de cálculo de rutas por defecto'),
('unidad_distancia', 'km', 'Unidad de medida para distancias'),
('velocidad_promedio', '60', 'Velocidad promedio en km/h para cálculo de tiempo'),
('max_nodos_visualizacion', '1000', 'Máximo número de nodos para visualización'),
('zoom_inicial', '1.0', 'Nivel de zoom inicial del mapa'),
('centro_latitud', '8.7500', 'Latitud del centro del mapa por defecto'),
('centro_longitud', '-75.8800', 'Longitud del centro del mapa por defecto');

-- =====================================================
-- VISTAS PARA FACILITAR CONSULTAS
-- =====================================================

-- Vista de nodos activos con información completa
CREATE VIEW vista_nodos_activos AS
SELECT 
    n.id,
    n.nombre,
    n.latitud,
    n.longitud,
    n.tipo,
    n.descripcion,
    n.fecha_creacion,
    COUNT(DISTINCT a1.id) as conexiones_salientes,
    COUNT(DISTINCT a2.id) as conexiones_entrantes
FROM nodos n
LEFT JOIN aristas a1 ON n.id = a1.nodo_origen_id AND a1.activo = TRUE
LEFT JOIN aristas a2 ON n.id = a2.nodo_destino_id AND a2.activo = TRUE
WHERE n.activo = TRUE
GROUP BY n.id, n.nombre, n.latitud, n.longitud, n.tipo, n.descripcion, n.fecha_creacion;

-- Vista de aristas activas con información de nodos
CREATE VIEW vista_aristas_activas AS
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
WHERE a.activo = TRUE AND n1.activo = TRUE AND n2.activo = TRUE;

-- Vista de estadísticas del sistema
CREATE VIEW vista_estadisticas AS
SELECT 
    (SELECT COUNT(*) FROM nodos WHERE activo = TRUE) as total_nodos,
    (SELECT COUNT(*) FROM aristas WHERE activo = TRUE) as total_aristas,
    (SELECT COUNT(*) FROM rutas_calculadas) as total_rutas_calculadas,
    (SELECT AVG(distancia) FROM aristas WHERE activo = TRUE) as distancia_promedio,
    (SELECT MIN(distancia) FROM aristas WHERE activo = TRUE) as distancia_minima,
    (SELECT MAX(distancia) FROM aristas WHERE activo = TRUE) as distancia_maxima;

-- =====================================================
-- PROCEDIMIENTOS ALMACENADOS ÚTILES
-- =====================================================

-- Procedimiento para limpiar datos antiguos
DELIMITER //
CREATE PROCEDURE LimpiarDatosAntiguos(IN dias INT)
BEGIN
    -- Eliminar rutas calculadas más antiguas que el número de días especificado
    DELETE FROM rutas_calculadas 
    WHERE fecha_calculo < DATE_SUB(NOW(), INTERVAL dias DAY);
    
    SELECT ROW_COUNT() as registros_eliminados;
END //
DELIMITER ;

-- Procedimiento para obtener la ruta más corta entre dos nodos
DELIMITER //
CREATE PROCEDURE ObtenerRutaMasCorta(IN origen_nombre VARCHAR(100), IN destino_nombre VARCHAR(100))
BEGIN
    DECLARE origen_id INT;
    DECLARE destino_id INT;
    
    -- Obtener IDs de los nodos
    SELECT id INTO origen_id FROM nodos WHERE nombre = origen_nombre AND activo = TRUE;
    SELECT id INTO destino_id FROM nodos WHERE nombre = destino_nombre AND activo = TRUE;
    
    -- Verificar que ambos nodos existen
    IF origen_id IS NULL OR destino_id IS NULL THEN
        SELECT 'ERROR: Uno o ambos nodos no existen o están inactivos' as mensaje;
    ELSE
        -- Aquí se implementaría el algoritmo de Dijkstra en SQL
        -- Por ahora, retornamos información básica
        SELECT 
            origen_id,
            destino_id,
            origen_nombre,
            destino_nombre,
            'Implementar algoritmo Dijkstra en SQL' as estado;
    END IF;
END //
DELIMITER ;

-- =====================================================
-- TRIGGERS PARA AUDITORÍA
-- =====================================================

-- Trigger para actualizar fecha de modificación en nodos
DELIMITER //
CREATE TRIGGER tr_nodos_update 
BEFORE UPDATE ON nodos
FOR EACH ROW
BEGIN
    SET NEW.fecha_modificacion = CURRENT_TIMESTAMP;
END //
DELIMITER ;

-- Trigger para actualizar fecha de modificación en aristas
DELIMITER //
CREATE TRIGGER tr_aristas_update 
BEFORE UPDATE ON aristas
FOR EACH ROW
BEGIN
    SET NEW.fecha_modificacion = CURRENT_TIMESTAMP;
END //
DELIMITER ;

-- =====================================================
-- ÍNDICES ADICIONALES PARA OPTIMIZACIÓN
-- =====================================================

-- Índice compuesto para búsquedas frecuentes
CREATE INDEX idx_nodo_nombre_activo ON nodos(nombre, activo);
CREATE INDEX idx_arista_activa_distancia ON aristas(activo, distancia);

-- =====================================================
-- COMENTARIOS Y DOCUMENTACIÓN
-- =====================================================

-- Comentarios en tablas
ALTER TABLE nodos COMMENT = 'Tabla principal de nodos/locaciones del grafo';
ALTER TABLE aristas COMMENT = 'Tabla de conexiones/aristas entre nodos';
ALTER TABLE rutas_calculadas COMMENT = 'Historial de rutas calculadas por el algoritmo';
ALTER TABLE secuencia_rutas COMMENT = 'Secuencia detallada de nodos en cada ruta';
ALTER TABLE configuraciones COMMENT = 'Configuraciones del sistema';

-- Re-habilitar verificación de foreign keys
SET FOREIGN_KEY_CHECKS = 1;

-- =====================================================
-- FIN DEL SCHEMA
-- =====================================================
