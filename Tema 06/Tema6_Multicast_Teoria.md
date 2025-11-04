# TEMA 6. MULTICAST

## 1. TEORÍA

### ¿Qué es Multicast?

Multicast en RxJava se refiere a la capacidad de compartir una misma suscripción a un Observable entre múltiples observadores. Por defecto, los Observables son "cold" (fríos), lo que significa que cada suscriptor recibe su propia secuencia independiente de eventos. Multicast convierte un Observable frío en uno "hot" (caliente), donde todos los suscriptores comparten la misma ejecución.

### Observables Cold vs Hot

**Observable Cold (Frío):**
- Cada suscriptor inicia una nueva ejecución del flujo de datos
- Los datos se producen independientemente para cada suscriptor
- Similar a un video bajo demanda: cada usuario reproduce desde el inicio
- Ejemplo: lectura de archivos, peticiones HTTP individuales

**Observable Hot (Caliente):**
- Los suscriptores comparten la misma ejecución del flujo
- Los datos se producen una sola vez y se transmiten a todos los suscriptores
- Similar a una transmisión en vivo: todos ven el mismo contenido al mismo tiempo
- Ejemplo: eventos del ratón, señales de sensores, streams de precios en tiempo real

### Operadores de Multicast

#### 1. **publish()**
Convierte un Observable frío en caliente usando un Subject internamente. Requiere llamar a `connect()` para iniciar la emisión.

```java
ConnectableObservable<Integer> observable = Observable.range(1, 5)
    .doOnNext(i -> System.out.println("Emitiendo: " + i))
    .publish();

// Primera suscripción
observable.subscribe(i -> System.out.println("Observer 1: " + i));

// Segunda suscripción
observable.subscribe(i -> System.out.println("Observer 2: " + i));

// Inicia la emisión para ambos suscriptores
observable.connect();
```

#### 2. **share()**
Equivalente a `publish().refCount()`. Comparte automáticamente la fuente entre suscriptores y gestiona la conexión automáticamente.

```java
Observable<Integer> shared = Observable.range(1, 5)
    .doOnNext(i -> System.out.println("Emitiendo: " + i))
    .share();

shared.subscribe(i -> System.out.println("Observer 1: " + i));
shared.subscribe(i -> System.out.println("Observer 2: " + i));
```

#### 3. **replay()**
Almacena en caché los valores emitidos para que los nuevos suscriptores puedan recibir valores anteriores.

```java
ConnectableObservable<Integer> replayed = Observable.range(1, 5)
    .replay();

replayed.connect();

// Este suscriptor recibe todos los valores aunque se suscriba después
Thread.sleep(1000);
replayed.subscribe(i -> System.out.println("Observer tardío: " + i));
```

Variantes de replay:
- `replay(int bufferSize)`: almacena los últimos N elementos
- `replay(long time, TimeUnit unit)`: almacena elementos emitidos en un período de tiempo
- `replay(int bufferSize, long time, TimeUnit unit)`: combinación de ambos

#### 4. **refCount()**
Gestiona automáticamente la conexión al ConnectableObservable. Se conecta cuando llega el primer suscriptor y se desconecta cuando se va el último.

```java
Observable<Integer> refCounted = Observable.range(1, 5)
    .publish()
    .refCount();
```

#### 5. **autoConnect()**
Similar a refCount pero permite especificar cuántos suscriptores deben estar presentes antes de conectar automáticamente.

```java
Observable<Integer> autoConnected = Observable.range(1, 5)
    .publish()
    .autoConnect(2); // Se conecta cuando hay 2 suscriptores
```

### ConnectableObservable

Es un tipo especial de Observable que no comienza a emitir elementos hasta que se llama a su método `connect()`. Permite preparar múltiples suscriptores antes de iniciar la emisión.

**Métodos principales:**
- `connect()`: Inicia la emisión de elementos
- `refCount()`: Retorna un Observable que gestiona la conexión automáticamente
- `autoConnect(int numberOfSubscribers)`: Conecta automáticamente después de N suscriptores

### Subjects como Multicast

Los Subjects funcionan naturalmente como multicasters porque son tanto Observable como Observer:

```java
PublishSubject<Integer> subject = PublishSubject.create();

subject.subscribe(i -> System.out.println("Observer 1: " + i));
subject.subscribe(i -> System.out.println("Observer 2: " + i));

subject.onNext(1);
subject.onNext(2);
```

### Casos de Uso Comunes

1. **Compartir peticiones HTTP costosas**: Evitar múltiples llamadas al servidor cuando varios componentes necesitan los mismos datos

2. **Eventos del sistema**: Compartir eventos de UI, sensores o WebSockets entre múltiples consumidores

3. **Caché de datos**: Usar replay para mantener un caché de los últimos valores emitidos

4. **Broadcasting**: Distribuir información a múltiples partes de la aplicación simultáneamente

### Consideraciones de Rendimiento

- **Memoria**: replay() puede consumir mucha memoria si se almacenan muchos elementos
- **Threading**: Los operadores multicast respetan el Scheduler en el que se ejecutan
- **Lifecycle**: Importante gestionar correctamente las suscripciones para evitar memory leaks
- **RefCount**: Puede reconectar y desconectar repetidamente si los suscriptores van y vienen

### Mejores Prácticas

1. Usar `share()` para casos simples donde no se necesita control explícito
2. Usar `publish().refCount()` cuando se necesita más control sobre la lógica de conexión
3. Usar `replay()` con límites de buffer o tiempo para evitar problemas de memoria
4. Considerar `cache()` para resultados que no cambiarán y se consultarán múltiples veces
5. Siempre gestionar correctamente las suscripciones con CompositeDisposable

## 2. REFERENCIAS DE INTERÉS

### Documentación Oficial
- **ReactiveX - Connectable Observable Operators**: http://reactivex.io/documentation/operators/connect.html
- **RxJava Wiki - Connectable Observables**: https://github.com/ReactiveX/RxJava/wiki/Connectable-Observable-Operators
- **RxJava JavaDoc - ConnectableObservable**: http://reactivex.io/RxJava/javadoc/io/reactivex/observables/ConnectableObservable.html

### Artículos y Tutoriales
- **"Hot vs Cold Observables"** - Ben Christensen (creador de RxJava): https://github.com/Reactive-Extensions/RxJS/blob/master/doc/gettingstarted/creating.md
- **"Understanding Hot and Cold Observables"** - Medium: https://medium.com/@benlesh/hot-vs-cold-observables-f8094ed53339
- **"Multicasting in RxJava"** - Baeldung: https://www.baeldung.com/rxjava-multicasting

### Libros
- **"Reactive Programming with RxJava"** - Tomasz Nurkiewicz y Ben Christensen (Capítulo 4: "Combining Observables")
- **"Learning RxJava"** - Thomas Nield (Capítulo 6: "Hot and Cold Observables, and Subjects")

### Videos y Cursos
- **"RxJava: Hot and Cold Observables"** - YouTube (Dan Lew): https://www.youtube.com/watch?v=R16OHcZJTno
- **"Advanced RxJava"** - Pluralsight: Sección sobre Multicasting y ConnectableObservables

### Repositorios y Ejemplos
- **RxJava Examples**: https://github.com/ReactiveX/RxJava/tree/2.x/src/test/java/io/reactivex/observable
- **RxJava Samples**: https://github.com/kaushikgopal/RxJava-Android-Samples

### Herramientas
- **RxMarbles**: https://rxmarbles.com/ - Diagramas interactivos de operadores (incluye publish, share, replay)
- **RxJava Playground**: Entorno para experimentar con operadores de multicast