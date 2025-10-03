-- =====================================================
-- SCRIPT PARA RESETEAR LA BASE DE DATOS
-- =====================================================

-- Eliminar base de datos si existe
DROP DATABASE IF EXISTS grafos_colombia;

-- Crear base de datos con charset UTF-8
CREATE DATABASE grafos_colombia 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;
USE grafos_colombia;

-- =====================================================
-- TABLA DE NODOS (LOCACIONES)
-- =====================================================
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
-- INSERTAR CONFIGURACIONES POR DEFECTO
-- =====================================================
CREATE TABLE configuraciones (
    id INT AUTO_INCREMENT PRIMARY KEY,
    clave VARCHAR(100) NOT NULL UNIQUE,
    valor TEXT NOT NULL,
    descripcion TEXT,
    fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

INSERT INTO configuraciones (clave, valor, descripcion) VALUES
('algoritmo_por_defecto', 'dijkstra', 'Algoritmo de cálculo de rutas por defecto'),
('unidad_distancia', 'km', 'Unidad de medida para distancias'),
('velocidad_promedio', '60', 'Velocidad promedio en km/h para cálculo de tiempo'),
('max_nodos_visualizacion', '1000', 'Máximo número de nodos para visualización'),
('zoom_inicial', '1.0', 'Nivel de zoom inicial del mapa'),
('centro_latitud', '8.7500', 'Latitud del centro del mapa por defecto'),
('centro_longitud', '-75.8800', 'Longitud del centro del mapa por defecto');

-- =====================================================
-- COMENTARIOS Y DOCUMENTACIÓN
-- =====================================================
ALTER TABLE nodos COMMENT = 'Tabla principal de nodos/locaciones del grafo';
ALTER TABLE aristas COMMENT = 'Tabla de conexiones/aristas entre nodos';
ALTER TABLE configuraciones COMMENT = 'Configuraciones del sistema';

