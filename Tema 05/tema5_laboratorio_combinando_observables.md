# LABORATORIO 5: COMBINANDO OBSERVABLES

## Objetivo

Aprenderás a aplicar los diferentes operadores de combinación de RxJava en escenarios prácticos, comprendiendo cuándo usar cada estrategia según las necesidades de tu aplicación.

## Configuración del Proyecto

### Dependencias Maven (pom.xml)

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

### Estructura del Proyecto

```
src/main/java/
└── com.curso.rxjava.lab5/
    ├── Ejercicio1_CombinacionSecuencial.java
    ├── Ejercicio2_CombinacionParalela.java
    ├── Ejercicio3_CombinacionCondicional.java
    ├── Ejercicio4_Agregacion.java
    ├── Ejercicio5_CasosPracticos.java
    └── modelos/
        ├── Usuario.java
        ├── Pedido.java
        ├── Producto.java
        └── PerfilCompleto.java
```

## Ejercicio 1: Combinación Secuencial

### Objetivo
Implementar operadores de combinación secuencial (concat, concatMap, startWith).

### Código Base

```java
package com.curso.rxjava.lab5;

import io.reactivex.rxjava3.core.Observable;
import java.util.concurrent.TimeUnit;

public class Ejercicio1_CombinacionSecuencial {
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Ejercicio 1: Combinación Secuencial ===\n");
        
        ejercicio1_Concat();
        Thread.sleep(1000);
        
        ejercicio2_ConcatMap();
        Thread.sleep(2000);
        
        ejercicio3_StartWith();
        Thread.sleep(1000);
        
        ejercicio4_ConcatenacionMultiple();
    }
    
    // TODO: Ejercicio 1.1 - Usar concat para procesar logs en orden
    private static void ejercicio1_Concat() {
        System.out.println("1.1 - Concatenación de Logs:");
        
        Observable<String> logsServidor1 = Observable.just(
            "[Server1] Iniciando...",
            "[Server1] Conectado",
            "[Server1] Listo"
        );
        
        Observable<String> logsServidor2 = Observable.just(
            "[Server2] Iniciando...",
            "[Server2] Conectado",
            "[Server2] Listo"
        );
        
        // TODO: Concatena logsServidor1 y logsServidor2 usando concat()
        // Imprime cada log
    }
    
    // TODO: Ejercicio 1.2 - Usar concatMap para procesar usuarios secuencialmente
    private static void ejercicio2_ConcatMap() {
        System.out.println("\n1.2 - ConcatMap con simulación de API:");
        
        Observable<String> usuarios = Observable.just("user1", "user2", "user3");
        
        // TODO: Usa concatMap para obtener datos de cada usuario secuencialmente
        // Simula una llamada a API con delay
        // Imprime: "Procesando user1", "Datos de user1 obtenidos", etc.
    }
    
    // TODO: Ejercicio 1.3 - Usar startWith para añadir encabezado
    private static void ejercicio3_StartWith() {
        System.out.println("\n1.3 - StartWith para encabezado:");
        
        Observable<String> contenido = Observable.just(
            "Línea 1",
            "Línea 2",
            "Línea 3"
        );
        
        // TODO: Usa startWith para añadir "=== INICIO DEL DOCUMENTO ==="
        // Imprime el resultado
    }
    
    // TODO: Ejercicio 1.4 - Concatenar múltiples fuentes
    private static void ejercicio4_ConcatenacionMultiple() {
        System.out.println("\n1.4 - Concatenación múltiple:");
        
        Observable<Integer> numeros1 = Observable.range(1, 3);
        Observable<Integer> numeros2 = Observable.range(10, 3);
        Observable<Integer> numeros3 = Observable.range(100, 3);
        
        // TODO: Concatena los tres Observables usando concat()
        // Imprime todos los números en orden
    }
    
    // Método auxiliar para simular llamada a API
    private static Observable<String> obtenerDatosUsuario(String usuario) {
        return Observable.just("Datos de " + usuario)
            .delay(500, TimeUnit.MILLISECONDS);
    }
}
```

### Tareas

1. Completa el método `ejercicio1_Concat()` usando `Observable.concat()`
2. Implementa `ejercicio2_ConcatMap()` simulando llamadas asíncronas secuenciales
3. Completa `ejercicio3_StartWith()` añadiendo un encabezado al stream
4. Implementa `ejercicio4_ConcatenacionMultiple()` con múltiples fuentes

## Ejercicio 2: Combinación Paralela

### Objetivo
Dominar operadores de combinación paralela (merge, zip, combineLatest).

### Código Base

```java
package com.curso.rxjava.lab5;

import io.reactivex.rxjava3.core.Observable;
import java.util.concurrent.TimeUnit;

public class Ejercicio2_CombinacionParalela {
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Ejercicio 2: Combinación Paralela ===\n");
        
        ejercicio1_Merge();
        Thread.sleep(3000);
        
        ejercicio2_Zip();
        Thread.sleep(1000);
        
        ejercicio3_CombineLatest();
        Thread.sleep(3000);
        
        ejercicio4_MergeDelayError();
    }
    
    // TODO: Ejercicio 2.1 - Merge de múltiples sensores
    private static void ejercicio1_Merge() {
        System.out.println("2.1 - Merge de sensores:");
        
        Observable<String> sensor1 = Observable.interval(500, TimeUnit.MILLISECONDS)
            .map(i -> "Sensor1: Temperatura " + (20 + i) + "°C")
            .take(4);
        
        Observable<String> sensor2 = Observable.interval(700, TimeUnit.MILLISECONDS)
            .map(i -> "Sensor2: Humedad " + (60 + i) + "%")
            .take(3);
        
        // TODO: Usa merge para combinar las lecturas de ambos sensores
        // Observa cómo se intercalan según el tiempo
    }
    
    // TODO: Ejercicio 2.2 - Zip para combinar información relacionada
    private static void ejercicio2_Zip() {
        System.out.println("\n2.2 - Zip de información de productos:");
        
        Observable<String> nombres = Observable.just(
            "Laptop", "Mouse", "Teclado", "Monitor"
        );
        
        Observable<Double> precios = Observable.just(
            999.99, 25.50, 75.00
        );
        
        Observable<Integer> stock = Observable.just(
            10, 50, 30, 5
        );
        
        // TODO: Usa zip para combinar nombre, precio y stock
        // Imprime: "Laptop - $999.99 - Stock: 10"
        // ¿Qué pasa con el Monitor que no tiene precio?
    }
    
    // TODO: Ejercicio 2.3 - CombineLatest para formulario reactivo
    private static void ejercicio3_CombineLatest() {
        System.out.println("\n2.3 - CombineLatest simulando formulario:");
        
        Observable<String> nombre = Observable.interval(500, TimeUnit.MILLISECONDS)
            .map(i -> {
                String[] nombres = {"", "J", "Jo", "Joh", "John"};
                return nombres[(int) Math.min(i, 4)];
            })
            .take(5);
        
        Observable<String> email = Observable.interval(700, TimeUnit.MILLISECONDS)
            .map(i -> {
                String[] emails = {"", "j", "j@", "j@em", "j@email.com"};
                return emails[(int) Math.min(i, 4)];
            })
            .take(5);
        
        // TODO: Usa combineLatest para validar el formulario
        // Imprime: "Formulario válido: true/false" cada vez que cambie algún campo
        // Considera válido cuando nombre.length() > 3 && email.contains("@")
    }
    
    // TODO: Ejercicio 2.4 - MergeDelayError para procesar múltiples fuentes
    private static void ejercicio4_MergeDelayError() {
        System.out.println("\n2.4 - MergeDelayError con manejo de errores:");
        
        Observable<Integer> fuente1 = Observable.just(1, 2, 3);
        
        Observable<Integer> fuente2 = Observable.create(emitter -> {
            emitter.onNext(10);
            emitter.onError(new Exception("Error en fuente2"));
        });
        
        Observable<Integer> fuente3 = Observable.just(100, 200);
        
        // TODO: Usa mergeDelayError para combinar las tres fuentes
        // Observa que se procesan todos los valores antes de emitir el error
    }
}
```

### Tareas

1. Implementa `ejercicio1_Merge()` para combinar lecturas de sensores
2. Completa `ejercicio2_Zip()` y observa qué ocurre con streams de diferente longitud
3. Implementa `ejercicio3_CombineLatest()` simulando validación de formulario en tiempo real
4. Completa `ejercicio4_MergeDelayError()` y compara con merge() normal

## Ejercicio 3: Combinación Condicional

### Objetivo
Aplicar operadores de combinación condicional (amb, takeUntil, skipUntil).

### Código Base

```java
package com.curso.rxjava.lab5;

import io.reactivex.rxjava3.core.Observable;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class Ejercicio3_CombinacionCondicional {
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Ejercicio 3: Combinación Condicional ===\n");
        
        ejercicio1_Amb();
        Thread.sleep(3000);
        
        ejercicio2_TakeUntil();
        Thread.sleep(4000);
        
        ejercicio3_SkipUntil();
        Thread.sleep(4000);
        
        ejercicio4_CombinacionCompleja();
    }
    
    // TODO: Ejercicio 3.1 - Amb para elegir el servidor más rápido
    private static void ejercicio1_Amb() {
        System.out.println("3.1 - Amb - Selección del servidor más rápido:");
        
        Observable<String> servidor1 = Observable.timer(1000, TimeUnit.MILLISECONDS)
            .map(i -> "Respuesta del Servidor 1")
            .concatWith(Observable.interval(500, TimeUnit.MILLISECONDS)
                .map(i -> "Datos-S1-" + i)
                .take(3));
        
        Observable<String> servidor2 = Observable.timer(500, TimeUnit.MILLISECONDS)
            .map(i -> "Respuesta del Servidor 2")
            .concatWith(Observable.interval(500, TimeUnit.MILLISECONDS)
                .map(i -> "Datos-S2-" + i)
                .take(3));
        
        Observable<String> servidor3 = Observable.timer(1500, TimeUnit.MILLISECONDS)
            .map(i -> "Respuesta del Servidor 3");
        
        // TODO: Usa amb para seleccionar el servidor que responda primero
        // Imprime todos los valores del servidor ganador
    }
    
    // TODO: Ejercicio 3.2 - TakeUntil para detener proceso
    private static void ejercicio2_TakeUntil() {
        System.out.println("\n3.2 - TakeUntil - Contador con señal de stop:");
        
        Observable<Long> contador = Observable.interval(500, TimeUnit.MILLISECONDS)
            .map(i -> {
                System.out.println("Emitiendo: " + i);
                return i;
            });
        
        Observable<Long> senalStop = Observable.timer(2500, TimeUnit.MILLISECONDS)
            .doOnNext(i -> System.out.println("¡Señal de STOP recibida!"));
        
        // TODO: Usa takeUntil para detener el contador cuando llegue la señal
        // El contador debe detenerse después de aproximadamente 2.5 segundos
    }
    
    // TODO: Ejercicio 3.3 - SkipUntil para comenzar proceso
    private static void ejercicio3_SkipUntil() {
        System.out.println("\n3.3 - SkipUntil - Esperando señal de inicio:");
        
        Observable<String> mensajes = Observable.interval(500, TimeUnit.MILLISECONDS)
            .map(i -> "Mensaje-" + i)
            .doOnNext(msg -> System.out.println("Generado: " + msg));
        
        Observable<Long> senalInicio = Observable.timer(2000, TimeUnit.MILLISECONDS)
            .doOnNext(i -> System.out.println("¡Señal de INICIO recibida!"));
        
        // TODO: Usa skipUntil para ignorar mensajes hasta que llegue la señal
        // Toma solo 5 mensajes después del inicio
    }
    
    // TODO: Ejercicio 3.4 - Combinación de takeUntil y skipUntil
    private static void ejercicio4_CombinacionCompleja() {
        System.out.println("\n3.4 - Combinación takeUntil + skipUntil:");
        
        Observable<Long> datos = Observable.interval(300, TimeUnit.MILLISECONDS)
            .map(i -> i);
        
        Observable<Long> inicio = Observable.timer(1000, TimeUnit.MILLISECONDS);
        Observable<Long> fin = Observable.timer(3000, TimeUnit.MILLISECONDS);
        
        // TODO: Usa skipUntil para comenzar después de 1 segundo
        // Y takeUntil para terminar después de 3 segundos
        // Crea una ventana de captura de datos entre segundo 1 y 3
    }
}
```

### Tareas

1. Implementa `ejercicio1_Amb()` para simular la selección del servidor más rápido
2. Completa `ejercicio2_TakeUntil()` para detener un proceso con una señal
3. Implementa `ejercicio3_SkipUntil()` para comenzar a procesar después de una señal
4. Completa `ejercicio4_CombinacionCompleja()` combinando ambos operadores

## Ejercicio 4: Operadores de Agregación

### Objetivo
Aplicar operadores de agregación (reduce, scan, collect).

### Código Base

```java
package com.curso.rxjava.lab5;

import io.reactivex.rxjava3.core.Observable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Ejercicio4_Agregacion {
    
    public static void main(String[] args) {
        System.out.println("=== Ejercicio 4: Operadores de Agregación ===\n");
        
        ejercicio1_Reduce();
        ejercicio2_Scan();
        ejercicio3_Collect();
        ejercicio4_AggregacionCompleja();
    }
    
    // TODO: Ejercicio 4.1 - Reduce para cálculos estadísticos
    private static void ejercicio1_Reduce() {
        System.out.println("4.1 - Reduce para estadísticas:");
        
        Observable<Integer> ventas = Observable.just(150, 200, 175, 300, 250, 180);
        
        // TODO: Usa reduce para calcular:
        // a) Total de ventas
        // b) Venta promedio (usa reduce y luego map)
        // c) Venta máxima
        
        System.out.println();
    }
    
    // TODO: Ejercicio 4.2 - Scan para acumulación progresiva
    private static void ejercicio2_Scan() {
        System.out.println("4.2 - Scan para balance de cuenta:");
        
        Observable<Integer> transacciones = Observable.just(
            100,   // depósito
            -50,   // retiro
            -20,   // retiro
            200,   // depósito
            -30    // retiro
        );
        
        // TODO: Usa scan para mostrar el balance después de cada transacción
        // Comienza con balance inicial de 1000
        // Imprime: "Balance actual: $1100", "Balance actual: $1050", etc.
    }
    
    // TODO: Ejercicio 4.3 - Collect para agrupar datos
    private static void ejercicio3_Collect() {
        System.out.println("\n4.3 - Collect para agrupar por categoría:");
        
        Observable<Producto> productos = Observable.just(
            new Producto("Laptop", "Electrónica", 999.99),
            new Producto("Mesa", "Muebles", 299.99),
            new Producto("Ratón", "Electrónica", 25.50),
            new Producto("Silla", "Muebles", 150.00),
            new Producto("Teclado", "Electrónica", 75.00)
        );
        
        // TODO: Usa collect para agrupar productos por categoría en un Map
        // Map<String, List<Producto>>
        // Imprime el resultado
    }
    
    // TODO: Ejercicio 4.4 - Agregación compleja con múltiples operadores
    private static void ejercicio4_AggregacionCompleja() {
        System.out.println("\n4.4 - Agregación compleja:");
        
        Observable<Integer> numeros = Observable.range(1, 20);
        
        // TODO: Realiza las siguientes operaciones:
        // 1. Filtra solo números pares
        // 2. Usa scan para mostrar la suma acumulativa
        // 3. Usa reduce para obtener la suma final
        // 4. Imprime ambos resultados
    }
    
    // Clase auxiliar
    static class Producto {
        String nombre;
        String categoria;
        double precio;
        
        public Producto(String nombre, String categoria, double precio) {
            this.nombre = nombre;
            this.categoria = categoria;
            this.precio = precio;
        }
        
        @Override
        public String toString() {
            return nombre + " ($" + precio + ")";
        }
    }
}
```

### Tareas

1. Implementa `ejercicio1_Reduce()` para calcular estadísticas de ventas
2. Completa `ejercicio2_Scan()` para mostrar un balance progresivo
3. Implementa `ejercicio3_Collect()` para agrupar productos por categoría
4. Completa `ejercicio4_AggregacionCompleja()` combinando varios operadores

## Ejercicio 5: Casos Prácticos Reales

### Objetivo
Integrar todos los operadores de combinación en escenarios del mundo real.

### Código Base

```java
package com.curso.rxjava.lab5;

import io.reactivex.rxjava3.core.Observable;
import java.util.concurrent.TimeUnit;

public class Ejercicio5_CasosPracticos {
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Ejercicio 5: Casos Prácticos ===\n");
        
        ejercicio1_CarritoCompras();
        Thread.sleep(2000);
        
        ejercicio2_BusquedaMultiServidor();
        Thread.sleep(3000);
        
        ejercicio3_DashboardTiempoReal();
    }
    
    // TODO: Ejercicio 5.1 - Carrito de compras con validaciones
    private static void ejercicio1_CarritoCompras() {
        System.out.println("5.1 - Proceso de checkout:");
        
        // TODO: Implementa un flujo de checkout que:
        // 1. Valida el carrito (concatMap)
        // 2. Verifica inventario (concatMap)
        // 3. Procesa el pago (concatMap)
        // 4. Envía confirmación (concatMap)
        // Simula cada paso con un delay y un mensaje
        
        Observable<String> carritoId = Observable.just("CART-12345");
        
        carritoId
            .doOnNext(id -> System.out.println("Iniciando proceso para: " + id))
            // TODO: Completa el flujo
            .subscribe(
                resultado -> System.out.println("✓ " + resultado),
                error -> System.err.println("✗ Error: " + error.getMessage())
            );
    }
    
    // TODO: Ejercicio 5.2 - Búsqueda en múltiples servidores con timeout
    private static void ejercicio2_BusquedaMultiServidor() {
        System.out.println("\n5.2 - Búsqueda multi-servidor:");
        
        String busqueda = "RxJava tutorial";
        
        Observable<String> servidor1 = buscarEnServidor(busqueda, "Server1", 800);
        Observable<String> servidor2 = buscarEnServidor(busqueda, "Server2", 1500);
        Observable<String> servidor3 = buscarEnServidor(busqueda, "Server3", 500);
        
        // TODO: Usa amb para obtener el resultado del servidor más rápido
        // Añade timeout de 2 segundos
        // Si falla, usa un servidor de respaldo (fallback)
    }
    
    // TODO: Ejercicio 5.3 - Dashboard en tiempo real
    private static void ejercicio3_DashboardTiempoReal() throws InterruptedException {
        System.out.println("\n5.3 - Dashboard en tiempo real:");
        
        Observable<Integer> usuariosActivos = Observable.interval(1, TimeUnit.SECONDS)
            .map(i -> 100 + (int)(Math.random() * 50));
        
        Observable<Double> cpuUsage = Observable.interval(500, TimeUnit.MILLISECONDS)
            .map(i -> 20.0 + (Math.random() * 60));
        
        Observable<Integer> requestsPorSegundo = Observable.interval(700, TimeUnit.MILLISECONDS)
            .map(i -> 50 + (int)(Math.random() * 100));
        
        // TODO: Usa combineLatest para actualizar el dashboard
        // cada vez que cualquier métrica cambie
        // Imprime: "Dashboard: [Usuarios: 125, CPU: 45.3%, Requests: 87]"
        // Toma solo 10 actualizaciones
        
        Thread.sleep(8000);
    }
    
    // Método auxiliar
    private static Observable<String> buscarEnServidor(String query, String servidor, long delay) {
        return Observable.timer(delay, TimeUnit.MILLISECONDS)
            .map(i -> "Resultados de '" + query + "' desde " + servidor);
    }
    
    // Simula validación de carrito
    private static Observable<String> validarCarrito(String carritoId) {
        return Observable.just("Carrito " + carritoId + " validado")
            .delay(300, TimeUnit.MILLISECONDS);
    }
    
    // Simula verificación de inventario
    private static Observable<String> verificarInventario(String carritoId) {
        return Observable.just("Inventario disponible")
            .delay(500, TimeUnit.MILLISECONDS);
    }
    
    // Simula procesamiento de pago
    private static Observable<String> procesarPago(String carritoId) {
        return Observable.just("Pago procesado")
            .delay(700, TimeUnit.MILLISECONDS);
    }
    
    // Simula envío de confirmación
    private static Observable<String> enviarConfirmacion(String carritoId) {
        return Observable.just("Confirmación enviada - Pedido completado")
            .delay(300, TimeUnit.MILLISECONDS);
    }
}
```

### Tareas

1. Implementa `ejercicio1_CarritoCompras()` con un flujo de checkout completo
2. Completa `ejercicio2_BusquedaMultiServidor()` con amb y manejo de timeout
3. Implementa `ejercicio3_DashboardTiempoReal()` combinando múltiples métricas

## Desafíos Adicionales

### Desafío 1: Sistema de Notificaciones
Implementa un sistema que combine notificaciones de múltiples fuentes (email, SMS, push) usando merge(), pero que solo envíe cada notificación una vez (usa distinct()).

### Desafío 2: Sincronización de Datos
Crea un sistema que sincronice datos de 3 APIs diferentes usando zip(), pero que maneje el caso donde alguna API falla usando zipDelayError().

### Desafío 3: Rate Limiting Inteligente
Implementa un sistema de rate limiting que combine:
- Un contador de requests con scan()
- Una ventana temporal con window()
- Una señal de stop cuando se exceda el límite con takeUntil()

## Preguntas de Reflexión

1. ¿Cuándo usarías `concat()` vs `merge()` en una aplicación real?

2. ¿Qué diferencias observaste entre `zip()` y `combineLatest()`? ¿En qué casos usarías cada uno?

3. ¿Por qué `scan()` emite valores intermedios mientras que `reduce()` solo emite el resultado final?

4. ¿Cómo afecta el uso de `amb()` al consumo de recursos cuando tienes múltiples fuentes de datos?

5. ¿Qué ventajas tiene usar `mergeDelayError()` en lugar de `merge()` normal?

## Soluciones de Referencia

Las soluciones completas están disponibles en el repositorio del curso. Sin embargo, intenta resolver los ejercicios por tu cuenta primero para maximizar el aprendizaje.

## Recursos Adicionales

**Testing:**
Usa `TestScheduler` para controlar el tiempo en tus tests de operadores de combinación.

**Debugging:**
Usa `.doOnNext()` en cada paso para visualizar cómo fluyen los datos a través de los operadores.

**Marble Diagrams:**
Visita https://rxmarbles.com/ para visualizar cómo funciona cada operador de combinación.
