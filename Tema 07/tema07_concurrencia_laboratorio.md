# Laboratorio: Concurrencia en RxJava

## Objetivos del Laboratorio

- Aplicar diferentes Schedulers de RxJava en casos prácticos
- Comprender la diferencia entre subscribeOn() y observeOn()
- Implementar patrones de concurrencia comunes
- Gestionar múltiples operaciones asíncronas
- Paralelizar operaciones de manera eficiente

## Configuración del Proyecto

### Dependencias Maven

```xml
<dependencies>
    <dependency>
        <groupId>io.reactivex.rxjava3</groupId>
        <artifactId>rxjava</artifactId>
        <version>3.1.8</version>
    </dependency>
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-simple</artifactId>
        <version>2.0.9</version>
    </dependency>
</dependencies>
```

### Dependencias Gradle

```gradle
dependencies {
    implementation 'io.reactivex.rxjava3:rxjava:3.1.8'
    implementation 'org.slf4j:slf4j-simple:2.0.9'
}
```

## Ejercicio 1: Explorando Schedulers Básicos

### Objetivo
Entender cómo funcionan los diferentes Schedulers y visualizar en qué threads se ejecutan las operaciones.

### Código Base

```java
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class Ejercicio1_SchedulersBasicos {
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Ejercicio 1: Schedulers Básicos ===\n");
        
        // TODO 1.1: Ejecutar sin Scheduler (thread principal)
        System.out.println("1.1 - Sin Scheduler:");
        sinScheduler();
        Thread.sleep(1000);
        
        // TODO 1.2: Usar Schedulers.io()
        System.out.println("\n1.2 - Con Schedulers.io():");
        conSchedulerIO();
        Thread.sleep(1000);
        
        // TODO 1.3: Usar Schedulers.computation()
        System.out.println("\n1.3 - Con Schedulers.computation():");
        conSchedulerComputation();
        Thread.sleep(1000);
        
        // TODO 1.4: Usar Schedulers.single()
        System.out.println("\n1.4 - Con Schedulers.single():");
        conSchedulerSingle();
        Thread.sleep(1000);
    }
    
    private static void sinScheduler() {
        Observable.range(1, 3)
            .doOnNext(i -> imprimirThread("Emitiendo", i))
            .map(i -> i * 2)
            .doOnNext(i -> imprimirThread("Procesando", i))
            .subscribe(i -> imprimirThread("Recibiendo", i));
    }
    
    private static void conSchedulerIO() {
        // TODO: Implementar usando subscribeOn(Schedulers.io())
        Observable.range(1, 3)
            .subscribeOn(Schedulers.io())
            .doOnNext(i -> imprimirThread("Emitiendo", i))
            .map(i -> i * 2)
            .doOnNext(i -> imprimirThread("Procesando", i))
            .subscribe(i -> imprimirThread("Recibiendo", i));
    }
    
    private static void conSchedulerComputation() {
        // TODO: Implementar usando subscribeOn(Schedulers.computation())
        Observable.range(1, 3)
            .subscribeOn(Schedulers.computation())
            .doOnNext(i -> imprimirThread("Emitiendo", i))
            .map(i -> i * 2)
            .doOnNext(i -> imprimirThread("Procesando", i))
            .subscribe(i -> imprimirThread("Recibiendo", i));
    }
    
    private static void conSchedulerSingle() {
        // TODO: Implementar usando subscribeOn(Schedulers.single())
        Observable.range(1, 3)
            .subscribeOn(Schedulers.single())
            .doOnNext(i -> imprimirThread("Emitiendo", i))
            .map(i -> i * 2)
            .doOnNext(i -> imprimirThread("Procesando", i))
            .subscribe(i -> imprimirThread("Recibiendo", i));
    }
    
    private static void imprimirThread(String etapa, Object valor) {
        System.out.printf("%s [%d] en thread: %s%n", 
            etapa, 
            valor, 
            Thread.currentThread().getName());
    }
}
```

### Tareas

1. Ejecuta el código y observa en qué threads se ejecuta cada operación
2. Modifica el número de elementos emitidos y observa el comportamiento
3. Agrega más operadores (filter, map) y observa dónde se ejecutan

### Preguntas de Reflexión

- ¿Qué diferencias observas entre los distintos Schedulers?
- ¿Qué pasa con el thread principal cuando usas subscribeOn()?
- ¿Por qué necesitamos Thread.sleep() al final?

## Ejercicio 2: subscribeOn vs observeOn

### Objetivo
Comprender la diferencia fundamental entre subscribeOn() y observeOn() y cómo afectan la ejecución.

### Código Base

```java
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.util.concurrent.TimeUnit;

public class Ejercicio2_SubscribeOnVsObserveOn {
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Ejercicio 2: subscribeOn vs observeOn ===\n");
        
        // TODO 2.1: Solo subscribeOn
        System.out.println("2.1 - Solo subscribeOn:");
        soloSubscribeOn();
        Thread.sleep(2000);
        
        // TODO 2.2: Solo observeOn
        System.out.println("\n2.2 - Solo observeOn:");
        soloObserveOn();
        Thread.sleep(2000);
        
        // TODO 2.3: Combinando ambos
        System.out.println("\n2.3 - Combinando subscribeOn y observeOn:");
        combinandoAmbos();
        Thread.sleep(2000);
        
        // TODO 2.4: Múltiples observeOn
        System.out.println("\n2.4 - Múltiples observeOn:");
        multiplesObserveOn();
        Thread.sleep(2000);
    }
    
    private static void soloSubscribeOn() {
        Observable.just("A", "B", "C")
            .subscribeOn(Schedulers.io())
            .doOnNext(s -> log("Después de just", s))
            .map(s -> {
                log("En map", s);
                return s.toLowerCase();
            })
            .subscribe(s -> log("En subscribe", s));
    }
    
    private static void soloObserveOn() {
        Observable.just("A", "B", "C")
            .doOnNext(s -> log("Después de just", s))
            .observeOn(Schedulers.computation())
            .map(s -> {
                log("En map", s);
                return s.toLowerCase();
            })
            .subscribe(s -> log("En subscribe", s));
    }
    
    private static void combinandoAmbos() {
        // TODO: Implementar usando subscribeOn(io) y observeOn(computation)
        Observable.just("A", "B", "C")
            .subscribeOn(Schedulers.io())
            .doOnNext(s -> log("Después de subscribeOn", s))
            .map(s -> {
                log("Antes de observeOn", s);
                return s.toLowerCase();
            })
            .observeOn(Schedulers.computation())
            .map(s -> {
                log("Después de observeOn", s);
                return s + "!";
            })
            .subscribe(s -> log("En subscribe", s));
    }
    
    private static void multiplesObserveOn() {
        // TODO: Usar observeOn varias veces en la cadena
        Observable.range(1, 5)
            .doOnNext(i -> log("Inicio", i))
            .observeOn(Schedulers.io())
            .map(i -> {
                log("En IO", i);
                return i * 2;
            })
            .observeOn(Schedulers.computation())
            .map(i -> {
                log("En Computation", i);
                return i + 10;
            })
            .observeOn(Schedulers.single())
            .subscribe(i -> log("En Single", i));
    }
    
    private static void log(String etapa, Object valor) {
        System.out.printf("[%s] %s = %s%n",
            Thread.currentThread().getName(),
            etapa,
            valor);
    }
}
```

### Tareas

1. Completa los métodos marcados con TODO
2. Ejecuta cada método y documenta en qué thread se ejecuta cada operación
3. Experimenta agregando más observeOn() en diferentes posiciones
4. Intenta usar múltiples subscribeOn() y observa qué sucede

### Preguntas de Reflexión

- ¿Qué efecto tiene subscribeOn() en toda la cadena?
- ¿Cómo afecta observeOn() solo a las operaciones posteriores?
- ¿Qué pasa si usas subscribeOn() dos veces?

## Ejercicio 3: Simulación de Llamadas a API

### Objetivo
Implementar un caso realista de llamadas a servicios externos usando Schedulers apropiados.

### Código Base

```java
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.util.concurrent.TimeUnit;

public class Ejercicio3_SimulacionAPI {
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Ejercicio 3: Simulación de APIs ===\n");
        
        // TODO 3.1: Llamada a API simple
        System.out.println("3.1 - Llamada API simple:");
        llamadaAPISimple();
        Thread.sleep(3000);
        
        // TODO 3.2: Múltiples llamadas secuenciales
        System.out.println("\n3.2 - Llamadas secuenciales:");
        llamadasSecuenciales();
        Thread.sleep(5000);
        
        // TODO 3.3: Múltiples llamadas paralelas
        System.out.println("\n3.3 - Llamadas paralelas:");
        llamadasParalelas();
        Thread.sleep(5000);
    }
    
    private static void llamadaAPISimple() {
        // TODO: Implementar llamada a API con subscribeOn(Schedulers.io())
        Observable.fromCallable(() -> llamarUsuarioAPI(1))
            .subscribeOn(Schedulers.io())
            .doOnSubscribe(d -> System.out.println("Iniciando llamada..."))
            .subscribe(
                usuario -> System.out.println("Usuario recibido: " + usuario),
                error -> System.err.println("Error: " + error),
                () -> System.out.println("Completado")
            );
    }
    
    private static void llamadasSecuenciales() {
        // TODO: Hacer 3 llamadas secuenciales usando concatMap
        Observable.just(1, 2, 3)
            .doOnNext(id -> System.out.println("Procesando ID: " + id))
            .concatMap(id -> 
                Observable.fromCallable(() -> llamarUsuarioAPI(id))
                    .subscribeOn(Schedulers.io())
            )
            .subscribe(
                usuario -> System.out.println("Usuario: " + usuario),
                Throwable::printStackTrace
            );
    }
    
    private static void llamadasParalelas() {
        // TODO: Hacer 3 llamadas en paralelo usando flatMap
        Observable.just(1, 2, 3)
            .flatMap(id ->
                Observable.fromCallable(() -> llamarUsuarioAPI(id))
                    .subscribeOn(Schedulers.io())
                    .doOnNext(u -> System.out.println("Completado: " + u))
            )
            .subscribe(
                usuario -> System.out.println("Recibido: " + usuario),
                Throwable::printStackTrace
            );
    }
    
    // Simulación de llamada a API (bloquea el thread 1-2 segundos)
    private static String llamarUsuarioAPI(int userId) {
        try {
            Thread.sleep(1000 + (long)(Math.random() * 1000));
            return "Usuario-" + userId;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    
    // Simulación de llamada a servicio de productos
    private static String llamarProductosAPI(int userId) {
        try {
            Thread.sleep(800 + (long)(Math.random() * 700));
            return "Productos[User-" + userId + "]";
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
```

### Tareas

1. Implementa las llamadas marcadas como TODO
2. Mide el tiempo de ejecución de las llamadas secuenciales vs paralelas
3. Agrega manejo de errores con onErrorReturn
4. Implementa un sistema de reintentos con retry()

### Ejercicio Adicional

Implementa un método que:
1. Llame a la API de usuario
2. Con el resultado, llame a la API de productos
3. Combine ambos resultados
4. Todo ejecutándose en los Schedulers apropiados

```java
private static void llamadaDependiente() {
    // TODO: Implementar
    Observable.fromCallable(() -> llamarUsuarioAPI(1))
        .subscribeOn(Schedulers.io())
        .flatMap(usuario ->
            Observable.fromCallable(() -> llamarProductosAPI(1))
                .subscribeOn(Schedulers.io())
                .map(productos -> usuario + " - " + productos)
        )
        .subscribe(System.out::println);
}
```

## Ejercicio 4: Procesamiento Paralelo de Datos

### Objetivo
Implementar procesamiento paralelo eficiente usando flatMap y limitando la concurrencia.

### Código Base

```java
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.util.concurrent.TimeUnit;

public class Ejercicio4_ProcesamientoParalelo {
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Ejercicio 4: Procesamiento Paralelo ===\n");
        
        // TODO 4.1: Procesamiento sin límite de concurrencia
        System.out.println("4.1 - Sin límite de concurrencia:");
        long inicio1 = System.currentTimeMillis();
        procesamientoSinLimite();
        Thread.sleep(5000);
        System.out.println("Tiempo: " + (System.currentTimeMillis() - inicio1) + "ms\n");
        
        // TODO 4.2: Procesamiento con límite de concurrencia
        System.out.println("4.2 - Con límite de concurrencia:");
        long inicio2 = System.currentTimeMillis();
        procesamientoConLimite();
        Thread.sleep(5000);
        System.out.println("Tiempo: " + (System.currentTimeMillis() - inicio2) + "ms");
    }
    
    private static void procesamientoSinLimite() {
        // TODO: Procesar 20 elementos en paralelo sin límite
        Observable.range(1, 20)
            .doOnNext(i -> System.out.println("Iniciando procesamiento de: " + i))
            .flatMap(i ->
                Observable.fromCallable(() -> procesarDato(i))
                    .subscribeOn(Schedulers.io())
            )
            .subscribe(
                resultado -> System.out.println("Completado: " + resultado),
                Throwable::printStackTrace
            );
    }
    
    private static void procesamientoConLimite() {
        // TODO: Procesar 20 elementos con máximo 5 concurrentes
        Observable.range(1, 20)
            .doOnNext(i -> System.out.println("Iniciando procesamiento de: " + i))
            .flatMap(i ->
                Observable.fromCallable(() -> procesarDato(i))
                    .subscribeOn(Schedulers.io()),
                5  // Máximo 5 operaciones concurrentes
            )
            .subscribe(
                resultado -> System.out.println("Completado: " + resultado),
                Throwable::printStackTrace
            );
    }
    
    private static String procesarDato(int dato) {
        try {
            // Simula procesamiento que tarda entre 200-500ms
            Thread.sleep(200 + (long)(Math.random() * 300));
            System.out.println("  [" + Thread.currentThread().getName() + "] Procesando: " + dato);
            return "Resultado-" + dato;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
```

### Tareas

1. Ejecuta ambos métodos y compara los resultados
2. Modifica el límite de concurrencia y observa el impacto
3. Agrega medición de tiempo más precisa
4. Experimenta con diferentes Schedulers

### Preguntas de Reflexión

- ¿Cuál método es más rápido y por qué?
- ¿Qué impacto tiene el límite de concurrencia?
- ¿Cuántos threads observas ejecutándose simultáneamente?

## Ejercicio 5: Uso de ParallelFlowable

### Objetivo
Utilizar ParallelFlowable para procesamiento paralelo estructurado.

### Código Base

```java
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class Ejercicio5_ParallelFlowable {
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Ejercicio 5: ParallelFlowable ===\n");
        
        // TODO 5.1: Procesamiento paralelo básico
        System.out.println("5.1 - ParallelFlowable básico:");
        parallelBasico();
        Thread.sleep(3000);
        
        // TODO 5.2: Procesamiento con número específico de rails
        System.out.println("\n5.2 - Con rails específicos:");
        parallelConRails();
        Thread.sleep(3000);
    }
    
    private static void parallelBasico() {
        // TODO: Usar parallel() para procesar datos
        Flowable.range(1, 20)
            .parallel()
            .runOn(Schedulers.computation())
            .map(i -> {
                log("Procesando", i);
                return procesarNumero(i);
            })
            .sequential()
            .subscribe(resultado -> log("Resultado", resultado));
    }
    
    private static void parallelConRails() {
        // TODO: Usar parallel(n) con número específico de rails
        int numCores = Runtime.getRuntime().availableProcessors();
        System.out.println("Número de cores: " + numCores);
        
        Flowable.range(1, 40)
            .parallel(4)  // 4 rails paralelos
            .runOn(Schedulers.computation())
            .map(i -> {
                log("Procesando", i);
                return procesarNumero(i);
            })
            .sequential()
            .subscribe(resultado -> log("Resultado", resultado));
    }
    
    private static int procesarNumero(int n) {
        try {
            Thread.sleep(100);
            return n * n;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    
    private static void log(String etapa, Object valor) {
        System.out.printf("[%s] %s: %s%n",
            Thread.currentThread().getName(),
            etapa,
            valor);
    }
}
```

### Tareas

1. Implementa los métodos usando ParallelFlowable
2. Compara el rendimiento con flatMap
3. Experimenta con diferentes números de rails
4. Observa cómo se distribuye el trabajo

## Ejercicio 6: Sistema de Procesamiento de Pedidos (Integrador)

### Objetivo
Crear un sistema completo que integre todos los conceptos de concurrencia aprendidos.

### Escenario

Debes implementar un sistema que:
1. Reciba pedidos de clientes
2. Valide cada pedido (operación I/O)
3. Calcule el precio con descuentos (operación CPU)
4. Procese el pago (operación I/O)
5. Envíe confirmación (operación I/O)

### Código Base

```java
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.util.concurrent.TimeUnit;

public class Ejercicio6_SistemaPedidos {
    
    static class Pedido {
        int id;
        String cliente;
        double monto;
        
        public Pedido(int id, String cliente, double monto) {
            this.id = id;
            this.cliente = cliente;
            this.monto = monto;
        }
        
        @Override
        public String toString() {
            return String.format("Pedido{id=%d, cliente='%s', monto=%.2f}", 
                id, cliente, monto);
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Ejercicio 6: Sistema de Pedidos ===\n");
        
        // TODO: Implementar el flujo completo
        procesarPedidos();
        
        Thread.sleep(10000);
    }
    
    private static void procesarPedidos() {
        Observable<Pedido> pedidos = generarPedidos();
        
        // TODO: Implementar el pipeline completo
        pedidos
            .subscribeOn(Schedulers.io())
            .doOnNext(p -> log("Pedido recibido", p))
            // 1. Validar pedido (I/O)
            .flatMap(pedido ->
                Observable.fromCallable(() -> validarPedido(pedido))
                    .subscribeOn(Schedulers.io())
            )
            .doOnNext(p -> log("Pedido validado", p))
            // 2. Calcular precio con descuento (CPU)
            .observeOn(Schedulers.computation())
            .map(pedido -> calcularDescuento(pedido))
            .doOnNext(p -> log("Descuento aplicado", p))
            // 3. Procesar pago (I/O)
            .observeOn(Schedulers.io())
            .flatMap(pedido ->
                Observable.fromCallable(() -> procesarPago(pedido))
                    .subscribeOn(Schedulers.io())
            )
            .doOnNext(p -> log("Pago procesado", p))
            // 4. Enviar confirmación (I/O)
            .flatMap(pedido ->
                Observable.fromCallable(() -> enviarConfirmacion(pedido))
                    .subscribeOn(Schedulers.io())
            )
            .subscribe(
                pedido -> log("COMPLETADO", pedido),
                error -> System.err.println("ERROR: " + error),
                () -> System.out.println("\n=== Todos los pedidos procesados ===")
            );
    }
    
    private static Observable<Pedido> generarPedidos() {
        return Observable.just(
            new Pedido(1, "Cliente A", 100.0),
            new Pedido(2, "Cliente B", 250.0),
            new Pedido(3, "Cliente C", 75.0),
            new Pedido(4, "Cliente D", 500.0),
            new Pedido(5, "Cliente E", 150.0)
        );
    }
    
    private static Pedido validarPedido(Pedido pedido) {
        try {
            Thread.sleep(500);  // Simula validación en BD
            log("  Validando", pedido.id);
            return pedido;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    
    private static Pedido calcularDescuento(Pedido pedido) {
        log("  Calculando descuento", pedido.id);
        if (pedido.monto > 200) {
            pedido.monto *= 0.9;  // 10% descuento
        }
        return pedido;
    }
    
    private static Pedido procesarPago(Pedido pedido) {
        try {
            Thread.sleep(800);  // Simula procesamiento de pago
            log("  Procesando pago", pedido.id);
            return pedido;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    
    private static Pedido enviarConfirmacion(Pedido pedido) {
        try {
            Thread.sleep(300);  // Simula envío de email
            log("  Enviando confirmación", pedido.id);
            return pedido;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    
    private static void log(String etapa, Object valor) {
        System.out.printf("[%s] %s: %s%n",
            Thread.currentThread().getName(),
            etapa,
            valor);
    }
}
```

### Tareas

1. Completa la implementación del pipeline
2. Agrega manejo de errores apropiado
3. Implementa reintentos para operaciones que pueden fallar
4. Agrega métricas de tiempo de procesamiento
5. Limita la concurrencia a 3 pedidos simultáneos

### Ejercicio Adicional Avanzado

Modifica el sistema para:
1. Procesar pedidos en lotes de 10
2. Implementar un sistema de prioridades
3. Agregar timeout para operaciones lentas
4. Implementar circuit breaker para servicios externos

## Ejercicio 7: Debugging y Monitoreo

### Objetivo
Aprender a debuggear y monitorear streams concurrentes.

### Código Base

```java
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.util.concurrent.atomic.AtomicInteger;

public class Ejercicio7_DebuggingConcurrencia {
    
    private static AtomicInteger contador = new AtomicInteger(0);
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Ejercicio 7: Debugging ===\n");
        
        // TODO: Implementar monitoreo completo
        monitorearStream();
        
        Thread.sleep(5000);
        System.out.println("\nTotal de operaciones: " + contador.get());
    }
    
    private static void monitorearStream() {
        Observable.range(1, 10)
            .doOnSubscribe(d -> log("SUBSCRIBE", "Iniciando stream"))
            .subscribeOn(Schedulers.io())
            .doOnNext(i -> {
                log("EMIT", i);
                contador.incrementAndGet();
            })
            .observeOn(Schedulers.computation())
            .map(i -> {
                log("MAP", "Procesando " + i);
                return i * 2;
            })
            .filter(i -> {
                boolean pasa = i % 4 == 0;
                log("FILTER", i + " -> " + pasa);
                return pasa;
            })
            .observeOn(Schedulers.single())
            .doOnComplete(() -> log("COMPLETE", "Stream completado"))
            .doOnError(e -> log("ERROR", e.getMessage()))
            .subscribe(
                i -> log("SUBSCRIBER", "Recibido: " + i),
                Throwable::printStackTrace
            );
    }
    
    private static void log(String nivel, Object mensaje) {
        System.out.printf("[%s][%s] %s%n",
            Thread.currentThread().getName(),
            nivel,
            mensaje);
    }
}
```

### Tareas

1. Ejecuta el código y analiza la salida
2. Agrega más operadores de debugging (doOnError, doOnComplete, doFinally)
3. Implementa un sistema de logging más robusto
4. Agrega timestamps a cada operación

## Desafíos Adicionales

### Desafío 1: Rate Limiting
Implementa un sistema que limite las llamadas a una API a 5 por segundo usando window() y concatMap().

### Desafío 2: Cache Distribuido
Crea un sistema de cache que:
- Consulte primero la cache (rápido, Schedulers.computation())
- Si no está, consulte la base de datos (lento, Schedulers.io())
- Actualice la cache con el resultado

### Desafío 3: Fan-out/Fan-in
Implementa un patrón donde:
- Un pedido se divide en múltiples subtareas
- Cada subtarea se procesa en paralelo
- Los resultados se combinan al final

## Recursos de Apoyo

### Código de Utilidad

```java
public class ThreadUtils {
    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
    
    public static void logThread(String message) {
        System.out.printf("[%s] %s%n", 
            Thread.currentThread().getName(), 
            message);
    }
    
    public static String getThreadName() {
        return Thread.currentThread().getName();
    }
}
```

### Plantilla de Testing

```java
import org.junit.jupiter.api.Test;
import io.reactivex.rxjava3.schedulers.TestScheduler;
import java.util.concurrent.TimeUnit;

public class ConcurrenciaTest {
    
    @Test
    public void testSchedulerVirtual() {
        TestScheduler scheduler = new TestScheduler();
        
        Observable.interval(1, TimeUnit.SECONDS, scheduler)
            .take(3)
            .test()
            .assertNoValues();
        
        scheduler.advanceTimeBy(3, TimeUnit.SECONDS);
        
        // Verificar resultados
    }
}
```

## Criterios de Evaluación

Para cada ejercicio, verifica que:

1. **Corrección**: El código funciona según lo especificado
2. **Scheduler apropiado**: Usa el Scheduler correcto para cada tipo de operación
3. **Eficiencia**: No desperdicia recursos ni threads
4. **Manejo de errores**: Incluye manejo apropiado de errores
5. **Logging**: Permite observar qué está ocurriendo
6. **Limpieza**: Dispone correctamente de recursos

## Notas Finales

- Experimenta cambiando los Schedulers y observa el impacto
- Usa herramientas de profiling para medir el rendimiento
- Recuerda que la concurrencia añade complejidad; úsala cuando sea necesaria
- Documenta tus decisiones sobre qué Scheduler usar y por qué
