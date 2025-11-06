# Laboratorio 9: Pruebas y Depuración en RxJava

## Objetivo del Laboratorio

En este laboratorio aprenderás a aplicar técnicas de testing y debugging en aplicaciones RxJava. Trabajarás con `TestScheduler`, `TestObserver` y `TestSubscriber` para verificar el comportamiento de flujos reactivos. También implementarás estrategias de depuración y aprenderás a probar operadores temporales, manejo de errores y operaciones complejas.

## Requisitos Previos

- JDK 11 o superior
- Maven o Gradle
- IDE (IntelliJ IDEA, Eclipse o VS Code)
- JUnit 5
- RxJava 3

## Configuración del Proyecto

### Dependencias Maven

```xml
<dependencies>
    <!-- RxJava -->
    <dependency>
        <groupId>io.reactivex.rxjava3</groupId>
        <artifactId>rxjava</artifactId>
        <version>3.1.5</version>
    </dependency>
    
    <!-- JUnit 5 -->
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>5.9.2</version>
        <scope>test</scope>
    </dependency>
    
    <!-- AssertJ para aserciones más expresivas -->
    <dependency>
        <groupId>org.assertj</groupId>
        <artifactId>assertj-core</artifactId>
        <version>3.24.2</version>
        <scope>test</scope>
    </dependency>
    
    <!-- Mockito para mocking -->
    <dependency>
        <groupId>org.mockito</groupId>
        <artifactId>mockito-core</artifactId>
        <version>5.2.0</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

## Ejercicio 1: Tests Básicos con TestObserver

### Objetivo

Aprender a usar `TestObserver` para verificar emisiones, completación y errores en Observables simples.

### Código Base

```java
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.observers.TestObserver;
import org.junit.jupiter.api.Test;

public class BasicTestingTest {

    @Test
    public void testSimpleEmissions() {
        // TODO: Crea un Observable que emita los números 1, 2, 3
        // Usa test() para crear un TestObserver
        // Verifica que:
        // - Se suscribió correctamente
        // - Se emitieron exactamente 3 valores
        // - Los valores son 1, 2, 3
        // - El flujo se completó
        // - No hay errores
    }

    @Test
    public void testFilterOperator() {
        // TODO: Crea un Observable con los números del 1 al 10
        // Aplica un filtro para obtener solo números pares
        // Verifica que se emitieron los valores correctos (2, 4, 6, 8, 10)
    }

    @Test
    public void testMapOperator() {
        // TODO: Crea un Observable con las strings "a", "b", "c"
        // Aplica map para convertirlas a mayúsculas
        // Verifica los valores resultantes
    }

    @Test
    public void testEmptyObservable() {
        // TODO: Crea un Observable vacío
        // Verifica que:
        // - No se emitieron valores
        // - Se completó correctamente
        // - No hay errores
    }

    @Test
    public void testErrorHandling() {
        // TODO: Crea un Observable que emita 1, 2 y luego un error
        // Verifica que:
        // - Se emitieron los valores 1 y 2
        // - Se emitió un error
        // - El error es del tipo esperado
    }
}
```

### Solución

```java
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.observers.TestObserver;
import org.junit.jupiter.api.Test;

public class BasicTestingTest {

    @Test
    public void testSimpleEmissions() {
        TestObserver<Integer> testObserver = Observable.just(1, 2, 3)
            .test();

        testObserver
            .assertSubscribed()
            .assertValueCount(3)
            .assertValues(1, 2, 3)
            .assertComplete()
            .assertNoErrors();
    }

    @Test
    public void testFilterOperator() {
        Observable.range(1, 10)
            .filter(n -> n % 2 == 0)
            .test()
            .assertValues(2, 4, 6, 8, 10)
            .assertComplete();
    }

    @Test
    public void testMapOperator() {
        Observable.just("a", "b", "c")
            .map(String::toUpperCase)
            .test()
            .assertValues("A", "B", "C")
            .assertComplete();
    }

    @Test
    public void testEmptyObservable() {
        Observable.empty()
            .test()
            .assertNoValues()
            .assertComplete()
            .assertNoErrors();
    }

    @Test
    public void testErrorHandling() {
        Observable.concat(
                Observable.just(1, 2),
                Observable.error(new IllegalStateException("Test error"))
            )
            .test()
            .assertValues(1, 2)
            .assertError(IllegalStateException.class)
            .assertErrorMessage("Test error");
    }
}
```

## Ejercicio 2: Testing con TestScheduler

### Objetivo

Aprender a usar `TestScheduler` para probar operadores que dependen del tiempo sin esperar tiempo real.

### Código Base

```java
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.observers.TestObserver;
import io.reactivex.rxjava3.schedulers.TestScheduler;
import org.junit.jupiter.api.Test;
import java.util.concurrent.TimeUnit;

public class TimeBasedTestingTest {

    @Test
    public void testDelay() {
        // TODO: Crea un TestScheduler
        // Crea un Observable que emita 1, 2, 3 con delay de 1 segundo
        // Usa el TestScheduler
        // Verifica que sin avanzar el tiempo no hay emisiones
        // Avanza 1 segundo y verifica las emisiones
    }

    @Test
    public void testInterval() {
        // TODO: Crea un Observable con interval de 1 segundo
        // Toma los primeros 5 elementos
        // Usa TestScheduler para avanzar el tiempo
        // Verifica las emisiones en cada segundo
    }

    @Test
    public void testTimeout() {
        // TODO: Crea un Observable que simule una operación lenta
        // Aplica un timeout de 2 segundos
        // Avanza el tiempo para provocar el timeout
        // Verifica que se emite un TimeoutException
    }

    @Test
    public void testDebounce() {
        // TODO: Crea un Observable que emita valores rápidamente
        // Aplica debounce de 500ms
        // Avanza el tiempo apropiadamente
        // Verifica que solo se emiten los valores "debounced"
    }
}
```

### Solución

```java
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.observers.TestObserver;
import io.reactivex.rxjava3.schedulers.TestScheduler;
import org.junit.jupiter.api.Test;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class TimeBasedTestingTest {

    @Test
    public void testDelay() {
        TestScheduler scheduler = new TestScheduler();
        
        TestObserver<Integer> testObserver = Observable.just(1, 2, 3)
            .delay(1, TimeUnit.SECONDS, scheduler)
            .test();

        // Sin avanzar el tiempo
        testObserver.assertNoValues();
        testObserver.assertNotComplete();

        // Avanzar 1 segundo
        scheduler.advanceTimeBy(1, TimeUnit.SECONDS);
        
        testObserver
            .assertValues(1, 2, 3)
            .assertComplete();
    }

    @Test
    public void testInterval() {
        TestScheduler scheduler = new TestScheduler();
        
        TestObserver<Long> testObserver = Observable
            .interval(1, TimeUnit.SECONDS, scheduler)
            .take(5)
            .test();

        // Verificar cada segundo
        testObserver.assertNoValues();
        
        scheduler.advanceTimeBy(1, TimeUnit.SECONDS);
        testObserver.assertValues(0L);
        
        scheduler.advanceTimeBy(1, TimeUnit.SECONDS);
        testObserver.assertValues(0L, 1L);
        
        scheduler.advanceTimeBy(3, TimeUnit.SECONDS);
        testObserver
            .assertValues(0L, 1L, 2L, 3L, 4L)
            .assertComplete();
    }

    @Test
    public void testTimeout() {
        TestScheduler scheduler = new TestScheduler();
        
        TestObserver<Integer> testObserver = Observable.<Integer>never()
            .timeout(2, TimeUnit.SECONDS, scheduler)
            .test();

        testObserver.assertNoErrors();
        
        // Avanzar más del timeout
        scheduler.advanceTimeBy(3, TimeUnit.SECONDS);
        
        testObserver
            .assertError(TimeoutException.class)
            .assertNoValues();
    }

    @Test
    public void testDebounce() {
        TestScheduler scheduler = new TestScheduler();
        
        TestObserver<String> testObserver = Observable
            .create(emitter -> {
                emitter.onNext("A");
                scheduler.advanceTimeTo(100, TimeUnit.MILLISECONDS);
                emitter.onNext("B");
                scheduler.advanceTimeTo(150, TimeUnit.MILLISECONDS);
                emitter.onNext("C");
                scheduler.advanceTimeTo(700, TimeUnit.MILLISECONDS);
                emitter.onNext("D");
                scheduler.advanceTimeTo(1500, TimeUnit.MILLISECONDS);
                emitter.onComplete();
            })
            .debounce(500, TimeUnit.MILLISECONDS, scheduler)
            .test();

        scheduler.advanceTimeTo(2, TimeUnit.SECONDS);
        
        testObserver
            .assertValues("C", "D")
            .assertComplete();
    }
}
```

## Ejercicio 3: Testing de Operadores de Combinación

### Objetivo

Probar operadores que combinan múltiples Observables como `merge()`, `zip()`, `combineLatest()`.

### Código Base

```java
import io.reactivex.rxjava3.core.Observable;
import org.junit.jupiter.api.Test;

public class CombinationOperatorsTest {

    @Test
    public void testMerge() {
        // TODO: Crea dos Observables
        // Observable 1: emite 1, 2, 3
        // Observable 2: emite 4, 5, 6
        // Combínalos con merge
        // Verifica que se emiten todos los valores (el orden puede variar)
    }

    @Test
    public void testZip() {
        // TODO: Crea dos Observables
        // Observable 1: nombres ("Alice", "Bob", "Charlie")
        // Observable 2: edades (25, 30, 35)
        // Usa zip para combinarlos en strings "nombre: edad"
        // Verifica los resultados
    }

    @Test
    public void testCombineLatest() {
        // TODO: Crea dos Observables con TestScheduler
        // Simula emisiones en diferentes momentos
        // Usa combineLatest para combinar los últimos valores
        // Verifica el comportamiento
    }

    @Test
    public void testConcat() {
        // TODO: Crea tres Observables
        // Usa concat para emitirlos secuencialmente
        // Verifica el orden de emisión
    }
}
```

### Solución

```java
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.TestScheduler;
import org.junit.jupiter.api.Test;
import java.util.concurrent.TimeUnit;
import static org.assertj.core.api.Assertions.assertThat;

public class CombinationOperatorsTest {

    @Test
    public void testMerge() {
        Observable<Integer> obs1 = Observable.just(1, 2, 3);
        Observable<Integer> obs2 = Observable.just(4, 5, 6);
        
        Observable.merge(obs1, obs2)
            .test()
            .assertValueCount(6)
            .assertComplete()
            .assertNoErrors();
    }

    @Test
    public void testZip() {
        Observable<String> names = Observable.just("Alice", "Bob", "Charlie");
        Observable<Integer> ages = Observable.just(25, 30, 35);
        
        Observable.zip(names, ages, (name, age) -> name + ": " + age)
            .test()
            .assertValues("Alice: 25", "Bob: 30", "Charlie: 35")
            .assertComplete();
    }

    @Test
    public void testCombineLatest() {
        TestScheduler scheduler = new TestScheduler();
        
        Observable<String> letters = Observable
            .create(emitter -> {
                scheduler.advanceTimeTo(0, TimeUnit.MILLISECONDS);
                emitter.onNext("A");
                scheduler.advanceTimeTo(100, TimeUnit.MILLISECONDS);
                emitter.onNext("B");
                scheduler.advanceTimeTo(200, TimeUnit.MILLISECONDS);
                emitter.onNext("C");
            });
        
        Observable<Integer> numbers = Observable
            .create(emitter -> {
                scheduler.advanceTimeTo(50, TimeUnit.MILLISECONDS);
                emitter.onNext(1);
                scheduler.advanceTimeTo(150, TimeUnit.MILLISECONDS);
                emitter.onNext(2);
            });
        
        var testObserver = Observable
            .combineLatest(letters, numbers, (letter, number) -> letter + number)
            .test();
        
        scheduler.advanceTimeTo(300, TimeUnit.MILLISECONDS);
        
        testObserver.assertValueCount(4);  // A1, B1, B2, C2
    }

    @Test
    public void testConcat() {
        Observable<String> obs1 = Observable.just("A", "B");
        Observable<String> obs2 = Observable.just("C", "D");
        Observable<String> obs3 = Observable.just("E", "F");
        
        Observable.concat(obs1, obs2, obs3)
            .test()
            .assertValues("A", "B", "C", "D", "E", "F")
            .assertComplete();
    }
}
```

## Ejercicio 4: Testing de Backpressure con TestSubscriber

### Objetivo

Probar el comportamiento de backpressure en Flowables usando `TestSubscriber`.

### Código Base

```java
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.subscribers.TestSubscriber;
import org.junit.jupiter.api.Test;

public class BackpressureTestingTest {

    @Test
    public void testBackpressureRequest() {
        // TODO: Crea un Flowable que emita números del 1 al 100
        // Crea un TestSubscriber solicitando inicialmente 5 elementos
        // Verifica que solo recibe 5
        // Solicita 10 más y verifica
    }

    @Test
    public void testBackpressureBuffer() {
        // TODO: Crea un Flowable con muchos elementos
        // Aplica una estrategia de backpressure
        // Verifica el comportamiento con TestSubscriber
    }

    @Test
    public void testBackpressureDrop() {
        // TODO: Simula un productor rápido con buffer limitado
        // Usa estrategia DROP
        // Verifica que se descartan elementos
    }
}
```

### Solución

```java
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.subscribers.TestSubscriber;
import org.junit.jupiter.api.Test;

public class BackpressureTestingTest {

    @Test
    public void testBackpressureRequest() {
        TestSubscriber<Integer> testSubscriber = Flowable.range(1, 100)
            .test(5);  // Solicita 5 elementos inicialmente

        testSubscriber
            .assertSubscribed()
            .assertValueCount(5)
            .assertValues(1, 2, 3, 4, 5)
            .assertNotComplete();

        // Solicitar 10 más
        testSubscriber.request(10);
        
        testSubscriber
            .assertValueCount(15)
            .assertNotComplete();

        // Solicitar todos los restantes
        testSubscriber.request(Long.MAX_VALUE);
        
        testSubscriber
            .assertValueCount(100)
            .assertComplete();
    }

    @Test
    public void testBackpressureBuffer() {
        TestSubscriber<Integer> testSubscriber = Flowable
            .create(emitter -> {
                for (int i = 1; i <= 1000; i++) {
                    emitter.onNext(i);
                }
                emitter.onComplete();
            }, BackpressureStrategy.BUFFER)
            .test(10);

        testSubscriber
            .assertValueCount(10)
            .assertNotComplete();

        testSubscriber.request(990);
        
        testSubscriber
            .assertValueCount(1000)
            .assertComplete();
    }

    @Test
    public void testBackpressureDrop() {
        TestSubscriber<Integer> testSubscriber = Flowable
            .create(emitter -> {
                for (int i = 1; i <= 1000; i++) {
                    emitter.onNext(i);
                }
                emitter.onComplete();
            }, BackpressureStrategy.DROP)
            .test(10);

        // Con DROP, algunos elementos se descartan
        testSubscriber
            .assertValueCount(10)
            .assertComplete();
    }
}
```

## Ejercicio 5: Debugging con Operadores do*

### Objetivo

Implementar debugging efectivo usando operadores `doOnNext()`, `doOnError()`, `doOnComplete()`, etc.

### Código Base

```java
import io.reactivex.rxjava3.core.Observable;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

public class DebuggingTest {

    @Test
    public void testLoggingPipeline() {
        // TODO: Crea una lista para registrar eventos
        // Crea un Observable con varios operadores
        // Usa doOnNext, doOnError, doOnComplete para logging
        // Verifica que los eventos se registraron correctamente
    }

    @Test
    public void testLifecycleEvents() {
        // TODO: Registra eventos del ciclo de vida
        // doOnSubscribe, doOnDispose, doFinally
        // Verifica que se llamaron en el orden correcto
    }

    @Test
    public void testErrorLogging() {
        // TODO: Crea un flujo que genere un error
        // Usa doOnError para registrar el error
        // Verifica que el error se manejó correctamente
    }
}
```

### Solución

```java
import io.reactivex.rxjava3.core.Observable;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

public class DebuggingTest {

    @Test
    public void testLoggingPipeline() {
        List<String> events = new ArrayList<>();
        
        Observable.just(1, 2, 3)
            .doOnSubscribe(d -> events.add("SUBSCRIBE"))
            .doOnNext(value -> events.add("NEXT: " + value))
            .doOnComplete(() -> events.add("COMPLETE"))
            .doOnError(error -> events.add("ERROR: " + error))
            .doFinally(() -> events.add("FINALLY"))
            .test()
            .assertComplete();

        assertThat(events).containsExactly(
            "SUBSCRIBE",
            "NEXT: 1",
            "NEXT: 2",
            "NEXT: 3",
            "COMPLETE",
            "FINALLY"
        );
    }

    @Test
    public void testLifecycleEvents() {
        List<String> lifecycle = new ArrayList<>();
        
        var testObserver = Observable.just(1, 2, 3)
            .doOnSubscribe(d -> lifecycle.add("SUBSCRIBED"))
            .doOnNext(v -> lifecycle.add("NEXT: " + v))
            .doOnDispose(() -> lifecycle.add("DISPOSED"))
            .doFinally(() -> lifecycle.add("FINALLY"))
            .test();

        testObserver.dispose();

        assertThat(lifecycle).contains(
            "SUBSCRIBED",
            "NEXT: 1",
            "NEXT: 2",
            "NEXT: 3",
            "FINALLY"
        );
    }

    @Test
    public void testErrorLogging() {
        List<String> events = new ArrayList<>();
        
        Observable.just(1, 2, 0, 4)
            .doOnNext(v -> events.add("Processing: " + v))
            .map(n -> 10 / n)
            .doOnError(error -> events.add("ERROR: " + error.getClass().getSimpleName()))
            .doFinally(() -> events.add("FINALLY"))
            .test()
            .assertError(ArithmeticException.class);

        assertThat(events).containsExactly(
            "Processing: 1",
            "Processing: 2",
            "Processing: 0",
            "ERROR: ArithmeticException",
            "FINALLY"
        );
    }
}
```

## Ejercicio 6: Testing de Componentes Reales

### Objetivo

Probar un servicio reactivo completo con dependencias mockeadas.

### Código Base

```java
// UserRepository.java
import io.reactivex.rxjava3.core.Single;

public interface UserRepository {
    Single<User> findById(String id);
    Single<List<User>> findAll();
}

// User.java
public class User {
    private final String id;
    private final String name;
    private final String email;

    public User(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    // Getters, equals, hashCode
}

// UserService.java
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

public class UserService {
    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public Single<User> getUserById(String id) {
        // TODO: Implementa usando repository.findById
        // Aplica validaciones si el ID es nulo o vacío
        return null;
    }

    public Observable<String> getUserEmails() {
        // TODO: Obtiene todos los usuarios y extrae sus emails
        // Filtra emails nulos o vacíos
        return null;
    }

    public Single<Integer> countUsers() {
        // TODO: Cuenta el total de usuarios
        return null;
    }
}

// UserServiceTest.java
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository mockRepository;
    
    private UserService userService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(mockRepository);
    }

    @Test
    public void testGetUserById() {
        // TODO: Mockea repository.findById
        // Verifica que getUserById devuelve el usuario correcto
    }

    @Test
    public void testGetUserByIdWithInvalidId() {
        // TODO: Prueba con ID nulo o vacío
        // Verifica que emite el error apropiado
    }

    @Test
    public void testGetUserEmails() {
        // TODO: Mockea repository.findAll
        // Verifica que se extraen y filtran los emails correctamente
    }

    @Test
    public void testCountUsers() {
        // TODO: Mockea repository.findAll
        // Verifica el conteo correcto
    }
}
```

### Solución

```java
// UserService.java
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

public class UserService {
    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public Single<User> getUserById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return Single.error(new IllegalArgumentException("ID cannot be null or empty"));
        }
        return repository.findById(id);
    }

    public Observable<String> getUserEmails() {
        return repository.findAll()
            .flatMapObservable(Observable::fromIterable)
            .map(User::getEmail)
            .filter(email -> email != null && !email.trim().isEmpty());
    }

    public Single<Integer> countUsers() {
        return repository.findAll()
            .map(List::size);
    }
}

// UserServiceTest.java
import io.reactivex.rxjava3.core.Single;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Arrays;
import java.util.List;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository mockRepository;
    
    private UserService userService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(mockRepository);
    }

    @Test
    public void testGetUserById() {
        User expectedUser = new User("1", "Alice", "alice@example.com");
        when(mockRepository.findById("1"))
            .thenReturn(Single.just(expectedUser));

        userService.getUserById("1")
            .test()
            .assertValue(expectedUser)
            .assertComplete()
            .assertNoErrors();

        verify(mockRepository).findById("1");
    }

    @Test
    public void testGetUserByIdWithInvalidId() {
        userService.getUserById(null)
            .test()
            .assertError(IllegalArgumentException.class)
            .assertErrorMessage("ID cannot be null or empty");

        userService.getUserById("")
            .test()
            .assertError(IllegalArgumentException.class);

        verifyNoInteractions(mockRepository);
    }

    @Test
    public void testGetUserEmails() {
        List<User> users = Arrays.asList(
            new User("1", "Alice", "alice@example.com"),
            new User("2", "Bob", "bob@example.com"),
            new User("3", "Charlie", "")  // Email vacío, debe filtrarse
        );

        when(mockRepository.findAll())
            .thenReturn(Single.just(users));

        userService.getUserEmails()
            .test()
            .assertValues("alice@example.com", "bob@example.com")
            .assertComplete();
    }

    @Test
    public void testCountUsers() {
        List<User> users = Arrays.asList(
            new User("1", "Alice", "alice@example.com"),
            new User("2", "Bob", "bob@example.com"),
            new User("3", "Charlie", "charlie@example.com")
        );

        when(mockRepository.findAll())
            .thenReturn(Single.just(users));

        userService.countUsers()
            .test()
            .assertValue(3)
            .assertComplete();
    }
}
```

## Desafío Integrador

### Sistema de Procesamiento de Pedidos con Testing Completo

Implementa un sistema de procesamiento de pedidos reactivo con una suite completa de tests que cubra:

1. **OrderProcessor**: Procesa pedidos asincrónicamente
   - Valida pedidos
   - Aplica descuentos
   - Calcula totales
   - Gestiona inventario

2. **Tests requeridos**:
   - Tests unitarios de cada operador
   - Tests de integración del flujo completo
   - Tests con tiempo simulado (timeouts, delays)
   - Tests de manejo de errores y retry
   - Tests de backpressure
   - Tests con mocks de dependencias externas

```java
// Order.java
public class Order {
    private final String id;
    private final String customerId;
    private final List<OrderItem> items;
    private final OrderStatus status;

    // Constructor, getters, builders
}

public enum OrderStatus {
    PENDING, VALIDATED, PROCESSING, COMPLETED, FAILED
}

// OrderItem.java
public class OrderItem {
    private final String productId;
    private final int quantity;
    private final double price;

    // Constructor, getters
}

// OrderRepository.java
public interface OrderRepository {
    Single<Order> save(Order order);
    Single<Order> findById(String id);
}

// InventoryService.java
public interface InventoryService {
    Single<Boolean> checkAvailability(String productId, int quantity);
    Single<Boolean> reserve(String productId, int quantity);
}

// OrderProcessor.java
public class OrderProcessor {
    private final OrderRepository repository;
    private final InventoryService inventoryService;

    public OrderProcessor(OrderRepository repository, 
                         InventoryService inventoryService) {
        this.repository = repository;
        this.inventoryService = inventoryService;
    }

    public Single<Order> processOrder(Order order) {
        // TODO: Implementa el flujo completo de procesamiento
        // 1. Validar pedido
        // 2. Verificar disponibilidad de productos
        // 3. Reservar inventario
        // 4. Aplicar descuentos
        // 5. Calcular total
        // 6. Guardar pedido
        // 7. Manejar errores y retries
        return null;
    }

    public Observable<Order> processOrders(List<Order> orders) {
        // TODO: Procesa múltiples pedidos con control de concurrencia
        return null;
    }

    private Single<Boolean> validateOrder(Order order) {
        // TODO: Valida que el pedido tenga items, precios válidos, etc.
        return null;
    }

    private Single<Order> applyDiscounts(Order order) {
        // TODO: Aplica descuentos según reglas de negocio
        return null;
    }
}

// OrderProcessorTest.java
public class OrderProcessorTest {
    // TODO: Implementa tests exhaustivos
}
```

## Preguntas de Reflexión

1. ¿Por qué es importante usar `TestScheduler` en lugar de esperar tiempo real en los tests?

2. ¿Cuál es la diferencia entre `TestObserver` y `TestSubscriber`? ¿Cuándo usarías cada uno?

3. ¿Cómo afecta el uso de diferentes Schedulers a la estrategia de testing?

4. ¿Qué ventajas ofrece el método `test()` en comparación con suscribirse manualmente y hacer aserciones?

5. ¿Por qué es importante probar tanto los casos exitosos como los casos de error?

6. ¿Cómo puedes verificar que no hay memory leaks en tus Observables?

7. ¿Qué técnicas usarías para debuggear un flujo reactivo complejo en producción?

8. ¿Cuándo es apropiado usar `blockingGet()` o `blockingSubscribe()` en tests?

9. ¿Cómo estructurarías los tests para un pipeline reactivo muy complejo con múltiples operadores?

10. ¿Qué consideraciones especiales debes tener al probar código reactivo que interactúa con recursos externos (bases de datos, APIs)?

## Recursos Adicionales

- **RxJava Testing Wiki**: https://github.com/ReactiveX/RxJava/wiki/Testing
- **Testing Reactive Systems**: https://www.baeldung.com/rxjava-testing
- **Advanced Testing Patterns**: https://blog.danlew.net/2016/01/25/testing-rxjava/
- **JUnit 5 User Guide**: https://junit.org/junit5/docs/current/user-guide/
- **Mockito Documentation**: https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html

## Conclusión

En este laboratorio has aprendido a aplicar técnicas profesionales de testing y debugging en aplicaciones RxJava. Dominar estas habilidades es esencial para construir aplicaciones reactivas robustas y mantenibles. La capacidad de probar flujos asincrónicos de manera determinista usando `TestScheduler`, verificar comportamientos complejos con `TestObserver`, y depurar efectivamente usando operadores `do*` te permitirá desarrollar con confianza sistemas reactivos de producción.
