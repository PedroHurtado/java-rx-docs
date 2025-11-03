# Tema 2: Introducción a ReactiveX y RxJava 2

## Objetivos del Tema
- Comprender qué es ReactiveX y su importancia en la programación reactiva
- Conocer la historia y evolución de RxJava
- Entender las diferencias entre RxJava 1 y RxJava 2
- Configurar el entorno de desarrollo para trabajar con RxJava 2
- Crear el primer programa reactivo con RxJava

---

## 1. ¿Qué es ReactiveX?

**ReactiveX** (Reactive Extensions) es una biblioteca para componer programas asíncronos y basados en eventos mediante el uso de secuencias observables. Combina lo mejor del patrón Observer, el patrón Iterator y la programación funcional.

### Características principales de ReactiveX

**Composable**: Permite encadenar operadores para crear flujos de datos complejos de manera declarativa.

**Flexible**: Soporta secuencias de cualquier tipo de dato con el mismo conjunto de operadores.

**Sin opiniones fuertes**: Funciona con cualquier framework o librería sin imponer restricciones arquitectónicas.

**Tolerante a errores**: Proporciona mecanismos integrados para el manejo de errores en flujos asíncronos.

**Concurrente**: Facilita la gestión de hilos y la programación concurrente de forma sencilla.

### El Manifiesto Reactivo

ReactiveX se alinea con los principios del Manifiesto Reactivo, que define sistemas reactivos como aquellos que son:

- **Responsive** (Responsivos): Responden rápidamente bajo todas las condiciones
- **Resilient** (Resilientes): Se mantienen disponibles ante fallos
- **Elastic** (Elásticos): Se adaptan a diferentes cargas de trabajo
- **Message Driven** (Orientados a mensajes): Utilizan paso de mensajes asíncrono

---

## 2. Historia de RxJava

### Cronología

**2012**: Microsoft lanza Reactive Extensions para .NET (Rx.NET).

**2013**: Netflix crea RxJava como port de Rx.NET para la JVM, lanzando la versión 0.x.

**2014**: Se lanza RxJava 1.0, ganando rápida adopción en la comunidad Java.

**2016**: Comienza el desarrollo de RxJava 2.x con importantes cambios arquitectónicos.

**2016 (Noviembre)**: Lanzamiento oficial de RxJava 2.0.

**2018-2021**: Evolución continua con versiones 2.x mejorando rendimiento y añadiendo características.

**2024**: RxJava 3.x es la versión actual, aunque RxJava 2.x sigue siendo ampliamente utilizado.

### ¿Por qué Netflix creó RxJava?

Netflix enfrentaba desafíos significativos en su arquitectura de microservicios:

- Necesidad de realizar múltiples llamadas a APIs de forma eficiente
- Gestión compleja de operaciones asíncronas
- Dificultad para componer y coordinar operaciones concurrentes
- Manejo de errores en sistemas distribuidos

RxJava proporcionó una solución elegante para estos problemas, permitiendo componer flujos asíncronos de manera declarativa.

---

## 3. RxJava 2: Características Principales

### 3.1. Arquitectura basada en Reactive Streams

RxJava 2 implementa la especificación **Reactive Streams**, que define un estándar para procesamiento asíncrono de flujos con contrapresión (backpressure).

La especificación Reactive Streams define cuatro interfaces:

```java
Publisher<T>      // Productor de elementos
Subscriber<T>     // Consumidor de elementos
Subscription      // Vínculo entre Publisher y Subscriber
Processor<T,R>    // Procesador que actúa como ambos
```

### 3.2. Tipos Observables en RxJava 2

RxJava 2 introduce varios tipos de observables, cada uno optimizado para casos de uso específicos:

**Observable<T>**: Flujo de 0 a N elementos, sin backpressure. Ideal para eventos de UI, streams pequeños.

**Flowable<T>**: Flujo de 0 a N elementos, con backpressure. Recomendado para grandes volúmenes de datos.

**Single<T>**: Emite exactamente un elemento o un error. Perfecto para operaciones que devuelven un único resultado.

**Maybe<T>**: Emite 0 o 1 elemento, o un error. Útil para operaciones que pueden no devolver resultado.

**Completable**: No emite elementos, solo señal de completado o error. Ideal para operaciones sin valor de retorno.

### 3.3. Null Safety

RxJava 2 no permite valores `null` en los flujos. Intentar emitir `null` lanzará una `NullPointerException`. Esto previene errores sutiles y mejora la robustez del código.

Para representar ausencia de valor, se pueden usar:

- `Optional<T>` (Java 8+)
- Objetos especiales como `Empty` o `None`
- El tipo `Maybe<T>` para representar 0 o 1 elementos

---

## 4. Diferencias entre RxJava 1 y RxJava 2

### Tabla Comparativa

| Aspecto | RxJava 1.x | RxJava 2.x |
|---------|-----------|-----------|
| **Reactive Streams** | No implementa | Implementa la especificación |
| **Backpressure** | Opcional y problemático | Separado en Observable/Flowable |
| **Null Safety** | Permite nulls | No permite nulls |
| **Tipos** | Observable y Single | Observable, Flowable, Single, Maybe, Completable |
| **Paquetes** | `rx.*` | `io.reactivex.*` |
| **Action/Func** | `Action0-9`, `Func0-9` | Interfaces funcionales de Java 8 |
| **Compatibilidad** | - | No compatible con RxJava 1 |

### Cambios Importantes

**Nombres de métodos**: Algunos operadores cambiaron de nombre para mayor claridad (ej: `flatMap` mantiene, pero `toList()` ahora devuelve `Single<List<T>>`).

**API funcional**: RxJava 2 usa interfaces funcionales de Java 8 en lugar de las clases `Func` y `Action`.

**Rendimiento**: Mejoras significativas en rendimiento y menor consumo de memoria.

**Interoperabilidad**: La librería `rxjava-interop` permite trabajar con ambas versiones en el mismo proyecto durante la migración.

---

## 5. Instalación y Configuración

### 5.1. Prerequisitos

- Java 8 o superior (recomendado Java 11+)
- Maven 3.6+ o Gradle 6+
- IDE: IntelliJ IDEA, Eclipse o VS Code

### 5.2. Configuración con Maven

Agregar la dependencia en el archivo `pom.xml`:

```xml
<dependencies>
    <!-- RxJava 2 -->
    <dependency>
        <groupId>io.reactivex.rxjava2</groupId>
        <artifactId>rxjava</artifactId>
        <version>2.2.21</version>
    </dependency>
    
    <!-- Opcional: RxJava Extensions para utilidades adicionales -->
    <dependency>
        <groupId>com.github.akarnokd</groupId>
        <artifactId>rxjava2-extensions</artifactId>
        <version>0.20.10</version>
    </dependency>
</dependencies>
```

### 5.3. Configuración con Gradle

Agregar en el archivo `build.gradle`:

```gradle
dependencies {
    // RxJava 2
    implementation 'io.reactivex.rxjava2:rxjava:2.2.21'
    
    // Opcional: RxJava Extensions
    implementation 'com.github.akarnokd:rxjava2-extensions:0.20.10'
}
```

### 5.4. Verificación de la instalación

Crear una clase simple para verificar:

```java
import io.reactivex.Observable;

public class VerificacionRxJava {
    public static void main(String[] args) {
        Observable.just("¡RxJava 2 funciona correctamente!")
                .subscribe(System.out::println);
    }
}
```

Si imprime el mensaje, la instalación es correcta.

---

## 6. Primer Programa con RxJava 2

### 6.1. Estructura básica

Todo programa RxJava sigue este patrón básico:

```
1. Crear un Observable (fuente de datos)
2. Aplicar operadores (transformación/filtrado)
3. Suscribirse (consumir los datos)
```

### 6.2. Ejemplo Simple: "Hello World"

```java
import io.reactivex.Observable;

public class HolaMundoRx {
    public static void main(String[] args) {
        // 1. Crear Observable
        Observable<String> observable = Observable.just(
            "Hola",
            "Mundo",
            "Reactivo"
        );
        
        // 2. Suscribirse y consumir
        observable.subscribe(
            palabra -> System.out.println(palabra),  // onNext
            error -> System.err.println("Error: " + error),  // onError
            () -> System.out.println("¡Completado!")  // onComplete
        );
    }
}
```

**Salida**:
```
Hola
Mundo
Reactivo
¡Completado!
```

### 6.3. Ejemplo con Transformación

```java
import io.reactivex.Observable;

public class TransformacionBasica {
    public static void main(String[] args) {
        Observable.range(1, 5)
                .map(n -> n * n)  // Elevar al cuadrado
                .filter(n -> n > 10)  // Filtrar mayores a 10
                .subscribe(
                    numero -> System.out.println("Resultado: " + numero),
                    error -> System.err.println("Error: " + error),
                    () -> System.out.println("Proceso completado")
                );
    }
}
```

**Salida**:
```
Resultado: 16
Resultado: 25
Proceso completado
```

### 6.4. Componentes Fundamentales

**Observable**: La fuente que emite elementos. Se crea con métodos factory como `just()`, `from()`, `range()`, `create()`.

**Observer/Consumer**: El consumidor que recibe los elementos. Define acciones para `onNext`, `onError` y `onComplete`.

**Subscription**: La conexión entre Observable y Observer. Permite cancelar la suscripción.

**Operadores**: Funciones que transforman, filtran o combinan observables (`map`, `filter`, `flatMap`, etc.).

**Schedulers**: Controlan en qué hilo se ejecutan las operaciones (`Schedulers.io()`, `Schedulers.computation()`, etc.).

---

## 7. El Patrón Observer en RxJava

### Contrato Observer

El patrón Observer en RxJava define un contrato claro:

```java
interface Observer<T> {
    void onSubscribe(Disposable d);  // Se llama al suscribirse
    void onNext(T value);            // Se llama por cada elemento
    void onError(Throwable e);       // Se llama si hay error (terminal)
    void onComplete();               // Se llama al completar (terminal)
}
```

**Eventos terminales**: `onError` y `onComplete` son mutuamente excluyentes. Una vez que ocurre uno, no se emiten más elementos.

### Diagrama de Flujo

```
Observable:  ---1---2---3---4---|-->
                ↓   ↓   ↓   ↓   ↓
Observer:    onNext(1)
             onNext(2)
             onNext(3)
             onNext(4)
             onComplete()
```

---

## 8. Conceptos Clave

### 8.1. Cold vs Hot Observables

**Cold Observable**: 
- Emite elementos solo cuando hay un observador suscrito
- Cada observador recibe la secuencia completa desde el inicio
- Ejemplo: leer un archivo, llamada HTTP

```java
Observable<Integer> cold = Observable.range(1, 3);
cold.subscribe(i -> System.out.println("Observer 1: " + i));
cold.subscribe(i -> System.out.println("Observer 2: " + i));
// Cada observador recibe 1, 2, 3
```

**Hot Observable**:
- Emite elementos independientemente de si hay observadores
- Los observadores reciben solo elementos emitidos después de su suscripción
- Ejemplo: eventos de mouse, streams de precios

```java
ConnectableObservable<Long> hot = Observable.interval(1, TimeUnit.SECONDS).publish();
hot.connect();  // Comienza a emitir
hot.subscribe(i -> System.out.println("Observer 1: " + i));
Thread.sleep(3000);
hot.subscribe(i -> System.out.println("Observer 2: " + i));
// Observer 1 recibe 0,1,2,3,4...
// Observer 2 recibe solo desde 3,4,5...
```

### 8.2. Backpressure

**Backpressure** es el mecanismo para manejar situaciones donde el productor emite elementos más rápido de lo que el consumidor puede procesarlos.

**Sin backpressure** (Observable): Si el consumidor es lento, los elementos se acumulan en memoria.

**Con backpressure** (Flowable): El consumidor puede indicar cuántos elementos puede manejar, evitando desbordamiento de memoria.

```java
// Flowable con backpressure
Flowable.range(1, 1_000_000)
    .onBackpressureBuffer(10)  // Buffer limitado
    .observeOn(Schedulers.io())
    .subscribe(
        i -> {
            Thread.sleep(100);  // Procesamiento lento
            System.out.println(i);
        },
        Throwable::printStackTrace
    );
```

### 8.3. Schedulers (Introducción)

Los **Schedulers** controlan en qué hilo se ejecutan las operaciones:

- `Schedulers.io()`: Para operaciones I/O (lectura de archivos, red)
- `Schedulers.computation()`: Para operaciones intensivas de CPU
- `Schedulers.newThread()`: Crea un nuevo hilo para cada tarea
- `Schedulers.single()`: Un único hilo worker para tareas secuenciales
- `Schedulers.trampoline()`: Ejecuta tareas en el hilo actual en cola

Ejemplo básico:

```java
Observable.just("Tarea")
    .subscribeOn(Schedulers.io())        // Se suscribe en hilo I/O
    .observeOn(Schedulers.computation()) // Se observa en hilo computation
    .subscribe(System.out::println);
```

---

## 9. Casos de Uso de RxJava 2

### Aplicaciones Backend
- Procesamiento paralelo de peticiones HTTP
- Integración de múltiples microservicios
- Procesamiento de streams de datos en tiempo real

### Aplicaciones Android
- Manejo de eventos de UI
- Llamadas a APIs REST
- Gestión de operaciones asíncronas

### Procesamiento de Datos
- ETL (Extract, Transform, Load)
- Análisis de logs en tiempo real
- Procesamiento de eventos financieros

### Internet de las Cosas (IoT)
- Procesamiento de streams de sensores
- Agregación de datos de múltiples dispositivos
- Respuesta a eventos en tiempo real

---

## 10. Ventajas y Desventajas

### Ventajas

**Código más limpio y legible**: Programación declarativa reduce complejidad.

**Gestión simplificada de asincronía**: No más callbacks anidados (callback hell).

**Composición poderosa**: Operadores permiten crear flujos complejos fácilmente.

**Manejo robusto de errores**: Mecanismos integrados para propagación y recuperación de errores.

**Concurrencia simplificada**: Schedulers abstraen la gestión de hilos.

**Reutilización**: Operadores estándar aplicables a cualquier tipo de flujo.

### Desventajas

**Curva de aprendizaje**: Paradigma diferente al imperativo tradicional.

**Debugging complejo**: Stacktraces pueden ser difíciles de interpretar.

**Sobrecarga inicial**: Para operaciones simples puede ser excesivo.

**Tamaño de la librería**: Añade dependencias adicionales al proyecto.

**Rendimiento**: En algunos casos, el overhead de RxJava puede afectar rendimiento en operaciones críticas.

---

## 11. Buenas Prácticas Iniciales

**Elegir el tipo correcto**: Usa Observable para UI y pequeños streams, Flowable para grandes volúmenes de datos.

**Gestionar las suscripciones**: Siempre disponer (`dispose()`) las suscripciones para evitar memory leaks.

```java
Disposable disposable = observable.subscribe(data -> process(data));
// Cuando ya no se necesite
disposable.dispose();
```

**No bloquear el hilo principal**: En aplicaciones Android, usar `subscribeOn()` y `observeOn()` adecuadamente.

**Manejo de errores**: Siempre proporcionar un handler de errores en las suscripciones.

```java
observable.subscribe(
    data -> process(data),
    error -> handleError(error)  // Siempre manejar errores
);
```

**Evitar side effects**: Los operadores deben ser funciones puras sin efectos secundarios cuando sea posible.

---

## Resumen del Tema

En este tema hemos aprendido:

- ReactiveX es una biblioteca para programación asíncrona y basada en eventos usando secuencias observables
- RxJava 2 es la implementación para JVM creada por Netflix
- RxJava 2 implementa Reactive Streams y añade backpressure, null safety y múltiples tipos observables
- Los cinco tipos principales son Observable, Flowable, Single, Maybe y Completable
- La estructura básica es: crear Observable → aplicar operadores → suscribirse
- Los conceptos clave incluyen Cold/Hot Observables, Backpressure y Schedulers
- RxJava proporciona una forma elegante de manejar asincronía y composición de operaciones

---

## Referencias de Interés

### Documentación Oficial

**RxJava 2 GitHub**: [https://github.com/ReactiveX/RxJava](https://github.com/ReactiveX/RxJava)
- Repositorio oficial con código fuente y ejemplos

**RxJava 2 Wiki**: [https://github.com/ReactiveX/RxJava/wiki](https://github.com/ReactiveX/RxJava/wiki)
- Documentación completa y guías detalladas

**ReactiveX.io**: [http://reactivex.io/](http://reactivex.io/)
- Portal principal de ReactiveX con diagramas de operadores (marble diagrams)

**Javadoc RxJava 2**: [http://reactivex.io/RxJava/2.x/javadoc/](http://reactivex.io/RxJava/2.x/javadoc/)
- API completa documentada

### Reactive Streams

**Reactive Streams Specification**: [https://www.reactive-streams.org/](https://www.reactive-streams.org/)
- Especificación oficial de Reactive Streams

**Reactive Manifesto**: [https://www.reactivemanifesto.org/](https://www.reactivemanifesto.org/)
- Principios de sistemas reactivos

### Tutoriales y Guías

**RxJava Tutorial - Vogella**: [https://www.vogella.com/tutorials/RxJava/article.html](https://www.vogella.com/tutorials/RxJava/article.html)
- Tutorial completo para principiantes

**Baeldung RxJava Guide**: [https://www.baeldung.com/rx-java](https://www.baeldung.com/rx-java)
- Guías prácticas con ejemplos

**RxJava by Example**: [https://github.com/politrons/reactive](https://github.com/politrons/reactive)
- Repositorio con 100+ ejemplos prácticos

### Libros Recomendados

**"Reactive Programming with RxJava"** por Tomasz Nurkiewicz y Ben Christensen (O'Reilly)
- Libro oficial escrito por creadores de RxJava

**"Learning RxJava"** por Thomas Nield (Packt)
- Excelente para principiantes con ejemplos prácticos

**"RxJava for Android Developers"** por Timo Tuominen (Packt)
- Enfocado en desarrollo Android

### Herramientas

**RxMarbles**: [https://rxmarbles.com/](https://rxmarbles.com/)
- Visualización interactiva de operadores RxJava

**RxJava Playground**: [https://github.com/politrons/reactive](https://github.com/politrons/reactive)
- Entorno para experimentar con RxJava

### Videos y Cursos

**"Reactive Programming with RxJava" - O'Reilly**: Curso en video de los autores del libro

**"RxJava 2.0" - Udemy**: Varios cursos disponibles para diferentes niveles

**Netflix Tech Blog**: [https://netflixtechblog.com/](https://netflixtechblog.com/)
- Artículos sobre cómo Netflix usa RxJava en producción

### Comunidad

**Stack Overflow - RxJava Tag**: [https://stackoverflow.com/questions/tagged/rx-java2](https://stackoverflow.com/questions/tagged/rx-java2)
- Comunidad activa para resolver dudas

**Gitter Chat**: [https://gitter.im/ReactiveX/RxJava](https://gitter.im/ReactiveX/RxJava)
- Chat en tiempo real con la comunidad

**Reddit r/reactive**: [https://www.reddit.com/r/reactive/](https://www.reddit.com/r/reactive/)
- Discusiones sobre programación reactiva

### Blogs y Artículos

**Dan Lew Codes**: [https://blog.danlew.net/](https://blog.danlew.net/)
- Excelentes artículos sobre RxJava y Android

**Advanced RxJava**: [http://akarnokd.blogspot.com/](http://akarnokd.blogspot.com/)
- Blog del contribuidor principal de RxJava

---

**Próximo tema**: Tema 3 - Observables y Observers (profundización en los tipos y su uso)