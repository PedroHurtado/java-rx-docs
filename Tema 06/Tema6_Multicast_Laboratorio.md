# LABORATORIO - TEMA 6: MULTICAST

## Objetivo
Comprender y aplicar los conceptos de multicast en RxJava, experimentando con Observables fríos y calientes, y utilizando los diferentes operadores de multicast.

## Requisitos Previos
- JDK 8 o superior
- RxJava 3.x
- IDE (IntelliJ IDEA, Eclipse o similar)

## Dependencias Maven

```xml
<dependency>
    <groupId>io.reactivex.rxjava3</groupId>
    <artifactId>rxjava</artifactId>
    <version>3.1.8</version>
</dependency>
```

## Dependencias Gradle

```gradle
implementation 'io.reactivex.rxjava3:rxjava:3.1.8'
```

---

## EJERCICIO 1: Observable Cold vs Hot

### Descripción
Crear un ejemplo que demuestre claramente la diferencia entre un Observable frío y uno caliente.

### Código Base

```java
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.observables.ConnectableObservable;

import java.util.concurrent.TimeUnit;

public class ColdVsHotObservable {
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== OBSERVABLE COLD ===");
        demonstrateColdObservable();
        
        Thread.sleep(3000);
        
        System.out.println("\n=== OBSERVABLE HOT ===");
        demonstrateHotObservable();
        
        Thread.sleep(5000);
    }
    
    private static void demonstrateColdObservable() throws InterruptedException {
        Observable<Long> coldObservable = Observable.interval(500, TimeUnit.MILLISECONDS)
            .take(5)
            .doOnNext(i -> System.out.println("Emitiendo: " + i));
        
        // Primera suscripción
        coldObservable.subscribe(i -> System.out.println("Observer 1: " + i));
        
        // Segunda suscripción después de 1 segundo
        Thread.sleep(1000);
        coldObservable.subscribe(i -> System.out.println("Observer 2: " + i));
    }
    
    private static void demonstrateHotObservable() throws InterruptedException {
        ConnectableObservable<Long> hotObservable = Observable
            .interval(500, TimeUnit.MILLISECONDS)
            .take(5)
            .doOnNext(i -> System.out.println("Emitiendo: " + i))
            .publish();
        
        // Primera suscripción
        hotObservable.subscribe(i -> System.out.println("Observer 1: " + i));
        
        // Inicia la emisión
        hotObservable.connect();
        
        // Segunda suscripción después de 1 segundo (pierde algunos valores)
        Thread.sleep(1000);
        hotObservable.subscribe(i -> System.out.println("Observer 2: " + i));
    }
}
```

### Tareas
1. Ejecutar el código y observar las diferencias en la salida
2. Modificar los tiempos de suscripción y analizar el comportamiento
3. Añadir un tercer observer que se suscriba después de 2 segundos

### Resultado Esperado
- En el Observable Cold: ambos observers reciben todos los valores desde el inicio
- En el Observable Hot: el segundo observer pierde los primeros valores emitidos antes de su suscripción

---

## EJERCICIO 2: Uso de publish() y connect()

### Descripción
Implementar un sistema que simule la cotización de acciones compartida entre múltiples inversores.

### Código Base

```java
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.observables.ConnectableObservable;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class StockPriceMulticast {
    
    public static void main(String[] args) throws InterruptedException {
        ConnectableObservable<Double> stockPrices = createStockPriceObservable()
            .publish();
        
        // Inversor 1
        stockPrices.subscribe(
            price -> System.out.println("Inversor 1 ve precio: $" + String.format("%.2f", price)),
            error -> System.err.println("Inversor 1 error: " + error),
            () -> System.out.println("Inversor 1 - Mercado cerrado")
        );
        
        // Inversor 2
        stockPrices.subscribe(
            price -> System.out.println("Inversor 2 ve precio: $" + String.format("%.2f", price)),
            error -> System.err.println("Inversor 2 error: " + error),
            () -> System.out.println("Inversor 2 - Mercado cerrado")
        );
        
        System.out.println("Iniciando transmisión de precios...\n");
        stockPrices.connect();
        
        // Inversor 3 se une tarde
        Thread.sleep(2000);
        System.out.println("\n[Inversor 3 se une al mercado]\n");
        stockPrices.subscribe(
            price -> System.out.println("Inversor 3 ve precio: $" + String.format("%.2f", price)),
            error -> System.err.println("Inversor 3 error: " + error),
            () -> System.out.println("Inversor 3 - Mercado cerrado")
        );
        
        Thread.sleep(5000);
    }
    
    private static Observable<Double> createStockPriceObservable() {
        Random random = new Random();
        double basePrice = 100.0;
        
        return Observable.interval(500, TimeUnit.MILLISECONDS)
            .take(10)
            .map(i -> {
                double change = (random.nextDouble() - 0.5) * 5;
                return basePrice + change;
            })
            .doOnNext(price -> System.out.println("[MERCADO] Nuevo precio: $" + String.format("%.2f", price)));
    }
}
```

### Tareas
1. Ejecutar el código y observar cómo los inversores comparten la misma fuente de precios
2. Notar que el Inversor 3 pierde los precios emitidos antes de su suscripción
3. Modificar para usar `replay()` en lugar de `publish()` y observar la diferencia

---

## EJERCICIO 3: Operador share()

### Descripción
Crear un sistema que simule una petición HTTP costosa compartida entre múltiples componentes de una aplicación.

### Código Base

```java
import io.reactivex.rxjava3.core.Observable;

import java.util.concurrent.TimeUnit;

public class SharedHttpRequest {
    
    public static void main(String[] args) throws InterruptedException {
        // Simulamos una petición HTTP costosa
        Observable<String> sharedRequest = expensiveHttpCall()
            .share();
        
        System.out.println("=== SIN SHARE ===");
        demonstrateWithoutShare();
        
        Thread.sleep(3000);
        
        System.out.println("\n=== CON SHARE ===");
        demonstrateWithShare(sharedRequest);
        
        Thread.sleep(3000);
    }
    
    private static void demonstrateWithoutShare() throws InterruptedException {
        Observable<String> request = expensiveHttpCall();
        
        // Componente 1
        request.subscribe(data -> System.out.println("Componente 1: " + data));
        
        // Componente 2
        request.subscribe(data -> System.out.println("Componente 2: " + data));
        
        Thread.sleep(2500);
    }
    
    private static void demonstrateWithShare(Observable<String> sharedRequest) throws InterruptedException {
        // Componente 1
        sharedRequest.subscribe(data -> System.out.println("Componente 1: " + data));
        
        // Componente 2
        sharedRequest.subscribe(data -> System.out.println("Componente 2: " + data));
        
        Thread.sleep(2500);
    }
    
    private static Observable<String> expensiveHttpCall() {
        return Observable.create(emitter -> {
            System.out.println("[HTTP] Iniciando petición costosa...");
            Thread.sleep(1000); // Simula latencia de red
            System.out.println("[HTTP] Petición completada");
            emitter.onNext("Datos del servidor");
            emitter.onComplete();
        });
    }
}
```

### Tareas
1. Ejecutar y observar cuántas veces se realiza la petición HTTP sin share()
2. Comparar con el comportamiento usando share()
3. Añadir un tercer componente que se suscriba 500ms después
4. Implementar un mecanismo de retry y observar cómo share() afecta el comportamiento

---

## EJERCICIO 4: Operador replay()

### Descripción
Implementar un sistema de notificaciones donde los nuevos suscriptores puedan ver las últimas notificaciones.

### Código Base

```java
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.observables.ConnectableObservable;

import java.util.concurrent.TimeUnit;

public class NotificationSystem {
    
    public static void main(String[] args) throws InterruptedException {
        // Crear un sistema que almacene las últimas 3 notificaciones
        ConnectableObservable<String> notifications = createNotificationStream()
            .replay(3);
        
        // Iniciar el stream de notificaciones
        notifications.connect();
        
        System.out.println("Sistema de notificaciones iniciado\n");
        
        // Usuario 1 se suscribe desde el inicio
        notifications.subscribe(msg -> 
            System.out.println("[Usuario 1] " + msg)
        );
        
        Thread.sleep(2000);
        
        // Usuario 2 se suscribe tarde pero recibe las últimas 3 notificaciones
        System.out.println("\n[Usuario 2 se une al sistema]\n");
        notifications.subscribe(msg -> 
            System.out.println("[Usuario 2] " + msg)
        );
        
        Thread.sleep(3000);
        
        // Usuario 3 se suscribe aún más tarde
        System.out.println("\n[Usuario 3 se une al sistema]\n");
        notifications.subscribe(msg -> 
            System.out.println("[Usuario 3] " + msg)
        );
        
        Thread.sleep(3000);
    }
    
    private static Observable<String> createNotificationStream() {
        return Observable.interval(500, TimeUnit.MILLISECONDS)
            .take(10)
            .map(i -> "Notificación #" + (i + 1) + ": Evento importante")
            .doOnNext(msg -> System.out.println("[SISTEMA] Enviando: " + msg));
    }
}
```

### Tareas
1. Ejecutar y observar cómo los usuarios tardíos reciben las últimas 3 notificaciones
2. Cambiar el buffer de replay a 5 notificaciones
3. Implementar replay con límite de tiempo: `replay(2, TimeUnit.SECONDS)`
4. Crear un sistema que combine buffer size y tiempo: `replay(3, 1, TimeUnit.SECONDS)`

---

## EJERCICIO 5: refCount() y Gestión Automática

### Descripción
Implementar un servicio que gestione automáticamente la conexión basándose en el número de suscriptores.

### Código Base

```java
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;

import java.util.concurrent.TimeUnit;

public class AutoManagedService {
    
    public static void main(String[] args) throws InterruptedException {
        Observable<Long> service = createExpensiveService()
            .publish()
            .refCount();
        
        System.out.println("=== Cliente 1 se conecta ===");
        Disposable client1 = service.subscribe(
            data -> System.out.println("Cliente 1: " + data),
            error -> System.err.println("Cliente 1 error: " + error),
            () -> System.out.println("Cliente 1 completado")
        );
        
        Thread.sleep(1500);
        
        System.out.println("\n=== Cliente 2 se conecta ===");
        Disposable client2 = service.subscribe(
            data -> System.out.println("Cliente 2: " + data),
            error -> System.err.println("Cliente 2 error: " + error),
            () -> System.out.println("Cliente 2 completado")
        );
        
        Thread.sleep(1500);
        
        System.out.println("\n=== Cliente 1 se desconecta ===");
        client1.dispose();
        
        Thread.sleep(1500);
        
        System.out.println("\n=== Cliente 2 se desconecta ===");
        client2.dispose();
        
        Thread.sleep(1000);
        
        System.out.println("\n=== Cliente 3 se conecta (nueva conexión) ===");
        Disposable client3 = service.subscribe(
            data -> System.out.println("Cliente 3: " + data),
            error -> System.err.println("Cliente 3 error: " + error),
            () -> System.out.println("Cliente 3 completado")
        );
        
        Thread.sleep(3000);
        client3.dispose();
    }
    
    private static Observable<Long> createExpensiveService() {
        return Observable.interval(500, TimeUnit.MILLISECONDS)
            .doOnSubscribe(disposable -> 
                System.out.println("[SERVICIO] Conexión establecida - Iniciando recursos costosos"))
            .doOnDispose(() -> 
                System.out.println("[SERVICIO] Última desconexión - Liberando recursos"))
            .doOnNext(tick -> 
                System.out.println("[SERVICIO] Tick: " + tick));
    }
}
```

### Tareas
1. Ejecutar y observar cuándo se conecta y desconecta el servicio
2. Notar que el servicio se reconecta cuando el Cliente 3 se suscribe
3. Modificar para usar `autoConnect(2)` en lugar de `refCount()`
4. Observar la diferencia en el comportamiento de conexión/desconexión

---

## EJERCICIO 6: Caso Práctico - Dashboard en Tiempo Real

### Descripción
Crear un dashboard que muestre datos en tiempo real compartidos entre múltiples widgets.

### Código Base

```java
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class RealtimeDashboard {
    
    private static final Random random = new Random();
    
    public static void main(String[] args) throws InterruptedException {
        CompositeDisposable disposables = new CompositeDisposable();
        
        // Stream de datos en tiempo real compartido
        Observable<SensorData> sensorStream = createSensorStream()
            .replay(1)
            .refCount();
        
        // Widget 1: Muestra temperatura
        disposables.add(
            sensorStream
                .map(data -> "🌡️ Temperatura: " + data.temperature + "°C")
                .subscribe(System.out::println)
        );
        
        // Widget 2: Muestra humedad
        disposables.add(
            sensorStream
                .map(data -> "💧 Humedad: " + data.humidity + "%")
                .subscribe(System.out::println)
        );
        
        // Widget 3: Alarmas (se activa si temperatura > 30)
        disposables.add(
            sensorStream
                .filter(data -> data.temperature > 30)
                .map(data -> "⚠️ ALERTA: Temperatura alta - " + data.temperature + "°C")
                .subscribe(System.out::println)
        );
        
        Thread.sleep(2000);
        
        // Widget 4: Se añade después (estadísticas)
        System.out.println("\n[Añadiendo widget de estadísticas]\n");
        disposables.add(
            sensorStream
                .buffer(3)
                .map(dataList -> {
                    double avgTemp = dataList.stream()
                        .mapToDouble(d -> d.temperature)
                        .average()
                        .orElse(0);
                    return "📊 Temperatura promedio (últimas 3 lecturas): " + 
                           String.format("%.2f", avgTemp) + "°C";
                })
                .subscribe(System.out::println)
        );
        
        Thread.sleep(5000);
        
        disposables.dispose();
        System.out.println("\nDashboard cerrado");
    }
    
    private static Observable<SensorData> createSensorStream() {
        return Observable.interval(500, TimeUnit.MILLISECONDS)
            .map(i -> new SensorData(
                20 + random.nextDouble() * 15,  // Temperatura entre 20-35°C
                40 + random.nextDouble() * 40   // Humedad entre 40-80%
            ))
            .doOnNext(data -> System.out.println(
                "[SENSOR] Nueva lectura: " + data.temperature + "°C, " + data.humidity + "%"
            ));
    }
    
    static class SensorData {
        final double temperature;
        final double humidity;
        
        SensorData(double temperature, double humidity) {
            this.temperature = temperature;
            this.humidity = humidity;
        }
    }
}
```

### Tareas
1. Ejecutar el dashboard y observar cómo todos los widgets reciben los mismos datos
2. Añadir un quinto widget que calcule el índice de confort (función de temperatura y humedad)
3. Modificar para que algunos widgets se suscriban con delay
4. Implementar un widget que muestre solo las últimas 5 lecturas usando replay(5)

---

## EJERCICIO 7: Comparación de Estrategias de Multicast

### Descripción
Crear un programa que compare el comportamiento de diferentes estrategias de multicast.

### Código Base

```java
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.observables.ConnectableObservable;

import java.util.concurrent.TimeUnit;

public class MulticastComparison {
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== 1. PUBLISH ===");
        testPublish();
        
        Thread.sleep(2000);
        
        System.out.println("\n=== 2. SHARE ===");
        testShare();
        
        Thread.sleep(2000);
        
        System.out.println("\n=== 3. REPLAY ===");
        testReplay();
        
        Thread.sleep(2000);
        
        System.out.println("\n=== 4. REPLAY + REFCOUNT ===");
        testReplayRefCount();
        
        Thread.sleep(3000);
    }
    
    private static void testPublish() throws InterruptedException {
        ConnectableObservable<Long> observable = Observable
            .interval(300, TimeUnit.MILLISECONDS)
            .take(5)
            .doOnNext(i -> System.out.println("  [Emit] " + i))
            .publish();
        
        observable.subscribe(i -> System.out.println("  Observer A: " + i));
        observable.connect();
        
        Thread.sleep(500);
        observable.subscribe(i -> System.out.println("  Observer B: " + i));
    }
    
    private static void testShare() throws InterruptedException {
        Observable<Long> observable = Observable
            .interval(300, TimeUnit.MILLISECONDS)
            .take(5)
            .doOnNext(i -> System.out.println("  [Emit] " + i))
            .share();
        
        observable.subscribe(i -> System.out.println("  Observer A: " + i));
        
        Thread.sleep(500);
        observable.subscribe(i -> System.out.println("  Observer B: " + i));
    }
    
    private static void testReplay() throws InterruptedException {
        ConnectableObservable<Long> observable = Observable
            .interval(300, TimeUnit.MILLISECONDS)
            .take(5)
            .doOnNext(i -> System.out.println("  [Emit] " + i))
            .replay();
        
        observable.subscribe(i -> System.out.println("  Observer A: " + i));
        observable.connect();
        
        Thread.sleep(500);
        observable.subscribe(i -> System.out.println("  Observer B: " + i));
    }
    
    private static void testReplayRefCount() throws InterruptedException {
        Observable<Long> observable = Observable
            .interval(300, TimeUnit.MILLISECONDS)
            .take(5)
            .doOnNext(i -> System.out.println("  [Emit] " + i))
            .replay(2)
            .refCount();
        
        observable.subscribe(i -> System.out.println("  Observer A: " + i));
        
        Thread.sleep(500);
        observable.subscribe(i -> System.out.println("  Observer B: " + i));
    }
}
```

### Tareas
1. Ejecutar y analizar las diferencias en cada estrategia
2. Documentar qué valores recibe el Observer B en cada caso
3. Añadir un Observer C que se suscriba después de 1 segundo en cada escenario
4. Crear una tabla comparativa con las características de cada estrategia

---

## EJERCICIO 8: Desafío Final - Sistema de Chat

### Descripción
Implementar un sistema de chat donde múltiples usuarios compartan mensajes en tiempo real, con historial de los últimos 10 mensajes para nuevos usuarios.

### Requisitos
1. Nuevos usuarios deben ver los últimos 10 mensajes
2. Todos los usuarios conectados deben recibir nuevos mensajes simultáneamente
3. El sistema debe desconectarse automáticamente cuando no hay usuarios
4. Implementar comandos: `/join`, `/leave`, `/history`

### Estructura Sugerida

```java
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.disposables.Disposable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ChatSystem {
    
    private final PublishSubject<ChatMessage> messageSubject;
    private final Observable<ChatMessage> messageStream;
    private final Map<String, Disposable> users;
    private final DateTimeFormatter timeFormatter;
    
    public ChatSystem() {
        this.messageSubject = PublishSubject.create();
        this.messageStream = messageSubject
            .replay(10)
            .refCount();
        this.users = new HashMap<>();
        this.timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    }
    
    // TODO: Implementar métodos
    // - joinUser(String username)
    // - leaveUser(String username)
    // - sendMessage(String username, String content)
    // - showHistory()
    
    static class ChatMessage {
        final String username;
        final String content;
        final LocalDateTime timestamp;
        
        // TODO: Constructor y métodos
    }
    
    public static void main(String[] args) {
        // TODO: Implementar interfaz de usuario con Scanner
        // Permitir múltiples usuarios simultáneos (simular con threads)
    }
}
```

### Criterios de Evaluación
- Correcta implementación de multicast con replay
- Gestión adecuada de suscripciones y memoria
- Manejo de concurrencia
- Experiencia de usuario fluida

---

## Preguntas de Reflexión

1. ¿En qué situaciones es preferible usar `share()` sobre `publish().refCount()`?
2. ¿Cuáles son los riesgos de memoria al usar `replay()` sin límites?
3. ¿Cómo afecta el threading a los operadores de multicast?
4. ¿Cuándo es apropiado usar `autoConnect()` en lugar de `refCount()`?
5. ¿Qué pasa si se llama a `connect()` múltiples veces en un ConnectableObservable?

## Recursos Adicionales

- Experimentar con RxMarbles para visualizar el comportamiento de los operadores
- Profundizar en el uso de Subjects como alternativa a multicast
- Investigar casos de uso en aplicaciones Android con LiveData y RxJava