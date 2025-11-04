# Laboratorio 8: Fluidos y Contrapresión

## Objetivo

Practicar el uso de Flowables y las diferentes estrategias de contrapresión en RxJava para manejar situaciones donde el productor genera datos más rápido que el consumidor.

## Requisitos Previos

- JDK 8 o superior
- RxJava 3.x en el proyecto
- IDE (IntelliJ IDEA, Eclipse, VS Code)

## Configuración del Proyecto

Asegúrate de tener la dependencia de RxJava en tu `pom.xml` o `build.gradle`:

**Maven:**
```xml
<dependency>
    <groupId>io.reactivex.rxjava3</groupId>
    <artifactId>rxjava</artifactId>
    <version>3.1.8</version>
</dependency>
```

**Gradle:**
```gradle
implementation 'io.reactivex.rxjava3:rxjava:3.1.8'
```

## Ejercicio 1: Comparando Observable y Flowable

### Objetivo
Entender la diferencia entre Observable y Flowable cuando hay contrapresión.

### Instrucciones

1. Crea una clase `Lab8Exercise1.java`

2. Implementa el siguiente código que genera eventos rápidamente:

```java
import io.reactivex.rxjava3.core.*;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class Lab8Exercise1 {
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Probando Observable (sin contrapresión) ===");
        testObservable();
        
        Thread.sleep(2000);
        
        System.out.println("\n=== Probando Flowable (con contrapresión) ===");
        testFlowable();
        
        Thread.sleep(5000);
    }
    
    private static void testObservable() {
        Observable.range(1, 1000000)
            .map(i -> {
                return i * 2;
            })
            .observeOn(Schedulers.computation())
            .subscribe(
                i -> {
                    Thread.sleep(10); // Simular procesamiento lento
                    System.out.println("Observable: " + i);
                },
                error -> System.err.println("Error: " + error),
                () -> System.out.println("Observable completado")
            );
    }
    
    private static void testFlowable() {
        Flowable.range(1, 1000000)
            .map(i -> i * 2)
            .observeOn(Schedulers.computation())
            .subscribe(
                i -> {
                    Thread.sleep(10); // Simular procesamiento lento
                    System.out.println("Flowable: " + i);
                },
                error -> System.err.println("Error: " + error),
                () -> System.out.println("Flowable completado")
            );
    }
}
```

3. Ejecuta el programa y observa las diferencias en el comportamiento.

### Preguntas de Análisis

1. ¿Qué diferencias notas en la ejecución entre Observable y Flowable?
2. ¿Cuál consume más memoria y por qué?
3. ¿Observas algún error con el Observable?

## Ejercicio 2: Estrategias de Contrapresión

### Objetivo
Experimentar con las diferentes estrategias de contrapresión: BUFFER, DROP, LATEST, ERROR.

### Instrucciones

1. Crea una clase `Lab8Exercise2.java`

2. Implementa métodos para probar cada estrategia:

```java
import io.reactivex.rxjava3.core.*;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class Lab8Exercise2 {
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== BUFFER Strategy ===");
        testBufferStrategy();
        Thread.sleep(3000);
        
        System.out.println("\n=== DROP Strategy ===");
        testDropStrategy();
        Thread.sleep(3000);
        
        System.out.println("\n=== LATEST Strategy ===");
        testLatestStrategy();
        Thread.sleep(3000);
        
        System.out.println("\n=== ERROR Strategy ===");
        testErrorStrategy();
        Thread.sleep(3000);
    }
    
    private static void testBufferStrategy() {
        Flowable.create(emitter -> {
            for (int i = 1; i <= 100; i++) {
                System.out.println("Emitiendo: " + i);
                emitter.onNext(i);
            }
            emitter.onComplete();
        }, BackpressureStrategy.BUFFER)
        .observeOn(Schedulers.io())
        .subscribe(
            i -> {
                Thread.sleep(50); // Procesamiento lento
                System.out.println("Procesado: " + i);
            },
            error -> System.err.println("Error: " + error),
            () -> System.out.println("Completado")
        );
    }
    
    private static void testDropStrategy() {
        Flowable.create(emitter -> {
            for (int i = 1; i <= 100; i++) {
                System.out.println("Emitiendo: " + i);
                emitter.onNext(i);
            }
            emitter.onComplete();
        }, BackpressureStrategy.DROP)
        .observeOn(Schedulers.io())
        .subscribe(
            i -> {
                Thread.sleep(50); // Procesamiento lento
                System.out.println("Procesado: " + i);
            },
            error -> System.err.println("Error: " + error),
            () -> System.out.println("Completado")
        );
    }
    
    private static void testLatestStrategy() {
        Flowable.create(emitter -> {
            for (int i = 1; i <= 100; i++) {
                System.out.println("Emitiendo: " + i);
                emitter.onNext(i);
            }
            emitter.onComplete();
        }, BackpressureStrategy.LATEST)
        .observeOn(Schedulers.io())
        .subscribe(
            i -> {
                Thread.sleep(50); // Procesamiento lento
                System.out.println("Procesado: " + i);
            },
            error -> System.err.println("Error: " + error),
            () -> System.out.println("Completado")
        );
    }
    
    private static void testErrorStrategy() {
        Flowable.create(emitter -> {
            for (int i = 1; i <= 100; i++) {
                System.out.println("Emitiendo: " + i);
                emitter.onNext(i);
                Thread.sleep(5); // Pequeña pausa
            }
            emitter.onComplete();
        }, BackpressureStrategy.ERROR)
        .observeOn(Schedulers.io())
        .subscribe(
            i -> {
                Thread.sleep(50); // Procesamiento lento
                System.out.println("Procesado: " + i);
            },
            error -> System.err.println("Error: " + error.getClass().getSimpleName() + " - " + error.getMessage()),
            () -> System.out.println("Completado")
        );
    }
}
```

3. Ejecuta el programa y observa el comportamiento de cada estrategia.

### Preguntas de Análisis

1. ¿Qué elementos se pierden con la estrategia DROP?
2. ¿Cuántos elementos procesa la estrategia LATEST?
3. ¿Cuándo se lanza el error en la estrategia ERROR?
4. ¿Cuál estrategia usarías para un sensor de temperatura que emite lecturas cada segundo?

## Ejercicio 3: Operadores de Contrapresión

### Objetivo
Aplicar operadores de contrapresión sobre Flowables existentes.

### Instrucciones

1. Crea una clase `Lab8Exercise3.java`

2. Implementa el siguiente código:

```java
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.util.concurrent.TimeUnit;

public class Lab8Exercise3 {
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== onBackpressureBuffer ===");
        testOnBackpressureBuffer();
        Thread.sleep(3000);
        
        System.out.println("\n=== onBackpressureDrop ===");
        testOnBackpressureDrop();
        Thread.sleep(3000);
        
        System.out.println("\n=== onBackpressureLatest ===");
        testOnBackpressureLatest();
        Thread.sleep(3000);
    }
    
    private static void testOnBackpressureBuffer() {
        Flowable.interval(1, TimeUnit.MILLISECONDS)
            .onBackpressureBuffer(50, 
                () -> System.out.println("⚠️ Buffer desbordado!"),
                BackpressureOverflowStrategy.DROP_OLDEST)
            .observeOn(Schedulers.computation())
            .subscribe(
                i -> {
                    Thread.sleep(100);
                    System.out.println("Procesado: " + i);
                },
                error -> System.err.println("Error: " + error)
            );
    }
    
    private static void testOnBackpressureDrop() {
        Flowable.interval(1, TimeUnit.MILLISECONDS)
            .onBackpressureDrop(item -> 
                System.out.println("🗑️ Descartado: " + item))
            .observeOn(Schedulers.computation())
            .subscribe(
                i -> {
                    Thread.sleep(100);
                    System.out.println("Procesado: " + i);
                },
                error -> System.err.println("Error: " + error)
            );
    }
    
    private static void testOnBackpressureLatest() {
        Flowable.interval(1, TimeUnit.MILLISECONDS)
            .onBackpressureLatest()
            .observeOn(Schedulers.computation())
            .subscribe(
                i -> {
                    Thread.sleep(100);
                    System.out.println("Procesado: " + i);
                },
                error -> System.err.println("Error: " + error)
            );
    }
}
```

3. Ejecuta y analiza el comportamiento de cada operador.

### Tareas Adicionales

Modifica el código para:

1. Cambiar el tamaño del buffer a 10 y observar qué sucede
2. Contar cuántos elementos se descartan con `onBackpressureDrop`
3. Comparar los valores procesados entre las tres estrategias

## Ejercicio 4: Control Manual de Peticiones

### Objetivo
Implementar control manual del flujo usando `Subscription.request()`.

### Instrucciones

1. Crea una clase `Lab8Exercise4.java`

2. Implementa el siguiente código:

```java
import io.reactivex.rxjava3.core.Flowable;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class Lab8Exercise4 {
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Control Manual de Peticiones ===");
        
        Flowable.range(1, 100)
            .subscribe(new Subscriber<Integer>() {
                private Subscription subscription;
                private int count = 0;
                
                @Override
                public void onSubscribe(Subscription s) {
                    this.subscription = s;
                    System.out.println("📝 Suscrito - Solicitando 5 elementos...");
                    subscription.request(5);
                }
                
                @Override
                public void onNext(Integer item) {
                    count++;
                    System.out.println("Procesando: " + item);
                    
                    // Simular procesamiento
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    
                    // Solicitar más elementos cada 5 procesados
                    if (count % 5 == 0) {
                        System.out.println("✅ Procesados 5 elementos - Solicitando 5 más...");
                        subscription.request(5);
                    }
                }
                
                @Override
                public void onError(Throwable t) {
                    System.err.println("❌ Error: " + t.getMessage());
                }
                
                @Override
                public void onComplete() {
                    System.out.println("🎉 Procesamiento completado. Total procesados: " + count);
                }
            });
        
        Thread.sleep(25000);
    }
}
```

3. Ejecuta el programa y observa cómo se controla el flujo.

### Preguntas de Análisis

1. ¿Cuántos elementos se procesan a la vez?
2. ¿Qué pasaría si no llamáramos a `subscription.request()` en `onNext()`?
3. ¿Cómo podrías implementar un procesamiento "bajo demanda"?

### Reto

Modifica el código para implementar un procesamiento adaptativo que solicite más elementos cuando el procesamiento sea rápido y menos cuando sea lento.

## Ejercicio 5: Caso Práctico - Procesamiento de Archivo Grande

### Objetivo
Implementar un sistema que lea un archivo grande línea por línea usando Flowable con contrapresión.

### Instrucciones

1. Crea un archivo de prueba `large-file.txt` con al menos 10,000 líneas:

```java
import java.io.*;

public class GenerateTestFile {
    public static void main(String[] args) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("large-file.txt"))) {
            for (int i = 1; i <= 10000; i++) {
                writer.write("Línea número " + i + " - Datos de prueba para procesamiento reactivo\n");
            }
        }
        System.out.println("Archivo generado correctamente");
    }
}
```

2. Crea una clase `Lab8Exercise5.java`:

```java
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.io.*;

public class Lab8Exercise5 {
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Procesamiento de Archivo Grande ===");
        
        Flowable.<String>generate(
            // Estado inicial - abrir el archivo
            () -> new BufferedReader(new FileReader("large-file.txt")),
            
            // Generar elementos
            (reader, emitter) -> {
                String line = reader.readLine();
                if (line != null) {
                    emitter.onNext(line);
                } else {
                    emitter.onComplete();
                }
            },
            
            // Limpiar recursos
            reader -> {
                System.out.println("Cerrando archivo...");
                reader.close();
            }
        )
        .subscribeOn(Schedulers.io())
        .onBackpressureBuffer(100)
        .observeOn(Schedulers.computation())
        .map(line -> {
            // Simular procesamiento complejo
            Thread.sleep(10);
            return line.toUpperCase();
        })
        .buffer(100) // Procesar en lotes de 100
        .subscribe(
            batch -> System.out.println("✅ Procesado lote de " + batch.size() + " líneas"),
            error -> System.err.println("❌ Error: " + error.getMessage()),
            () -> System.out.println("🎉 Archivo procesado completamente")
        );
        
        Thread.sleep(120000); // Esperar a que termine
    }
}
```

3. Ejecuta el programa y observa el procesamiento.

### Tareas Adicionales

1. Modifica el código para contar las palabras en cada línea
2. Implementa un filtro para procesar solo líneas que contengan cierto texto
3. Guarda el resultado en un nuevo archivo usando Flowable
4. Mide el tiempo de procesamiento con diferentes tamaños de buffer

## Ejercicio 6: Simulador de Sensor con Contrapresión

### Objetivo
Crear un simulador de sensor que emite datos continuamente y aplicar diferentes estrategias de contrapresión.

### Instrucciones

1. Crea una clase `Lab8Exercise6.java`:

```java
import io.reactivex.rxjava3.core.*;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Lab8Exercise6 {
    
    static class SensorReading {
        final long timestamp;
        final double temperature;
        final double humidity;
        
        SensorReading(double temperature, double humidity) {
            this.timestamp = System.currentTimeMillis();
            this.temperature = temperature;
            this.humidity = humidity;
        }
        
        @Override
        public String toString() {
            return String.format("Sensor[temp=%.2f°C, hum=%.2f%%, time=%d]", 
                temperature, humidity, timestamp);
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Simulador de Sensor con Contrapresión ===");
        
        Random random = new Random();
        
        Flowable<SensorReading> sensorStream = Flowable.create(emitter -> {
            System.out.println("🌡️ Sensor iniciado...");
            
            int count = 0;
            while (!emitter.isCancelled() && count < 1000) {
                double temperature = 20 + random.nextDouble() * 10; // 20-30°C
                double humidity = 40 + random.nextDouble() * 20;    // 40-60%
                
                SensorReading reading = new SensorReading(temperature, humidity);
                emitter.onNext(reading);
                
                count++;
                Thread.sleep(1); // Sensor muy rápido
            }
            
            emitter.onComplete();
            System.out.println("🛑 Sensor detenido");
        }, BackpressureStrategy.LATEST);
        
        // Procesamiento con contrapresión
        sensorStream
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.computation())
            .filter(reading -> reading.temperature > 25) // Solo temperaturas altas
            .sample(100, TimeUnit.MILLISECONDS) // Muestrear cada 100ms
            .subscribe(
                reading -> {
                    Thread.sleep(50); // Simular procesamiento
                    System.out.println("📊 Procesando: " + reading);
                },
                error -> System.err.println("❌ Error: " + error),
                () -> System.out.println("✅ Monitoreo completado")
            );
        
        Thread.sleep(10000);
    }
}
```

2. Ejecuta el programa y observa el comportamiento.

### Tareas Adicionales

1. Cambia la estrategia de contrapresión a DROP y compara los resultados
2. Implementa un sistema de alertas cuando la temperatura supere 28°C
3. Calcula el promedio de temperatura en ventanas de 1 segundo
4. Implementa persistencia guardando las lecturas en un archivo

### Preguntas de Análisis

1. ¿Por qué se usa `BackpressureStrategy.LATEST` en este caso?
2. ¿Qué efecto tiene el operador `sample()`?
3. ¿Cuántas lecturas se procesan versus cuántas se emiten?

## Ejercicio 7: Conversión Observable ↔ Flowable

### Objetivo
Practicar la conversión entre Observable y Flowable con diferentes estrategias.

### Instrucciones

1. Crea una clase `Lab8Exercise7.java`:

```java
import io.reactivex.rxjava3.core.*;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class Lab8Exercise7 {
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Conversión Observable → Flowable ===");
        testObservableToFlowable();
        
        Thread.sleep(3000);
        
        System.out.println("\n=== Conversión Flowable → Observable ===");
        testFlowableToObservable();
        
        Thread.sleep(3000);
    }
    
    private static void testObservableToFlowable() {
        // Observable original
        Observable<Integer> observable = Observable.range(1, 100)
            .observeOn(Schedulers.io());
        
        System.out.println("📌 Conversión con estrategia BUFFER:");
        observable
            .toFlowable(BackpressureStrategy.BUFFER)
            .subscribe(
                i -> {
                    Thread.sleep(10);
                    System.out.println("BUFFER: " + i);
                },
                error -> System.err.println("Error: " + error)
            );
        
        try { Thread.sleep(2000); } catch (InterruptedException e) {}
        
        System.out.println("\n📌 Conversión con estrategia DROP:");
        observable
            .toFlowable(BackpressureStrategy.DROP)
            .subscribe(
                i -> {
                    Thread.sleep(10);
                    System.out.println("DROP: " + i);
                },
                error -> System.err.println("Error: " + error)
            );
    }
    
    private static void testFlowableToObservable() {
        // Flowable original
        Flowable<Integer> flowable = Flowable.range(1, 50);
        
        // Conversión a Observable (se pierde contrapresión)
        Observable<Integer> observable = flowable.toObservable();
        
        observable
            .observeOn(Schedulers.computation())
            .subscribe(
                i -> System.out.println("Observable: " + i),
                error -> System.err.println("Error: " + error),
                () -> System.out.println("Completado")
            );
    }
}
```

2. Ejecuta y analiza las conversiones.

### Preguntas de Análisis

1. ¿Cuándo es necesario convertir de Observable a Flowable?
2. ¿Qué se pierde al convertir de Flowable a Observable?
3. ¿Cuál es la mejor estrategia para APIs que no puedes modificar?

## Ejercicio Integrador: Sistema de Procesamiento de Logs

### Objetivo
Crear un sistema completo que procese logs de un servidor usando Flowables con contrapresión apropiada.

### Instrucciones

1. Crea la clase `LogEntry.java`:

```java
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogEntry {
    private final LocalDateTime timestamp;
    private final String level;
    private final String message;
    private final String source;
    
    public LogEntry(String level, String message, String source) {
        this.timestamp = LocalDateTime.now();
        this.level = level;
        this.message = message;
        this.source = source;
    }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getLevel() { return level; }
    public String getMessage() { return message; }
    public String getSource() { return source; }
    
    public boolean isError() {
        return "ERROR".equals(level);
    }
    
    public boolean isWarning() {
        return "WARN".equals(level);
    }
    
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format("[%s] [%s] [%s] %s", 
            timestamp.format(formatter), level, source, message);
    }
}
```

2. Crea la clase `LogProcessor.java`:

```java
import io.reactivex.rxjava3.core.*;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class LogProcessor {
    
    private static final String[] LEVELS = {"INFO", "WARN", "ERROR", "DEBUG"};
    private static final String[] SOURCES = {"API", "Database", "Cache", "Queue", "Auth"};
    private static final String[] MESSAGES = {
        "Request processed successfully",
        "Connection timeout",
        "Invalid credentials",
        "Cache miss",
        "Transaction committed",
        "Null pointer exception",
        "Service unavailable"
    };
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Sistema de Procesamiento de Logs ===\n");
        
        // Generador de logs
        Flowable<LogEntry> logStream = createLogStream();
        
        // Pipeline de procesamiento
        logStream
            .onBackpressureBuffer(
                1000,
                () -> System.out.println("⚠️ Buffer de logs lleno - descartando antiguos"),
                BackpressureOverflowStrategy.DROP_OLDEST
            )
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.computation())
            // Filtrar solo errores y warnings
            .filter(log -> log.isError() || log.isWarning())
            // Enriquecer logs (agregar información adicional)
            .map(log -> enrichLog(log))
            // Agrupar por nivel
            .groupBy(LogEntry::getLevel)
            .flatMap(group -> 
                group
                    .buffer(5, TimeUnit.SECONDS) // Lotes cada 5 segundos
                    .map(logs -> processLogBatch(group.getKey(), logs))
            )
            .subscribe(
                result -> System.out.println("📊 " + result),
                error -> System.err.println("❌ Error en procesamiento: " + error),
                () -> System.out.println("✅ Procesamiento de logs completado")
            );
        
        // Mantener programa vivo
        Thread.sleep(30000);
    }
    
    private static Flowable<LogEntry> createLogStream() {
        Random random = new Random();
        
        return Flowable.create(emitter -> {
            System.out.println("🚀 Generador de logs iniciado...\n");
            
            for (int i = 0; i < 500; i++) {
                if (emitter.isCancelled()) {
                    break;
                }
                
                String level = LEVELS[random.nextInt(LEVELS.length)];
                String source = SOURCES[random.nextInt(SOURCES.length)];
                String message = MESSAGES[random.nextInt(MESSAGES.length)];
                
                LogEntry log = new LogEntry(level, message, source);
                emitter.onNext(log);
                
                // Simular logs a diferente velocidad
                Thread.sleep(random.nextInt(50));
            }
            
            emitter.onComplete();
            System.out.println("\n🛑 Generador de logs detenido");
        }, BackpressureStrategy.BUFFER);
    }
    
    private static LogEntry enrichLog(LogEntry log) {
        // Simular enriquecimiento (agregar contexto, lookup en BD, etc.)
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return log;
    }
    
    private static String processLogBatch(String level, java.util.List<LogEntry> logs) {
        return String.format("Procesado lote [%s]: %d logs", level, logs.size());
    }
}
```

3. Ejecuta el programa y analiza el procesamiento.

### Tareas del Ejercicio Integrador

Extiende el sistema para:

1. **Persistencia**: Guardar errores en un archivo `errors.log`
2. **Alertas**: Enviar notificación cuando haya más de 10 errores en 1 minuto
3. **Estadísticas**: Calcular estadísticas por fuente y nivel
4. **Dashboard**: Mostrar en consola un resumen cada 10 segundos

### Ejemplo de Extensión - Estadísticas

```java
logStream
    .window(10, TimeUnit.SECONDS)
    .flatMap(window -> 
        window
            .groupBy(LogEntry::getLevel)
            .flatMap(group -> 
                group.count().map(count -> 
                    new Statistics(group.getKey(), count)
                )
            )
            .toList()
            .toFlowable()
    )
    .subscribe(stats -> {
        System.out.println("\n=== Estadísticas (últimos 10s) ===");
        stats.forEach(s -> 
            System.out.println(s.getLevel() + ": " + s.getCount() + " logs")
        );
    });
```

## Conclusiones del Laboratorio

Al completar este laboratorio, habrás practicado:

1. ✅ Diferencias entre Observable y Flowable
2. ✅ Las cinco estrategias de contrapresión
3. ✅ Operadores de contrapresión (`onBackpressureBuffer`, `onBackpressureDrop`, `onBackpressureLatest`)
4. ✅ Control manual de flujo con `Subscription.request()`
5. ✅ Casos prácticos reales (archivos, sensores, logs)
6. ✅ Conversión entre Observable y Flowable
7. ✅ Diseño de sistemas reactivos completos con contrapresión

## Preguntas Finales de Reflexión

1. ¿Cuándo usarías Observable vs Flowable en un proyecto real?
2. ¿Qué estrategia de contrapresión elegirías para un sistema de trading en tiempo real?
3. ¿Cómo implementarías contrapresión en una API REST que consume millones de registros?
4. ¿Qué patrones aprendiste que podrías aplicar en tu trabajo actual?

## Recursos Adicionales para Práctica

- Implementa un sistema de monitoreo de métricas con Flowable
- Crea un procesador de eventos de streaming (estilo Kafka consumer)
- Desarrolla un crawler web que respete contrapresión
- Construye un sistema de ETL (Extract, Transform, Load) reactivo
