# 🗄️ Base de Datos - Grafos Colombia

## 📋 Descripción

Este directorio contiene todos los archivos necesarios para configurar y gestionar la base de datos del proyecto Grafos Colombia. La base de datos permite almacenar y gestionar nodos (ubicaciones) y aristas (conexiones) de forma dinámica, eliminando la necesidad de hardcodear los datos en el código.

## 🏗️ Estructura de la Base de Datos

### Tablas Principales

#### 📍 `nodos`
- **Propósito**: Almacena todas las ubicaciones/ciudades del grafo
- **Campos principales**:
  - `id`: Identificador único (AUTO_INCREMENT)
  - `nombre`: Nombre de la ubicación (UNIQUE)
  - `latitud`: Coordenada de latitud
  - `longitud`: Coordenada de longitud
  - `tipo`: Tipo de ubicación (ciudad, corregimiento, puerto, etc.)
  - `descripcion`: Descripción adicional
  - `activo`: Estado de activación (soft delete)

#### 🛣️ `aristas`
- **Propósito**: Almacena las conexiones entre ubicaciones
- **Campos principales**:
  - `id`: Identificador único (AUTO_INCREMENT)
  - `nodo_origen_id`: ID del nodo origen (FK)
  - `nodo_destino_id`: ID del nodo destino (FK)
  - `distancia`: Distancia en kilómetros
  - `tipo_ruta`: Tipo de ruta (carretera, sendero, etc.)
  - `tiempo_estimado`: Tiempo estimado en minutos
  - `costo`: Costo de la ruta
  - `activo`: Estado de activación (soft delete)

#### 📊 `rutas_calculadas`
- **Propósito**: Historial de rutas calculadas por el algoritmo
- **Campos principales**:
  - `id`: Identificador único
  - `nodo_origen_id`: ID del nodo origen
  - `nodo_destino_id`: ID del nodo destino
  - `distancia_total`: Distancia total de la ruta
  - `tiempo_total`: Tiempo total estimado
  - `fecha_calculo`: Timestamp del cálculo

#### 🔗 `secuencia_rutas`
- **Propósito**: Secuencia detallada de nodos en cada ruta
- **Campos principales**:
  - `ruta_id`: ID de la ruta (FK)
  - `orden`: Orden del nodo en la ruta
  - `nodo_id`: ID del nodo (FK)

#### ⚙️ `configuraciones`
- **Propósito**: Configuraciones del sistema
- **Campos principales**:
  - `clave`: Nombre de la configuración
  - `valor`: Valor de la configuración
  - `descripcion`: Descripción de la configuración

## 🚀 Instalación

### Prerrequisitos
- MySQL Server 8.0 o superior
- Usuario con permisos para crear bases de datos

### Instalación Automática

#### Windows
```batch
cd database
install_database.bat
```

#### Linux/macOS
```bash
cd database
chmod +x install_database.sh
./install_database.sh
```

### Instalación Manual

1. **Crear la base de datos**:
```sql
mysql -u root -p
CREATE DATABASE grafos_colombia;
USE grafos_colombia;
```

2. **Ejecutar el schema**:
```bash
mysql -u root -p < schema.sql
```

3. **Insertar datos de ejemplo**:
```bash
mysql -u root -p < datos_ejemplo.sql
```

## 📁 Archivos Incluidos

### `schema.sql`
- Script completo de creación de la base de datos
- Definición de todas las tablas
- Índices para optimización
- Vistas para facilitar consultas
- Procedimientos almacenados
- Triggers para auditoría

### `datos_ejemplo.sql`
- Datos de ejemplo de ubicaciones en Córdoba, Colombia
- Más de 60 ubicaciones con coordenadas reales
- Conexiones entre ubicaciones con distancias reales
- Configuraciones del sistema

### `database.properties`
- Configuración de conexión a la base de datos
- Parámetros de pool de conexiones
- Configuraciones de la aplicación

### `install_database.bat` / `install_database.sh`
- Scripts de instalación automática
- Verificación de prerrequisitos
- Creación completa de la base de datos

## 🔧 Configuración

### Parámetros de Conexión

Edita `database.properties` para configurar la conexión:

```properties
# Configuración básica
db.host=localhost
db.port=3306
db.name=grafos_colombia
db.username=root
db.password=tu_contraseña
```

### Configuraciones de la Aplicación

```properties
# Habilitar/deshabilitar base de datos
app.database.enabled=true

# Usar datos hardcoded como fallback
app.fallback.to.hardcoded=true

# Sincronización automática con ColombianLocations
app.auto.sync.colombian.locations=true
```

## 💻 Uso en la Aplicación

### Inicialización Automática

La aplicación detecta automáticamente si la base de datos está disponible:

```java
// En MainController.initialize()
initializeDatabase(); // Detecta y conecta automáticamente
loadGraphData();      // Carga desde BD o datos hardcoded
```

### Carga de Datos

```java
// Cargar grafo completo
GraphDataLoader loader = new GraphDataLoader();
Graph graph = loader.cargarGrafoCompleto();

// Cargar en GraphView
loader.cargarGrafoEnGraphView(graphView);
```

### Operaciones CRUD

```java
// Nodos
NodoDAO nodoDAO = new NodoDAO();
List<GeoNode> nodos = nodoDAO.obtenerTodosLosNodos();
nodoDAO.insertarNodo(nodo, "ciudad", "Descripción");

// Aristas
AristaDAO aristaDAO = new AristaDAO();
List<Edge> aristas = aristaDAO.obtenerTodasLasAristas();
aristaDAO.insertarArista("Origen", "Destino", 10.5, "carretera", 15, 0.0, "Descripción");
```

## 📊 Vistas y Consultas Útiles

### Vista de Nodos Activos
```sql
SELECT * FROM vista_nodos_activos;
```

### Vista de Aristas Activas
```sql
SELECT * FROM vista_aristas_activas;
```

### Vista de Estadísticas
```sql
SELECT * FROM vista_estadisticas;
```

### Consultas Personalizadas

```sql
-- Nodos por tipo
SELECT tipo, COUNT(*) FROM nodos WHERE activo = TRUE GROUP BY tipo;

-- Aristas más largas
SELECT origen_nombre, destino_nombre, distancia 
FROM vista_aristas_activas 
ORDER BY distancia DESC LIMIT 10;

-- Nodos con más conexiones
SELECT nombre, conexiones_salientes + conexiones_entrantes as total_conexiones
FROM vista_nodos_activos
ORDER BY total_conexiones DESC LIMIT 10;
```

## 🔍 Mantenimiento

### Limpieza de Datos Antiguos
```sql
CALL LimpiarDatosAntiguos(30); -- Eliminar rutas de más de 30 días
```

### Verificación de Integridad
```java
GraphDataLoader loader = new GraphDataLoader();
boolean integridadOk = loader.verificarIntegridadDatos();
```

### Sincronización con ColombianLocations
```java
GraphDataLoader loader = new GraphDataLoader();
loader.sincronizarConColombianLocations();
```

## 🚨 Solución de Problemas

### Error de Conexión
1. Verificar que MySQL esté ejecutándose
2. Verificar credenciales en `database.properties`
3. Verificar que la base de datos exista

### Error de Tablas Faltantes
1. Ejecutar `schema.sql` nuevamente
2. Verificar permisos del usuario de base de datos

### Datos No Se Cargan
1. Verificar que `datos_ejemplo.sql` se ejecutó correctamente
2. Verificar que los nodos y aristas están activos (`activo = TRUE`)

## 🔄 Migración y Actualizaciones

### Agregar Nuevos Nodos
```sql
INSERT INTO nodos (nombre, latitud, longitud, tipo, descripcion) 
VALUES ('Nueva Ciudad', 8.1234, -75.5678, 'ciudad', 'Descripción');
```

### Agregar Nuevas Aristas
```sql
INSERT INTO aristas (nodo_origen_id, nodo_destino_id, distancia, tipo_ruta, descripcion)
VALUES (
    (SELECT id FROM nodos WHERE nombre = 'Origen'),
    (SELECT id FROM nodos WHERE nombre = 'Destino'),
    15.5, 'carretera', 'Nueva conexión'
);
```

### Actualizar Configuraciones
```sql
UPDATE configuraciones SET valor = 'nuevo_valor' WHERE clave = 'configuracion';
```

## 📈 Ventajas del Sistema de Base de Datos

### ✅ Flexibilidad
- Agregar/eliminar nodos y aristas sin modificar código
- Configuraciones dinámicas
- Escalabilidad ilimitada

### ✅ Persistencia
- Datos permanentes entre ejecuciones
- Historial de rutas calculadas
- Auditoría de cambios

### ✅ Performance
- Índices optimizados para consultas rápidas
- Pool de conexiones
- Consultas eficientes

### ✅ Mantenimiento
- Soft delete para recuperación
- Triggers para auditoría
- Procedimientos almacenados

## 🎯 Próximas Funcionalidades

- [ ] Interfaz web para gestión de datos
- [ ] API REST para operaciones CRUD
- [ ] Importación/exportación de datos
- [ ] Backup automático
- [ ] Replicación para alta disponibilidad
- [ ] Métricas y analytics avanzados

---

**📞 Soporte**: Para problemas con la base de datos, revisar los logs de la aplicación y verificar la conectividad con MySQL.
