# Tema 8: Fluidos y Contrapresión

## Introducción

En programación reactiva, la **contrapresión** (backpressure) es un mecanismo fundamental que permite al consumidor controlar la velocidad a la que recibe datos del productor. Esto es crucial cuando el productor genera datos más rápido de lo que el consumidor puede procesarlos.

Los **Flowables** en RxJava 2 y 3 son streams reactivos que implementan el protocolo Reactive Streams, proporcionando soporte nativo para contrapresión.

## ¿Por qué necesitamos Flowable?

### Problema con Observable

Los `Observable` no tienen soporte para contrapresión. Si un productor emite eventos más rápido de lo que el observador puede procesarlos, se pueden acumular en memoria, causando:

- **OutOfMemoryError**: Acumulación excesiva de eventos en buffers
- **MissingBackpressureException**: Cuando los buffers internos se desbordan
- Consumo ineficiente de recursos

### Solución: Flowable

`Flowable` implementa el estándar Reactive Streams (`org.reactivestreams.Publisher`) y proporciona:

- Control de flujo bidireccional
- Estrategias de contrapresión configurables
- Gestión eficiente de memoria
- Interoperabilidad con otras librerías reactivas

## Diferencias entre Observable y Flowable

| Característica | Observable | Flowable |
|---------------|-----------|----------|
| **Contrapresión** | No soportada | Soportada nativamente |
| **Protocolo** | RxJava específico | Reactive Streams |
| **Uso recomendado** | < 1000 elementos | Grandes volúmenes de datos |
| **Overhead** | Menor | Ligeramente mayor |
| **Sincronización** | Menos compleja | Thread-safe por diseño |

## Creación de Flowables

### Métodos básicos de creación

```java
// 1. just() - emite elementos específicos
Flowable<Integer> flowable1 = Flowable.just(1, 2, 3, 4, 5);

// 2. fromArray() - desde un array
Flowable<String> flowable2 = Flowable.fromArray("A", "B", "C");

// 3. fromIterable() - desde colecciones
List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
Flowable<Integer> flowable3 = Flowable.fromIterable(list);

// 4. range() - secuencia numérica
Flowable<Integer> flowable4 = Flowable.range(1, 100);

// 5. interval() - emisiones periódicas
Flowable<Long> flowable5 = Flowable.interval(1, TimeUnit.SECONDS);
```

### Creación personalizada con create()

```java
Flowable<Integer> flowable = Flowable.create(emitter -> {
    for (int i = 0; i < 1000; i++) {
        if (emitter.isCancelled()) {
            return;
        }
        emitter.onNext(i);
    }
    emitter.onComplete();
}, BackpressureStrategy.BUFFER);
```

## Estrategias de Contrapresión

RxJava proporciona cinco estrategias principales para manejar situaciones donde el productor es más rápido que el consumidor:

### 1. BackpressureStrategy.BUFFER

**Descripción**: Almacena todos los eventos en un buffer sin límite hasta que el consumidor los procese.

**Cuándo usar**:
- Cuando se sabe que el volumen de datos es manejable
- En escenarios donde no se pueden perder datos
- Cuando hay ráfagas temporales de datos

**Riesgo**: Puede causar `OutOfMemoryError` si el buffer crece indefinidamente.

```java
Flowable.create(emitter -> {
    for (int i = 0; i < 100000; i++) {
        emitter.onNext(i);
    }
    emitter.onComplete();
}, BackpressureStrategy.BUFFER);
```

### 2. BackpressureStrategy.DROP

**Descripción**: Descarta los eventos más recientes cuando el consumidor no puede procesarlos.

**Cuándo usar**:
- Datos en tiempo real donde solo importa el valor más actual
- Telemetría o métricas donde perder algunos datos es aceptable
- Sensores que emiten lecturas continuas

```java
Flowable.create(emitter -> {
    for (int i = 0; i < 100000; i++) {
        emitter.onNext(i);
    }
    emitter.onComplete();
}, BackpressureStrategy.DROP);
```

### 3. BackpressureStrategy.LATEST

**Descripción**: Mantiene solo el último evento emitido, descartando los anteriores no procesados.

**Cuándo usar**:
- Cuando solo interesa el valor más reciente
- Actualizaciones de UI
- Precio de acciones en tiempo real

```java
Flowable.create(emitter -> {
    for (int i = 0; i < 100000; i++) {
        emitter.onNext(i);
    }
    emitter.onComplete();
}, BackpressureStrategy.LATEST);
```

### 4. BackpressureStrategy.ERROR

**Descripción**: Emite un error `MissingBackpressureException` cuando el consumidor no puede seguir el ritmo.

**Cuándo usar**:
- Desarrollo y debugging
- Cuando la contrapresión no debería ocurrir y queremos detectarla
- Escenarios donde perder datos es inaceptable

```java
Flowable.create(emitter -> {
    for (int i = 0; i < 100000; i++) {
        emitter.onNext(i);
    }
    emitter.onComplete();
}, BackpressureStrategy.ERROR);
```

### 5. BackpressureStrategy.MISSING

**Descripción**: No aplica ninguna estrategia. El comportamiento depende del operador downstream.

**Cuándo usar**:
- Cuando se aplicará un operador de contrapresión específico después
- Casos avanzados con control manual

```java
Flowable.create(emitter -> {
    for (int i = 0; i < 100000; i++) {
        emitter.onNext(i);
    }
    emitter.onComplete();
}, BackpressureStrategy.MISSING)
    .onBackpressureBuffer(100); // Aplicar estrategia después
```

## Operadores de Contrapresión

### onBackpressureBuffer()

Crea un buffer con capacidad configurable:

```java
flowable
    .onBackpressureBuffer(
        1000,                    // capacidad máxima
        () -> {                  // acción al desbordar
            System.out.println("Buffer overflow!");
        },
        BackpressureOverflowStrategy.DROP_OLDEST  // estrategia de desbordamiento
    )
    .subscribe(System.out::println);
```

**Estrategias de desbordamiento**:
- `DROP_OLDEST`: Descarta el elemento más antiguo
- `DROP_LATEST`: Descarta el elemento más reciente
- `ERROR`: Lanza error

### onBackpressureDrop()

Descarta elementos cuando el downstream no puede procesarlos:

```java
flowable
    .onBackpressureDrop(item -> {
        System.out.println("Dropped: " + item);
    })
    .subscribe(System.out::println);
```

### onBackpressureLatest()

Mantiene solo el último elemento emitido:

```java
flowable
    .onBackpressureLatest()
    .subscribe(System.out::println);
```

## Peticiones (Requests) en Flowable

El consumidor solicita explícitamente cuántos elementos puede procesar:

```java
Flowable.range(1, 1000)
    .subscribe(new Subscriber<Integer>() {
        private Subscription subscription;
        
        @Override
        public void onSubscribe(Subscription s) {
            this.subscription = s;
            subscription.request(10);  // Solicitar 10 elementos inicialmente
        }
        
        @Override
        public void onNext(Integer item) {
            System.out.println("Procesando: " + item);
            // Procesar el elemento...
            subscription.request(1);  // Solicitar el siguiente
        }
        
        @Override
        public void onError(Throwable t) {
            t.printStackTrace();
        }
        
        @Override
        public void onComplete() {
            System.out.println("Completado");
        }
    });
```

## Conversión entre Observable y Flowable

### Observable → Flowable

```java
Observable<Integer> observable = Observable.range(1, 100);

// Conversión con estrategia de contrapresión
Flowable<Integer> flowable = observable.toFlowable(BackpressureStrategy.BUFFER);
```

### Flowable → Observable

```java
Flowable<Integer> flowable = Flowable.range(1, 100);

// Conversión (se pierde soporte de contrapresión)
Observable<Integer> observable = flowable.toObservable();
```

## Patrones Comunes

### 1. Productor rápido con procesamiento lento

```java
Flowable.interval(1, TimeUnit.MILLISECONDS)
    .onBackpressureBuffer(1000)
    .observeOn(Schedulers.computation())
    .map(item -> {
        Thread.sleep(100);  // Simular procesamiento lento
        return item * 2;
    })
    .subscribe(
        System.out::println,
        Throwable::printStackTrace
    );
```

### 2. Lectura de archivo grande

```java
Flowable.generate(() -> new BufferedReader(new FileReader("large-file.txt")),
    (reader, emitter) -> {
        String line = reader.readLine();
        if (line != null) {
            emitter.onNext(line);
        } else {
            emitter.onComplete();
        }
    },
    BufferedReader::close)
    .subscribeOn(Schedulers.io())
    .subscribe(System.out::println);
```

### 3. Procesamiento por lotes (batching)

```java
Flowable.range(1, 1000)
    .buffer(50)  // Agrupar en lotes de 50
    .subscribe(batch -> {
        System.out.println("Procesando lote de " + batch.size() + " elementos");
        // Procesar el lote...
    });
```

## Mejores Prácticas

### 1. Elegir entre Observable y Flowable

- **Usar Observable cuando**:
  - Se tienen menos de 1000 elementos
  - Los eventos son generados por UI (clicks, gestos)
  - La contrapresión no es una preocupación

- **Usar Flowable cuando**:
  - Se trabaja con grandes volúmenes de datos
  - Se leen streams de datos (archivos, red, bases de datos)
  - El productor puede ser significativamente más rápido que el consumidor

### 2. Dimensionar buffers apropiadamente

```java
// Evitar buffers ilimitados
flowable.onBackpressureBuffer()  // ¡Peligroso!

// Mejor: establecer límite
flowable.onBackpressureBuffer(1000)  // Más seguro
```

### 3. Monitorear la contrapresión

```java
flowable
    .doOnRequest(n -> System.out.println("Requested: " + n))
    .subscribe(System.out::println);
```

### 4. Usar operadores de ventana para datos infinitos

```java
Flowable.interval(1, TimeUnit.MILLISECONDS)
    .window(100)  // Ventanas de 100 elementos
    .flatMap(window -> window.toList().toFlowable())
    .subscribe(list -> System.out.println("Ventana: " + list.size()));
```

## Troubleshooting

### Problema: MissingBackpressureException

**Causa**: El productor emite más rápido de lo que el consumidor procesa.

**Solución**:
```java
// Aplicar estrategia de contrapresión
observable.toFlowable(BackpressureStrategy.DROP)
    .subscribe(System.out::println);
```

### Problema: OutOfMemoryError con BUFFER

**Causa**: El buffer crece indefinidamente.

**Solución**:
```java
// Limitar tamaño del buffer
flowable.onBackpressureBuffer(1000, 
    () -> System.out.println("Buffer lleno"),
    BackpressureOverflowStrategy.DROP_OLDEST)
    .subscribe(System.out::println);
```

### Problema: Pérdida de datos con DROP

**Causa**: Se están descartando datos importantes.

**Solución**:
```java
// Usar BUFFER o ralentizar el productor
flowable
    .onBackpressureBuffer()
    .observeOn(Schedulers.computation(), false, 128)
    .subscribe(System.out::println);
```

## Referencias de Interés

### Documentación Oficial
- [RxJava Flowable Documentation](https://reactivex.io/RxJava/3.x/javadoc/io/reactivex/rxjava3/core/Flowable.html)
- [Reactive Streams Specification](http://www.reactive-streams.org/)
- [RxJava Wiki - Backpressure](https://github.com/ReactiveX/RxJava/wiki/Backpressure-(2.0))

### Artículos y Tutoriales
- [Understanding Backpressure in RxJava](https://www.baeldung.com/rxjava-backpressure)
- [Flowable vs Observable - When to use which?](https://medium.com/@vanniktech/rxjava-2-flowable-vs-observable-c9c1a5e1e3f)
- [Advanced RxJava: Backpressure](http://akarnokd.blogspot.com/2015/05/pitfalls-of-operator-implementations.html)

### Videos
- [RxJava 2.0: Flowable and Backpressure](https://www.youtube.com/watch?v=t707RWc51b4)
- [Reactive Streams and Backpressure](https://www.infoq.com/presentations/reactive-streams-backpressure/)

### Libros
- "Reactive Programming with RxJava" - Tomasz Nurkiewicz, Ben Christensen (Capítulo sobre Backpressure)
- "Learning RxJava" - Thomas Nield (Capítulo 6: Backpressure and Flowables)

### Herramientas
- [RxMarbles - Visualización de operadores](https://rxmarbles.com/)
- [RxJava Playground](https://github.com/mutexkid/rxjava-playground)
