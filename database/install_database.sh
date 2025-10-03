#!/bin/bash

echo "====================================================="
echo "INSTALACION DE BASE DE DATOS - GRAFOS COLOMBIA"
echo "Sistema completo con cache de rutas y aristas optimizadas"
echo "====================================================="
echo

echo "Paso 1: Reseteando base de datos..."
mysql -u root -p < reset_database.sql
if [ $? -ne 0 ]; then
    echo "ERROR: No se pudo resetear la base de datos"
    exit 1
fi

echo "Paso 2: Creando esquema de base de datos con soporte UTF-8..."
mysql -u root -p < schema.sql
if [ $? -ne 0 ]; then
    echo "ERROR: No se pudo crear el esquema de la base de datos"
    exit 1
fi

echo "Paso 3: Insertando nodos (633 ubicaciones de Colombia)..."
mysql -u root -p grafos_colombia < nodos.sql
if [ $? -ne 0 ]; then
    echo "ERROR: No se pudieron insertar los nodos"
    exit 1
fi

echo "Paso 4: Insertando aristas principales..."
mysql -u root -p grafos_colombia < aristas_completas.sql
if [ $? -ne 0 ]; then
    echo "ERROR: No se pudieron insertar las aristas principales"
    exit 1
fi

echo "Paso 5: Insertando aristas adicionales..."
mysql -u root -p grafos_colombia < aristas_adicionales.sql
if [ $? -ne 0 ]; then
    echo "ERROR: No se pudieron insertar las aristas adicionales"
    exit 1
fi

echo
echo "====================================================="
echo "INSTALACION COMPLETADA EXITOSAMENTE!"
echo "====================================================="
echo
echo "La base de datos grafos_colombia ha sido creada con:"
echo "- 633 nodos (ciudades, municipios y corregimientos)"
echo "- Miles de aristas con distancias reales calculadas"
echo "- Sistema de cache de rutas calculadas"
echo "- Configuraciones del sistema"
echo "- Tablas para historial de consultas"
echo
echo "Características del sistema:"
echo "- Distancias calculadas con fórmula de Haversine"
echo "- Conexiones bidireccionales automáticas"
echo "- Cache inteligente para optimizar rendimiento"
echo "- Historial de rutas calculadas"
echo "- Soporte completo para caracteres especiales (UTF-8)"
echo "- Nombres de lugares con tildes y acentos correctos"
echo
echo "Puedes ahora ejecutar tu aplicación Java con todas las"
echo "funcionalidades de cache y optimización implementadas."
echo