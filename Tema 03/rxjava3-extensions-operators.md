# RxJava3 Extensions - Guía de Operadores

## Información General

**Dependencia:**
```xml
   <dependency>
      <groupId>com.github.akarnokd</groupId>
      <artifactId>rxjava3-extensions</artifactId>
      <version>3.1.1</version>
    </dependency>
```



---

## Operadores de Transformación

### `mapFilter`
Combina las operaciones de map y filter en un solo paso.

```java
Observable.range(1, 10)
    .compose(Observables.mapFilter(
        x -> x * 2,      // transformación
        x -> x > 10      // filtro
    ))
    .subscribe(System.out::println);
```

### `flatMapSync`
FlatMap síncrono sin cambio de scheduler, más eficiente cuando no se requiere asincronía.

```java
Observable.just(1, 2, 3)
    .compose(Observables.flatMapSync(x -> Observable.range(x, 3)))
    .subscribe(System.out::println);
```

### `flatMapAsync`
FlatMap con ejecución asíncrona controlada.

```java
Observable.just("A", "B", "C")
    .compose(Observables.flatMapAsync(
        s -> callApi(s),
        maxConcurrency
    ))
    .subscribe();
```

### `zipLatest`
Combina los últimos valores emitidos de múltiples sources.

```java
Observable<String> result = Observables.zipLatest(
    observable1,
    observable2,
    (a, b) -> a + b
);
```

---

## Operadores de Backpressure

### `onBackpressureTimeout`
Maneja backpressure descartando items que no pueden ser procesados dentro de un timeout.

```java
Flowable.interval(1, TimeUnit.MILLISECONDS)
    .compose(Flowables.onBackpressureTimeout(
        100, 
        TimeUnit.MILLISECONDS,
        Schedulers.computation()
    ))
    .subscribe();
```

### `onBackpressureLatestDelayed`
Entrega el último item disponible con un delay configurable.

```java
Flowable.range(1, 1000)
    .compose(Flowables.onBackpressureLatestDelayed())
    .subscribe();
```

### `rebatchRequests`
Reagrupa peticiones de demanda del downstream.

```java
Flowable.range(1, 100)
    .compose(Flowables.rebatchRequests(10))
    .subscribe();
```

---

## Subjects Especiales

### `UnicastWorkSubject`
Subject optimizado para un único consumidor con cola de trabajo.

```java
UnicastWorkSubject<Integer> subject = UnicastWorkSubject.create();
subject.onNext(1);
subject.onNext(2);
subject.subscribe(System.out::println);
```

### `DispatchWorkSubject`
Distribuye trabajo entre múltiples suscriptores de forma balanceada.

```java
DispatchWorkSubject<Task> dispatcher = DispatchWorkSubject.create();
dispatcher.subscribe(worker1);
dispatcher.subscribe(worker2);
dispatcher.onNext(new Task());
```

### `NonoSubject`
Subject sin valor, solo señales de completado o error.

```java
NonoSubject subject = NonoSubject.create();
subject.subscribe(() -> System.out.println("Completed"));
subject.onComplete();
```

---

## Operadores de Buffer y Window

### `bufferWhile`
Acumula items mientras se cumpla una condición.

```java
Observable.range(1, 20)
    .compose(Observables.bufferWhile(x -> x < 10))
    .subscribe(System.out::println);
```

### `bufferUntil`
Acumula items hasta que se cumpla una condición.

```java
Observable.interval(100, TimeUnit.MILLISECONDS)
    .compose(Observables.bufferUntil(x -> x == 5))
    .subscribe(System.out::println);
```

### `bufferSplit`
Divide el buffer en múltiples streams basado en un predicado.

```java
Observable.range(1, 100)
    .compose(Observables.bufferSplit(
        x -> x % 10 == 0,  // condición de split
        50                  // tamaño máximo
    ))
    .subscribe();
```

---

## Operadores de Control de Flujo

### `spanout`
Distribuye las emisiones uniformemente en el tiempo.

```java
Observable.range(1, 10)
    .compose(Observables.spanout(
        1, 
        TimeUnit.SECONDS,
        Schedulers.computation()
    ))
    .subscribe(System.out::println);
```

### `valve`
Controla el flujo como una válvula (on/off).

```java
PublishSubject<Boolean> valveControl = PublishSubject.create();

Observable.interval(100, TimeUnit.MILLISECONDS)
    .compose(Observables.valve(valveControl))
    .subscribe(System.out::println);

valveControl.onNext(true);   // abre la válvula
Thread.sleep(1000);
valveControl.onNext(false);  // cierra la válvula
```

### `pausable`
Pausa y reanuda las emisiones del stream.

```java
PublishSubject<Boolean> pauser = PublishSubject.create();

Observable.interval(100, TimeUnit.MILLISECONDS)
    .compose(Observables.pausable(pauser))
    .subscribe(System.out::println);

pauser.onNext(false);  // pausa
pauser.onNext(true);   // reanuda
```

### `switchIfEmpty(Supplier)`
Versión lazy de switchIfEmpty que solo crea el fallback si es necesario.

```java
Observable<String> result = observable
    .compose(Observables.switchIfEmpty(() -> expensiveOperation()));
```

---

## Operadores de Repetición y Retry

### `repeatCallable`
Repite la suscripción llamando a un Callable cada vez.

```java
Observable<Long> result = Observables.repeatCallable(
    () -> System.currentTimeMillis(),
    5  // número de repeticiones
);
```

### `retryWhen` mejorado
Políticas de retry más sofisticadas con backoff exponencial.

```java
observable.retryWhen(Observables.retryExponentialBackoff(
    3,                           // intentos máximos
    1, TimeUnit.SECONDS,        // delay inicial
    2.0                         // factor multiplicador
));
```

---

## Operadores de Combinación

### `combineLatestAll`
Combina todos los Observables de un Observable de Observables.

```java
Observable<Observable<Integer>> sources = Observable.just(
    Observable.just(1, 2),
    Observable.just(3, 4)
);

Observable<List<Integer>> combined = 
    sources.compose(Observables.combineLatestAll());
```

### `zipAll`
Zip de una colección de Observables.

```java
List<Observable<Integer>> sources = Arrays.asList(
    Observable.just(1, 2, 3),
    Observable.just(4, 5, 6)
);

Observable<List<Integer>> zipped = 
    Observables.zipAll(sources);
```

### `mergeArray`
Merge optimizado para arrays de sources.

```java
Observable<Integer> merged = Observables.mergeArray(
    Observable.just(1, 2),
    Observable.just(3, 4),
    Observable.just(5, 6)
);
```

---

## Testing y Debugging

### `TestConsumer`
Consumidor mejorado para testing con más aserciones.

```java
TestConsumer<Integer> test = new TestConsumer<>();
observable.subscribe(test);

test.assertValueCount(5)
    .assertNoErrors()
    .assertComplete();
```

### `debug()`
Operadores de debugging avanzados que loguean todas las señales.

```java
Observable.range(1, 5)
    .compose(Observables.debug("MiObservable"))
    .subscribe();
```

### `timeInterval`
Mide los intervalos de tiempo entre emisiones consecutivas.

```java
Observable.interval(100, TimeUnit.MILLISECONDS)
    .take(5)
    .compose(Observables.timeInterval())
    .subscribe(timed -> 
        System.out.println("Valor: " + timed.value() + 
                         ", Intervalo: " + timed.time() + "ms")
    );
```

---

## Transformers (Composición)

Los Transformers permiten reutilizar cadenas de operadores.

### `FlowableTransformers`

```java
FlowableTransformer<Integer, Integer> transformer = 
    FlowableTransformers.<Integer>flatMapAsync(
        x -> callApi(x),
        maxConcurrency
    );

Flowable.range(1, 100)
    .compose(transformer)
    .subscribe();
```

### `ObservableTransformers`

```java
ObservableTransformer<String, String> uppercaseAndFilter = 
    upstream -> upstream
        .map(String::toUpperCase)
        .filter(s -> s.length() > 3);

Observable.just("hola", "mundo")
    .compose(uppercaseAndFilter)
    .subscribe();
```

### Otros Transformers disponibles

- `SingleTransformers` - Para Single
- `MaybeTransformers` - Para Maybe
- `CompletableTransformers` - Para Completable

---

## Operadores Matemáticos y Estadísticos

### Suma

```java
// Suma de enteros
Observable.range(1, 10)
    .compose(MathObservable.sumInt())
    .subscribe(sum -> System.out.println("Suma: " + sum));

// Suma de doubles
Observable.just(1.5, 2.3, 3.7)
    .compose(MathObservable.sumDouble())
    .subscribe(sum -> System.out.println("Suma: " + sum));
```

### Promedio

```java
Observable.range(1, 10)
    .compose(MathObservable.averageDouble())
    .subscribe(avg -> System.out.println("Promedio: " + avg));
```

### Mínimo y Máximo

```java
Observable.just(5, 2, 8, 1, 9)
    .compose(MathObservable.min(Comparator.naturalOrder()))
    .subscribe(min -> System.out.println("Mínimo: " + min));

Observable.just(5, 2, 8, 1, 9)
    .compose(MathObservable.max(Comparator.naturalOrder()))
    .subscribe(max -> System.out.println("Máximo: " + max));
```

### Count

```java
Observable.range(1, 100)
    .filter(x -> x % 2 == 0)
    .count()
    .subscribe(count -> System.out.println("Elementos: " + count));
```

---

## Conectores y Caché

### `cache` mejorado
Cache con políticas de expiración por tiempo o tamaño.

```java
Observable<Data> cached = source
    .compose(Observables.cache(
        5,                      // tamaño máximo
        1, TimeUnit.MINUTES,   // tiempo de expiración
        Schedulers.computation()
    ));
```

### `replay` con tiempo
Replay con ventana de tiempo deslizante.

```java
Observable<Long> replayed = Observable.interval(100, TimeUnit.MILLISECONDS)
    .compose(Observables.replay(
        2, TimeUnit.SECONDS,   // ventana de tiempo
        Schedulers.computation()
    ));
```

### `refCount` con timeout
RefCount que se desconecta automáticamente después de un timeout sin suscriptores.

```java
Observable<Data> autoDisconnect = source
    .publish()
    .compose(Observables.refCount(
        1, TimeUnit.MINUTES   // timeout de desconexión
    ));
```

---

## Operadores de Colección

### `toList` con capacidad inicial

```java
Observable.range(1, 1000)
    .compose(Observables.toList(1000))  // pre-aloca capacidad
    .subscribe();
```

### `toMap` mejorado

```java
Observable.range(1, 10)
    .compose(Observables.toMap(
        x -> "key" + x,           // key selector
        x -> x * 2,               // value selector
        HashMap::new              // map factory
    ))
    .subscribe();
```

### `toMultimap`

```java
Observable.just("apple", "apricot", "banana", "blueberry")
    .compose(Observables.toMultimap(
        s -> s.charAt(0),         // agrupa por primera letra
        s -> s                    // valor
    ))
    .subscribe(map -> {
        System.out.println(map.get('a')); // [apple, apricot]
        System.out.println(map.get('b')); // [banana, blueberry]
    });
```

---

## Operadores de Tiempo

### `delay` con selector dinámico

```java
Observable.range(1, 5)
    .compose(Observables.delay(
        x -> Observable.timer(x * 100, TimeUnit.MILLISECONDS)
    ))
    .subscribe();
```

### `timeout` con fallback dinámico

```java
Observable<Data> result = source
    .compose(Observables.timeout(
        5, TimeUnit.SECONDS,
        x -> getFallbackForItem(x)  // fallback específico por item
    ));
```

### `sample` con throttle

```java
Observable.interval(10, TimeUnit.MILLISECONDS)
    .compose(Observables.sampleWithThrottle(
        100, TimeUnit.MILLISECONDS
    ))
    .subscribe();
```

---

## Operadores de Ordenamiento

### `orderedMerge`
Merge manteniendo el orden según un comparador.

```java
Observable<Integer> merged = Observables.orderedMerge(
    Comparator.naturalOrder(),
    Observable.just(1, 3, 5),
    Observable.just(2, 4, 6)
);
// Resultado: 1, 2, 3, 4, 5, 6
```

### `sorted`
Ordena los elementos según un comparador.

```java
Observable.just(5, 2, 8, 1, 9)
    .compose(Observables.sorted(Comparator.naturalOrder()))
    .subscribe(System.out::println);
```

---

## Operadores de Conversión

### `toFlowable` con estrategia de backpressure

```java
Flowable<Integer> flowable = observable
    .compose(Observables.toFlowable(BackpressureStrategy.BUFFER));
```

### `toObservable` desde Flowable

```java
Observable<Integer> observable = flowable
    .compose(Flowables.toObservable());
```

---

## Operadores Condicionales

### `takeUntil` con predicado

```java
Observable.range(1, 100)
    .compose(Observables.takeUntil(x -> x >= 50))
    .subscribe();
```

### `skipUntil` con predicado

```java
Observable.range(1, 100)
    .compose(Observables.skipUntil(x -> x > 50))
    .subscribe();
```

### `takeWhileInclusive`
Como takeWhile pero incluye el elemento que falla la condición.

```java
Observable.range(1, 10)
    .compose(Observables.takeWhileInclusive(x -> x < 5))
    .subscribe(System.out::println);
// Resultado: 1, 2, 3, 4, 5
```

---

## Operadores de Error Handling

### `onErrorResumeNext` con selector

```java
Observable<Data> result = source
    .compose(Observables.onErrorResumeNext(
        error -> {
            if (error instanceof NetworkException) {
                return getCachedData();
            }
            return Observable.error(error);
        }
    ));
```

### `onErrorReturn` con función

```java
Observable<Integer> result = source
    .compose(Observables.onErrorReturn(
        error -> {
            logError(error);
            return getDefaultValue();
        }
    ));
```

### `retry` con predicado

```java
Observable<Data> result = source
    .compose(Observables.retry((attempt, error) -> 
        attempt < 3 && error instanceof RetryableException
    ));
```

---

## Utilidades Avanzadas

### `compose` helpers

```java
// Aplicar transformación solo si se cumple condición
Observable<Integer> result = source
    .compose(Observables.composeIf(
        shouldTransform,
        upstream -> upstream.map(x -> x * 2)
    ));
```

### `using` con limpieza asíncrona

```java
Observable<Data> result = Observables.using(
    () -> openResource(),              // resource factory
    resource -> getData(resource),     // observable factory
    resource -> closeResourceAsync(resource)  // cleanup async
);
```

---

## Ejemplo Completo de Uso

```java
import hu.akarnokd.rxjava3.operators.*;

public class RxJavaExtensionsExample {
    public static void main(String[] args) {
        // Simular stream de datos con control de flujo
        PublishSubject<Boolean> valve = PublishSubject.create();
        
        Observable.interval(100, TimeUnit.MILLISECONDS)
            // Controlar flujo con válvula
            .compose(Observables.valve(valve))
            // Transformar y filtrar en un paso
            .compose(Observables.mapFilter(
                x -> x * 2,
                x -> x < 20
            ))
            // Distribuir en el tiempo
            .compose(Observables.spanout(
                500, TimeUnit.MILLISECONDS,
                Schedulers.computation()
            ))
            // Debug
            .compose(Observables.debug("DataStream"))
            .subscribe(
                value -> System.out.println("Recibido: " + value),
                error -> System.err.println("Error: " + error),
                () -> System.out.println("Completado")
            );
        
        // Controlar la válvula
        valve.onNext(true);  // abrir
        Thread.sleep(2000);
        valve.onNext(false); // cerrar
        Thread.sleep(1000);
        valve.onNext(true);  // reabrir
    }
}
```

---

## Recursos Adicionales

- **Repositorio GitHub**: [akarnokd/RxJavaExtensions](https://github.com/akarnokd/RxJavaExtensions)
- **JavaDoc**: Documentación completa en el repositorio
- **RxJava Core**: [ReactiveX/RxJava](https://github.com/ReactiveX/RxJava)

---

## Notas Importantes

1. **Rendimiento**: Muchos de estos operadores están optimizados para casos específicos y pueden ser más eficientes que combinar operadores básicos.

2. **Compatibilidad**: Asegúrate de usar la versión correcta de RxJava3 Extensions compatible con tu versión de RxJava3.

3. **Imports**: Los operadores están en el paquete `hu.akarnokd.rxjava3.operators`.

4. **Compose**: La mayoría de operadores se usan con `.compose()` para mejor composición.

5. **Testing**: Usa los utilities de testing incluidos para facilitar las pruebas unitarias.

---

**Última actualización**: Noviembre 2025  
**Versión de RxJava3 Extensions**: 3.1.1  
**Versión de RxJava3**: 3.1.9
