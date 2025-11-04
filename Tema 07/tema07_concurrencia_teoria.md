# Tema 7: Concurrencia en RxJava

## Introducción

La concurrencia es uno de los aspectos más poderosos de RxJava. Permite ejecutar operaciones en diferentes hilos de manera declarativa y eficiente, facilitando la gestión de tareas asíncronas sin la complejidad tradicional de la programación concurrente en Java.

## Conceptos Fundamentales

### ¿Qué es un Scheduler?

Un **Scheduler** es un componente de RxJava que controla en qué hilo se ejecutan las operaciones. Los Schedulers abstraen la complejidad de gestionar threads y pools de threads, proporcionando una API simple y declarativa.

### Principio de Operación

Por defecto, RxJava opera en el mismo hilo donde se realiza la suscripción. Sin embargo, podemos modificar este comportamiento usando dos operadores clave:

- **subscribeOn()**: Define el hilo donde se ejecuta la fuente Observable (emisión de datos)
- **observeOn()**: Define el hilo donde se procesan los datos (consumo)

## Schedulers Predefinidos

RxJava proporciona varios Schedulers predefinidos para diferentes escenarios:

### 1. Schedulers.io()

```java
Schedulers.io()
```

**Características:**
- Pool de threads elástico que crece según demanda
- Ideal para operaciones I/O bloqueantes (lectura de archivos, peticiones HTTP, acceso a bases de datos)
- Los threads inactivos se mantienen por un tiempo antes de liberarse
- No tiene límite teórico de threads

**Casos de uso:**
- Llamadas a APIs REST
- Operaciones de lectura/escritura en disco
- Consultas a bases de datos

### 2. Schedulers.computation()

```java
Schedulers.computation()
```

**Características:**
- Pool de threads fijo, limitado al número de procesadores disponibles
- Optimizado para operaciones CPU-intensive
- No debe usarse para operaciones bloqueantes
- Ideal para cálculos matemáticos y procesamiento de datos

**Casos de uso:**
- Cálculos complejos
- Procesamiento de imágenes
- Transformaciones de datos

### 3. Schedulers.newThread()

```java
Schedulers.newThread()
```

**Características:**
- Crea un nuevo thread para cada unidad de trabajo
- Sin reutilización de threads
- Costoso en términos de recursos
- Uso limitado a casos muy específicos

**Casos de uso:**
- Tareas que requieren aislamiento completo
- Operaciones de larga duración que no deben compartir thread

### 4. Schedulers.single()

```java
Schedulers.single()
```

**Características:**
- Un único thread reutilizable
- Garantiza ejecución secuencial
- Útil para serializar operaciones

**Casos de uso:**
- Operaciones que deben ejecutarse en orden estricto
- Acceso a recursos compartidos que requieren sincronización

### 5. Schedulers.trampoline()

```java
Schedulers.trampoline()
```

**Características:**
- Ejecuta tareas en cola en el thread actual
- No crea nuevos threads
- Útil para testing

**Casos de uso:**
- Pruebas unitarias
- Debugging

### 6. Schedulers.from(Executor)

```java
Executor executor = Executors.newFixedThreadPool(10);
Scheduler customScheduler = Schedulers.from(executor);
```

**Características:**
- Permite usar un Executor personalizado
- Mayor control sobre la gestión de threads
- Útil para integración con código legacy

## Operadores de Concurrencia

### subscribeOn()

Especifica el Scheduler en el que se ejecutará la fuente Observable y todas las operaciones upstream.

```java
Observable.just(1, 2, 3)
    .subscribeOn(Schedulers.io())
    .subscribe(System.out::println);
```

**Regla importante:** Solo el primer `subscribeOn()` tiene efecto. Las llamadas subsecuentes son ignoradas.

```java
Observable.just(1, 2, 3)
    .subscribeOn(Schedulers.io())        // Este se aplica
    .subscribeOn(Schedulers.computation()) // Este se ignora
    .subscribe(System.out::println);
```

### observeOn()

Cambia el Scheduler para las operaciones downstream (posteriores).

```java
Observable.just(1, 2, 3)
    .subscribeOn(Schedulers.io())
    .map(i -> i * 2)
    .observeOn(Schedulers.computation())
    .map(i -> i + 1)
    .subscribe(System.out::println);
```

**Regla importante:** Cada `observeOn()` afecta a todas las operaciones siguientes hasta el próximo `observeOn()`.

### Combinando subscribeOn y observeOn

```java
Observable.just("A", "B", "C")
    .subscribeOn(Schedulers.io())          // Emisión en I/O thread
    .map(s -> {
        // Se ejecuta en I/O thread
        return s.toLowerCase();
    })
    .observeOn(Schedulers.computation())   // Cambia a computation
    .map(s -> {
        // Se ejecuta en computation thread
        return s + "!";
    })
    .observeOn(Schedulers.single())        // Cambia a single
    .subscribe(s -> {
        // Se ejecuta en single thread
        System.out.println(s);
    });
```

## Paralelización

### flatMap para Paralelización

`flatMap()` puede ejecutar operaciones en paralelo cuando se combina con `subscribeOn()`:

```java
Observable.range(1, 10)
    .flatMap(i -> 
        Observable.just(i)
            .subscribeOn(Schedulers.computation())
            .map(num -> procesarDato(num))
    )
    .subscribe(System.out::println);
```

### Parallel Flowable

Para paralelización más estructurada, RxJava ofrece `ParallelFlowable`:

```java
Flowable.range(1, 100)
    .parallel()
    .runOn(Schedulers.computation())
    .map(i -> procesarDato(i))
    .sequential()
    .subscribe(System.out::println);
```

## Gestión de Recursos y Buenas Prácticas

### 1. Disposición de Recursos

```java
Disposable disposable = Observable.interval(1, TimeUnit.SECONDS)
    .subscribeOn(Schedulers.io())
    .subscribe(System.out::println);

// Cuando ya no se necesite
disposable.dispose();
```

### 2. CompositeDisposable

Para gestionar múltiples suscripciones:

```java
CompositeDisposable compositeDisposable = new CompositeDisposable();

compositeDisposable.add(
    observable1.subscribe(...)
);

compositeDisposable.add(
    observable2.subscribe(...)
);

// Disponer todas las suscripciones
compositeDisposable.clear();
```

### 3. Evitar Bloqueos

Nunca usar operaciones bloqueantes en `Schedulers.computation()`:

```java
// ❌ INCORRECTO
Observable.just(1)
    .subscribeOn(Schedulers.computation())
    .map(i -> {
        Thread.sleep(1000); // Bloqueante
        return i;
    })
    .subscribe();

// ✅ CORRECTO
Observable.just(1)
    .subscribeOn(Schedulers.io())
    .map(i -> {
        Thread.sleep(1000); // OK en io()
        return i;
    })
    .subscribe();
```

### 4. Limitar Concurrencia

Usar `flatMap` con maxConcurrency:

```java
Observable.range(1, 100)
    .flatMap(i -> 
        Observable.just(i)
            .subscribeOn(Schedulers.io())
            .map(num -> procesarDato(num)),
        8 // Máximo 8 operaciones concurrentes
    )
    .subscribe(System.out::println);
```

## Patrones Comunes

### Patrón 1: Llamada a API + Procesamiento

```java
Observable.fromCallable(() -> llamadaAPI())
    .subscribeOn(Schedulers.io())           // API call en I/O
    .observeOn(Schedulers.computation())    // Procesamiento en computation
    .map(data -> procesarDatos(data))
    .observeOn(AndroidSchedulers.mainThread()) // UI en main thread
    .subscribe(
        result -> actualizarUI(result),
        error -> manejarError(error)
    );
```

### Patrón 2: Múltiples Fuentes Concurrentes

```java
Observable<String> obs1 = Observable.fromCallable(() -> fuente1())
    .subscribeOn(Schedulers.io());

Observable<String> obs2 = Observable.fromCallable(() -> fuente2())
    .subscribeOn(Schedulers.io());

Observable.zip(obs1, obs2, (r1, r2) -> r1 + r2)
    .subscribe(System.out::println);
```

### Patrón 3: Retry con Delay

```java
Observable.fromCallable(() -> operacionFallable())
    .subscribeOn(Schedulers.io())
    .retryWhen(errors -> 
        errors.zipWith(Observable.range(1, 3), (error, attempt) -> attempt)
              .flatMap(attempt -> Observable.timer(attempt, TimeUnit.SECONDS))
    )
    .subscribe();
```

## Debugging de Concurrencia

### Imprimir Thread Actual

```java
Observable.just(1, 2, 3)
    .subscribeOn(Schedulers.io())
    .doOnNext(i -> System.out.println("Emit: " + Thread.currentThread().getName()))
    .observeOn(Schedulers.computation())
    .doOnNext(i -> System.out.println("Process: " + Thread.currentThread().getName()))
    .subscribe(i -> System.out.println("Subscribe: " + Thread.currentThread().getName()));
```

### Hooks de RxJava

```java
RxJavaPlugins.setErrorHandler(e -> {
    System.err.println("Error no manejado: " + e);
});
```

## Consideraciones de Rendimiento

1. **Evitar crear Schedulers innecesarios**: Reutilizar los predefinidos
2. **No abusar de observeOn()**: Cada cambio de thread tiene overhead
3. **Usar backpressure**: Para evitar sobrecarga en cadenas con diferentes velocidades
4. **Medir y optimizar**: Usar herramientas de profiling para identificar cuellos de botella

## Referencias de Interés

### Documentación Oficial
- **RxJava Wiki - Scheduler**: https://github.com/ReactiveX/RxJava/wiki/Scheduler
- **ReactiveX - Schedulers**: http://reactivex.io/documentation/scheduler.html

### Artículos y Tutoriales
- **Threading in RxJava 2**: https://www.baeldung.com/rxjava-threading
- **RxJava Schedulers - Tutorial**: https://www.vogella.com/tutorials/RxJava/article.html

### Libros
- **"Reactive Programming with RxJava"** por Tomasz Nurkiewicz y Ben Christensen - Capítulo sobre Schedulers
- **"Learning RxJava"** por Thomas Nield - Capítulo 6: Concurrency

### Videos y Cursos
- **RxJava: Reactive Extensions for the JVM** - Pluralsight
- **Understanding RxJava Threading** - YouTube (Channel: Coding in Flow)

### Herramientas
- **RxJava Extras**: https://github.com/davidmoten/rxjava-extras - Utilidades adicionales para concurrencia
- **RxJava Debug**: https://github.com/akaita/RxJava2Debug - Debugging de streams reactivos

### Blogs Recomendados
- **Blog de Dan Lew**: https://blog.danlew.net/2014/09/15/grokking-rxjava-part-1/
- **Tomás Ruiz-López Blog**: https://medium.com/@tomas_31960/rxjava-schedulers-5c6f2f24e1b1
