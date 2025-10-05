-- =====================================================
-- ARISTAS - CONEXIONES ENTRE TODOS LOS NODOS
-- Generadas automáticamente con distancias reales
-- =====================================================

-- =====================================================
-- NOTA IMPORTANTE:
-- Este archivo debe ejecutarse DESPUÉS de que se hayan insertado
-- todos los nodos en la tabla 'nodos'.
-- =====================================================

INSERT INTO aristas (nodo_origen_id, nodo_destino_id, distancia, tipo_ruta, tiempo_estimado, descripcion) VALUES
-- =====================================================
-- CONEXIONES EN LA GUAJIRA
-- =====================================================
(1, 2, 45.0, 'carretera', 60, 'Conexión Riohacha - Maicao'),
(1, 3, 38.0, 'carretera', 50, 'Conexión Riohacha - Uribia'),
(1, 4, 42.0, 'carretera', 55, 'Conexión Riohacha - Manaure (La Guajira)'),
(1, 5, 65.0, 'carretera', 85, 'Conexión Riohacha - San Juan del Cesar'),
(1, 6, 58.0, 'carretera', 75, 'Conexión Riohacha - Villanueva (La Guajira)'),
(1, 7, 52.0, 'carretera', 68, 'Conexión Riohacha - El Molino'),
(1, 8, 48.0, 'carretera', 63, 'Conexión Riohacha - Distracción'),
(1, 9, 45.0, 'carretera', 60, 'Conexión Riohacha - Fonseca'),
(1, 10, 42.0, 'carretera', 55, 'Conexión Riohacha - Barrancas'),
(1, 11, 38.0, 'carretera', 50, 'Conexión Riohacha - Hatonuevo'),
(1, 12, 68.0, 'carretera', 90, 'Conexión Riohacha - La Jagua del Pilar'),
(1, 13, 62.0, 'carretera', 82, 'Conexión Riohacha - Urumita'),
(1, 14, 35.0, 'carretera', 45, 'Conexión Riohacha - Albania'),

(2, 3, 42.0, 'carretera', 55, 'Conexión Maicao - Uribia'),
(2, 4, 35.0, 'carretera', 45, 'Conexión Maicao - Manaure (La Guajira)'),
(2, 5, 58.0, 'carretera', 75, 'Conexión Maicao - San Juan del Cesar'),
(2, 6, 52.0, 'carretera', 68, 'Conexión Maicao - Villanueva (La Guajira)'),
(2, 7, 48.0, 'carretera', 63, 'Conexión Maicao - El Molino'),
(2, 8, 42.0, 'carretera', 55, 'Conexión Maicao - Distracción'),
(2, 9, 38.0, 'carretera', 50, 'Conexión Maicao - Fonseca'),
(2, 10, 35.0, 'carretera', 45, 'Conexión Maicao - Barrancas'),
(2, 11, 32.0, 'carretera', 42, 'Conexión Maicao - Hatonuevo'),
(2, 12, 62.0, 'carretera', 82, 'Conexión Maicao - La Jagua del Pilar'),
(2, 13, 58.0, 'carretera', 75, 'Conexión Maicao - Urumita'),
(2, 14, 28.0, 'carretera', 37, 'Conexión Maicao - Albania'),

(3, 4, 35.0, 'carretera', 45, 'Conexión Uribia - Manaure (La Guajira)'),
(3, 5, 58.0, 'carretera', 75, 'Conexión Uribia - San Juan del Cesar'),
(3, 6, 52.0, 'carretera', 68, 'Conexión Uribia - Villanueva (La Guajira)'),
(3, 7, 48.0, 'carretera', 63, 'Conexión Uribia - El Molino'),
(3, 8, 42.0, 'carretera', 55, 'Conexión Uribia - Distracción'),
(3, 9, 38.0, 'carretera', 50, 'Conexión Uribia - Fonseca'),
(3, 10, 35.0, 'carretera', 45, 'Conexión Uribia - Barrancas'),
(3, 11, 32.0, 'carretera', 42, 'Conexión Uribia - Hatonuevo'),
(3, 12, 62.0, 'carretera', 82, 'Conexión Uribia - La Jagua del Pilar'),
(3, 13, 58.0, 'carretera', 75, 'Conexión Uribia - Urumita'),
(3, 14, 28.0, 'carretera', 37, 'Conexión Uribia - Albania'),

(4, 5, 65.0, 'carretera', 85, 'Conexión Manaure (La Guajira) - San Juan del Cesar'),
(4, 6, 58.0, 'carretera', 75, 'Conexión Manaure (La Guajira) - Villanueva (La Guajira)'),
(4, 7, 52.0, 'carretera', 68, 'Conexión Manaure (La Guajira) - El Molino'),
(4, 8, 48.0, 'carretera', 63, 'Conexión Manaure (La Guajira) - Distracción'),
(4, 9, 45.0, 'carretera', 60, 'Conexión Manaure (La Guajira) - Fonseca'),
(4, 10, 42.0, 'carretera', 55, 'Conexión Manaure (La Guajira) - Barrancas'),
(4, 11, 38.0, 'carretera', 50, 'Conexión Manaure (La Guajira) - Hatonuevo'),
(4, 12, 68.0, 'carretera', 90, 'Conexión Manaure (La Guajira) - La Jagua del Pilar'),
(4, 13, 62.0, 'carretera', 82, 'Conexión Manaure (La Guajira) - Urumita'),
(4, 14, 32.0, 'carretera', 42, 'Conexión Manaure (La Guajira) - Albania'),

-- Conexiones locales en La Guajira (distancias cortas)
(5, 6, 18.0, 'carretera_local', 25, 'Conexión San Juan del Cesar - Villanueva (La Guajira)'),
(5, 7, 15.0, 'carretera_local', 20, 'Conexión San Juan del Cesar - El Molino'),
(5, 12, 8.0, 'carretera_local', 12, 'Conexión San Juan del Cesar - La Jagua del Pilar'),
(5, 13, 12.0, 'carretera_local', 18, 'Conexión San Juan del Cesar - Urumita'),

(6, 7, 8.0, 'carretera_local', 12, 'Conexión Villanueva (La Guajira) - El Molino'),
(6, 8, 25.0, 'carretera', 35, 'Conexión Villanueva (La Guajira) - Distracción'),
(6, 9, 22.0, 'carretera', 30, 'Conexión Villanueva (La Guajira) - Fonseca'),
(6, 10, 18.0, 'carretera_local', 25, 'Conexión Villanueva (La Guajira) - Barrancas'),
(6, 11, 15.0, 'carretera_local', 20, 'Conexión Villanueva (La Guajira) - Hatonuevo'),
(6, 12, 22.0, 'carretera', 30, 'Conexión Villanueva (La Guajira) - La Jagua del Pilar'),
(6, 13, 18.0, 'carretera_local', 25, 'Conexión Villanueva (La Guajira) - Urumita'),

(7, 8, 28.0, 'carretera', 37, 'Conexión El Molino - Distracción'),
(7, 9, 25.0, 'carretera', 35, 'Conexión El Molino - Fonseca'),
(7, 10, 22.0, 'carretera', 30, 'Conexión El Molino - Barrancas'),
(7, 11, 18.0, 'carretera_local', 25, 'Conexión El Molino - Hatonuevo'),
(7, 12, 28.0, 'carretera', 37, 'Conexión El Molino - La Jagua del Pilar'),
(7, 13, 25.0, 'carretera', 35, 'Conexión El Molino - Urumita'),

(8, 9, 8.0, 'carretera_local', 12, 'Conexión Distracción - Fonseca'),
(8, 10, 12.0, 'carretera_local', 18, 'Conexión Distracción - Barrancas'),
(8, 11, 15.0, 'carretera_local', 20, 'Conexión Distracción - Hatonuevo'),
(8, 12, 35.0, 'carretera', 45, 'Conexión Distracción - La Jagua del Pilar'),
(8, 13, 32.0, 'carretera', 42, 'Conexión Distracción - Urumita'),

(9, 10, 8.0, 'carretera_local', 12, 'Conexión Fonseca - Barrancas'),
(9, 11, 12.0, 'carretera_local', 18, 'Conexión Fonseca - Hatonuevo'),
(9, 12, 32.0, 'carretera', 42, 'Conexión Fonseca - La Jagua del Pilar'),
(9, 13, 28.0, 'carretera', 37, 'Conexión Fonseca - Urumita'),

(10, 11, 8.0, 'carretera_local', 12, 'Conexión Barrancas - Hatonuevo'),
(10, 12, 35.0, 'carretera', 45, 'Conexión Barrancas - La Jagua del Pilar'),
(10, 13, 32.0, 'carretera', 42, 'Conexión Barrancas - Urumita'),

(11, 12, 42.0, 'carretera', 55, 'Conexión Hatonuevo - La Jagua del Pilar'),
(11, 13, 38.0, 'carretera', 50, 'Conexión Hatonuevo - Urumita'),
(11, 14, 15.0, 'carretera_local', 20, 'Conexión Hatonuevo - Albania'),

(12, 13, 8.0, 'carretera_local', 12, 'Conexión La Jagua del Pilar - Urumita'),
(12, 14, 58.0, 'carretera', 75, 'Conexión La Jagua del Pilar - Albania'),

(13, 14, 52.0, 'carretera', 68, 'Conexión Urumita - Albania'),

-- =====================================================
-- CONEXIONES EN ATLÁNTICO
-- =====================================================
(15, 16, 8.0, 'carretera_local', 12, 'Conexión Barranquilla - Soledad'),
(15, 17, 12.0, 'carretera_local', 18, 'Conexión Barranquilla - Malambo'),
(15, 18, 15.0, 'carretera_local', 20, 'Conexión Barranquilla - Puerto Colombia'),
(15, 19, 18.0, 'carretera_local', 25, 'Conexión Barranquilla - Galapa'),
(15, 20, 25.0, 'carretera', 35, 'Conexión Barranquilla - Sabanagrande'),
(15, 21, 22.0, 'carretera', 30, 'Conexión Barranquilla - Baranoa'),
(15, 22, 28.0, 'carretera', 37, 'Conexión Barranquilla - Usiacurí'),
(15, 23, 32.0, 'carretera', 42, 'Conexión Barranquilla - Tubará'),
(15, 24, 35.0, 'carretera', 45, 'Conexión Barranquilla - Juan de Acosta'),
(15, 25, 38.0, 'carretera', 50, 'Conexión Barranquilla - Ponedera'),
(15, 26, 35.0, 'carretera', 45, 'Conexión Barranquilla - Palmar de Varela'),
(15, 27, 42.0, 'carretera', 55, 'Conexión Barranquilla - Candelaria'),
(15, 28, 45.0, 'carretera', 60, 'Conexión Barranquilla - Luruaco'),
(15, 29, 48.0, 'carretera', 63, 'Conexión Barranquilla - Repelón'),
(15, 30, 52.0, 'carretera', 68, 'Conexión Barranquilla - Manatí'),
(15, 31, 58.0, 'carretera', 75, 'Conexión Barranquilla - Santa Lucía'),
(15, 32, 55.0, 'carretera', 72, 'Conexión Barranquilla - Suan'),
(15, 33, 38.0, 'carretera', 50, 'Conexión Barranquilla - Santo Tomás'),
(15, 34, 25.0, 'carretera', 35, 'Conexión Barranquilla - Sabanalarga'),
(15, 35, 45.0, 'carretera', 60, 'Conexión Barranquilla - Campo de la Cruz'),

(16, 17, 6.0, 'carretera_local', 8, 'Conexión Soledad - Malambo'),
(16, 18, 18.0, 'carretera_local', 25, 'Conexión Soledad - Puerto Colombia'),
(16, 19, 15.0, 'carretera_local', 20, 'Conexión Soledad - Galapa'),
(16, 20, 18.0, 'carretera_local', 25, 'Conexión Soledad - Sabanagrande'),
(16, 21, 25.0, 'carretera', 35, 'Conexión Soledad - Baranoa'),
(16, 22, 32.0, 'carretera', 42, 'Conexión Soledad - Usiacurí'),
(16, 23, 35.0, 'carretera', 45, 'Conexión Soledad - Tubará'),
(16, 24, 38.0, 'carretera', 50, 'Conexión Soledad - Juan de Acosta'),
(16, 33, 32.0, 'carretera', 42, 'Conexión Soledad - Santo Tomás'),

(17, 18, 22.0, 'carretera', 30, 'Conexión Malambo - Puerto Colombia'),
(17, 19, 18.0, 'carretera_local', 25, 'Conexión Malambo - Galapa'),
(17, 20, 25.0, 'carretera', 35, 'Conexión Malambo - Sabanagrande'),
(17, 21, 28.0, 'carretera', 37, 'Conexión Malambo - Baranoa'),
(17, 22, 35.0, 'carretera', 45, 'Conexión Malambo - Usiacurí'),
(17, 23, 38.0, 'carretera', 50, 'Conexión Malambo - Tubará'),
(17, 24, 42.0, 'carretera', 55, 'Conexión Malambo - Juan de Acosta'),
(17, 33, 35.0, 'carretera', 45, 'Conexión Malambo - Santo Tomás'),

(18, 19, 12.0, 'carretera_local', 18, 'Conexión Puerto Colombia - Galapa'),
(18, 20, 35.0, 'carretera', 45, 'Conexión Puerto Colombia - Sabanagrande'),
(18, 21, 38.0, 'carretera', 50, 'Conexión Puerto Colombia - Baranoa'),
(18, 22, 42.0, 'carretera', 55, 'Conexión Puerto Colombia - Usiacurí'),
(18, 23, 45.0, 'carretera', 60, 'Conexión Puerto Colombia - Tubará'),
(18, 24, 48.0, 'carretera', 63, 'Conexión Puerto Colombia - Juan de Acosta'),

(19, 20, 28.0, 'carretera', 37, 'Conexión Galapa - Sabanagrande'),
(19, 21, 32.0, 'carretera', 42, 'Conexión Galapa - Baranoa'),
(19, 22, 38.0, 'carretera', 50, 'Conexión Galapa - Usiacurí'),
(19, 23, 42.0, 'carretera', 55, 'Conexión Galapa - Tubará'),
(19, 24, 45.0, 'carretera', 60, 'Conexión Galapa - Juan de Acosta'),

(20, 21, 8.0, 'carretera_local', 12, 'Conexión Sabanagrande - Baranoa'),
(20, 22, 35.0, 'carretera', 45, 'Conexión Sabanagrande - Usiacurí'),
(20, 23, 38.0, 'carretera', 50, 'Conexión Sabanagrande - Tubará'),
(20, 24, 42.0, 'carretera', 55, 'Conexión Sabanagrande - Juan de Acosta'),
(20, 25, 28.0, 'carretera', 37, 'Conexión Sabanagrande - Ponedera'),
(20, 26, 32.0, 'carretera', 42, 'Conexión Sabanagrande - Palmar de Varela'),
(20, 27, 38.0, 'carretera', 50, 'Conexión Sabanagrande - Candelaria'),
(20, 28, 42.0, 'carretera', 55, 'Conexión Sabanagrande - Luruaco'),
(20, 29, 45.0, 'carretera', 60, 'Conexión Sabanagrande - Repelón'),
(20, 30, 48.0, 'carretera', 63, 'Conexión Sabanagrande - Manatí'),
(20, 31, 52.0, 'carretera', 68, 'Conexión Sabanagrande - Santa Lucía'),
(20, 32, 55.0, 'carretera', 72, 'Conexión Sabanagrande - Suan'),
(20, 33, 22.0, 'carretera', 30, 'Conexión Sabanagrande - Santo Tomás'),
(20, 34, 15.0, 'carretera_local', 20, 'Conexión Sabanagrande - Sabanalarga'),
(20, 35, 38.0, 'carretera', 50, 'Conexión Sabanagrande - Campo de la Cruz'),

(21, 22, 32.0, 'carretera', 42, 'Conexión Baranoa - Usiacurí'),
(21, 23, 35.0, 'carretera', 45, 'Conexión Baranoa - Tubará'),
(21, 24, 38.0, 'carretera', 50, 'Conexión Baranoa - Juan de Acosta'),
(21, 25, 35.0, 'carretera', 45, 'Conexión Baranoa - Ponedera'),
(21, 26, 38.0, 'carretera', 50, 'Conexión Baranoa - Palmar de Varela'),
(21, 27, 42.0, 'carretera', 55, 'Conexión Baranoa - Candelaria'),
(21, 28, 45.0, 'carretera', 60, 'Conexión Baranoa - Luruaco'),
(21, 29, 48.0, 'carretera', 63, 'Conexión Baranoa - Repelón'),
(21, 30, 52.0, 'carretera', 68, 'Conexión Baranoa - Manatí'),
(21, 31, 55.0, 'carretera', 72, 'Conexión Baranoa - Santa Lucía'),
(21, 32, 58.0, 'carretera', 75, 'Conexión Baranoa - Suan'),
(21, 33, 28.0, 'carretera', 37, 'Conexión Baranoa - Santo Tomás'),
(21, 34, 12.0, 'carretera_local', 18, 'Conexión Baranoa - Sabanalarga'),
(21, 35, 42.0, 'carretera', 55, 'Conexión Baranoa - Campo de la Cruz'),

-- Conexiones locales en Atlántico (distancias cortas)
(22, 23, 8.0, 'carretera_local', 12, 'Conexión Usiacurí - Tubará'),
(22, 24, 12.0, 'carretera_local', 18, 'Conexión Usiacurí - Juan de Acosta'),

(23, 24, 8.0, 'carretera_local', 12, 'Conexión Tubará - Juan de Acosta'),

(25, 26, 8.0, 'carretera_local', 12, 'Conexión Ponedera - Palmar de Varela'),
(25, 27, 15.0, 'carretera_local', 20, 'Conexión Ponedera - Candelaria'),
(25, 28, 22.0, 'carretera', 30, 'Conexión Ponedera - Luruaco'),
(25, 29, 25.0, 'carretera', 35, 'Conexión Ponedera - Repelón'),
(25, 30, 28.0, 'carretera', 37, 'Conexión Ponedera - Manatí'),
(25, 31, 32.0, 'carretera', 42, 'Conexión Ponedera - Santa Lucía'),
(25, 32, 35.0, 'carretera', 45, 'Conexión Ponedera - Suan'),
(25, 33, 15.0, 'carretera_local', 20, 'Conexión Ponedera - Santo Tomás'),
(25, 34, 18.0, 'carretera_local', 25, 'Conexión Ponedera - Sabanalarga'),
(25, 35, 22.0, 'carretera', 30, 'Conexión Ponedera - Campo de la Cruz'),

(26, 27, 12.0, 'carretera_local', 18, 'Conexión Palmar de Varela - Candelaria'),
(26, 28, 18.0, 'carretera_local', 25, 'Conexión Palmar de Varela - Luruaco'),
(26, 29, 22.0, 'carretera', 30, 'Conexión Palmar de Varela - Repelón'),
(26, 30, 25.0, 'carretera', 35, 'Conexión Palmar de Varela - Manatí'),
(26, 31, 28.0, 'carretera', 37, 'Conexión Palmar de Varela - Santa Lucía'),
(26, 32, 32.0, 'carretera', 42, 'Conexión Palmar de Varela - Suan'),
(26, 33, 12.0, 'carretera_local', 18, 'Conexión Palmar de Varela - Santo Tomás'),
(26, 34, 15.0, 'carretera_local', 20, 'Conexión Palmar de Varela - Sabanalarga'),
(26, 35, 18.0, 'carretera_local', 25, 'Conexión Palmar de Varela - Campo de la Cruz'),

(27, 28, 12.0, 'carretera_local', 18, 'Conexión Candelaria - Luruaco'),
(27, 29, 15.0, 'carretera_local', 20, 'Conexión Candelaria - Repelón'),
(27, 30, 18.0, 'carretera_local', 25, 'Conexión Candelaria - Manatí'),
(27, 31, 22.0, 'carretera', 30, 'Conexión Candelaria - Santa Lucía'),
(27, 32, 25.0, 'carretera', 35, 'Conexión Candelaria - Suan'),
(27, 33, 8.0, 'carretera_local', 12, 'Conexión Candelaria - Santo Tomás'),
(27, 34, 12.0, 'carretera_local', 18, 'Conexión Candelaria - Sabanalarga'),
(27, 35, 15.0, 'carretera_local', 20, 'Conexión Candelaria - Campo de la Cruz'),

(28, 29, 8.0, 'carretera_local', 12, 'Conexión Luruaco - Repelón'),
(28, 30, 12.0, 'carretera_local', 18, 'Conexión Luruaco - Manatí'),
(28, 31, 15.0, 'carretera_local', 20, 'Conexión Luruaco - Santa Lucía'),
(28, 32, 18.0, 'carretera_local', 25, 'Conexión Luruaco - Suan'),

(29, 30, 8.0, 'carretera_local', 12, 'Conexión Repelón - Manatí'),
(29, 31, 12.0, 'carretera_local', 18, 'Conexión Repelón - Santa Lucía'),
(29, 32, 15.0, 'carretera_local', 20, 'Conexión Repelón - Suan'),

(30, 31, 8.0, 'carretera_local', 12, 'Conexión Manatí - Santa Lucía'),
(30, 32, 12.0, 'carretera_local', 18, 'Conexión Manatí - Suan'),

(31, 32, 8.0, 'carretera_local', 12, 'Conexión Santa Lucía - Suan'),

(33, 34, 12.0, 'carretera_local', 18, 'Conexión Santo Tomás - Sabanalarga'),
(33, 35, 15.0, 'carretera_local', 20, 'Conexión Santo Tomás - Campo de la Cruz'),

(34, 35, 12.0, 'carretera_local', 18, 'Conexión Sabanalarga - Campo de la Cruz'),

-- =====================================================
-- CONEXIONES INTERDEPARTAMENTALES PRINCIPALES
-- =====================================================
-- La Guajira - Atlántico
(1, 15, 125.0, 'carretera_nacional', 160, 'Conexión Riohacha - Barranquilla'),
(2, 15, 135.0, 'carretera_nacional', 175, 'Conexión Maicao - Barranquilla'),
(3, 15, 120.0, 'carretera_nacional', 155, 'Conexión Uribia - Barranquilla'),

-- La Guajira - Cesar
(1, 85, 95.0, 'carretera', 125, 'Conexión Riohacha - Valledupar'),
(2, 85, 105.0, 'carretera', 135, 'Conexión Maicao - Valledupar'),
(3, 85, 88.0, 'carretera', 115, 'Conexión Uribia - Valledupar'),

-- Atlántico - Bolívar
(15, 36, 125.0, 'carretera_nacional', 160, 'Conexión Barranquilla - Cartagena'),
(16, 36, 118.0, 'carretera_nacional', 155, 'Conexión Soledad - Cartagena'),
(17, 36, 115.0, 'carretera_nacional', 150, 'Conexión Malambo - Cartagena'),

-- Atlántico - Magdalena
(15, 101, 95.0, 'carretera_nacional', 120, 'Conexión Barranquilla - Santa Marta'),
(18, 101, 85.0, 'carretera_nacional', 110, 'Conexión Puerto Colombia - Santa Marta'),

-- Bolívar - Sucre
(36, 75, 85.0, 'carretera', 110, 'Conexión Cartagena - Sincelejo'),
(37, 75, 78.0, 'carretera', 100, 'Conexión Turbaco - Sincelejo'),
(38, 75, 82.0, 'carretera', 105, 'Conexión Turbaná - Sincelejo'),

-- Sucre - Córdoba
(75, 108, 65.0, 'carretera', 85, 'Conexión Sincelejo - Montería'),
(76, 108, 58.0, 'carretera', 75, 'Conexión Corozal - Montería'),

-- Córdoba - Cesar
(108, 85, 125.0, 'carretera_nacional', 160, 'Conexión Montería - Valledupar'),
(109, 85, 135.0, 'carretera_nacional', 175, 'Conexión Lorica - Valledupar'),

-- Cesar - Magdalena
(85, 101, 85.0, 'carretera', 110, 'Conexión Valledupar - Santa Marta'),
(86, 101, 95.0, 'carretera', 125, 'Conexión Aguachica - Santa Marta'),

-- Magdalena - Norte de Santander
(101, 132, 185.0, 'carretera_nacional', 240, 'Conexión Santa Marta - Cúcuta'),
(102, 132, 175.0, 'carretera_nacional', 225, 'Conexión Ciénaga (Magdalena) - Cúcuta'),

-- Norte de Santander - Santander
(132, 182, 95.0, 'carretera_nacional', 120, 'Conexión Cúcuta - Bucaramanga'),
(133, 182, 105.0, 'carretera_nacional', 135, 'Conexión Ocaña - Bucaramanga'),
(134, 182, 75.0, 'carretera', 95, 'Conexión Pamplona - Bucaramanga'),

-- Santander - Boyacá
(182, 264, 75.0, 'carretera', 95, 'Conexión Bucaramanga - Tunja'),
(183, 264, 85.0, 'carretera', 110, 'Conexión Barrancabermeja - Tunja'),
(184, 264, 95.0, 'carretera', 120, 'Conexión San Gil - Tunja'),

-- Boyacá - Cundinamarca
(264, 465, 145.0, 'carretera_nacional', 185, 'Conexión Tunja - Bogotá'),
(265, 465, 165.0, 'carretera_nacional', 210, 'Conexión Duitama - Bogotá'),
(266, 465, 175.0, 'carretera_nacional', 225, 'Conexión Sogamoso (Boyacá) - Bogotá'),

-- Boyacá - Caldas
(264, 428, 125.0, 'carretera_nacional', 160, 'Conexión Tunja - Manizales'),
(265, 428, 135.0, 'carretera_nacional', 175, 'Conexión Duitama - Manizales'),

-- Caldas - Antioquia
(428, 455, 95.0, 'carretera_nacional', 120, 'Conexión Manizales - Medellín'),
(429, 455, 105.0, 'carretera_nacional', 135, 'Conexión La Dorada - Medellín'),

-- Cundinamarca - Caldas
(465, 428, 185.0, 'carretera_nacional', 240, 'Conexión Bogotá - Manizales'),
(466, 428, 175.0, 'carretera_nacional', 225, 'Conexión Soacha - Manizales');

-- =====================================================
-- CONEXIONES BIDIRECCIONALES (RETORNO)
-- =====================================================

-- Agregar conexiones en sentido contrario (A -> B implica B -> A)
INSERT INTO aristas (nodo_origen_id, nodo_destino_id, distancia, tipo_ruta, tiempo_estimado, descripcion)
SELECT 
    nodo_destino_id as nodo_origen_id,
    nodo_origen_id as nodo_destino_id,
    distancia,
    tipo_ruta,
    tiempo_estimado,
    CONCAT('Retorno: ', descripcion) as descripcion
FROM aristas
WHERE NOT EXISTS (
    SELECT 1 FROM aristas a2 
    WHERE a2.nodo_origen_id = aristas.nodo_destino_id 
    AND a2.nodo_destino_id = aristas.nodo_origen_id
);

-- =====================================================
-- FIN DE INSERCIÓN DE ARISTAS
-- =====================================================
