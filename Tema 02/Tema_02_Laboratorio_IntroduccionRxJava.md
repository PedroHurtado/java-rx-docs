# Laboratorio 2: Introducción a ReactiveX y RxJava 2

## Objetivos del Laboratorio

Al finalizar este laboratorio, el estudiante será capaz de:

- Configurar correctamente un proyecto con RxJava 2
- Crear diferentes tipos de Observables
- Implementar suscripciones básicas con manejo de eventos
- Distinguir entre Cold y Hot Observables
- Aplicar transformaciones básicas a streams reactivos
- Utilizar Schedulers para control de concurrencia

**Duración estimada**: 2.5 horas

---

## Requisitos Previos

- Java JDK 8 o superior instalado
- Maven o Gradle configurado
- IDE (IntelliJ IDEA, Eclipse o VS Code)
- Conocimientos básicos de Java y programación funcional

---

## Parte 1: Configuración del Entorno (20 minutos)

### Ejercicio 1.1: Crear Proyecto Maven

**Objetivo**: Configurar un proyecto Maven con RxJava 2.

**Pasos**:

1. Crear un nuevo proyecto Maven:

```bash
mvn archetype:generate -DgroupId=com.rxjava.lab -DartifactId=rxjava-lab02 -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false
```

2. Editar el archivo `pom.xml` y configurar:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.rxjava.lab</groupId>
    <artifactId>rxjava-lab02</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.source>11</maven.compiler.source>
        <maven.compiler.target>11</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <dependencies>
        <!-- RxJava 2 -->
        <dependency>
            <groupId>io.reactivex.rxjava2</groupId>
            <artifactId>rxjava</artifactId>
            <version>2.2.21</version>
        </dependency>

        <!-- SLF4J para logging (opcional pero recomendado) -->
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-simple</artifactId>
            <version>1.7.32</version>
        </dependency>
    </dependencies>
</project>
```

3. Compilar el proyecto:

```bash
mvn clean compile
```

**Verificación**: El proyecto debe compilar sin errores.

### Ejercicio 1.2: Verificar Instalación

**Objetivo**: Confirmar que RxJava está correctamente configurado.

Crear la clase `VerificacionInstalacion.java`:

```java
package com.rxjava.lab;

import io.reactivex.Observable;

public class VerificacionInstalacion {
    public static void main(String[] args) {
        System.out.println("=== Verificación de RxJava 2 ===\n");
        
        Observable.just("RxJava", "está", "instalado", "correctamente")
                .subscribe(palabra -> System.out.println("✓ " + palabra));
        
        System.out.println("\n=== Verificación completada ===");
    }
}
```

**Ejecución**:

```bash
mvn exec:java -Dexec.mainClass="com.rxjava.lab.VerificacionInstalacion"
```

**Resultado esperado**:

```
=== Verificación de RxJava 2 ===

✓ RxJava
✓ está
✓ instalado
✓ correctamente

=== Verificación completada ===
```

---

## Parte 2: Creación de Observables (30 minutos)

### Ejercicio 2.1: Observables Básicos con just() y from()

**Objetivo**: Crear Observables usando diferentes métodos factory.

Crear la clase `CreacionObservables.java`:

```java
package com.rxjava.lab;

import io.reactivex.Observable;
import java.util.Arrays;
import java.util.List;

public class CreacionObservables {
    
    public static void main(String[] args) {
        ejemploJust();
        ejemploFromIterable();
        ejemploFromArray();
        ejemploRange();
    }
    
    // Ejemplo 1: Observable.just()
    public static void ejemploJust() {
        System.out.println("\n=== Ejemplo 1: Observable.just() ===");
        
        Observable<String> observable = Observable.just(
            "Lunes", "Martes", "Miércoles", "Jueves", "Viernes"
        );
        
        observable.subscribe(
            dia -> System.out.println("Día: " + dia),
            error -> System.err.println("Error: " + error),
            () -> System.out.println("Semana completa")
        );
    }
    
    // Ejemplo 2: Observable.fromIterable()
    public static void ejemploFromIterable() {
        System.out.println("\n=== Ejemplo 2: Observable.fromIterable() ===");
        
        List<String> lenguajes = Arrays.asList("Java", "Kotlin", "Scala", "Groovy");
        
        Observable<String> observable = Observable.fromIterable(lenguajes);
        
        observable.subscribe(
            lenguaje -> System.out.println("Lenguaje: " + lenguaje)
        );
    }
    
    // Ejemplo 3: Observable.fromArray()
    public static void ejemploFromArray() {
        System.out.println("\n=== Ejemplo 3: Observable.fromArray() ===");
        
        Integer[] numeros = {2, 4, 6, 8, 10};
        
        Observable.fromArray(numeros)
                .subscribe(n -> System.out.println("Número par: " + n));
    }
    
    // Ejemplo 4: Observable.range()
    public static void ejemploRange() {
        System.out.println("\n=== Ejemplo 4: Observable.range() ===");
        
        Observable.range(1, 10)
                .subscribe(n -> System.out.println("Conteo: " + n));
    }
}
```

**Tarea**:

1. Ejecutar el código y observar los resultados
2. Modificar `ejemploJust()` para emitir los nombres de 5 ciudades
3. Modificar `ejemploRange()` para generar los números del 10 al 20

**Preguntas de reflexión**:

- ¿Qué diferencia hay entre `just()` y `fromIterable()`?
- ¿Cuándo usarías cada uno?

### Ejercicio 2.2: Observable.create()

**Objetivo**: Crear Observables personalizados usando `create()`.

Crear la clase `ObservablePersonalizado.java`:

```java
package com.rxjava.lab;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;

public class ObservablePersonalizado {
    
    public static void main(String[] args) {
        observableBasico();
        observableConError();
    }
    
    // Ejemplo 1: Observable.create() básico
    public static void observableBasico() {
        System.out.println("\n=== Observable.create() Básico ===");
        
        Observable<Integer> observable = Observable.create(emitter -> {
            try {
                // Emitir elementos
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
                emitter.onNext(4);
                emitter.onNext(5);
                
                // Señalar finalización
                emitter.onComplete();
                
            } catch (Exception e) {
                emitter.onError(e);
            }
        });
        
        observable.subscribe(
            numero -> System.out.println("Recibido: " + numero),
            error -> System.err.println("Error: " + error),
            () -> System.out.println("Completado")
        );
    }
    
    // Ejemplo 2: Observable con lógica condicional
    public static void observableConError() {
        System.out.println("\n=== Observable con Manejo de Error ===");
        
        Observable<Integer> observable = Observable.create(emitter -> {
            for (int i = 1; i <= 10; i++) {
                if (emitter.isDisposed()) {
                    return; // Salir si el observador se desuscribió
                }
                
                if (i == 7) {
                    emitter.onError(new RuntimeException("¡El número 7 no está permitido!"));
                    return;
                }
                
                emitter.onNext(i);
            }
            emitter.onComplete();
        });
        
        observable.subscribe(
            numero -> System.out.println("Número: " + numero),
            error -> System.err.println("Error capturado: " + error.getMessage()),
            () -> System.out.println("Proceso completado")
        );
    }
}
```

**Tarea**:

1. Crear un Observable que emita los números del 1 al 100, pero que lance un error si encuentra un número divisible por 13
2. Implementar el manejo de errores apropiadamente

---

## Parte 3: Suscripciones y Observers (30 minutos)

### Ejercicio 3.1: Diferentes Formas de Suscribirse

**Objetivo**: Explorar las diferentes formas de suscribirse a un Observable.

Crear la clase `TiposSuscripcion.java`:

```java
package com.rxjava.lab;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class TiposSuscripcion {
    
    public static void main(String[] args) {
        suscripcionSimple();
        suscripcionConErrorHandler();
        suscripcionCompleta();
        suscripcionConObserver();
    }
    
    // Forma 1: Suscripción simple (solo onNext)
    public static void suscripcionSimple() {
        System.out.println("\n=== Suscripción Simple ===");
        
        Observable.just("A", "B", "C")
                .subscribe(letra -> System.out.println("Letra: " + letra));
    }
    
    // Forma 2: Con manejo de errores
    public static void suscripcionConErrorHandler() {
        System.out.println("\n=== Suscripción con Error Handler ===");
        
        Observable.just(1, 2, 0, 4)
                .map(n -> 10 / n)  // Esto causará error con 0
                .subscribe(
                    resultado -> System.out.println("Resultado: " + resultado),
                    error -> System.err.println("Error capturado: " + error.getMessage())
                );
    }
    
    // Forma 3: Suscripción completa (onNext, onError, onComplete)
    public static void suscripcionCompleta() {
        System.out.println("\n=== Suscripción Completa ===");
        
        Observable.range(1, 5)
                .subscribe(
                    numero -> System.out.println("Número: " + numero),
                    error -> System.err.println("Error: " + error),
                    () -> System.out.println("¡Secuencia completada!")
                );
    }
    
    // Forma 4: Usando interfaz Observer
    public static void suscripcionConObserver() {
        System.out.println("\n=== Suscripción con Observer ===");
        
        Observable<String> observable = Observable.just("Reactive", "Programming", "RxJava");
        
        Observer<String> observer = new Observer<String>() {
            private Disposable disposable;
            private int count = 0;
            
            @Override
            public void onSubscribe(Disposable d) {
                this.disposable = d;
                System.out.println("Suscripción iniciada");
            }
            
            @Override
            public void onNext(String palabra) {
                count++;
                System.out.println(count + ". " + palabra);
            }
            
            @Override
            public void onError(Throwable e) {
                System.err.println("Error: " + e.getMessage());
            }
            
            @Override
            public void onComplete() {
                System.out.println("Total de palabras procesadas: " + count);
            }
        };
        
        observable.subscribe(observer);
    }
}
```

**Tarea**:

1. Ejecutar el código y analizar cada forma de suscripción
2. Crear un Observable que emita temperaturas (15, 20, 25, 30, 35) y suscribirse mostrando si hace frío (<18), templado (18-28) o calor (>28)

### Ejercicio 3.2: Gestión de Disposables

**Objetivo**: Aprender a gestionar suscripciones y evitar memory leaks.

Crear la clase `GestionDisposables.java`:

```java
package com.rxjava.lab;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import java.util.concurrent.TimeUnit;

public class GestionDisposables {
    
    public static void main(String[] args) throws InterruptedException {
        ejemploDisposable();
        ejemploDisposableMultiple();
    }
    
    // Ejemplo 1: Disposable básico
    public static void ejemploDisposable() throws InterruptedException {
        System.out.println("\n=== Gestión de Disposable ===");
        
        // Observable que emite cada segundo
        Observable<Long> observable = Observable.interval(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io());
        
        System.out.println("Iniciando suscripción...");
        Disposable disposable = observable.subscribe(
            numero -> System.out.println("Tick: " + numero)
        );
        
        // Dejar que emita algunos elementos
        Thread.sleep(5000);
        
        // Cancelar suscripción
        if (!disposable.isDisposed()) {
            System.out.println("Cancelando suscripción...");
            disposable.dispose();
            System.out.println("Suscripción cancelada");
        }
        
        // Esperar para verificar que no se emiten más elementos
        Thread.sleep(3000);
        System.out.println("Fin del ejemplo");
    }
    
    // Ejemplo 2: Múltiples Disposables
    public static void ejemploDisposableMultiple() throws InterruptedException {
        System.out.println("\n=== Múltiples Disposables ===");
        
        Observable<Long> observable1 = Observable.interval(500, TimeUnit.MILLISECONDS);
        Observable<Long> observable2 = Observable.interval(1000, TimeUnit.MILLISECONDS);
        
        Disposable d1 = observable1.subscribe(n -> System.out.println("Observable 1: " + n));
        Disposable d2 = observable2.subscribe(n -> System.out.println("Observable 2: " + n));
        
        Thread.sleep(3000);
        
        // Cancelar ambos
        d1.dispose();
        d2.dispose();
        
        System.out.println("Ambas suscripciones canceladas");
        Thread.sleep(2000);
    }
}
```

**Tarea**:

1. Modificar el ejemplo para usar `CompositeDisposable` que permita gestionar múltiples suscripciones fácilmente
2. Implementar un ejemplo donde se cancele la suscripción automáticamente después de recibir 10 elementos

---

## Parte 4: Transformaciones Básicas (30 minutos)

### Ejercicio 4.1: Operadores map() y filter()

**Objetivo**: Aplicar transformaciones y filtros a streams reactivos.

Crear la clase `TransformacionesBasicas.java`:

```java
package com.rxjava.lab;

import io.reactivex.Observable;

public class TransformacionesBasicas {
    
    public static void main(String[] args) {
        ejemploMap();
        ejemploFilter();
        ejemploMapYFilter();
        ejercicioTemperaturas();
    }
    
    // Ejemplo 1: Operador map()
    public static void ejemploMap() {
        System.out.println("\n=== Operador map() ===");
        
        Observable.just("java", "kotlin", "scala")
                .map(String::toUpperCase)
                .subscribe(lenguaje -> System.out.println("Lenguaje: " + lenguaje));
    }
    
    // Ejemplo 2: Operador filter()
    public static void ejemploFilter() {
        System.out.println("\n=== Operador filter() ===");
        
        Observable.range(1, 20)
                .filter(n -> n % 2 == 0)  // Solo números pares
                .subscribe(n -> System.out.println("Número par: " + n));
    }
    
    // Ejemplo 3: Combinando map() y filter()
    public static void ejemploMapYFilter() {
        System.out.println("\n=== Combinando map() y filter() ===");
        
        Observable.range(1, 10)
                .filter(n -> n % 2 != 0)      // Solo impares
                .map(n -> n * n)               // Elevar al cuadrado
                .filter(n -> n > 25)           // Mayores a 25
                .subscribe(n -> System.out.println("Resultado: " + n));
    }
    
    // Ejercicio: Procesamiento de temperaturas
    public static void ejercicioTemperaturas() {
        System.out.println("\n=== Ejercicio: Temperaturas ===");
        
        Observable.just(15.5, 22.3, 28.7, 31.2, 18.9, 25.4)
                .map(celsius -> celsius * 9/5 + 32)  // Convertir a Fahrenheit
                .filter(fahrenheit -> fahrenheit > 77)  // Solo temperaturas cálidas
                .subscribe(temp -> System.out.printf("Temperatura cálida: %.1f°F%n", temp));
    }
}
```

**Tarea**:

1. Crear un Observable que emita edades (15, 18, 21, 25, 30, 35, 40) y:
   - Filtrar solo mayores de edad (≥18)
   - Calcular el año de nacimiento (año actual - edad)
   - Mostrar el resultado

2. Implementar un pipeline que procese nombres de productos:
   - Convertir a mayúsculas
   - Filtrar los que tienen más de 5 caracteres
   - Añadir prefijo "PRODUCTO: "

### Ejercicio 4.2: Operadores take(), skip() y distinct()

**Objetivo**: Controlar el flujo de elementos en un Observable.

Crear la clase `ControlFlujo.java`:

```java
package com.rxjava.lab;

import io.reactivex.Observable;

public class ControlFlujo {
    
    public static void main(String[] args) {
        ejemploTake();
        ejemploSkip();
        ejemploDistinct();
        ejemploCombinado();
    }
    
    // Ejemplo 1: take() - tomar primeros N elementos
    public static void ejemploTake() {
        System.out.println("\n=== Operador take() ===");
        
        Observable.range(1, 100)
                .take(5)  // Solo los primeros 5
                .subscribe(n -> System.out.println("Número: " + n));
    }
    
    // Ejemplo 2: skip() - saltar primeros N elementos
    public static void ejemploSkip() {
        System.out.println("\n=== Operador skip() ===");
        
        Observable.just("Primero", "Segundo", "Tercero", "Cuarto", "Quinto")
                .skip(2)  // Saltar los primeros 2
                .subscribe(elemento -> System.out.println("Elemento: " + elemento));
    }
    
    // Ejemplo 3: distinct() - eliminar duplicados
    public static void ejemploDistinct() {
        System.out.println("\n=== Operador distinct() ===");
        
        Observable.just("Java", "Python", "Java", "Kotlin", "Python", "Scala", "Java")
                .distinct()
                .subscribe(lenguaje -> System.out.println("Lenguaje único: " + lenguaje));
    }
    
    // Ejemplo 4: Combinación de operadores
    public static void ejemploCombinado() {
        System.out.println("\n=== Combinación de Operadores ===");
        
        Observable.range(1, 20)
                .skip(5)           // Saltar primeros 5
                .take(10)          // Tomar siguiente 10
                .filter(n -> n % 2 == 0)  // Solo pares
                .map(n -> "Número: " + n)
                .subscribe(System.out::println);
    }
}
```

**Tarea**:

1. Crear un Observable de 50 números y extraer los elementos del 10 al 20
2. Implementar un pipeline que procese una lista de calificaciones (pueden tener duplicados) y:
   - Eliminar duplicados
   - Filtrar aprobados (≥5)
   - Tomar las 10 mejores calificaciones

---

## Parte 5: Cold vs Hot Observables (30 minutos)

### Ejercicio 5.1: Comportamiento de Cold Observables

**Objetivo**: Entender el comportamiento de Cold Observables.

Crear la clase `ColdObservables.java`:

```java
package com.rxjava.lab;

import io.reactivex.Observable;

public class ColdObservables {
    
    public static void main(String[] args) throws InterruptedException {
        ejemploColdObservable();
    }
    
    public static void ejemploColdObservable() throws InterruptedException {
        System.out.println("\n=== Cold Observable ===");
        
        // Cold Observable: emite desde el inicio para cada suscriptor
        Observable<Integer> coldObservable = Observable.create(emitter -> {
            System.out.println("Comenzando emisión de elementos...");
            for (int i = 1; i <= 5; i++) {
                emitter.onNext(i);
                Thread.sleep(500);
            }
            emitter.onComplete();
        });
        
        System.out.println("Suscriptor 1:");
        coldObservable.subscribe(n -> System.out.println("  Suscriptor 1 recibió: " + n));
        
        System.out.println("\nEsperando 2 segundos...\n");
        Thread.sleep(2000);
        
        System.out.println("Suscriptor 2:");
        coldObservable.subscribe(n -> System.out.println("  Suscriptor 2 recibió: " + n));
    }
}
```

**Análisis**:

- Observar que ambos suscriptores reciben la secuencia completa
- Cada suscriptor causa una nueva ejecución de la lógica de emisión

**Tarea**:

1. Modificar el ejemplo para que emita nombres de ciudades
2. Agregar un tercer suscriptor después de 4 segundos

### Ejercicio 5.2: Convertir a Hot Observable

**Objetivo**: Entender el comportamiento de Hot Observables usando `publish()`.

Crear la clase `HotObservables.java`:

```java
package com.rxjava.lab;

import io.reactivex.Observable;
import io.reactivex.observables.ConnectableObservable;

import java.util.concurrent.TimeUnit;

public class HotObservables {
    
    public static void main(String[] args) throws InterruptedException {
        ejemploHotObservable();
    }
    
    public static void ejemploHotObservable() throws InterruptedException {
        System.out.println("\n=== Hot Observable ===");
        
        // Convertir a Hot Observable usando publish()
        ConnectableObservable<Long> hotObservable = Observable
                .interval(1, TimeUnit.SECONDS)
                .publish();
        
        // Suscriptor 1
        System.out.println("Suscriptor 1 conectado");
        hotObservable.subscribe(n -> System.out.println("  Suscriptor 1: " + n));
        
        // Iniciar emisión
        hotObservable.connect();
        
        // Esperar 3 segundos
        Thread.sleep(3000);
        
        // Suscriptor 2 (se une tarde)
        System.out.println("\nSuscriptor 2 conectado (se perdió elementos 0, 1, 2)");
        hotObservable.subscribe(n -> System.out.println("  Suscriptor 2: " + n));
        
        // Continuar por 5 segundos más
        Thread.sleep(5000);
    }
}
```

**Análisis**:

- El Suscriptor 1 recibe todos los elementos desde 0
- El Suscriptor 2 solo recibe elementos desde que se suscribió
- La emisión es independiente de las suscripciones

**Tarea**:

1. Experimentar con diferentes tiempos de espera
2. Agregar un tercer suscriptor en un momento diferente

---

## Parte 6: Introducción a Schedulers (30 minutos)

### Ejercicio 6.1: Schedulers Básicos

**Objetivo**: Comprender cómo los Schedulers controlan la ejecución en diferentes hilos.

Crear la clase `IntroSchedulers.java`:

```java
package com.rxjava.lab;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class IntroSchedulers {
    
    public static void main(String[] args) throws InterruptedException {
        sinScheduler();
        Thread.sleep(1000);
        conScheduler();
        Thread.sleep(3000);
    }
    
    // Sin Scheduler (ejecuta en hilo principal)
    public static void sinScheduler() {
        System.out.println("\n=== Sin Scheduler ===");
        
        Observable.just("A", "B", "C")
                .map(letra -> {
                    System.out.println("  map() en hilo: " + Thread.currentThread().getName());
                    return letra.toLowerCase();
                })
                .subscribe(letra -> {
                    System.out.println("  subscribe() en hilo: " + Thread.currentThread().getName());
                    System.out.println("  Letra: " + letra);
                });
    }
    
    // Con Scheduler (ejecuta en hilo diferente)
    public static void conScheduler() {
        System.out.println("\n=== Con Scheduler ===");
        
        Observable.just("A", "B", "C")
                .subscribeOn(Schedulers.io())  // Observable en hilo I/O
                .map(letra -> {
                    System.out.println("  map() en hilo: " + Thread.currentThread().getName());
                    return letra.toLowerCase();
                })
                .observeOn(Schedulers.computation())  // Observer en hilo computation
                .subscribe(letra -> {
                    System.out.println("  subscribe() en hilo: " + Thread.currentThread().getName());
                    System.out.println("  Letra: " + letra);
                });
    }
}
```

**Análisis**:

- `subscribeOn()`: Define en qué hilo se ejecuta el Observable
- `observeOn()`: Define en qué hilo se procesan los resultados
- Observar los nombres de los hilos en cada caso

### Ejercicio 6.2: Tipos de Schedulers

**Objetivo**: Experimentar con diferentes tipos de Schedulers.

Crear la clase `TiposSchedulers.java`:

```java
package com.rxjava.lab;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class TiposSchedulers {
    
    public static void main(String[] args) throws InterruptedException {
        ejemploIO();
        Thread.sleep(2000);
        
        ejemploComputation();
        Thread.sleep(2000);
        
        ejemploNewThread();
        Thread.sleep(2000);
    }
    
    // Schedulers.io() - para operaciones I/O
    public static void ejemploIO() {
        System.out.println("\n=== Schedulers.io() ===");
        
        Observable.range(1, 5)
                .subscribeOn(Schedulers.io())
                .map(n -> {
                    System.out.println("  Procesando " + n + " en: " + Thread.currentThread().getName());
                    return n * 10;
                })
                .subscribe(resultado -> System.out.println("  Resultado: " + resultado));
    }
    
    // Schedulers.computation() - para cálculos intensivos
    public static void ejemploComputation() {
        System.out.println("\n=== Schedulers.computation() ===");
        
        Observable.range(1, 5)
                .subscribeOn(Schedulers.computation())
                .map(n -> {
                    System.out.println("  Calculando " + n + " en: " + Thread.currentThread().getName());
                    return Math.pow(n, 2);
                })
                .subscribe(resultado -> System.out.println("  Resultado: " + resultado));
    }
    
    // Schedulers.newThread() - crea nuevo hilo para cada tarea
    public static void ejemploNewThread() {
        System.out.println("\n=== Schedulers.newThread() ===");
        
        Observable.range(1, 3)
                .flatMap(n -> Observable.just(n)
                        .subscribeOn(Schedulers.newThread())
                        .map(num -> {
                            System.out.println("  Procesando " + num + " en: " + Thread.currentThread().getName());
                            return num * 100;
                        })
                )
                .subscribe(resultado -> System.out.println("  Resultado: " + resultado));
    }
}
```

**Tarea**:

1. Ejecutar el código y analizar en qué hilos se ejecuta cada operación
2. Modificar para usar `Schedulers.single()` y observar el comportamiento

---

## Ejercicios Integradores (30 minutos)

### Ejercicio Integrador 1: Procesador de Pedidos

**Objetivo**: Integrar múltiples conceptos en un ejemplo práctico.

Crear la clase `ProcesadorPedidos.java`:

```java
package com.rxjava.lab;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

import java.util.Arrays;
import java.util.List;

class Pedido {
    private int id;
    private String producto;
    private double precio;
    private boolean enStock;
    
    public Pedido(int id, String producto, double precio, boolean enStock) {
        this.id = id;
        this.producto = producto;
        this.precio = precio;
        this.enStock = enStock;
    }
    
    // Getters
    public int getId() { return id; }
    public String getProducto() { return producto; }
    public double getPrecio() { return precio; }
    public boolean isEnStock() { return enStock; }
    
    @Override
    public String toString() {
        return String.format("Pedido #%d: %s ($%.2f) - %s", 
            id, producto, precio, enStock ? "En stock" : "Agotado");
    }
}

public class ProcesadorPedidos {
    
    public static void main(String[] args) throws InterruptedException {
        List<Pedido> pedidos = Arrays.asList(
            new Pedido(1, "Laptop", 899.99, true),
            new Pedido(2, "Mouse", 25.50, true),
            new Pedido(3, "Teclado", 75.00, false),
            new Pedido(4, "Monitor", 299.99, true),
            new Pedido(5, "Webcam", 89.99, false),
            new Pedido(6, "Auriculares", 150.00, true)
        );
        
        System.out.println("=== Procesador de Pedidos ===\n");
        
        Observable.fromIterable(pedidos)
                .subscribeOn(Schedulers.io())
                .filter(Pedido::isEnStock)  // Solo productos en stock
                .filter(p -> p.getPrecio() > 50)  // Precio mayor a 50
                .map(p -> {
                    // Aplicar descuento del 10%
                    double precioDescuento = p.getPrecio() * 0.9;
                    System.out.println("Procesando: " + p.getProducto() + 
                        " - Precio con descuento: $" + String.format("%.2f", precioDescuento));
                    return p;
                })
                .take(3)  // Procesar solo los primeros 3
                .observeOn(Schedulers.computation())
                .subscribe(
                    pedido -> System.out.println("✓ Pedido confirmado: " + pedido),
                    error -> System.err.println("Error: " + error),
                    () -> System.out.println("\n¡Procesamiento completado!")
                );
        
        Thread.sleep(2000);
    }
}
```

**Tarea**:

1. Modificar el filtro para incluir productos con precio entre 50 y 500
2. Agregar un operador `doOnNext()` para simular envío de notificación
3. Implementar manejo de errores personalizado

### Ejercicio Integrador 2: Monitor de Sensores

**Objetivo**: Simular un sistema de monitoreo con streams de datos.

```java
package com.rxjava.lab;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MonitorSensores {
    
    private static Random random = new Random();
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Monitor de Sensores de Temperatura ===\n");
        
        // Simular lecturas de sensor cada segundo
        Observable<Double> sensorTemperatura = Observable
                .interval(1, TimeUnit.SECONDS)
                .map(tick -> {
                    // Generar temperatura aleatoria entre 15 y 35 grados
                    double temp = 15 + (random.nextDouble() * 20);
                    return Math.round(temp * 10.0) / 10.0;  // Redondear a 1 decimal
                })
                .subscribeOn(Schedulers.io());
        
        // Procesar lecturas
        sensorTemperatura
                .filter(temp -> temp > 30)  // Alerta si supera 30°
                .observeOn(Schedulers.computation())
                .subscribe(
                    temp -> System.out.println("⚠️  ALERTA: Temperatura alta detectada: " + temp + "°C"),
                    error -> System.err.println("Error en sensor: " + error),
                    () -> System.out.println("Monitoreo finalizado")
                );
        
        // También mostrar todas las lecturas
        sensorTemperatura
                .subscribe(temp -> System.out.println("📊 Lectura: " + temp + "°C"));
        
        // Monitorear por 15 segundos
        Thread.sleep(15000);
        System.out.println("\n=== Fin del monitoreo ===");
    }
}
```

**Tarea**:

1. Modificar para monitorear dos tipos de sensores (temperatura y humedad)
2. Implementar diferentes niveles de alerta (warning, critical)
3. Agregar contador de lecturas anómalas

---

## Desafíos Adicionales

### Desafío 1: Sistema de Calificaciones

Crear un sistema que:

1. Reciba calificaciones de estudiantes (usando `Observable.fromIterable()`)
2. Filtre las calificaciones aprobadas (≥60)
3. Calcule el promedio
4. Clasifique en Excelente (≥90), Bueno (≥75), Regular (≥60)
5. Use Schedulers apropiados

### Desafío 2: Procesador de Logs

Implementar un procesador que:

1. Lea líneas de log (simular con Observable.create())
2. Filtre solo logs de tipo ERROR
3. Extraiga información relevante (timestamp, mensaje)
4. Agrupe errores por tipo
5. Genere reporte final

### Desafío 3: Conversor de Divisas en Tiempo Real

Crear una aplicación que:

1. Emita valores de moneda cada segundo
2. Aplique tasas de conversión
3. Notifique cuando el valor supere un umbral
4. Use Hot Observable para múltiples suscriptores
5. Implemente gestión adecuada de Disposables

---

## Preguntas de Repaso

1. ¿Cuál es la diferencia principal entre RxJava 1 y RxJava 2?
2. ¿Qué es Reactive Streams y por qué es importante?
3. ¿Cuándo usarías Observable vs Flowable?
4. ¿Por qué RxJava 2 no permite valores null?
5. ¿Cuál es la diferencia entre un Cold y un Hot Observable?
6. ¿Qué hace el método `publish()` en un Observable?
7. ¿Para qué sirve un Disposable y por qué es importante gestionarlo?
8. ¿Cuál es la diferencia entre `subscribeOn()` y `observeOn()`?
9. ¿En qué casos usarías cada tipo de Scheduler?
10. ¿Qué ventajas ofrece RxJava sobre programación imperativa tradicional?

---

## Soluciones de Ejercicios Seleccionados

### Solución Tarea 2.1.3

```java
// Modificación de ejemploRange()
public static void ejemploRange() {
    System.out.println("\n=== Números del 10 al 20 ===");
    
    Observable.range(10, 11)  // range(start, count)
            .subscribe(n -> System.out.println("Número: " + n));
}
```

### Solución Tarea 4.1.1

```java
public static void ejercicioEdades() {
    int anioActual = 2025;
    
    Observable.just(15, 18, 21, 25, 30, 35, 40)
            .filter(edad -> edad >= 18)
            .map(edad -> {
                int anioNacimiento = anioActual - edad;
                return String.format("Edad: %d años -> Nacido en: %d", edad, anioNacimiento);
            })
            .subscribe(System.out::println);
}
```

---

## Recursos para Práctica Adicional

### Ejercicios Online

- **RxMarbles**: [https://rxmarbles.com/](https://rxmarbles.com/) - Visualización interactiva de operadores
- **Learn RxJava**: Tutoriales interactivos disponibles en GitHub

### Proyectos Sugeridos

1. **Analizador de Archivos CSV**: Leer archivo, procesar datos, generar estadísticas
2. **Cliente API REST**: Realizar peticiones HTTP reactivas con Retrofit + RxJava
3. **Chat en Tiempo Real**: Implementar sistema de mensajería con Hot Observables
4. **Monitor de Sistema**: Leer métricas del sistema (CPU, memoria) cada segundo

### Documentación Adicional

- Javadoc oficial de RxJava 2
- Wiki de ReactiveX
- Blog de Advanced RxJava

---

## Conclusión del Laboratorio

En este laboratorio has:

✅ Configurado un proyecto con RxJava 2  
✅ Creado diferentes tipos de Observables  
✅ Implementado suscripciones con manejo completo de eventos  
✅ Aplicado transformaciones y filtros a streams  
✅ Distinguido entre Cold y Hot Observables  
✅ Utilizado Schedulers para control de concurrencia  
✅ Gestionado Disposables para prevenir memory leaks  

