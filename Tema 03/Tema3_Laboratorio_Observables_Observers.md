# Laboratorio 3: Observables y Observers

## Objetivo

Practicar la creación de Observables, implementación de Observers y gestión de suscripciones en RxJava.

## Requisitos Previos

- JDK 8 o superior instalado
- Maven o Gradle configurado
- IDE (IntelliJ IDEA, Eclipse o VS Code)
- Dependencia RxJava 3.x en el proyecto

## Configuración del Proyecto

### Maven (pom.xml)

```xml
<dependencies>
    <dependency>
        <groupId>io.reactivex.rxjava3</groupId>
        <artifactId>rxjava</artifactId>
        <version>3.1.8</version>
    </dependency>
</dependencies>
```

### Gradle (build.gradle)

```gradle
dependencies {
    implementation 'io.reactivex.rxjava3:rxjava:3.1.8'
}
```

## Ejercicio 1: Creación Básica de Observables

### Descripción

Crear diferentes tipos de Observables utilizando los métodos de creación de RxJava.

### Código

```java
package com.curso.rxjava.lab3;

import io.reactivex.rxjava3.core.Observable;
import java.util.Arrays;
import java.util.List;

public class Ejercicio1_CreacionObservables {
    
    public static void main(String[] args) {
        System.out.println("=== Ejercicio 1: Creación de Observables ===\n");
        
        // 1. Observable.just()
        System.out.println("1. Observable.just():");
        Observable<String> justObservable = Observable.just("Hola", "Mundo", "Reactivo");
        justObservable.subscribe(item -> System.out.println("  " + item));
        
        System.out.println();
        
        // 2. Observable.fromIterable()
        System.out.println("2. Observable.fromIterable():");
        List<Integer> numeros = Arrays.asList(10, 20, 30, 40, 50);
        Observable<Integer> fromIterableObservable = Observable.fromIterable(numeros);
        fromIterableObservable.subscribe(item -> System.out.println("  " + item));
        
        System.out.println();
        
        // 3. Observable.range()
        System.out.println("3. Observable.range():");
        Observable<Integer> rangeObservable = Observable.range(1, 5);
        rangeObservable.subscribe(item -> System.out.println("  Número: " + item));
        
        System.out.println();
        
        // 4. Observable.create()
        System.out.println("4. Observable.create():");
        Observable<String> createObservable = Observable.create(emitter -> {
            emitter.onNext("Primer elemento");
            emitter.onNext("Segundo elemento");
            emitter.onNext("Tercer elemento");
            emitter.onComplete();
        });
        createObservable.subscribe(item -> System.out.println("  " + item));
        
        System.out.println();
        
        // 5. Observable.empty()
        System.out.println("5. Observable.empty():");
        Observable<Object> emptyObservable = Observable.empty();
        emptyObservable.subscribe(
            item -> System.out.println("  Elemento: " + item),
            error -> System.err.println("  Error: " + error),
            () -> System.out.println("  Completado (sin elementos)")
        );
    }
}
```

### Tareas

1. Ejecuta el código y observa las diferentes formas de crear Observables
2. Modifica el Observable.just() para emitir números en lugar de strings
3. Crea un Observable.fromArray() con un array de strings
4. En Observable.create(), añade un elemento más y luego emite un error antes del onComplete
5. Crea un Observable.never() y observa qué sucede

## Ejercicio 2: Implementación de Observer Completo

### Descripción

Implementar un Observer personalizado con todos sus métodos para entender el ciclo de vida completo.

### Código

```java
package com.curso.rxjava.lab3;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;

public class Ejercicio2_ObserverCompleto {
    
    public static void main(String[] args) {
        System.out.println("=== Ejercicio 2: Observer Completo ===\n");
        
        // Crear un Observable
        Observable<Integer> observable = Observable.create(emitter -> {
            System.out.println("Observable: Iniciando emisiones...");
            emitter.onNext(1);
            emitter.onNext(2);
            emitter.onNext(3);
            emitter.onNext(4);
            emitter.onNext(5);
            emitter.onComplete();
            System.out.println("Observable: Emisiones completadas");
        });
        
        // Crear un Observer personalizado
        Observer<Integer> observer = new Observer<Integer>() {
            private Disposable disposable;
            private int contadorElementos = 0;
            
            @Override
            public void onSubscribe(Disposable d) {
                this.disposable = d;
                System.out.println("Observer: Suscripción iniciada");
            }
            
            @Override
            public void onNext(Integer value) {
                contadorElementos++;
                System.out.println("Observer: Recibido -> " + value);
                
                // Cancelar después de recibir 3 elementos
                if (contadorElementos == 3) {
                    System.out.println("Observer: Cancelando suscripción después de 3 elementos");
                    disposable.dispose();
                }
            }
            
            @Override
            public void onError(Throwable e) {
                System.err.println("Observer: Error -> " + e.getMessage());
            }
            
            @Override
            public void onComplete() {
                System.out.println("Observer: Flujo completado");
                System.out.println("Observer: Total de elementos recibidos: " + contadorElementos);
            }
        };
        
        // Suscribir el Observer al Observable
        observable.subscribe(observer);
    }
}
```

### Tareas

1. Ejecuta el código y observa el ciclo de vida completo
2. Cambia el número de elementos antes de cancelar la suscripción
3. Modifica el Observable para emitir un error después del tercer elemento
4. Añade lógica para imprimir si el Disposable está activo o no después de dispose()
5. Crea un segundo Observer que no cancele la suscripción y observa las diferencias

## Ejercicio 3: Gestión de Disposables

### Descripción

Practicar la gestión correcta de suscripciones utilizando Disposable y CompositeDisposable.

### Código

```java
package com.curso.rxjava.lab3;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

import java.util.concurrent.TimeUnit;

public class Ejercicio3_GestionDisposables {
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Ejercicio 3: Gestión de Disposables ===\n");
        
        // Parte 1: Disposable Simple
        System.out.println("--- Parte 1: Disposable Simple ---");
        Observable<Long> observable1 = Observable.interval(500, TimeUnit.MILLISECONDS);
        
        Disposable disposable = observable1.subscribe(
            item -> System.out.println("Observable 1 -> " + item)
        );
        
        Thread.sleep(2500);
        System.out.println("\nCancelando suscripción...");
        disposable.dispose();
        System.out.println("¿Está cancelado? " + disposable.isDisposed());
        
        Thread.sleep(1000);
        System.out.println("(Ya no deberían emitirse más elementos)\n");
        
        // Parte 2: CompositeDisposable
        System.out.println("--- Parte 2: CompositeDisposable ---");
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        
        Observable<Long> observable2 = Observable.interval(300, TimeUnit.MILLISECONDS)
            .map(i -> i * 10);
        
        Observable<String> observable3 = Observable.interval(400, TimeUnit.MILLISECONDS)
            .map(i -> "Letra-" + (char)('A' + i));
        
        Observable<Integer> observable4 = Observable.range(100, 5)
            .delay(200, TimeUnit.MILLISECONDS);
        
        Disposable d1 = observable2.subscribe(
            item -> System.out.println("  Observable 2 (números): " + item)
        );
        
        Disposable d2 = observable3.subscribe(
            item -> System.out.println("  Observable 3 (letras): " + item)
        );
        
        Disposable d3 = observable4.subscribe(
            item -> System.out.println("  Observable 4 (rango): " + item)
        );
        
        compositeDisposable.add(d1);
        compositeDisposable.add(d2);
        compositeDisposable.add(d3);
        
        System.out.println("CompositeDisposable contiene " + compositeDisposable.size() + " suscripciones");
        
        Thread.sleep(2000);
        System.out.println("\nCancelando todas las suscripciones...");
        compositeDisposable.dispose();
        
        Thread.sleep(1000);
        System.out.println("(Ya no deberían emitirse más elementos)");
        System.out.println("CompositeDisposable contiene " + compositeDisposable.size() + " suscripciones");
    }
}
```

### Tareas

1. Ejecuta el código y observa cómo se gestionan las suscripciones
2. Modifica los intervalos de tiempo para ver diferentes patrones de emisión
3. Añade un cuarto Observable al CompositeDisposable
4. Implementa un método que cancele solo una suscripción específica del CompositeDisposable
5. Crea un escenario donde se elimine una suscripción del composite sin cancelarla

## Ejercicio 4: Manejo de Errores en Observers

### Descripción

Explorar diferentes formas de manejar errores en la cadena reactiva.

### Código

```java
package com.curso.rxjava.lab3;

import io.reactivex.rxjava3.core.Observable;

public class Ejercicio4_ManejoErrores {
    
    public static void main(String[] args) {
        System.out.println("=== Ejercicio 4: Manejo de Errores ===\n");
        
        // Parte 1: Observable con error
        System.out.println("--- Parte 1: Observable con Error ---");
        Observable<Integer> observableConError = Observable.create(emitter -> {
            emitter.onNext(1);
            emitter.onNext(2);
            emitter.onError(new RuntimeException("Error intencional"));
            emitter.onNext(3); // Este no se emitirá
        });
        
        observableConError.subscribe(
            item -> System.out.println("Valor: " + item),
            error -> System.err.println("Error capturado: " + error.getMessage()),
            () -> System.out.println("Completado")
        );
        
        System.out.println();
        
        // Parte 2: onErrorReturn
        System.out.println("--- Parte 2: onErrorReturn ---");
        Observable<Integer> observable2 = Observable.create(emitter -> {
            emitter.onNext(10);
            emitter.onNext(20);
            emitter.onError(new Exception("Algo salió mal"));
        });
        
        observable2
            .onErrorReturn(error -> {
                System.out.println("Manejando error con valor por defecto");
                return -1;
            })
            .subscribe(
                item -> System.out.println("Valor: " + item),
                error -> System.err.println("Error: " + error),
                () -> System.out.println("Completado con éxito")
            );
        
        System.out.println();
        
        // Parte 3: onErrorResumeNext
        System.out.println("--- Parte 3: onErrorResumeNext ---");
        Observable<String> observable3 = Observable.create(emitter -> {
            emitter.onNext("A");
            emitter.onNext("B");
            emitter.onError(new Exception("Error en el flujo principal"));
        });
        
        Observable<String> observableFallback = Observable.just("X", "Y", "Z");
        
        observable3
            .onErrorResumeNext(observableFallback)
            .subscribe(
                item -> System.out.println("Valor: " + item),
                error -> System.err.println("Error: " + error),
                () -> System.out.println("Completado")
            );
        
        System.out.println();
        
        // Parte 4: retry
        System.out.println("--- Parte 4: Retry ---");
        Observable<Integer> observableConReintento = Observable.create(emitter -> {
            System.out.println("  Intento de emisión...");
            emitter.onNext(1);
            emitter.onNext(2);
            emitter.onError(new Exception("Error temporal"));
        });
        
        observableConReintento
            .retry(2)
            .subscribe(
                item -> System.out.println("Valor: " + item),
                error -> System.err.println("Error final después de reintentos: " + error.getMessage()),
                () -> System.out.println("Completado")
            );
    }
}
```

### Tareas

1. Ejecuta el código y observa cómo se manejan los diferentes errores
2. Modifica onErrorReturn para devolver diferentes valores según el tipo de error
3. Cambia el número de reintentos en retry() y observa el comportamiento
4. Implementa retry() con una condición (retryWhen) para reintentar solo ciertos errores
5. Combina onErrorReturn con doOnError para registrar el error antes de recuperarse

## Ejercicio 5: Tipos de Observables (Single, Maybe, Completable)

### Descripción

Trabajar con los diferentes tipos especializados de Observables en RxJava.

### Código

```java
package com.curso.rxjava.lab3;

import io.reactivex.rxjava3.core.*;

public class Ejercicio5_TiposObservables {
    
    public static void main(String[] args) {
        System.out.println("=== Ejercicio 5: Tipos de Observables ===\n");
        
        // 1. Single - Emite exactamente un elemento
        System.out.println("--- 1. Single ---");
        Single<String> single = Single.just("Único valor");
        
        single.subscribe(
            valor -> System.out.println("Single recibido: " + valor),
            error -> System.err.println("Error: " + error)
        );
        
        // Single desde Observable
        Observable<Integer> observable = Observable.range(1, 5);
        Single<Integer> singleDesdeObservable = observable
            .reduce((a, b) -> a + b)
            .toSingle();
        
        singleDesdeObservable.subscribe(
            suma -> System.out.println("Suma total: " + suma)
        );
        
        System.out.println();
        
        // 2. Maybe - Puede emitir un elemento, ninguno, o error
        System.out.println("--- 2. Maybe ---");
        Maybe<String> maybeConValor = Maybe.just("Tengo un valor");
        Maybe<String> maybeVacio = Maybe.empty();
        
        System.out.println("Maybe con valor:");
        maybeConValor.subscribe(
            valor -> System.out.println("  Valor: " + valor),
            error -> System.err.println("  Error: " + error),
            () -> System.out.println("  Completado sin valor")
        );
        
        System.out.println("Maybe vacío:");
        maybeVacio.subscribe(
            valor -> System.out.println("  Valor: " + valor),
            error -> System.err.println("  Error: " + error),
            () -> System.out.println("  Completado sin valor")
        );
        
        // Maybe desde Observable
        Observable<Integer> observableNumeros = Observable.range(10, 5);
        Maybe<Integer> maybeDesdeObservable = observableNumeros.firstElement();
        
        maybeDesdeObservable.subscribe(
            primero -> System.out.println("Primer elemento: " + primero)
        );
        
        System.out.println();
        
        // 3. Completable - Solo señala completado o error
        System.out.println("--- 3. Completable ---");
        Completable completable = Completable.fromAction(() -> {
            System.out.println("  Ejecutando acción...");
            // Simular una operación
            for (int i = 0; i < 3; i++) {
                System.out.println("    Procesando paso " + (i + 1));
            }
        });
        
        completable.subscribe(
            () -> System.out.println("Completable finalizado con éxito"),
            error -> System.err.println("Error: " + error)
        );
        
        System.out.println();
        
        // 4. Conversiones entre tipos
        System.out.println("--- 4. Conversiones entre Tipos ---");
        
        // Observable -> Single
        Observable<String> obs1 = Observable.just("Solo uno");
        Single<String> single1 = obs1.first("Valor por defecto");
        single1.subscribe(v -> System.out.println("Observable -> Single: " + v));
        
        // Single -> Observable
        Single<Integer> single2 = Single.just(42);
        Observable<Integer> obs2 = single2.toObservable();
        obs2.subscribe(v -> System.out.println("Single -> Observable: " + v));
        
        // Maybe -> Single con valor por defecto
        Maybe<String> maybe = Maybe.empty();
        Single<String> single3 = maybe.defaultIfEmpty("Valor por defecto").toSingle();
        single3.subscribe(v -> System.out.println("Maybe -> Single: " + v));
        
        // Observable -> Completable (ignora elementos)
        Observable<Integer> obs3 = Observable.range(1, 5);
        Completable completable2 = obs3.ignoreElements();
        completable2.subscribe(
            () -> System.out.println("Observable -> Completable: Finalizado")
        );
    }
}
```

### Tareas

1. Ejecuta el código y observa las diferencias entre los tipos
2. Crea un Single que emita el resultado de una operación matemática compleja
3. Implementa un Maybe que devuelva un valor solo si cumple una condición
4. Crea un Completable que simule una operación de guardado en base de datos
5. Encadena conversiones: Observable -> Maybe -> Single -> Observable

## Ejercicio 6: Proyecto Integrador - Sistema de Notificaciones

### Descripción

Crear un sistema de notificaciones que utilice diferentes tipos de Observables y demuestre el manejo completo de suscripciones y errores.

### Código

```java
package com.curso.rxjava.lab3;

import io.reactivex.rxjava3.core.*;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Ejercicio6_SistemaNotificaciones {
    
    private static CompositeDisposable compositeDisposable = new CompositeDisposable();
    private static Random random = new Random();
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Sistema de Notificaciones Reactivo ===\n");
        
        // Iniciar el sistema
        iniciarSistema();
        
        // Ejecutar por 10 segundos
        Thread.sleep(10000);
        
        // Detener el sistema
        detenerSistema();
        
        System.out.println("\nSistema detenido.");
    }
    
    private static void iniciarSistema() {
        System.out.println("Iniciando sistema de notificaciones...\n");
        
        // 1. Notificaciones urgentes (Single)
        Disposable d1 = generarNotificacionUrgente()
            .subscribe(
                notif -> System.out.println("🚨 URGENTE: " + notif),
                error -> System.err.println("Error en notificación urgente: " + error.getMessage())
            );
        compositeDisposable.add(d1);
        
        // 2. Notificaciones normales (Observable con intervalo)
        Disposable d2 = generarNotificacionesNormales()
            .subscribe(
                notif -> System.out.println("📬 Normal: " + notif),
                error -> System.err.println("Error en notificaciones normales: " + error.getMessage())
            );
        compositeDisposable.add(d2);
        
        // 3. Verificación de estado (Maybe)
        Disposable d3 = verificarEstadoSistema()
            .subscribe(
                estado -> System.out.println("✅ Estado del sistema: " + estado),
                error -> System.err.println("Error en verificación: " + error.getMessage()),
                () -> System.out.println("⚠️ No se pudo verificar el estado")
            );
        compositeDisposable.add(d3);
        
        // 4. Proceso de limpieza (Completable)
        Disposable d4 = ejecutarLimpieza()
            .subscribe(
                () -> System.out.println("🧹 Limpieza completada"),
                error -> System.err.println("Error en limpieza: " + error.getMessage())
            );
        compositeDisposable.add(d4);
        
        System.out.println("Sistema iniciado con " + compositeDisposable.size() + " procesos activos.\n");
    }
    
    private static Single<String> generarNotificacionUrgente() {
        return Single.create(emitter -> {
            try {
                Thread.sleep(1000);
                if (random.nextBoolean()) {
                    emitter.onSuccess("Actualización crítica del sistema");
                } else {
                    emitter.onSuccess("Alerta de seguridad detectada");
                }
            } catch (Exception e) {
                emitter.onError(e);
            }
        });
    }
    
    private static Observable<String> generarNotificacionesNormales() {
        return Observable.interval(2, TimeUnit.SECONDS)
            .map(i -> {
                String[] mensajes = {
                    "Nuevo mensaje recibido",
                    "Actualización disponible",
                    "Recordatorio de evento",
                    "Tarea completada"
                };
                return mensajes[random.nextInt(mensajes.length)];
            })
            .onErrorReturn(error -> "Error al generar notificación");
    }
    
    private static Maybe<String> verificarEstadoSistema() {
        return Maybe.create(emitter -> {
            try {
                Thread.sleep(3000);
                int estado = random.nextInt(3);
                
                switch (estado) {
                    case 0:
                        emitter.onSuccess("Sistema operativo normal");
                        break;
                    case 1:
                        emitter.onSuccess("Sistema con advertencias menores");
                        break;
                    case 2:
                        emitter.onComplete(); // Sin estado disponible
                        break;
                }
            } catch (Exception e) {
                emitter.onError(e);
            }
        });
    }
    
    private static Completable ejecutarLimpieza() {
        return Completable.fromAction(() -> {
            Thread.sleep(2500);
            System.out.println("  - Limpiando caché temporal");
            Thread.sleep(500);
            System.out.println("  - Optimizando recursos");
            Thread.sleep(500);
            System.out.println("  - Liberando memoria");
        });
    }
    
    private static void detenerSistema() {
        System.out.println("\nDeteniendo sistema...");
        compositeDisposable.dispose();
        System.out.println("Todas las suscripciones canceladas.");
    }
}
```

### Tareas

1. Ejecuta el sistema y observa cómo interactúan los diferentes tipos de Observables
2. Añade un nuevo tipo de notificación usando Flowable con backpressure
3. Implementa un mecanismo de prioridades para las notificaciones
4. Añade manejo de errores con retry para las notificaciones que fallen
5. Crea un panel de estadísticas que cuente las notificaciones por tipo

## Preguntas de Repaso

1. ¿Cuál es la diferencia fundamental entre un Observable y un Observer?
2. ¿Qué sucede si llamas a onNext() después de onComplete() o onError()?
3. ¿Por qué es importante gestionar los Disposables?
4. ¿Cuándo usarías Single en lugar de Observable?
5. ¿Qué ventaja ofrece CompositeDisposable sobre gestionar múltiples Disposables individuales?
6. ¿Cuál es la diferencia entre onErrorReturn y onErrorResumeNext?
7. ¿Qué tipo de Observable usarías para una operación que no devuelve datos pero debe notificar su finalización?
8. ¿Cómo afecta dispose() a un Observable que está emitiendo elementos?

## Consejos para el Desarrollo

- Siempre llama a dispose() cuando ya no necesites una suscripción
- Usa CompositeDisposable en componentes con múltiples suscripciones
- Maneja siempre los errores, nunca dejes un Observable sin onError
- Elige el tipo de Observable apropiado para cada caso de uso
- Utiliza los operadores de error para hacer tu código más robusto
- Prueba diferentes escenarios de error para entender mejor el comportamiento

## Recursos Adicionales

- Documentación oficial de RxJava: https://github.com/ReactiveX/RxJava
- RxMarbles para visualizar operadores: https://rxmarbles.com/
- Reactive Programming with RxJava (libro): https://www.oreilly.com/library/view/reactive-programming-with/9781491931646/
