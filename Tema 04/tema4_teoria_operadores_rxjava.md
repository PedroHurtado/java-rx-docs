# Tema 4: Operadores RxJava

## Introducción

Los operadores son el corazón de RxJava. Son funciones que permiten transformar, filtrar, combinar y manipular los elementos emitidos por los Observables. RxJava proporciona más de 100 operadores que se pueden clasificar en diferentes categorías según su propósito.

## Categorías de Operadores

### 1. Operadores de Creación

Ya conocemos algunos operadores de creación como `just()`, `fromArray()`, `create()`. Otros importantes son:

**range(start, count)**: Emite una secuencia de números enteros.
```java
Observable.range(1, 5) // Emite: 1, 2, 3, 4, 5
```

**interval(period, timeUnit)**: Emite números secuenciales a intervalos regulares.
```java
Observable.interval(1, TimeUnit.SECONDS) // Emite: 0, 1, 2, 3...
```

**timer(delay, timeUnit)**: Emite un único valor después de un retardo.
```java
Observable.timer(2, TimeUnit.SECONDS) // Emite 0 después de 2 segundos
```

**defer()**: Crea un Observable que genera un nuevo Observable cada vez que se suscribe un Observer.
```java
Observable<Long> deferred = Observable.defer(() -> 
    Observable.just(System.currentTimeMillis())
);
```

### 2. Operadores de Transformación

**map()**: Transforma cada elemento emitido aplicando una función.
```java
Observable.just(1, 2, 3)
    .map(x -> x * 2)  // Emite: 2, 4, 6
    .subscribe(System.out::println);
```

**flatMap()**: Transforma cada elemento en un Observable y aplana las emisiones. No garantiza el orden.
```java
Observable.just("A", "B")
    .flatMap(s -> Observable.just(s + "1", s + "2"))
    // Puede emitir: A1, A2, B1, B2 o A1, B1, A2, B2
    .subscribe(System.out::println);
```

**concatMap()**: Similar a flatMap pero mantiene el orden de las emisiones.
```java
Observable.just("A", "B")
    .concatMap(s -> Observable.just(s + "1", s + "2"))
    // Emite siempre: A1, A2, B1, B2
    .subscribe(System.out::println);
```

**switchMap()**: Cancela el Observable anterior cuando llega un nuevo elemento. Útil para búsquedas.
```java
Observable.just("consulta1", "consulta2")
    .switchMap(query -> buscarEnBD(query))
    .subscribe(System.out::println);
```

**scan()**: Aplica una función acumuladora a cada elemento.
```java
Observable.just(1, 2, 3, 4)
    .scan((acumulador, valor) -> acumulador + valor)
    // Emite: 1, 3, 6, 10
    .subscribe(System.out::println);
```

**buffer()**: Agrupa emisiones en listas.
```java
Observable.range(1, 10)
    .buffer(3)  // Emite: [1,2,3], [4,5,6], [7,8,9], [10]
    .subscribe(System.out::println);
```

**window()**: Similar a buffer pero emite Observables en lugar de listas.

**groupBy()**: Divide un Observable en múltiples Observables agrupados por una clave.
```java
Observable.range(1, 10)
    .groupBy(x -> x % 2 == 0 ? "par" : "impar")
    .subscribe(group -> {
        group.subscribe(value -> 
            System.out.println(group.getKey() + ": " + value)
        );
    });
```

### 3. Operadores de Filtrado

**filter()**: Emite solo los elementos que cumplen una condición.
```java
Observable.range(1, 10)
    .filter(x -> x % 2 == 0)  // Emite: 2, 4, 6, 8, 10
    .subscribe(System.out::println);
```

**take(n)**: Emite solo los primeros n elementos.
```java
Observable.range(1, 100)
    .take(5)  // Emite: 1, 2, 3, 4, 5
    .subscribe(System.out::println);
```

**takeLast(n)**: Emite solo los últimos n elementos.
```java
Observable.range(1, 10)
    .takeLast(3)  // Emite: 8, 9, 10
    .subscribe(System.out::println);
```

**skip(n)**: Omite los primeros n elementos.
```java
Observable.range(1, 10)
    .skip(5)  // Emite: 6, 7, 8, 9, 10
    .subscribe(System.out::println);
```

**skipLast(n)**: Omite los últimos n elementos.

**distinct()**: Emite solo elementos únicos.
```java
Observable.just(1, 2, 2, 3, 3, 3, 4)
    .distinct()  // Emite: 1, 2, 3, 4
    .subscribe(System.out::println);
```

**distinctUntilChanged()**: Emite elementos solo si son diferentes del anterior.
```java
Observable.just(1, 1, 2, 2, 2, 3, 1)
    .distinctUntilChanged()  // Emite: 1, 2, 3, 1
    .subscribe(System.out::println);
```

**debounce(tiempo, unidad)**: Emite un elemento solo si ha pasado un tiempo sin nuevas emisiones. Ideal para búsquedas mientras el usuario escribe.
```java
Observable.interval(100, TimeUnit.MILLISECONDS)
    .debounce(300, TimeUnit.MILLISECONDS)
    .subscribe(System.out::println);
```

**throttleFirst(tiempo, unidad)**: Emite el primer elemento, luego ignora emisiones durante un período.
```java
Observable.interval(100, TimeUnit.MILLISECONDS)
    .throttleFirst(500, TimeUnit.MILLISECONDS)
    .subscribe(System.out::println);
```

**elementAt(indice)**: Emite solo el elemento en la posición especificada.
```java
Observable.just("A", "B", "C", "D")
    .elementAt(2)  // Emite: "C"
    .subscribe(System.out::println);
```

### 4. Operadores de Combinación

**merge()**: Combina múltiples Observables en uno solo, emitiendo elementos de cualquiera de ellos tan pronto como estén disponibles.
```java
Observable<String> obs1 = Observable.just("A", "B");
Observable<String> obs2 = Observable.just("1", "2");
Observable.merge(obs1, obs2)
    // Puede emitir: A, B, 1, 2 o A, 1, B, 2 (sin orden específico)
    .subscribe(System.out::println);
```

**concat()**: Combina múltiples Observables en secuencia, esperando a que cada uno complete antes de suscribirse al siguiente.
```java
Observable<String> obs1 = Observable.just("A", "B");
Observable<String> obs2 = Observable.just("1", "2");
Observable.concat(obs1, obs2)
    // Emite: A, B, 1, 2 (siempre en orden)
    .subscribe(System.out::println);
```

**zip()**: Combina las emisiones de múltiples Observables usando una función especificada.
```java
Observable<String> nombres = Observable.just("Juan", "Ana", "Luis");
Observable<Integer> edades = Observable.just(25, 30, 35);
Observable.zip(nombres, edades, (nombre, edad) -> nombre + " tiene " + edad + " años")
    .subscribe(System.out::println);
```

**combineLatest()**: Combina los últimos valores emitidos por múltiples Observables.
```java
Observable<Long> obs1 = Observable.interval(100, TimeUnit.MILLISECONDS);
Observable<Long> obs2 = Observable.interval(150, TimeUnit.MILLISECONDS);
Observable.combineLatest(obs1, obs2, (a, b) -> "A:" + a + " B:" + b)
    .subscribe(System.out::println);
```

**startWith()**: Emite un elemento o secuencia específica antes de comenzar a emitir los elementos del Observable fuente.
```java
Observable.just(2, 3, 4)
    .startWith(1)  // Emite: 1, 2, 3, 4
    .subscribe(System.out::println);
```

### 5. Operadores de Utilidad

**delay()**: Retrasa las emisiones del Observable.
```java
Observable.just(1, 2, 3)
    .delay(2, TimeUnit.SECONDS)
    .subscribe(System.out::println);
```

**timeout()**: Emite un error si el Observable no emite ningún elemento en el tiempo especificado.
```java
Observable.timer(3, TimeUnit.SECONDS)
    .timeout(2, TimeUnit.SECONDS)
    .subscribe(
        value -> System.out.println("Valor: " + value),
        error -> System.out.println("Timeout!")
    );
```

**repeat()**: Repite la secuencia de emisiones.
```java
Observable.just(1, 2, 3)
    .repeat(2)  // Emite: 1, 2, 3, 1, 2, 3
    .subscribe(System.out::println);
```

**doOnNext()**: Ejecuta una acción para cada elemento emitido sin modificar el flujo.
```java
Observable.just(1, 2, 3)
    .doOnNext(x -> System.out.println("Procesando: " + x))
    .map(x -> x * 2)
    .subscribe(System.out::println);
```

**doOnComplete()**: Ejecuta una acción cuando el Observable completa.
```java
Observable.just(1, 2, 3)
    .doOnComplete(() -> System.out.println("¡Completado!"))
    .subscribe(System.out::println);
```

**doOnError()**: Ejecuta una acción cuando ocurre un error.
```java
Observable.error(new Exception("Error de prueba"))
    .doOnError(e -> System.out.println("Error capturado: " + e.getMessage()))
    .subscribe();
```

**subscribeOn()**: Especifica el Scheduler en el que el Observable comenzará a operar.

**observeOn()**: Especifica el Scheduler en el que el Observer recibirá las notificaciones.

### 6. Operadores de Manejo de Errores

**onErrorReturn()**: Retorna un elemento específico cuando ocurre un error.
```java
Observable.just(1, 2, 0, 4)
    .map(x -> 10 / x)
    .onErrorReturn(error -> -1)
    .subscribe(System.out::println);
```

**onErrorResumeNext()**: Cambia a un Observable alternativo cuando ocurre un error.
```java
Observable.just(1, 2, 0, 4)
    .map(x -> 10 / x)
    .onErrorResumeNext(Observable.just(999, 888))
    .subscribe(System.out::println);
```

**retry()**: Reintenta la suscripción cuando ocurre un error.
```java
Observable.just(1, 2, 0, 4)
    .map(x -> 10 / x)
    .retry(3)  // Reintenta hasta 3 veces
    .subscribe(System.out::println);
```

### 7. Operadores de Agregación

**reduce()**: Aplica una función acumuladora y emite solo el resultado final.
```java
Observable.just(1, 2, 3, 4)
    .reduce((acc, x) -> acc + x)
    .subscribe(total -> System.out.println("Total: " + total));
```

**count()**: Cuenta el número de elementos emitidos.
```java
Observable.just(1, 2, 3, 4, 5)
    .count()
    .subscribe(count -> System.out.println("Elementos: " + count));
```

**all()**: Determina si todos los elementos cumplen una condición.
```java
Observable.just(2, 4, 6, 8)
    .all(x -> x % 2 == 0)
    .subscribe(result -> System.out.println("Todos pares: " + result));
```

**any()**: Determina si algún elemento cumple una condición.
```java
Observable.just(1, 3, 5, 6)
    .any(x -> x % 2 == 0)
    .subscribe(result -> System.out.println("Algún par: " + result));
```

**contains()**: Determina si un elemento específico fue emitido.
```java
Observable.just("A", "B", "C")
    .contains("B")
    .subscribe(result -> System.out.println("Contiene B: " + result));
```

## Encadenamiento de Operadores

La verdadera potencia de RxJava viene al encadenar múltiples operadores:

```java
Observable.range(1, 20)
    .filter(x -> x % 2 == 0)        // Solo pares: 2, 4, 6, 8...
    .map(x -> x * x)                 // Elevar al cuadrado
    .take(3)                         // Tomar los primeros 3
    .scan((acc, x) -> acc + x)       // Suma acumulativa
    .subscribe(System.out::println); // Resultado: 4, 20, 56
```

## Consideraciones Importantes

1. **Inmutabilidad**: Los operadores no modifican el Observable original, crean uno nuevo.

2. **Lazy Evaluation**: Los operadores no se ejecutan hasta que hay una suscripción.

3. **Orden importa**: El orden de los operadores afecta el resultado final.

4. **Elección del operador correcto**: `flatMap` vs `concatMap` vs `switchMap` tienen comportamientos muy diferentes.

5. **Gestión de recursos**: Algunos operadores como `interval` siguen emitiendo indefinidamente, usa `take()` o `takeUntil()` para limitar.

## Referencias de Interés

### Documentación Oficial
- **ReactiveX Operators**: http://reactivex.io/documentation/operators.html
  - Catálogo completo con diagramas de mármol (marble diagrams)
  
- **RxJava Javadoc**: http://reactivex.io/RxJava/3.x/javadoc/
  - Documentación detallada de cada operador

### Guías y Tutoriales
- **RxMarbles**: https://rxmarbles.com/
  - Visualización interactiva de operadores con diagramas de mármol
  
- **Learn RxJava Operators**: https://github.com/ReactiveX/RxJava/wiki
  - Wiki oficial con ejemplos y casos de uso

### Artículos Recomendados
- **"Which Operator Do I Use?"**: http://reactivex.io/documentation/operators.html#tree
  - Árbol de decisión para elegir el operador correcto
  
- **RxJava Operator Guide**: https://www.baeldung.com/rxjava-operators
  - Guía práctica de operadores más comunes

### Videos
- **RxJava Operators Explained**: Buscar en YouTube tutoriales sobre operadores específicos
  
### Libros
- **"Reactive Programming with RxJava"** - Tomasz Nurkiewicz y Ben Christensen
  - Capítulos 4 y 5 dedicados a operadores

### Herramientas
- **RxJava Playground**: Proyectos en GitHub para experimentar con operadores
  - https://github.com/topics/rxjava-examples
