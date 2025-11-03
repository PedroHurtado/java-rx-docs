# Tema 3: Observables y Observers

## Introducción

Los Observables y Observers forman el corazón de la programación reactiva con RxJava. Representan el patrón Observer extendido, donde un Observable emite datos y un Observer los consume de manera asíncrona.

## ¿Qué es un Observable?

Un **Observable** es una fuente de datos que emite elementos a lo largo del tiempo. Puede emitir cero o más elementos y terminar exitosamente o con error.

### Características principales:

- **Emisión asíncrona**: Los datos se emiten cuando están disponibles
- **Push-based**: El Observable "empuja" datos al Observer
- **Lazy**: No emite datos hasta que alguien se suscribe
- **Composable**: Se pueden combinar y transformar fácilmente

### Estados de un Observable:

1. **onNext**: Emite un nuevo elemento
2. **onComplete**: Señala que no habrá más emisiones
3. **onError**: Indica que ocurrió un error (termina el flujo)

## ¿Qué es un Observer?

Un **Observer** es el consumidor que se suscribe a un Observable para recibir sus emisiones.

### Métodos del Observer:

```java
public interface Observer<T> {
    void onSubscribe(Disposable d);
    void onNext(T value);
    void onError(Throwable e);
    void onComplete();
}
```

- **onSubscribe**: Se llama al suscribirse, recibe un Disposable para cancelar
- **onNext**: Recibe cada elemento emitido
- **onError**: Maneja errores (terminal)
- **onComplete**: Indica finalización exitosa (terminal)

## Creación de Observables

### 1. Observable.just()

Crea un Observable que emite elementos específicos:

```java
Observable<String> observable = Observable.just("Elemento 1", "Elemento 2", "Elemento 3");
```

### 2. Observable.fromIterable()

Crea un Observable desde una colección:

```java
List<Integer> numeros = Arrays.asList(1, 2, 3, 4, 5);
Observable<Integer> observable = Observable.fromIterable(numeros);
```

### 3. Observable.create()

Permite control total sobre las emisiones:

```java
Observable<Integer> observable = Observable.create(emitter -> {
    emitter.onNext(1);
    emitter.onNext(2);
    emitter.onComplete();
});
```

### 4. Observable.range()

Emite una secuencia de enteros:

```java
Observable<Integer> observable = Observable.range(1, 10); // Emite 1 a 10
```

### 5. Observable.interval()

Emite números secuenciales en intervalos de tiempo:

```java
Observable<Long> observable = Observable.interval(1, TimeUnit.SECONDS);
```

## Suscripción a un Observable

### Forma básica con Consumer:

```java
observable.subscribe(
    item -> System.out.println("Recibido: " + item),
    error -> System.err.println("Error: " + error),
    () -> System.out.println("Completado")
);
```

### Forma completa con Observer:

```java
observable.subscribe(new Observer<String>() {
    @Override
    public void onSubscribe(Disposable d) {
        System.out.println("Suscrito");
    }

    @Override
    public void onNext(String value) {
        System.out.println("Valor: " + value);
    }

    @Override
    public void onError(Throwable e) {
        System.err.println("Error: " + e.getMessage());
    }

    @Override
    public void onComplete() {
        System.out.println("Flujo completado");
    }
});
```

## Disposable: Gestión de Suscripciones

El **Disposable** permite cancelar una suscripción y liberar recursos:

```java
Disposable disposable = observable.subscribe(
    item -> System.out.println(item)
);

// Cancelar la suscripción
disposable.dispose();

// Verificar si está cancelada
if (disposable.isDisposed()) {
    System.out.println("Suscripción cancelada");
}
```

## CompositeDisposable

Útil para gestionar múltiples suscripciones:

```java
CompositeDisposable compositeDisposable = new CompositeDisposable();

Disposable d1 = observable1.subscribe(item -> System.out.println(item));
Disposable d2 = observable2.subscribe(item -> System.out.println(item));

compositeDisposable.add(d1);
compositeDisposable.add(d2);

// Cancelar todas las suscripciones
compositeDisposable.dispose();
```

## Hot vs Cold Observables

### Cold Observables:

- Comienzan a emitir cuando se suscriben
- Cada suscriptor recibe el flujo completo desde el inicio
- Son la mayoría de los Observables por defecto

```java
Observable<Long> cold = Observable.interval(1, TimeUnit.SECONDS);
```

### Hot Observables:

- Emiten datos independientemente de si hay suscriptores
- Los suscriptores reciben solo los datos emitidos después de la suscripción
- Se crean con `publish()`, `share()` o Subjects

```java
ConnectableObservable<Long> hot = Observable
    .interval(1, TimeUnit.SECONDS)
    .publish();
    
hot.connect(); // Inicia las emisiones
```

## Tipos de Observable en RxJava

### 1. Observable

Observable estándar que emite 0 a N elementos:

```java
Observable<String> observable = Observable.just("A", "B", "C");
```

### 2. Flowable

Similar a Observable pero con soporte para backpressure:

```java
Flowable<Integer> flowable = Flowable.range(1, 1000);
```

### 3. Single

Emite exactamente un elemento o un error:

```java
Single<String> single = Single.just("Único valor");
```

### 4. Maybe

Emite cero o un elemento, o un error:

```java
Maybe<String> maybe = Maybe.just("Puede ser");
Maybe<String> empty = Maybe.empty();
```

### 5. Completable

No emite elementos, solo señal de completado o error:

```java
Completable completable = Completable.fromAction(() -> 
    System.out.println("Acción completada")
);
```

## Patrones de Error

### onErrorReturn

Reemplaza el error con un valor por defecto:

```java
observable
    .onErrorReturn(error -> "Valor por defecto")
    .subscribe(System.out::println);
```

### onErrorResumeNext

Reemplaza el error con otro Observable:

```java
observable
    .onErrorResumeNext(Observable.just("Recuperación"))
    .subscribe(System.out::println);
```

### retry

Reintenta la suscripción en caso de error:

```java
observable
    .retry(3) // Reintenta 3 veces
    .subscribe(System.out::println);
```

## Buenas Prácticas

1. **Siempre gestionar Disposables**: Evita fugas de memoria cancelando suscripciones
2. **Manejar errores apropiadamente**: Usa operadores de error para no terminar flujos inesperadamente
3. **Usar el tipo correcto**: Elige entre Observable, Single, Maybe según necesites
4. **CompositeDisposable en vistas**: Cancela todas las suscripciones al destruir componentes
5. **No bloquear el hilo**: Evita operaciones síncronas pesadas en onNext

## Referencias de Interés

### Documentación Oficial

- **RxJava Wiki - Observable**: https://github.com/ReactiveX/RxJava/wiki/Observable
- **ReactiveX - Observable Contract**: http://reactivex.io/documentation/contract.html
- **RxJava Javadoc - Observable**: http://reactivex.io/RxJava/3.x/javadoc/io/reactivex/rxjava3/core/Observable.html

### Tutoriales y Guías

- **RxJava Tutorial - Vogella**: https://www.vogella.com/tutorials/RxJava/article.html
- **Baeldung - Intro to RxJava**: https://www.baeldung.com/rx-java
- **RxMarbles - Visualización de operadores**: https://rxmarbles.com/

### Artículos Avanzados

- **Hot vs Cold Observables**: https://medium.com/@benlesh/hot-vs-cold-observables-f8094ed53339
- **Understanding RxJava Subject**: https://blog.mindorks.com/understanding-rxjava-subject-publish-replay-behavior-and-async-subject-224d663d452f
- **Best Practices for RxJava**: https://blog.kaush.co/2014/12/24/implementing-an-event-bus-with-rxjava-rxbus/

### Libros Recomendados

- **"Reactive Programming with RxJava"** - Tomasz Nurkiewicz & Ben Christensen
- **"Learning RxJava"** - Thomas Nield (O'Reilly)
- **"RxJava for Android Developers"** - Timo Tuominen

### Recursos Interactivos

- **RxJava Playground**: https://github.com/politrons/reactive
- **Learn RxJava**: https://www.learnrxjava.com/
- **ReactiveX Operators Decision Tree**: https://reactivex.io/documentation/operators.html
