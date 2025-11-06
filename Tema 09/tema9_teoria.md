# Tema 9: Pruebas y Depuración en RxJava

## Introducción

En este tema aprenderás las técnicas y herramientas necesarias para probar y depurar aplicaciones reactivas construidas con RxJava. Las pruebas en programación reactiva presentan desafíos únicos debido a la naturaleza asíncrona de los flujos de datos, la gestión del tiempo y la concurrencia. Dominar estas técnicas es fundamental para garantizar la calidad y el correcto funcionamiento de tus aplicaciones reactivas.

## Fundamentos de Pruebas Reactivas

### ¿Por qué las pruebas reactivas son diferentes?

Las pruebas en RxJava presentan características únicas que las diferencian de las pruebas tradicionales:

**Asincronía**: Los Observables emiten datos en diferentes momentos, lo que dificulta predecir cuándo se completará una operación.

**Concurrencia**: Los operadores que cambian de Scheduler introducen hilos adicionales que pueden complicar las pruebas.

**Tiempo**: Muchas operaciones reactivas dependen del tiempo (delays, timeouts, intervalos), lo que haría que las pruebas reales fueran lentas.

**Efectos secundarios**: Las suscripciones pueden desencadenar efectos secundarios que necesitan ser verificados.

### Principios de Testing Reactivo

Al probar código reactivo, debes considerar:

**Verificación de emisiones**: Comprobar qué elementos emite un Observable, en qué orden y cuántas veces.

**Verificación de terminación**: Asegurar que el flujo se completa correctamente o emite el error esperado.

**Control del tiempo**: Manipular el tiempo virtual para probar operadores temporales sin esperar tiempo real.

**Aislamiento**: Probar componentes reactivos de forma aislada, sin dependencias externas.

**Determinismo**: Las pruebas deben ser reproducibles y no depender de condiciones aleatorias.

## TestScheduler: Control del Tiempo Virtual

El `TestScheduler` es la herramienta más importante para probar código reactivo. Permite controlar el flujo del tiempo de forma determinista.

### ¿Qué es TestScheduler?

`TestScheduler` es una implementación especial de `Scheduler` que no utiliza tiempo real. En su lugar, permite avanzar el tiempo virtual manualmente, lo que hace que las pruebas sean:

- Rápidas (no hay esperas reales)
- Deterministas (control total sobre el timing)
- Predecibles (sin race conditions)

### Funcionalidades principales

**advanceTimeBy(long time, TimeUnit unit)**: Avanza el tiempo virtual la cantidad especificada.

**advanceTimeTo(long time, TimeUnit unit)**: Avanza el tiempo hasta un momento específico.

**triggerActions()**: Ejecuta todas las acciones pendientes en el tiempo actual.

### Ejemplo básico de uso

```java
TestScheduler scheduler = new TestScheduler();
TestObserver<Long> testObserver = new TestObserver<>();

Observable.interval(1, TimeUnit.SECONDS, scheduler)
    .take(3)
    .subscribe(testObserver);

// Sin avanzar el tiempo, no hay emisiones
testObserver.assertNoValues();

// Avanzar 1 segundo
scheduler.advanceTimeBy(1, TimeUnit.SECONDS);
testObserver.assertValue(0L);

// Avanzar 2 segundos más
scheduler.advanceTimeBy(2, TimeUnit.SECONDS);
testObserver.assertValues(0L, 1L, 2L);
testObserver.assertComplete();
```

### Casos de uso del TestScheduler

**Testing de delays**: Probar operadores como `delay()`, `delaySubscription()` sin esperar tiempo real.

**Testing de timeouts**: Verificar que los timeouts se comportan correctamente.

**Testing de intervalos**: Probar `interval()`, `timer()` y operadores similares.

**Testing de throttling**: Verificar `throttleFirst()`, `throttleLast()`, `debounce()`.

**Testing de ventanas temporales**: Comprobar `window()`, `buffer()` con ventanas de tiempo.

## TestObserver y TestSubscriber

`TestObserver` y `TestSubscriber` son clases especiales diseñadas para facilitar la verificación de Observables y Flowables en tests.

### TestObserver

`TestObserver` es un Observer especial que registra todos los eventos que recibe y proporciona métodos de aserción para verificarlos.

### Métodos de aserción principales

**assertNoValues()**: Verifica que no se han emitido valores.

**assertValue(T value)**: Verifica que se emitió exactamente un valor específico.

**assertValues(T... values)**: Verifica que se emitieron los valores especificados en orden.

**assertValueCount(int count)**: Verifica el número de valores emitidos.

**assertComplete()**: Verifica que el flujo se completó exitosamente.

**assertNotComplete()**: Verifica que el flujo no se ha completado.

**assertError(Class<? extends Throwable> errorClass)**: Verifica que se emitió un error del tipo especificado.

**assertError(Throwable error)**: Verifica que se emitió un error específico.

**assertNoErrors()**: Verifica que no se emitieron errores.

**assertSubscribed()**: Verifica que hubo suscripción.

**assertNotSubscribed()**: Verifica que no hubo suscripción.

### Ejemplo completo de TestObserver

```java
@Test
public void testFilterAndMap() {
    TestObserver<String> testObserver = Observable
        .just(1, 2, 3, 4, 5)
        .filter(n -> n % 2 == 0)
        .map(n -> "Número: " + n)
        .test(); // Crea y suscribe un TestObserver

    testObserver
        .assertSubscribed()
        .assertValueCount(2)
        .assertValues("Número: 2", "Número: 4")
        .assertComplete()
        .assertNoErrors();
}
```

### TestSubscriber para Flowables

`TestSubscriber` funciona de manera similar a `TestObserver` pero para `Flowable`, incluyendo funcionalidades adicionales para manejar backpressure.

```java
@Test
public void testBackpressure() {
    TestSubscriber<Integer> testSubscriber = Flowable
        .range(1, 100)
        .test(5); // Solicita inicialmente 5 elementos

    testSubscriber
        .assertSubscribed()
        .assertValueCount(5)
        .assertNotComplete();

    testSubscriber.request(10); // Solicita 10 más
    testSubscriber.assertValueCount(15);
}
```

## Estrategias de Testing

### Testing de operadores de transformación

Los operadores de transformación son relativamente simples de probar ya que suelen ser síncronos y deterministas.

```java
@Test
public void testMapOperator() {
    Observable.just(1, 2, 3)
        .map(n -> n * 2)
        .test()
        .assertValues(2, 4, 6)
        .assertComplete();
}
```

### Testing de operadores de filtrado

```java
@Test
public void testFilterOperator() {
    Observable.range(1, 10)
        .filter(n -> n % 2 == 0)
        .test()
        .assertValues(2, 4, 6, 8, 10)
        .assertComplete();
}
```

### Testing de operadores de combinación

Los operadores de combinación pueden requerir más configuración, especialmente si involucran múltiples flujos.

```java
@Test
public void testZipOperator() {
    Observable<String> names = Observable.just("Alice", "Bob");
    Observable<Integer> ages = Observable.just(25, 30);

    Observable.zip(names, ages, (name, age) -> name + ": " + age)
        .test()
        .assertValues("Alice: 25", "Bob: 30")
        .assertComplete();
}
```

### Testing de manejo de errores

```java
@Test
public void testErrorHandling() {
    Observable.just(1, 2, 0, 4)
        .map(n -> 10 / n)
        .onErrorReturn(error -> -1)
        .test()
        .assertValues(10, 5, -1)
        .assertComplete();
}

@Test
public void testErrorPropagation() {
    Observable.just(1, 2, 0, 4)
        .map(n -> 10 / n)
        .test()
        .assertError(ArithmeticException.class)
        .assertValues(10, 5);
}
```

### Testing con múltiples Schedulers

Cuando pruebas código que usa diferentes Schedulers, necesitas reemplazarlos con TestScheduler.

```java
@Test
public void testWithSchedulers() {
    TestScheduler scheduler = new TestScheduler();

    Observable.just(1, 2, 3)
        .delay(1, TimeUnit.SECONDS, scheduler)
        .test()
        .assertNoValues() // Sin avanzar el tiempo
        .assertNotComplete();

    scheduler.advanceTimeBy(1, TimeUnit.SECONDS);

    // Ahora las aserciones pasarán
}
```

## Técnicas de Depuración

### Operador doOnEach()

El operador `doOnEach()` permite observar cada evento (onNext, onError, onComplete) sin modificar el flujo.

```java
Observable.just(1, 2, 3)
    .doOnEach(notification -> {
        if (notification.isOnNext()) {
            System.out.println("onNext: " + notification.getValue());
        } else if (notification.isOnError()) {
            System.out.println("onError: " + notification.getError());
        } else if (notification.isOnComplete()) {
            System.out.println("onComplete");
        }
    })
    .subscribe();
```

### Operadores doOnNext(), doOnError(), doOnComplete()

Para casos más específicos, estos operadores ofrecen mayor claridad:

```java
Observable.just(1, 2, 3)
    .doOnSubscribe(disposable -> 
        System.out.println("Suscripción iniciada"))
    .doOnNext(value -> 
        System.out.println("Valor recibido: " + value))
    .doOnComplete(() -> 
        System.out.println("Flujo completado"))
    .doOnError(error -> 
        System.out.println("Error: " + error.getMessage()))
    .doFinally(() -> 
        System.out.println("Finalizando (siempre se ejecuta)"))
    .subscribe();
```

### Operador doOnSubscribe() y doOnDispose()

Útiles para rastrear el ciclo de vida de las suscripciones:

```java
Observable.interval(1, TimeUnit.SECONDS)
    .doOnSubscribe(disposable -> 
        System.out.println("Suscrito: " + disposable))
    .doOnDispose(() -> 
        System.out.println("Disposable eliminado"))
    .take(3)
    .subscribe();
```

### Debugging con timestamps

El operador `timestamp()` añade información temporal a cada emisión:

```java
Observable.interval(100, TimeUnit.MILLISECONDS)
    .take(5)
    .timestamp()
    .subscribe(timed -> 
        System.out.println("Valor: " + timed.value() + 
                         " en tiempo: " + timed.time()));
```

### Operador materialize() y dematerialize()

`materialize()` convierte los eventos del Observable (onNext, onError, onComplete) en objetos `Notification` que pueden ser inspeccionados:

```java
Observable.just(1, 2, 3)
    .concatWith(Observable.error(new Exception("Error")))
    .materialize()
    .subscribe(notification -> {
        System.out.println("Tipo: " + notification.getClass().getSimpleName());
        if (notification.isOnNext()) {
            System.out.println("Valor: " + notification.getValue());
        } else if (notification.isOnError()) {
            System.out.println("Error: " + notification.getError());
        }
    });
```

### Logging estructurado

Una práctica recomendada es crear operadores de logging personalizados:

```java
public static <T> Observable<T> log(Observable<T> observable, String tag) {
    return observable
        .doOnSubscribe(d -> System.out.println(tag + " - Subscribe"))
        .doOnNext(value -> System.out.println(tag + " - Next: " + value))
        .doOnError(error -> System.out.println(tag + " - Error: " + error))
        .doOnComplete(() -> System.out.println(tag + " - Complete"))
        .doOnDispose(() -> System.out.println(tag + " - Dispose"));
}

// Uso
log(Observable.just(1, 2, 3), "TEST").subscribe();
```

## Testing de Componentes Complejos

### Mocking de dependencias

Cuando tus componentes reactivos tienen dependencias externas, es importante poder mockearlas:

```java
public class UserService {
    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public Observable<User> getUser(String id) {
        return repository.findById(id)
            .flatMap(user -> Observable.just(user));
    }
}

@Test
public void testUserService() {
    UserRepository mockRepository = mock(UserRepository.class);
    User expectedUser = new User("1", "Alice");
    
    when(mockRepository.findById("1"))
        .thenReturn(Single.just(expectedUser));

    UserService service = new UserService(mockRepository);
    
    service.getUser("1")
        .test()
        .assertValue(expectedUser)
        .assertComplete();
}
```

### Testing de cadenas complejas

Para cadenas de operadores complejas, divide las pruebas en pasos lógicos:

```java
@Test
public void testComplexPipeline() {
    // Paso 1: Verificar filtrado inicial
    TestObserver<Integer> step1 = source()
        .filter(n -> n > 0)
        .test();
    
    step1.assertValues(1, 2, 3);

    // Paso 2: Verificar transformación
    TestObserver<String> step2 = source()
        .filter(n -> n > 0)
        .map(n -> "Number: " + n)
        .test();
    
    step2.assertValues("Number: 1", "Number: 2", "Number: 3");

    // Paso 3: Verificar pipeline completo
    source()
        .filter(n -> n > 0)
        .map(n -> "Number: " + n)
        .reduce((a, b) -> a + ", " + b)
        .test()
        .assertValue("Number: 1, Number: 2, Number: 3");
}
```

## Buenas Prácticas

### Organización de tests

**Naming conventions**: Usa nombres descriptivos que indiquen qué se está probando y qué se espera.

```java
@Test
public void givenEmptyObservable_whenFiltering_thenNoEmissions() {
    // test implementation
}
```

**Estructura AAA (Arrange-Act-Assert)**: Organiza tus tests en tres secciones claras.

```java
@Test
public void testExample() {
    // Arrange
    TestScheduler scheduler = new TestScheduler();
    Observable<Long> source = Observable.interval(1, TimeUnit.SECONDS, scheduler);
    
    // Act
    TestObserver<Long> test = source.take(3).test();
    scheduler.advanceTimeBy(3, TimeUnit.SECONDS);
    
    // Assert
    test.assertValues(0L, 1L, 2L).assertComplete();
}
```

### Testing de edge cases

Siempre prueba los casos extremos:

**Observables vacíos**: ¿Qué pasa cuando no hay emisiones?

**Observables con un solo elemento**: ¿El comportamiento es correcto con un único valor?

**Errores inmediatos**: ¿Cómo se maneja un error en la primera emisión?

**Concurrencia extrema**: ¿Qué pasa con muchos threads concurrentes?

```java
@Test
public void testEmptyObservable() {
    Observable.<Integer>empty()
        .reduce(Integer::sum)
        .test()
        .assertNoValues()
        .assertComplete();
}

@Test
public void testSingleValue() {
    Observable.just(42)
        .reduce(Integer::sum)
        .test()
        .assertValue(42)
        .assertComplete();
}
```

### Manejo de timeouts en tests

Define timeouts explícitos para evitar tests que se cuelgan:

```java
@Test(timeout = 1000) // Timeout en JUnit
public void testShouldCompleteQuickly() {
    Observable.just(1, 2, 3)
        .delay(100, TimeUnit.MILLISECONDS)
        .test()
        .awaitDone(500, TimeUnit.MILLISECONDS) // Timeout en RxJava
        .assertComplete();
}
```

### Limpieza de recursos

Asegúrate de limpiar recursos en los tests, especialmente cuando usas Schedulers reales:

```java
@After
public void tearDown() {
    // Cleanup si es necesario
}
```

### Testing de memory leaks

Verifica que las suscripciones se disponen correctamente:

```java
@Test
public void testNoMemoryLeak() {
    TestObserver<Integer> test = Observable.range(1, 1000000)
        .subscribeOn(Schedulers.computation())
        .test();
    
    test.dispose();
    test.assertSubscribed();
    assertTrue(test.isDisposed());
}
```

## Herramientas Adicionales

### BlockingObservable

Para tests simples, puedes convertir un Observable en bloqueante:

```java
@Test
public void testBlocking() {
    List<Integer> result = Observable.just(1, 2, 3)
        .toList()
        .blockingGet();
    
    assertEquals(Arrays.asList(1, 2, 3), result);
}
```

Sin embargo, esta técnica debe usarse con precaución y preferiblemente solo en tests simples, ya que pierde los beneficios de la reactividad.

### Plugins de RxJava

RxJava permite registrar hooks globales que pueden ser útiles para debugging:

```java
RxJavaPlugins.setErrorHandler(error -> {
    System.err.println("Error no manejado: " + error);
});

RxJavaPlugins.setOnObservableSubscribe((observable, observer) -> {
    System.out.println("Nueva suscripción a Observable");
    return observer;
});
```

### Bibliotecas de testing adicionales

**RxJava Extras**: Proporciona utilidades adicionales para testing y debugging.

**AssertJ**: Ofrece aserciones fluidas que pueden combinarse con TestObserver.

```java
@Test
public void testWithAssertJ() {
    List<Integer> values = Observable.just(1, 2, 3)
        .toList()
        .blockingGet();
    
    assertThat(values)
        .hasSize(3)
        .containsExactly(1, 2, 3);
}
```

## Conclusión

Las pruebas y la depuración son aspectos críticos del desarrollo con RxJava. El `TestScheduler` te permite controlar el tiempo virtual y hacer que las pruebas sean rápidas y deterministas. Los `TestObserver` y `TestSubscriber` proporcionan una API rica para verificar el comportamiento de tus flujos reactivos. Los operadores de debugging como `doOnNext()`, `doOnError()` y `doOnComplete()` te ayudan a entender qué está sucediendo en tus pipelines reactivos.

Dominar estas técnicas te permitirá construir aplicaciones reactivas robustas, mantenibles y confiables. Recuerda siempre probar no solo los casos felices, sino también los casos extremos y los escenarios de error.

## Referencias de Interés

### Documentación Oficial

- **RxJava Testing**: https://github.com/ReactiveX/RxJava/wiki/Testing
- **TestScheduler API**: http://reactivex.io/RxJava/javadoc/io/reactivex/schedulers/TestScheduler.html
- **TestObserver API**: http://reactivex.io/RxJava/javadoc/io/reactivex/observers/TestObserver.html

### Libros

- **"Reactive Programming with RxJava"** de Tomasz Nurkiewicz y Ben Christensen (O'Reilly) - Capítulo 9: Testing and Troubleshooting
- **"Learning RxJava"** de Thomas Nield (Packt) - Capítulo 11: Testing and Debugging

### Artículos y Tutoriales

- **Testing RxJava Code**: https://www.baeldung.com/rxjava-testing
- **Advanced RxJava: Unit Testing**: https://blog.danlew.net/2016/01/25/testing-rxjava/
- **How to Test RxJava**: https://medium.com/@ValCanBuild/testing-rxjava-2-code-with-testobserver-and-testsubscriber-7c3f5b7c8f1d

### Videos

- **Testing Reactive Applications**: https://www.youtube.com/watch?v=htIXKI5gOQU
- **RxJava Testing Best Practices**: Buscar en YouTube tutoriales actualizados sobre testing en RxJava

### Herramientas

- **RxJava Extras**: https://github.com/davidmoten/rxjava-extras
- **AssertJ**: https://assertj.github.io/doc/
- **Mockito**: https://site.mockito.org/ (para mocking en tests)

### Comunidad

- **Stack Overflow - RxJava Testing**: https://stackoverflow.com/questions/tagged/rx-java+testing
- **Gitter RxJava**: https://gitter.im/ReactiveX/RxJava (chat de la comunidad)
