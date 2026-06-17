# Respuestas a las Preguntas de Reflexión - Laboratorio 13

A continuación se presentan las respuestas detalladas a las preguntas planteadas en la sección 10 del laboratorio de **Integración Continua con GitHub Actions**:

---

### 1. ¿Qué diferencia existe entre ejecutar `mvn test` y `mvn verify`? ¿Qué pruebas ejecuta cada uno?
* **`mvn test`**: Ejecuta las pruebas unitarias del proyecto. Por defecto, compila y ejecuta las clases de prueba que siguen el patrón de nomenclatura de JUnit (como `*Test.java`) a través del plugin `maven-surefire-plugin`. Se detiene al terminar las pruebas unitarias y no empaqueta la aplicación.
* **`mvn verify`**: Ejecuta un ciclo de vida mucho más completo. Además de compilar y correr las pruebas unitarias, empaqueta el proyecto (generando el archivo `.jar` en la carpeta `target/`) y ejecuta las pruebas de integración (configuradas usualmente con el `maven-failsafe-plugin` para buscar clases tipo `*IT.java` o `*IntegrationTest.java`). Finalmente, realiza verificaciones adicionales de calidad para asegurar que el paquete sea correcto y seguro para producción.

---

### 2. ¿Por qué el pipeline usa `ubuntu-latest` como entorno de ejecución en lugar de Windows?
* **Velocidad de arranque y eficiencia**: Las máquinas virtuales basadas en Linux (`ubuntu-latest`) en GitHub Actions se inicializan y aprovisionan mucho más rápido que las de Windows, acortando el tiempo total del pipeline.
* **Consumo de minutos (Costo)**: Los runners de Windows en GitHub Actions consumen el doble de minutos del plan gratuito por cada minuto de ejecución en comparación con los runners de Linux.
* **Paridad con Producción**: La gran mayoría de los entornos de producción reales (AWS, GCP, Kubernetes, contenedores Docker) corren sobre sistemas Linux. Ejecutar el pipeline sobre Ubuntu garantiza que el código se pruebe en un entorno similar al que se usará en producción.

---

### 3. ¿Qué ocurriría si el paso de compilación fallara? ¿Se ejecutarían los pasos de pruebas?
* Si el paso de compilación (`mvn compile`) falla, el pipeline se detiene inmediatamente con estado de error (❌) y **no se ejecutan** los pasos posteriores de pruebas (`mvn test` y `mvn verify`). 
* Por defecto, GitHub Actions aborta el workflow cuando un paso previo retorna un código de salida diferente de cero, protegiendo recursos y notificando al desarrollador que el código ni siquiera es válido sintácticamente. El único paso que se ejecutaría sería el de guardar artefactos debido a la directiva `if: always()`, pero este paso no encontraría reportes y terminaría sin efectos.

---

### 4. El workflow se activa con `push` y `pull_request`. ¿En qué situación sería más útil activarlo con `pull_request`?
* Es especialmente útil en flujos de trabajo colaborativos y desarrollo basado en ramas (Git Flow o Trunk Based Development).
* Cuando un desarrollador trabaja en una rama de características (feature branch) y solicita fusionar sus cambios mediante un **Pull Request (PR)** hacia la rama principal (`main`), el pipeline se ejecuta para validar que los nuevos cambios compilan y no rompen ninguna prueba existente.
* Esto actúa como una barrera de control: los revisores de código del equipo pueden ver directamente en GitHub si el PR "está en verde" antes de aprobar el merge, impidiendo que código defectuoso rompa la rama principal compartida.

---

### 5. ¿Qué ventaja tiene el caché de dependencias Maven (`cache: maven`) en términos de tiempo de ejecución?
* **Evita descargas redundantes**: En un entorno limpio de CI, Maven descarga todas las dependencias del `pom.xml` desde Maven Central en cada build, lo que puede tardar varios minutos y consumir mucho ancho de banda.
* Al usar `cache: maven`, GitHub Actions almacena el directorio local `.m2/repository` del desarrollador. En las siguientes ejecuciones del pipeline, las librerías se recuperan instantáneamente desde la caché de GitHub en lugar de descargarse de internet, reduciendo el tiempo de ejecución del pipeline de varios minutos a unos pocos segundos.
