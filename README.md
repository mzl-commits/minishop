# MiniShop REST API

![CI Pipeline](https://github.com/mzl-commits/minishop/actions/workflows/ci.yml/badge.svg)

MiniShop es una API REST pequeña desarrollada en Java con Spring Boot 3 y base de datos en memoria H2. Cuenta con una arquitectura en capas (Controller -> Service -> Repository) y un conjunto robusto de pruebas unitarias y de integración.

## Características
* Registro de productos con validaciones de negocio (precio mayor a cero, stock no negativo).
* Consulta de todos los productos.
* Consulta de productos por ID.
* Base de datos H2 integrada y consola de H2.
* Pipeline de Integración Continua (CI) configurado con GitHub Actions.

## Requisitos
* Java 17 o superior.
* Maven 3.8 o superior.

## Ejecución Local

### Ejecutar la Aplicación
Puedes ejecutar el servidor de desarrollo local de Spring Boot usando:
```bash
mvn spring-boot:run
```

### Ejecutar las Pruebas
* Para ejecutar las pruebas unitarias únicamente:
  ```bash
  mvn test
  ```
* Para ejecutar todas las pruebas (unitarias e integración) y generar el reporte:
  ```bash
  mvn verify
  ```
