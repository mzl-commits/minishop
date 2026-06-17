# Reporte de Hallazgos y Acciones de Mejora - Laboratorio 14

A continuación se detallan los hallazgos del análisis estático realizado sobre el código fuente del proyecto **MiniShop**, completando la tabla de evidencias del laboratorio y planteando acciones concretas de mejora técnica.

---

## 1. Tabla de Cobertura JaCoCo (Local)

| Clase | Cobertura de Líneas | Métodos Sin Cobertura | Observación |
| :--- | :---: | :--- | :--- |
| `ProductService` | **100.00 %** | Ninguno | Cobertura completa por pruebas unitarias de servicio. |
| `ProductController` | **100.00 %** | Ninguno | Cobertura completa por pruebas unitarias e integración de API. |
| `ProductRepository` | **100.00 %** | Ninguno | Cobertura completa por pruebas unitarias de base de datos H2. |
| `Product` | **100.00 %** | `equals`, `hashCode`, `toString`, `canEqual` | Los métodos generados por Lombok no se invocan directamente en los flujos de prueba (lo cual es normal para código boilerplate). |
| `MinishopApplication` | **33.33 %** | `main` | Clase de arranque estándar de Spring Boot; el método principal `main` no es ejecutado durante los tests unitarios. |

* **Cobertura Total del Proyecto**: **93.33 %** (Cumple y supera ampliamente el umbral del **70%** configurado en JaCoCo).

---

## 2. Tabla de Hallazgos Estáticos (Bugs y Code Smells)

| Categoría | Cantidad Encontrada | Severidad más Alta | Clase más Afectada | Acción Recomendada |
| :--- | :---: | :--- | :--- | :--- |
| **Bugs Potenciales** | 1 | Major | `ProductService` | Añadir controles de nulabilidad (`null checks`) antes de las comparaciones de tipos empaquetados (`Double`, `Integer`). |
| **Vulnerabilidades** | 0 | Ninguna | Ninguna | No se detectaron vulnerabilidades de seguridad inmediatas en las librerías o código. |
| **Code Smells** | 3 | Major | `ProductController`, `ProductService` | 1. Implementar excepciones personalizadas.<br>2. Eliminar manejadores de excepciones genéricos en controladores.<br>3. Mudar el manejo de excepciones a un `@ControllerAdvice`. |
| **Duplicación** | 0 % | Ninguna | Ninguna | No hay bloques de código duplicados. |

---

## 3. Acciones de Mejora Identificadas y Justificadas

A partir del análisis estático del código fuente, se identifican las siguientes oportunidades de mejora:

### Acción 1: Evitar el lanzamiento de excepciones genéricas (`RuntimeException`)
* **Ubicación**: `ProductService.java` (Línea 36)
* **Código Actual**: 
  ```java
  public Product findById(Long id) {
      return productRepository.findById(id)
              .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + id));
  }
  ```
* **Problema**: Lanzar `RuntimeException` es una mala práctica de diseño. Es demasiado genérico y hace imposible que la capa superior (el controlador) distinga entre un error de "Entidad no encontrada" (404) y otros fallos de ejecución interna (500).
* **Solución propuesta**: Crear una excepción de dominio llamada `ProductNotFoundException` que extienda de una excepción más específica y anotarla con `@ResponseStatus(HttpStatus.NOT_FOUND)`.
  ```java
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public class ProductNotFoundException extends RuntimeException {
      public ProductNotFoundException(String message) {
          super(message);
      }
  }
  ```

### Acción 2: Eliminar el manejador genérico de `RuntimeException` en la API
* **Ubicación**: `ProductController.java` (Líneas 36-39)
* **Código Actual**:
  ```java
  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
  }
  ```
* **Problema**: Este manejador captura de forma indiscriminada cualquier excepción de ejecución (incluyendo errores de conexión de base de datos, desbordamientos, errores lógicos o valores nulos) y los expone directamente al cliente HTTP con el mensaje interno de la excepción. Esto constituye una fuga de información sensible (vulnerabilidad de seguridad) y devuelve un formato plano (`text/plain`) en lugar de JSON estructurado.
* **Solución propuesta**:
  1. Utilizar un `@ControllerAdvice` global.
  2. Controlar excepciones de forma específica (ej. `ProductNotFoundException`, `MethodArgumentNotValidException`).
  3. Retornar un objeto JSON estándar que contenga campos como `timestamp`, `status`, `error` y `message`.

### Acción 3: Agregar validaciones de nulos en las propiedades del producto
* **Ubicación**: `ProductService.java` (Líneas 21 y 24)
* **Código Actual**:
  ```java
  public Product save(Product product) {
      if (product.getPrice() <= MIN_PRICE) { ... }
      if (product.getStock() < MIN_STOCK) { ... }
  }
  ```
* **Problema**: Los atributos `price` y `stock` del modelo `Product` se declaran como objetos (`Double` e `Integer`). Si un cliente envía una petición HTTP con un producto cuyo precio o stock sea nulo, el método intentará hacer un desempaquetado automático (`unboxing`) a tipo primitivo (`double` e `int`), arrojando inmediatamente un `NullPointerException` (un bug crítico) antes de ejecutar nuestras validaciones.
* **Solución propuesta**: Añadir controles de nulabilidad explícitos:
  ```java
  public Product save(Product product) {
      if (product.getPrice() == null || product.getPrice() <= MIN_PRICE) {
          throw new IllegalArgumentException("El precio debe ser mayor a cero");
      }
      if (product.getStock() == null || product.getStock() < MIN_STOCK) {
          throw new IllegalArgumentException("El stock no puede ser negativo");
      }
      return productRepository.save(product);
  }
  ```
