# Proyecto Grafos Colombia

Este proyecto utiliza el algoritmo de Dijkstra para encontrar la ruta más corta entre distintas ciudades , municicios y corregimientos.

## 🛠️ Requisitos

- **Java Development Kit (JDK)**: Versión 11 o superior.
- **Apache Maven**: Para compilar y ejecutar el proyecto.

## 🚀 Cómo ejecutar

1.  Abre una terminal en la carpeta raíz del proyecto (`proyecto_grafos`).

2.  **Compila el proyecto** y descarga las dependencias con Maven:
    ```bash
    mvn clean install
    ```

3.  **Ejecuta la aplicación**:
    ```bash
    mvn     javafx:run
    ```

## ✨ Funcionalidades Principales

- **Cálculo de Rutas**: Uso del algoritmo de Dijkstra para encontrar rutas óptimas.
- **Caché Inteligente**: Almacena rutas calculadas para una respuesta más rápida en consultas repetidas.
- **Historial de Consultas**: Permite visualizar las últimas 20 rutas calculadas junto con sus fechas.
- **Gestión de Cache**: Herramientas para limpiar el caché de rutas antiguas.
- **Indicadores Visuales**: Informa si una ruta se obtuvo del caché o fue calculada en tiempo real.

### Nuevos Botones en la Interfaz

- **Ver Historial de Rutas**: Abre un diálogo para consultar el historial de consultas.
- **Limpiar Cache de Rutas**: Elimina rutas anteriores a 30 días con confirmación previa.

## 📅 Historial y Notas Adicionales
La aplicación ha sido actualizada para integrar completamente el sistema de caché con la base de datos, mejorando el rendimiento y la experiencia del usuario. Para más detalles técnicos, consulta `IMPLEMENTACION_COMPLETA.md`.
