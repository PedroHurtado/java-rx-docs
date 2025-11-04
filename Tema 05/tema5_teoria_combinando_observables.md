# TEMA 5: COMBINANDO OBSERVABLES

## Introducción

En aplicaciones reales, rara vez trabajarás con un único flujo de datos aislado. La verdadera potencia de RxJava surge cuando necesitas coordinar múltiples fuentes de información: combinar respuestas de varios servicios, sincronizar eventos de usuario con datos del servidor, o fusionar streams de diferentes orígenes.

En este tema aprenderás las estrategias fundamentales para combinar Observables de manera efectiva, entendiendo cuándo usar cada operador según tus necesidades específicas.

## 1. Estrategias de Combinación

### 1.1 Tipos de Combinación

Existen diferentes formas de combinar Observables según la lógica que necesites:

**Combinación secuencial**: Los Observables se ejecutan uno tras otro.

**Combinación paralela**: Los Observables se ejecutan simultáneamente y sus emisiones se combinan.

**Combinación condicional**: La combinación depende de ciertas condiciones o del orden de emisión.

## 2. Operadores de Combinación Secuencial

### 2.1 concat() y concatWith()

`concat()` concatena múltiples Observables emitiendo todos los elementos del primero antes de suscribirse al segundo.

**Características:**
- Mantiene el orden estricto
- No se suscribe al siguiente hasta que el anterior complete
- Si alguno emite error, se detiene la cadena

```java
Observable<String> observable1 = Observable.just("A", "B", "C");
Observable<String> observable2 = Observable.just("D", "E", "F");

Observable.concat(observable1, observable2)
    .subscribe(System.out::println);
// Salida: A, B, C, D, E, F
```

**Uso con concatWith():**

```java
observable1.concatWith(observable2)
    .subscribe(System.out::println);
```

### 2.2 concatMap()

`concatMap()` transforma cada elemento en un Observable y los concatena secuencialmente, esperando a que cada uno complete antes de suscribirse al siguiente.

```java
Observable.just("Usuario1", "Usuario2", "Usuario3")
    .concatMap(usuario -> obtenerDatosUsuario(usuario)) // Observable<DatosUsuario>
    .subscribe(datos -> System.out.println("Datos: " + datos));
```

**Diferencia clave con flatMap()**: `concatMap()` mantiene el orden de emisión original, mientras que `flatMap()` no lo garantiza.

### 2.3 startWith() y startWithArray()

Estos operadores añaden elementos al inicio del stream.

```java
Observable.just(2, 3, 4)
    .startWith(1)
    .subscribe(System.out::println);
// Salida: 1, 2, 3, 4

Observable.just("C", "D")
    .startWithArray("A", "B")
    .subscribe(System.out::println);
// Salida: A, B, C, D
```

## 3. Operadores de Combinación Paralela

### 3.1 merge() y mergeWith()

`merge()` combina múltiples Observables emitiendo elementos tan pronto como cualquiera de ellos los produce, sin esperar a que completen.

```java
Observable<String> obs1 = Observable.interval(1, TimeUnit.SECONDS)
    .map(i -> "Stream1: " + i);
    
Observable<String> obs2 = Observable.interval(500, TimeUnit.MILLISECONDS)
    .map(i -> "Stream2: " + i);

Observable.merge(obs1, obs2)
    .take(10)
    .subscribe(System.out::println);
// Las emisiones se intercalan según su tiempo de llegada
```

**Características:**
- No mantiene orden específico
- Procesa emisiones en paralelo
- Si un Observable emite error, se propaga inmediatamente

### 3.2 mergeDelayError()

Variante de `merge()` que retrasa el error hasta que todos los Observables completen.

```java
Observable<Integer> obs1 = Observable.just(1, 2, 3);
Observable<Integer> obs2 = Observable.error(new Exception("Error"));
Observable<Integer> obs3 = Observable.just(4, 5);

Observable.mergeDelayError(obs1, obs2, obs3)
    .subscribe(
        System.out::println,
        error -> System.err.println("Error al final: " + error)
    );
// Emite: 1, 2, 3, 4, 5, luego el error
```

### 3.3 zip()

`zip()` combina emisiones de múltiples Observables usando una función de combinación, esperando a que todos emitan antes de producir un resultado.

```java
Observable<String> nombres = Observable.just("Ana", "Luis", "María");
Observable<Integer> edades = Observable.just(25, 30, 28);
Observable<String> ciudades = Observable.just("Madrid", "Barcelona", "Valencia");

Observable.zip(nombres, edades, ciudades, 
    (nombre, edad, ciudad) -> nombre + " tiene " + edad + " años y vive en " + ciudad)
    .subscribe(System.out::println);

// Salida:
// Ana tiene 25 años y vive en Madrid
// Luis tiene 30 años y vive en Barcelona
// María tiene 28 años y vive en Valencia
```

**Características importantes:**
- Sincroniza emisiones: espera a que todos tengan un valor
- Si los Observables tienen diferente cantidad de elementos, zip() completa cuando el más corto termina
- Útil para combinar datos relacionados de diferentes fuentes

### 3.4 combineLatest()

`combineLatest()` combina las emisiones más recientes de cada Observable cada vez que cualquiera emite un nuevo valor.

```java
Observable<String> letras = Observable.interval(1, TimeUnit.SECONDS)
    .map(i -> "Letra: " + (char)('A' + i));
    
Observable<Long> numeros = Observable.interval(500, TimeUnit.MILLISECONDS);

Observable.combineLatest(letras, numeros,
    (letra, numero) -> letra + " - Número: " + numero)
    .take(10)
    .subscribe(System.out::println);

// Cada vez que cualquiera emite, se combina con el último valor del otro
```

**Diferencia con zip()**: `combineLatest()` no necesita que todos tengan valores nuevos, reutiliza el último valor emitido.

## 4. Operadores de Combinación Condicional

### 4.1 amb() y ambWith()

`amb()` (ambiguous) se suscribe a múltiples Observables pero solo emite del primero que comienza a emitir, descartando el resto.

```java
Observable<String> lento = Observable.timer(2, TimeUnit.SECONDS)
    .map(i -> "Observable lento");
    
Observable<String> rapido = Observable.timer(500, TimeUnit.MILLISECONDS)
    .map(i -> "Observable rápido");

Observable.amb(Arrays.asList(lento, rapido))
    .subscribe(System.out::println);
// Salida: "Observable rápido" (el lento se descarta)
```

**Caso de uso**: Consultar múltiples servidores y usar la respuesta del más rápido.

### 4.2 takeUntil()

Emite elementos hasta que otro Observable emita o complete.

```java
Observable<Long> contador = Observable.interval(500, TimeUnit.MILLISECONDS);
Observable<Long> senalParada = Observable.timer(3, TimeUnit.SECONDS);

contador.takeUntil(senalParada)
    .subscribe(System.out::println);
// Emite números durante 3 segundos, luego se detiene
```

### 4.3 skipUntil()

Ignora emisiones hasta que otro Observable emita.

```java
Observable<Long> datos = Observable.interval(500, TimeUnit.MILLISECONDS);
Observable<Long> senalInicio = Observable.timer(2, TimeUnit.SECONDS);

datos.skipUntil(senalInicio)
    .take(5)
    .subscribe(System.out::println);
// Comienza a emitir después de 2 segundos
```

## 5. Operadores de Agregación

### 5.1 reduce()

Aplica una función acumuladora a todos los elementos y emite un único resultado final.

```java
Observable.just(1, 2, 3, 4, 5)
    .reduce((acumulador, valor) -> acumulador + valor)
    .subscribe(suma -> System.out.println("Suma total: " + suma));
// Salida: Suma total: 15
```

**Con valor inicial:**

```java
Observable.just("Hola", "mundo", "RxJava")
    .reduce("", (acumulador, palabra) -> acumulador + " " + palabra)
    .subscribe(resultado -> System.out.println(resultado.trim()));
// Salida: Hola mundo RxJava
```

### 5.2 scan()

Similar a `reduce()` pero emite cada resultado intermedio.

```java
Observable.just(1, 2, 3, 4, 5)
    .scan((acumulador, valor) -> acumulador + valor)
    .subscribe(System.out::println);
// Salida: 1, 3, 6, 10, 15 (suma acumulativa)
```

### 5.3 collect()

Acumula elementos en una colección mutable.

```java
Observable.just("A", "B", "C", "D")
    .collect(ArrayList::new, ArrayList::add)
    .subscribe(lista -> System.out.println("Lista completa: " + lista));
// Salida: Lista completa: [A, B, C, D]
```

## 6. Casos de Uso Prácticos

### 6.1 Combinar datos de múltiples APIs

```java
Observable<Usuario> usuario = servicioUsuario.obtenerUsuario(id);
Observable<List<Pedido>> pedidos = servicioPedidos.obtenerPedidos(id);
Observable<Preferencias> preferencias = servicioPreferencias.obtener(id);

Observable.zip(usuario, pedidos, preferencias,
    (u, p, pref) -> new PerfilCompleto(u, p, pref))
    .subscribe(perfil -> mostrarPerfil(perfil));
```

### 6.2 Búsqueda con timeout y fallback

```java
Observable<Resultado> busquedaPrincipal = servicioPrincipal.buscar(query)
    .timeout(2, TimeUnit.SECONDS);
    
Observable<Resultado> busquedaSecundaria = servicioSecundario.buscar(query);

busquedaPrincipal
    .onErrorResumeNext(busquedaSecundaria)
    .subscribe(resultado -> mostrarResultados(resultado));
```

### 6.3 Proceso de checkout con múltiples pasos

```java
validarCarrito()
    .concatMap(carrito -> validarInventario(carrito))
    .concatMap(carrito -> procesarPago(carrito))
    .concatMap(pago -> generarFactura(pago))
    .concatMap(factura -> enviarConfirmacion(factura))
    .subscribe(
        confirmacion -> System.out.println("Compra completada: " + confirmacion),
        error -> manejarErrorCompra(error)
    );
```

## 7. Buenas Prácticas

**Elige el operador correcto según tus necesidades:**
- Usa `concat()` cuando el orden sea crítico y quieras operaciones secuenciales
- Usa `merge()` cuando quieras procesar múltiples streams en paralelo sin importar el orden
- Usa `zip()` cuando necesites sincronizar valores relacionados de diferentes fuentes
- Usa `combineLatest()` para formularios o interfaces que reaccionan a múltiples inputs
- Usa `amb()` para optimizar latencia eligiendo el stream más rápido

**Manejo de errores en combinaciones:**

Considera usar las variantes `*DelayError()` cuando quieras que todos los Observables completen antes de propagar errores.

**Performance:**

`flatMap()` es más eficiente que `concatMap()` si no necesitas mantener el orden, pero puede consumir más memoria al procesar múltiples streams simultáneamente.

**Testing:**

Los operadores de combinación son más complejos de probar. Usa `TestScheduler` para controlar el tiempo en tus tests.

## Referencias de Interés

**Documentación oficial:**
- ReactiveX Operators - Combining: http://reactivex.io/documentation/operators.html#combining
- RxJava 3 Wiki - Combining Observables: https://github.com/ReactiveX/RxJava/wiki/Combining-Observables

**Tutoriales y guías:**
- RxMarbles - Diagramas interactivos: https://rxmarbles.com/
- Combining Observables in RxJava: https://www.baeldung.com/rxjava-combining-observables
- RxJava Combining Operators Guide: https://proandroiddev.com/rxjava-combining-operators-2c083e9cd19a

**Videos:**
- RxJava Tutorial - Combining Operators: https://www.youtube.com/watch?v=5k0PoZ7Jnvc
- Advanced RxJava - Combining Streams: https://www.youtube.com/watch?v=3LKMwkuK0ZE

**Libros:**
- "Reactive Programming with RxJava" - Capítulo sobre operadores de combinación
- "Learning RxJava" de Thomas Nield - Sección de combinación de Observables

**Artículos técnicos:**
- When to use concat, merge, zip and combineLatest: https://medium.com/@luukgruijs/understanding-rxjs-merge-concat-combinelatest-zip-52e45d1b0742
- RxJava Combining Operators Deep Dive: https://blog.mindorks.com/rxjava-operator-combine
