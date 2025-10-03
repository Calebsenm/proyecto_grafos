-- =====================================================
-- ARISTAS ADICIONALES - CONEXIONES COMPLEMENTARIAS
-- Conexiones adicionales para completar el grafo
-- =====================================================

USE grafos_colombia;

-- =====================================================
-- CONEXIONES LOCALES POR DEPARTAMENTO
-- =====================================================

INSERT INTO aristas (nodo_origen_id, nodo_destino_id, distancia, tipo_ruta, tiempo_estimado, descripcion) VALUES

-- =====================================================
-- CONEXIONES EN BOLÍVAR
-- =====================================================
(36, 37, 25.0, 'carretera', 35, 'Conexión Cartagena - Turbaco'),
(36, 38, 28.0, 'carretera', 40, 'Conexión Cartagena - Turbaná'),
(36, 39, 35.0, 'carretera', 45, 'Conexión Cartagena - Arjona'),
(36, 40, 42.0, 'carretera', 55, 'Conexión Cartagena - Mahates'),
(36, 41, 55.0, 'carretera', 70, 'Conexión Cartagena - María la Baja'),
(36, 42, 48.0, 'carretera', 62, 'Conexión Cartagena - San Pablo'),
(36, 43, 32.0, 'carretera', 42, 'Conexión Cartagena - Villanueva (Bolívar)'),
(36, 44, 38.0, 'carretera', 50, 'Conexión Cartagena - Calamar'),
(36, 45, 35.0, 'carretera', 45, 'Conexión Cartagena - Soplaviento'),
(36, 46, 42.0, 'carretera', 55, 'Conexión Cartagena - Cordoba'),
(36, 47, 28.0, 'carretera', 37, 'Conexión Cartagena - Clemencia'),
(36, 48, 32.0, 'carretera', 42, 'Conexión Cartagena - Santa Rosa'),
(36, 49, 85.0, 'carretera', 110, 'Conexión Cartagena - Santa Rosa del Sur'),
(36, 50, 95.0, 'carretera', 125, 'Conexión Cartagena - Morales'),
(36, 51, 88.0, 'carretera', 115, 'Conexión Cartagena - Simití'),
(36, 52, 92.0, 'carretera', 120, 'Conexión Cartagena - Río Viejo'),
(36, 53, 98.0, 'carretera', 128, 'Conexión Cartagena - Tiquisio'),
(36, 54, 85.0, 'carretera', 110, 'Conexión Cartagena - Regidor'),
(36, 55, 78.0, 'carretera', 100, 'Conexión Cartagena - Arenal'),
(36, 56, 82.0, 'carretera', 105, 'Conexión Cartagena - San Martín de Loba'),
(36, 57, 88.0, 'carretera', 115, 'Conexión Cartagena - Barranco de Loba'),
(36, 58, 85.0, 'carretera', 110, 'Conexión Cartagena - Hatillo de Loba'),
(36, 59, 92.0, 'carretera', 120, 'Conexión Cartagena - Altos del Rosario'),
(36, 60, 95.0, 'carretera', 125, 'Conexión Cartagena - El Peñón (Bolívar)'),
(36, 61, 88.0, 'carretera', 115, 'Conexión Cartagena - Montecristo'),
(36, 62, 82.0, 'carretera', 105, 'Conexión Cartagena - Pinillos'),
(36, 63, 72.0, 'carretera', 95, 'Conexión Cartagena - Talaigua Nuevo'),
(36, 64, 75.0, 'carretera', 95, 'Conexión Cartagena - Mompos'),
(36, 65, 78.0, 'carretera', 100, 'Conexión Cartagena - San Fernando'),
(36, 66, 85.0, 'carretera', 110, 'Conexión Cartagena - Chimichagua'),
(36, 67, 92.0, 'carretera', 120, 'Conexión Cartagena - Tamalameque'),
(36, 68, 95.0, 'carretera', 125, 'Conexión Cartagena - La Gloria'),
(36, 69, 98.0, 'carretera', 128, 'Conexión Cartagena - Pelaya'),
(36, 70, 88.0, 'carretera', 115, 'Conexión Cartagena - Curumaní'),
(36, 71, 82.0, 'carretera', 105, 'Conexión Cartagena - El Banco'),
(36, 72, 85.0, 'carretera', 110, 'Conexión Cartagena - Guamal'),
(36, 73, 95.0, 'carretera', 125, 'Conexión Cartagena - Ciénaga (Bolivar)'),

-- Conexiones locales en Bolívar
(37, 38, 8.0, 'carretera_local', 12, 'Conexión Turbaco - Turbaná'),
(37, 39, 15.0, 'carretera_local', 20, 'Conexión Turbaco - Arjona'),
(37, 40, 22.0, 'carretera', 30, 'Conexión Turbaco - Mahates'),
(37, 43, 12.0, 'carretera_local', 18, 'Conexión Turbaco - Villanueva (Bolívar)'),
(37, 44, 18.0, 'carretera_local', 25, 'Conexión Turbaco - Calamar'),
(37, 45, 15.0, 'carretera_local', 20, 'Conexión Turbaco - Soplaviento'),
(37, 46, 22.0, 'carretera', 30, 'Conexión Turbaco - Cordoba'),
(37, 47, 8.0, 'carretera_local', 12, 'Conexión Turbaco - Clemencia'),
(37, 48, 12.0, 'carretera_local', 18, 'Conexión Turbaco - Santa Rosa'),

(38, 39, 12.0, 'carretera_local', 18, 'Conexión Turbaná - Arjona'),
(38, 40, 18.0, 'carretera_local', 25, 'Conexión Turbaná - Mahates'),
(38, 41, 32.0, 'carretera', 42, 'Conexión Turbaná - María la Baja'),
(38, 42, 28.0, 'carretera', 37, 'Conexión Turbaná - San Pablo'),
(38, 43, 15.0, 'carretera_local', 20, 'Conexión Turbaná - Villanueva (Bolívar)'),
(38, 44, 22.0, 'carretera', 30, 'Conexión Turbaná - Calamar'),
(38, 45, 18.0, 'carretera_local', 25, 'Conexión Turbaná - Soplaviento'),
(38, 46, 25.0, 'carretera', 35, 'Conexión Turbaná - Cordoba'),
(38, 47, 12.0, 'carretera_local', 18, 'Conexión Turbaná - Clemencia'),
(38, 48, 15.0, 'carretera_local', 20, 'Conexión Turbaná - Santa Rosa'),

(39, 40, 8.0, 'carretera_local', 12, 'Conexión Arjona - Mahates'),
(39, 41, 22.0, 'carretera', 30, 'Conexión Arjona - María la Baja'),
(39, 42, 18.0, 'carretera_local', 25, 'Conexión Arjona - San Pablo'),
(39, 43, 12.0, 'carretera_local', 18, 'Conexión Arjona - Villanueva (Bolívar)'),
(39, 44, 15.0, 'carretera_local', 20, 'Conexión Arjona - Calamar'),
(39, 45, 12.0, 'carretera_local', 18, 'Conexión Arjona - Soplaviento'),
(39, 46, 15.0, 'carretera_local', 20, 'Conexión Arjona - Cordoba'),
(39, 47, 18.0, 'carretera_local', 25, 'Conexión Arjona - Clemencia'),
(39, 48, 15.0, 'carretera_local', 20, 'Conexión Arjona - Santa Rosa'),

(40, 41, 18.0, 'carretera_local', 25, 'Conexión Mahates - María la Baja'),
(40, 42, 15.0, 'carretera_local', 20, 'Conexión Mahates - San Pablo'),
(40, 43, 22.0, 'carretera', 30, 'Conexión Mahates - Villanueva (Bolívar)'),
(40, 44, 25.0, 'carretera', 35, 'Conexión Mahates - Calamar'),
(40, 45, 22.0, 'carretera', 30, 'Conexión Mahates - Soplaviento'),
(40, 46, 28.0, 'carretera', 37, 'Conexión Mahates - Cordoba'),
(40, 47, 25.0, 'carretera', 35, 'Conexión Mahates - Clemencia'),
(40, 48, 22.0, 'carretera', 30, 'Conexión Mahates - Santa Rosa'),

(41, 42, 8.0, 'carretera_local', 12, 'Conexión María la Baja - San Pablo'),
(41, 43, 35.0, 'carretera', 45, 'Conexión María la Baja - Villanueva (Bolívar)'),
(41, 44, 38.0, 'carretera', 50, 'Conexión María la Baja - Calamar'),
(41, 45, 35.0, 'carretera', 45, 'Conexión María la Baja - Soplaviento'),
(41, 46, 42.0, 'carretera', 55, 'Conexión María la Baja - Cordoba'),
(41, 47, 38.0, 'carretera', 50, 'Conexión María la Baja - Clemencia'),
(41, 48, 35.0, 'carretera', 45, 'Conexión María la Baja - Santa Rosa'),

(42, 43, 28.0, 'carretera', 37, 'Conexión San Pablo - Villanueva (Bolívar)'),
(42, 44, 32.0, 'carretera', 42, 'Conexión San Pablo - Calamar'),
(42, 45, 28.0, 'carretera', 37, 'Conexión San Pablo - Soplaviento'),
(42, 46, 35.0, 'carretera', 45, 'Conexión San Pablo - Cordoba'),
(42, 47, 32.0, 'carretera', 42, 'Conexión San Pablo - Clemencia'),
(42, 48, 28.0, 'carretera', 37, 'Conexión San Pablo - Santa Rosa'),

(43, 44, 8.0, 'carretera_local', 12, 'Conexión Villanueva (Bolívar) - Calamar'),
(43, 45, 12.0, 'carretera_local', 18, 'Conexión Villanueva (Bolívar) - Soplaviento'),
(43, 46, 15.0, 'carretera_local', 20, 'Conexión Villanueva (Bolívar) - Cordoba'),
(43, 47, 12.0, 'carretera_local', 18, 'Conexión Villanueva (Bolívar) - Clemencia'),
(43, 48, 8.0, 'carretera_local', 12, 'Conexión Villanueva (Bolívar) - Santa Rosa'),

(44, 45, 8.0, 'carretera_local', 12, 'Conexión Calamar - Soplaviento'),
(44, 46, 12.0, 'carretera_local', 18, 'Conexión Calamar - Cordoba'),
(44, 47, 15.0, 'carretera_local', 20, 'Conexión Calamar - Clemencia'),
(44, 48, 12.0, 'carretera_local', 18, 'Conexión Calamar - Santa Rosa'),

(45, 46, 8.0, 'carretera_local', 12, 'Conexión Soplaviento - Cordoba'),
(45, 47, 12.0, 'carretera_local', 18, 'Conexión Soplaviento - Clemencia'),
(45, 48, 8.0, 'carretera_local', 12, 'Conexión Soplaviento - Santa Rosa'),

(46, 47, 8.0, 'carretera_local', 12, 'Conexión Cordoba - Clemencia'),
(46, 48, 8.0, 'carretera_local', 12, 'Conexión Cordoba - Santa Rosa'),

(47, 48, 8.0, 'carretera_local', 12, 'Conexión Clemencia - Santa Rosa'),

-- =====================================================
-- CONEXIONES EN SUCRE
-- =====================================================
(75, 76, 15.0, 'carretera_local', 20, 'Conexión Sincelejo - Corozal'),
(75, 77, 12.0, 'carretera_local', 18, 'Conexión Sincelejo - Morroa'),
(75, 78, 18.0, 'carretera_local', 25, 'Conexión Sincelejo - Los Palmitos'),
(75, 79, 25.0, 'carretera', 35, 'Conexión Sincelejo - Ovejas'),
(75, 80, 32.0, 'carretera', 42, 'Conexión Sincelejo - Chalán'),
(75, 81, 28.0, 'carretera', 37, 'Conexión Sincelejo - Coloso'),
(75, 82, 22.0, 'carretera', 30, 'Conexión Sincelejo - Galeras'),
(75, 83, 45.0, 'carretera', 60, 'Conexión Sincelejo - Guaranda'),
(75, 84, 38.0, 'carretera', 50, 'Conexión Sincelejo - La Unión (Sucre)'),
(75, 85, 48.0, 'carretera', 63, 'Conexión Sincelejo - Majagual'),
(75, 86, 35.0, 'carretera', 45, 'Conexión Sincelejo - Palmito'),
(75, 87, 42.0, 'carretera', 55, 'Conexión Sincelejo - San Benito Abad'),
(75, 88, 28.0, 'carretera', 37, 'Conexión Sincelejo - San Juan de Betulia'),
(75, 89, 38.0, 'carretera', 50, 'Conexión Sincelejo - San Marcos'),
(75, 90, 48.0, 'carretera', 63, 'Conexión Sincelejo - San Onofre'),
(75, 91, 45.0, 'carretera', 60, 'Conexión Sincelejo - San Pedro'),
(75, 92, 52.0, 'carretera', 68, 'Conexión Sincelejo - Santiago de Tolú'),
(75, 93, 42.0, 'carretera', 55, 'Conexión Sincelejo - Sucre (Bolívar)'),
(75, 94, 38.0, 'carretera', 50, 'Conexión Sincelejo - Tolú Viejo'),
(75, 95, 25.0, 'carretera', 35, 'Conexión Sincelejo - Buenavista (Sucre)'),
(75, 96, 35.0, 'carretera', 45, 'Conexión Sincelejo - Caimito'),
(75, 97, 22.0, 'carretera', 30, 'Conexión Sincelejo - San Luis de Sincé'),

-- Conexiones locales en Sucre
(76, 77, 8.0, 'carretera_local', 12, 'Conexión Corozal - Morroa'),
(76, 78, 12.0, 'carretera_local', 18, 'Conexión Corozal - Los Palmitos'),
(76, 79, 22.0, 'carretera', 30, 'Conexión Corozal - Ovejas'),
(76, 80, 28.0, 'carretera', 37, 'Conexión Corozal - Chalán'),
(76, 81, 25.0, 'carretera', 35, 'Conexión Corozal - Coloso'),
(76, 82, 18.0, 'carretera_local', 25, 'Conexión Corozal - Galeras'),
(76, 88, 22.0, 'carretera', 30, 'Conexión Corozal - San Juan de Betulia'),
(76, 94, 32.0, 'carretera', 42, 'Conexión Corozal - Tolú Viejo'),
(76, 95, 18.0, 'carretera_local', 25, 'Conexión Corozal - Buenavista (Sucre)'),
(76, 97, 15.0, 'carretera_local', 20, 'Conexión Corozal - San Luis de Sincé'),

(77, 78, 8.0, 'carretera_local', 12, 'Conexión Morroa - Los Palmitos'),
(77, 79, 18.0, 'carretera_local', 25, 'Conexión Morroa - Ovejas'),
(77, 80, 25.0, 'carretera', 35, 'Conexión Morroa - Chalán'),
(77, 81, 22.0, 'carretera', 30, 'Conexión Morroa - Coloso'),
(77, 82, 15.0, 'carretera_local', 20, 'Conexión Morroa - Galeras'),
(77, 88, 18.0, 'carretera_local', 25, 'Conexión Morroa - San Juan de Betulia'),
(77, 94, 28.0, 'carretera', 37, 'Conexión Morroa - Tolú Viejo'),
(77, 95, 15.0, 'carretera_local', 20, 'Conexión Morroa - Buenavista (Sucre)'),
(77, 97, 12.0, 'carretera_local', 18, 'Conexión Morroa - San Luis de Sincé'),

(78, 79, 15.0, 'carretera_local', 20, 'Conexión Los Palmitos - Ovejas'),
(78, 80, 22.0, 'carretera', 30, 'Conexión Los Palmitos - Chalán'),
(78, 81, 18.0, 'carretera_local', 25, 'Conexión Los Palmitos - Coloso'),
(78, 82, 12.0, 'carretera_local', 18, 'Conexión Los Palmitos - Galeras'),
(78, 88, 15.0, 'carretera_local', 20, 'Conexión Los Palmitos - San Juan de Betulia'),
(78, 94, 25.0, 'carretera', 35, 'Conexión Los Palmitos - Tolú Viejo'),
(78, 95, 12.0, 'carretera_local', 18, 'Conexión Los Palmitos - Buenavista (Sucre)'),
(78, 97, 8.0, 'carretera_local', 12, 'Conexión Los Palmitos - San Luis de Sincé'),

(79, 80, 8.0, 'carretera_local', 12, 'Conexión Ovejas - Chalán'),
(79, 81, 8.0, 'carretera_local', 12, 'Conexión Ovejas - Coloso'),
(79, 82, 12.0, 'carretera_local', 18, 'Conexión Ovejas - Galeras'),
(79, 88, 15.0, 'carretera_local', 20, 'Conexión Ovejas - San Juan de Betulia'),
(79, 94, 22.0, 'carretera', 30, 'Conexión Ovejas - Tolú Viejo'),
(79, 95, 8.0, 'carretera_local', 12, 'Conexión Ovejas - Buenavista (Sucre)'),
(79, 97, 12.0, 'carretera_local', 18, 'Conexión Ovejas - San Luis de Sincé'),

(80, 81, 8.0, 'carretera_local', 12, 'Conexión Chalán - Coloso'),
(80, 82, 15.0, 'carretera_local', 20, 'Conexión Chalán - Galeras'),
(80, 88, 18.0, 'carretera_local', 25, 'Conexión Chalán - San Juan de Betulia'),
(80, 94, 25.0, 'carretera', 35, 'Conexión Chalán - Tolú Viejo'),
(80, 95, 12.0, 'carretera_local', 18, 'Conexión Chalán - Buenavista (Sucre)'),
(80, 97, 15.0, 'carretera_local', 20, 'Conexión Chalán - San Luis de Sincé'),

(81, 82, 12.0, 'carretera_local', 18, 'Conexión Coloso - Galeras'),
(81, 88, 15.0, 'carretera_local', 20, 'Conexión Coloso - San Juan de Betulia'),
(81, 94, 22.0, 'carretera', 30, 'Conexión Coloso - Tolú Viejo'),
(81, 95, 8.0, 'carretera_local', 12, 'Conexión Coloso - Buenavista (Sucre)'),
(81, 97, 12.0, 'carretera_local', 18, 'Conexión Coloso - San Luis de Sincé'),

(82, 88, 12.0, 'carretera_local', 18, 'Conexión Galeras - San Juan de Betulia'),
(82, 94, 18.0, 'carretera_local', 25, 'Conexión Galeras - Tolú Viejo'),
(82, 95, 8.0, 'carretera_local', 12, 'Conexión Galeras - Buenavista (Sucre)'),
(82, 97, 8.0, 'carretera_local', 12, 'Conexión Galeras - San Luis de Sincé'),

(88, 94, 15.0, 'carretera_local', 20, 'Conexión San Juan de Betulia - Tolú Viejo'),
(88, 95, 12.0, 'carretera_local', 18, 'Conexión San Juan de Betulia - Buenavista (Sucre)'),
(88, 97, 8.0, 'carretera_local', 12, 'Conexión San Juan de Betulia - San Luis de Sincé'),

(94, 95, 8.0, 'carretera_local', 12, 'Conexión Tolú Viejo - Buenavista (Sucre)'),
(94, 97, 12.0, 'carretera_local', 18, 'Conexión Tolú Viejo - San Luis de Sincé'),

(95, 97, 8.0, 'carretera_local', 12, 'Conexión Buenavista (Sucre) - San Luis de Sincé'),

-- =====================================================
-- CONEXIONES EN CÓRDOBA
-- =====================================================
(108, 109, 45.0, 'carretera', 60, 'Conexión Montería - Lorica'),
(108, 110, 35.0, 'carretera', 45, 'Conexión Montería - Sahagún'),
(108, 111, 12.0, 'carretera_local', 18, 'Conexión Montería - Cereté'),
(108, 112, 15.0, 'carretera_local', 20, 'Conexión Montería - San Pelayo'),
(108, 113, 22.0, 'carretera', 30, 'Conexión Montería - Ciénaga de Oro'),
(108, 384, 55.0, 'carretera', 70, 'Conexión Montería - Tierralta'),
(108, 385, 65.0, 'carretera', 85, 'Conexión Montería - Montelíbano'),
(108, 386, 85.0, 'carretera', 110, 'Conexión Montería - Puerto Escondido'),
(108, 387, 48.0, 'carretera', 63, 'Conexión Montería - San Bernardo del Viento'),

-- Conexiones locales en Córdoba
(109, 110, 28.0, 'carretera', 37, 'Conexión Lorica - Sahagún'),
(109, 111, 38.0, 'carretera', 50, 'Conexión Lorica - Cereté'),
(109, 112, 42.0, 'carretera', 55, 'Conexión Lorica - San Pelayo'),
(109, 113, 35.0, 'carretera', 45, 'Conexión Lorica - Ciénaga de Oro'),
(109, 387, 22.0, 'carretera', 30, 'Conexión Lorica - San Bernardo del Viento'),

(110, 111, 22.0, 'carretera', 30, 'Conexión Sahagún - Cereté'),
(110, 112, 25.0, 'carretera', 35, 'Conexión Sahagún - San Pelayo'),
(110, 113, 18.0, 'carretera_local', 25, 'Conexión Sahagún - Ciénaga de Oro'),
(110, 385, 45.0, 'carretera', 60, 'Conexión Sahagún - Montelíbano'),

(111, 112, 8.0, 'carretera_local', 12, 'Conexión Cereté - San Pelayo'),
(111, 113, 15.0, 'carretera_local', 20, 'Conexión Cereté - Ciénaga de Oro'),

(112, 113, 12.0, 'carretera_local', 18, 'Conexión San Pelayo - Ciénaga de Oro'),

(384, 385, 35.0, 'carretera', 45, 'Conexión Tierralta - Montelíbano'),
(384, 386, 25.0, 'carretera', 35, 'Conexión Tierralta - Puerto Escondido'),

(385, 386, 45.0, 'carretera', 60, 'Conexión Montelíbano - Puerto Escondido'),
(385, 387, 35.0, 'carretera', 45, 'Conexión Montelíbano - San Bernardo del Viento'),

(386, 387, 22.0, 'carretera', 30, 'Conexión Puerto Escondido - San Bernardo del Viento');

