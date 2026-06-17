# Respuestas a las Preguntas de Reflexión - Laboratorio 14

### 1. ¿Qué diferencia existe entre un Bug reportado por SonarCloud y un test fallido en JUnit?
* **Test fallido en JUnit (Análisis Dinámico)**: Ocurre en tiempo de ejecución. Indica que un flujo de prueba específico ha fallado porque el comportamiento del software no coincide con la aserción esperada (por ejemplo, devolvió `500` en lugar de `200`, o retornó un valor incorrecto). Valida la lógica de negocio y la funcionalidad.
* **Bug reportado por SonarCloud (Análisis Estático)**: Ocurre en tiempo de compilación/análisis sin ejecutar el código. Indica una estructura o patrón de código defectuoso que tiene una alta probabilidad de causar fallos en producción bajo ciertas condiciones (por ejemplo, una desreferencia de puntero nulo, una variable no inicializada o una fuga de memoria).

---

### 2. ¿Podría un código tener todos los tests en verde y aun así tener bugs reportados por SonarQube?
* **Sí, totalmente**. Los tests JUnit solo validan los escenarios que el desarrollador escribió explícitamente en el código de prueba. Si las pruebas JUnit no cubren escenarios de error, valores nulos, desbordamientos o concurrencia, pasarán con éxito ("en verde"). 
* SonarQube/SonarCloud analiza todos los caminos lógicos posibles y detecta vulnerabilidades matemáticas o estructurales (como usar variables de entrada directamente en consultas SQL o no cerrar un canal de datos) que pasarán desapercibidas por pruebas JUnit simples y limitadas.

---

### 3. Si la cobertura de tu proyecto es del 65%, ¿qué harías primero: escribir nuevas pruebas o corregir los code smells? Justifica tu respuesta.
* **Primero se deben escribir nuevas pruebas** para elevar la cobertura (al menos por encima del 70%-80%). 
* **Justificación**: Corregir "code smells" implica refactorizar el código existente. Si refactorizamos código con baja cobertura (65%), no tenemos una red de seguridad (safety net) que nos avise si el cambio rompió la funcionalidad actual del sistema en la tercera parte no probada (35%). Al subir la cobertura primero, garantizamos que las refactorizaciones posteriores para resolver los smells sean seguras y no introduzcan nuevos bugs no deseados (regresiones).

---

### 4. ¿Qué significa que un método tenga deuda técnica de "2 horas"? ¿Cómo calcularías si vale la pena pagarla ahora?
* **Significado**: Es una métrica estimada por SonarQube basada en el esfuerzo promedio de desarrollo requerido para refactorizar y limpiar ese método, eliminando sus malas prácticas o complejidades cognitivas para llevarlo a un estado estándar de calidad.
* **Cálculo de conveniencia**:
  * **Frecuencia de cambio (Churn)**: Si el método pertenece a una clase que se modifica con frecuencia para agregar nuevas características, vale la pena pagarla ahora porque el código limpio reducirá la fricción de futuros desarrollos.
  * **Criticidad**: Si el método procesa transacciones o lógica central del negocio, pagarla ahora reduce el riesgo de fallos costosos en producción.
  * **Si el código es estable y casi no cambia**: Es mejor no pagarla inmediatamente, ya que el riesgo de tocar código antiguo y estable supera el beneficio teórico de la limpieza inmediata.

---

### 5. ¿Por qué el paso 1 del workflow actualizado usa `fetch-depth: 0`? ¿Qué pasaría si se dejara el valor por defecto?
* **Por qué se usa**: Por defecto, `actions/checkout@v4` realiza un clonado superficial (`shallow clone`) con `fetch-depth: 1` para clonar solo el commit más reciente y ahorrar tiempo. Al definir `fetch-depth: 0`, se descarga el **historial completo de Git** (todos los commits, ramas y etiquetas).
* **Qué pasaría si se dejara por defecto**: SonarCloud no podría realizar el cálculo correcto de métricas de "código nuevo" (New Code) ni correlacionar qué commits o autores introdujeron los bugs o smells (Git Blame). Esto daría como resultado un análisis incompleto, advertencias en el pipeline de SonarCloud y la imposibilidad de evaluar políticas de calidad (Quality Gates) basadas en commits específicos.
