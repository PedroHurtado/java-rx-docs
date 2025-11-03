# TEMA 1: FUNDAMENTOS SOBRE PROGRAMACIÓN FUNCIONAL Y REACTIVA

---

## 1. Introducción

La programación funcional en Java ha revolucionado la forma en que escribimos código, permitiendo un estilo más declarativo, conciso y expresivo. Este tema establece las bases necesarias para comprender la programación reactiva, comenzando con los fundamentos de programación funcional en Java.

---

## 2. Interfaces Funcionales en Java

### 2.1 ¿Qué es una Interfaz Funcional?

Una **interfaz funcional** es una interfaz que contiene exactamente un método abstracto (SAM - Single Abstract Method). Estas interfaces pueden tener múltiples métodos default y static, pero solo un método sin implementación.

**Anotación @FunctionalInterface:**
```java
@FunctionalInterface
public interface MiOperacion {
    int calcular(int a, int b);
    
    // Permitido: métodos default
    default void mostrarResultado(int resultado) {
        System.out.println("Resultado: " + resultado);
    }
    
    // Permitido: métodos static
    static void info() {
        System.out.println("Interfaz para operaciones matemáticas");
    }
}
```

### 2.2 Interfaces Funcionales Predefinidas en Java

Java proporciona interfaces funcionales en el paquete `java.util.function`:

#### **Function<T, R>**
Representa una función que acepta un argumento de tipo T y produce un resultado de tipo R.

```java
Function<String, Integer> longitudCadena = texto -> texto.length();
Integer longitud = longitudCadena.apply("Hola Mundo"); // 10
```

#### **Predicate<T>**
Representa un predicado (función booleana) de un argumento.

```java
Predicate<Integer> esPar = numero -> numero % 2 == 0;
boolean resultado = esPar.test(4); // true
```

#### **Consumer<T>**
Representa una operación que acepta un argumento y no devuelve resultado.

```java
Consumer<String> imprimir = texto -> System.out.println(texto);
imprimir.accept("Hola desde Consumer");
```

#### **Supplier<T>**
Representa un proveedor de resultados, no toma argumentos.

```java
Supplier<Double> numeroAleatorio = () -> Math.random();
Double valor = numeroAleatorio.get();
```

#### **BiFunction<T, U, R>**
Función que acepta dos argumentos y produce un resultado.

```java
BiFunction<Integer, Integer, Integer> suma = (a, b) -> a + b;
Integer resultado = suma.apply(5, 3); // 8
```

#### **UnaryOperator<T>**
Especialización de Function donde el argumento y resultado son del mismo tipo.

```java
UnaryOperator<Integer> cuadrado = x -> x * x;
Integer resultado = cuadrado.apply(5); // 25
```

#### **BinaryOperator<T>**
Especialización de BiFunction donde ambos argumentos y el resultado son del mismo tipo.

```java
BinaryOperator<Integer> multiplicacion = (a, b) -> a * b;
Integer resultado = multiplicacion.apply(4, 5); // 20
```

### 2.3 Expresiones Lambda

Las expresiones lambda son implementaciones concisas de interfaces funcionales:

**Sintaxis básica:**
```java
(parametros) -> expresion
(parametros) -> { bloque de código }
```

**Ejemplos:**
```java
// Sin parámetros
Runnable tarea = () -> System.out.println("Ejecutando tarea");

// Un parámetro (paréntesis opcionales)
Consumer<String> saludo = nombre -> System.out.println("Hola " + nombre);

// Múltiples parámetros
BiFunction<Integer, Integer, Integer> suma = (a, b) -> a + b;

// Con bloque de código
Predicate<String> esEmailValido = email -> {
    if (email == null) return false;
    return email.contains("@") && email.contains(".");
};
```

### 2.4 Method References (Referencias a Métodos)

Las referencias a métodos son una forma abreviada de expresiones lambda que ejecutan un único método.

**Tipos de referencias a métodos:**

```java
// 1. Referencia a método estático
Function<String, Integer> parseInt = Integer::parseInt;

// 2. Referencia a método de instancia de un objeto particular
String prefijo = "Prefijo: ";
Function<String, String> agregarPrefijo = prefijo::concat;

// 3. Referencia a método de instancia de un objeto arbitrario
Function<String, String> toUpperCase = String::toUpperCase;

// 4. Referencia a constructor
Supplier<ArrayList<String>> listSupplier = ArrayList::new;
```

---

## 3. Métodos Default en Interfaces

### 3.1 ¿Qué son los Métodos Default?

Introducidos en Java 8, los métodos default permiten agregar implementaciones a interfaces sin romper las clases existentes que las implementan.

```java
public interface Vehiculo {
    void arrancar(); // método abstracto
    
    // Método default con implementación
    default void mostrarInfo() {
        System.out.println("Este es un vehículo");
    }
    
    default void detener() {
        System.out.println("Vehículo detenido");
    }
}

class Coche implements Vehiculo {
    @Override
    public void arrancar() {
        System.out.println("Coche arrancando...");
    }
    // No es obligatorio sobrescribir los métodos default
}
```

### 3.2 Ventajas de los Métodos Default

1. **Evolución de APIs**: Permiten agregar funcionalidad sin romper compatibilidad
2. **Comportamiento común**: Proporcionan implementaciones por defecto
3. **Composición de interfaces**: Facilitan la creación de interfaces más ricas

### 3.3 Resolución de Conflictos

Cuando una clase implementa múltiples interfaces con métodos default del mismo nombre:

```java
interface InterfazA {
    default void metodo() {
        System.out.println("InterfazA");
    }
}

interface InterfazB {
    default void metodo() {
        System.out.println("InterfazB");
    }
}

class MiClase implements InterfazA, InterfazB {
    @Override
    public void metodo() {
        // Debemos resolver el conflicto explícitamente
        InterfazA.super.metodo(); // Llamar a implementación específica
        // o proporcionar nuestra propia implementación
        System.out.println("MiClase");
    }
}
```

---

## 4. Stream API en Java

### 4.1 ¿Qué es un Stream?

Un Stream es una secuencia de elementos que soporta operaciones agregadas secuenciales y paralelas. Los Streams no almacenan datos, sino que procesan datos de una fuente.

**Características principales:**
- No modifican la fuente de datos original
- Son lazy (evaluación perezosa)
- Pueden ser infinitos
- Las operaciones pueden encadenarse

### 4.2 Creación de Streams

```java
// Desde una colección
List<String> lista = Arrays.asList("a", "b", "c");
Stream<String> stream1 = lista.stream();

// Desde un array
String[] array = {"x", "y", "z"};
Stream<String> stream2 = Arrays.stream(array);

// Usando Stream.of()
Stream<Integer> stream3 = Stream.of(1, 2, 3, 4, 5);

// Stream vacío
Stream<String> streamVacio = Stream.empty();

// Stream infinito con generate
Stream<Double> streamAleatorio = Stream.generate(Math::random);

// Stream infinito con iterate
Stream<Integer> streamNumeros = Stream.iterate(0, n -> n + 2);
```

### 4.3 Operaciones Intermedias

Las operaciones intermedias retornan un nuevo Stream y son lazy (no se ejecutan hasta que hay una operación terminal).

#### **filter(Predicate<T>)**
Filtra elementos según una condición.

```java
List<Integer> numeros = Arrays.asList(1, 2, 3, 4, 5, 6);
List<Integer> pares = numeros.stream()
    .filter(n -> n % 2 == 0)
    .collect(Collectors.toList()); // [2, 4, 6]
```

#### **map(Function<T,R>)**
Transforma cada elemento aplicando una función.

```java
List<String> nombres = Arrays.asList("ana", "luis", "pedro");
List<String> mayusculas = nombres.stream()
    .map(String::toUpperCase)
    .collect(Collectors.toList()); // [ANA, LUIS, PEDRO]
```

#### **flatMap(Function<T,Stream<R>>)**
Aplana streams anidados en un único stream.

```java
List<List<Integer>> listaDeListas = Arrays.asList(
    Arrays.asList(1, 2),
    Arrays.asList(3, 4),
    Arrays.asList(5, 6)
);

List<Integer> listaPlana = listaDeListas.stream()
    .flatMap(List::stream)
    .collect(Collectors.toList()); // [1, 2, 3, 4, 5, 6]
```

#### **distinct()**
Elimina elementos duplicados.

```java
List<Integer> numeros = Arrays.asList(1, 2, 2, 3, 3, 4);
List<Integer> unicos = numeros.stream()
    .distinct()
    .collect(Collectors.toList()); // [1, 2, 3, 4]
```

#### **sorted()**
Ordena los elementos.

```java
List<Integer> numeros = Arrays.asList(5, 2, 8, 1, 9);
List<Integer> ordenados = numeros.stream()
    .sorted()
    .collect(Collectors.toList()); // [1, 2, 5, 8, 9]

// Con comparador personalizado
List<String> nombres = Arrays.asList("Juan", "Ana", "Pedro");
List<String> ordenadosPorLongitud = nombres.stream()
    .sorted((a, b) -> Integer.compare(a.length(), b.length()))
    .collect(Collectors.toList());
```

#### **limit(long n)**
Limita el stream a n elementos.

```java
List<Integer> primerosTres = Stream.iterate(1, n -> n + 1)
    .limit(3)
    .collect(Collectors.toList()); // [1, 2, 3]
```

#### **skip(long n)**
Omite los primeros n elementos.

```java
List<Integer> sinPrimerosDos = Arrays.asList(1, 2, 3, 4, 5).stream()
    .skip(2)
    .collect(Collectors.toList()); // [3, 4, 5]
```

#### **peek(Consumer<T>)**
Ejecuta una acción sobre cada elemento sin modificarlo (útil para debugging).

```java
List<Integer> resultado = Arrays.asList(1, 2, 3).stream()
    .peek(n -> System.out.println("Procesando: " + n))
    .map(n -> n * 2)
    .collect(Collectors.toList());
```

### 4.4 Operaciones Terminales

Las operaciones terminales producen un resultado o efecto secundario y cierran el stream.

#### **collect(Collector)**
Acumula los elementos en una colección.

```java
List<String> lista = stream.collect(Collectors.toList());
Set<String> conjunto = stream.collect(Collectors.toSet());
Map<Integer, String> mapa = personas.stream()
    .collect(Collectors.toMap(Persona::getId, Persona::getNombre));
```

#### **forEach(Consumer<T>)**
Ejecuta una acción para cada elemento.

```java
nombres.stream()
    .forEach(nombre -> System.out.println(nombre));
```

#### **reduce()**
Combina los elementos del stream en un único resultado.

```java
// Suma de números
Optional<Integer> suma = numeros.stream()
    .reduce((a, b) -> a + b);

// Con valor inicial
Integer sumaTotal = numeros.stream()
    .reduce(0, (a, b) -> a + b);

// Concatenar strings
String concatenado = palabras.stream()
    .reduce("", (a, b) -> a + b);
```

#### **count()**
Cuenta los elementos del stream.

```java
long cantidad = numeros.stream()
    .filter(n -> n > 5)
    .count();
```

#### **anyMatch(), allMatch(), noneMatch()**
Verifican condiciones sobre los elementos.

```java
boolean hayPares = numeros.stream().anyMatch(n -> n % 2 == 0);
boolean todosPares = numeros.stream().allMatch(n -> n % 2 == 0);
boolean ningunNegativo = numeros.stream().noneMatch(n -> n < 0);
```

#### **findFirst(), findAny()**
Encuentran elementos en el stream.

```java
Optional<Integer> primero = numeros.stream()
    .filter(n -> n > 5)
    .findFirst();

Optional<Integer> cualquiera = numeros.stream()
    .filter(n -> n > 5)
    .findAny();
```

#### **min(), max()**
Encuentran el elemento mínimo o máximo.

```java
Optional<Integer> minimo = numeros.stream()
    .min(Integer::compareTo);

Optional<Integer> maximo = numeros.stream()
    .max(Integer::compareTo);
```

### 4.5 El Papel de las Interfaces Funcionales en Streams

Los Streams dependen fuertemente de las interfaces funcionales:

```java
List<String> nombres = Arrays.asList("Ana", "Luis", "Pedro", "María");

List<String> resultado = nombres.stream()
    .filter(nombre -> nombre.length() > 3)     // Predicate<String>
    .map(String::toUpperCase)                   // Function<String, String>
    .sorted((a, b) -> a.compareTo(b))          // Comparator (interfaz funcional)
    .peek(n -> System.out.println(n))          // Consumer<String>
    .collect(Collectors.toList());

// El método collect usa Supplier, BiConsumer y otras interfaces funcionales internamente
```

**Relación directa:**
- `filter()` → `Predicate<T>`
- `map()` → `Function<T, R>`
- `forEach()` → `Consumer<T>`
- `reduce()` → `BinaryOperator<T>`
- `sorted()` → `Comparator<T>`

### 4.6 Streams Paralelos

Los streams pueden procesarse en paralelo automáticamente:

```java
List<Integer> numeros = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

// Stream secuencial
int sumaSecuencial = numeros.stream()
    .mapToInt(n -> n * 2)
    .sum();

// Stream paralelo
int sumaParalela = numeros.parallelStream()
    .mapToInt(n -> n * 2)
    .sum();

// Convertir stream secuencial a paralelo
int suma = numeros.stream()
    .parallel()
    .mapToInt(n -> n * 2)
    .sum();
```

---

## 5. Métodos Default en Interfaces y su Rol en Streams

### 5.1 Interfaces de Colecciones con Métodos Default

Las interfaces de colecciones fueron enriquecidas con métodos default para soportar Streams:

```java
public interface Collection<E> extends Iterable<E> {
    // Métodos default añadidos en Java 8
    default Stream<E> stream() {
        return StreamSupport.stream(spliterator(), false);
    }
    
    default Stream<E> parallelStream() {
        return StreamSupport.stream(spliterator(), true);
    }
    
    default boolean removeIf(Predicate<? super E> filter) {
        Objects.requireNonNull(filter);
        boolean removed = false;
        final Iterator<E> each = iterator();
        while (each.hasNext()) {
            if (filter.test(each.next())) {
                each.remove();
                removed = true;
            }
        }
        return removed;
    }
}
```

### 5.2 Métodos Default en Interfaces Funcionales

Las interfaces funcionales también tienen métodos default para composición:

```java
// Function tiene métodos default para composición
Function<Integer, Integer> multiplicarPor2 = x -> x * 2;
Function<Integer, Integer> sumar3 = x -> x + 3;

// andThen: primero aplica esta función, luego la otra
Function<Integer, Integer> multiplicarYSumar = multiplicarPor2.andThen(sumar3);
System.out.println(multiplicarYSumar.apply(5)); // (5*2)+3 = 13

// compose: primero aplica la otra función, luego esta
Function<Integer, Integer> sumarYMultiplicar = multiplicarPor2.compose(sumar3);
System.out.println(sumarYMultiplicar.apply(5)); // (5+3)*2 = 16

// Predicate tiene métodos default para lógica booleana
Predicate<Integer> esMayorQue5 = x -> x > 5;
Predicate<Integer> esPar = x -> x % 2 == 0;

Predicate<Integer> esMayorQue5YPar = esMayorQue5.and(esPar);
Predicate<Integer> esMayorQue5OPar = esMayorQue5.or(esPar);
Predicate<Integer> noEsMayorQue5 = esMayorQue5.negate();

// Consumer tiene andThen
Consumer<String> imprimir = s -> System.out.println(s);
Consumer<String> guardarEnLog = s -> System.err.println("LOG: " + s);
Consumer<String> imprimirYLoguear = imprimir.andThen(guardarEnLog);
```

### 5.3 Ventajas en el Desarrollo de Streams

Los métodos default permiten:

1. **Composición fluida**: Encadenar operaciones de forma elegante
2. **Código más expresivo**: Las operaciones se leen como lenguaje natural
3. **Reutilización**: Los métodos default proporcionan funcionalidad común
4. **Evolución de APIs**: Nuevas características sin romper código existente

```java
// Ejemplo de composición fluida gracias a métodos default
List<String> emails = obtenerEmails();

Predicate<String> esGmail = email -> email.endsWith("@gmail.com");
Predicate<String> noEstaVacio = email -> !email.isEmpty();
Predicate<String> filtroCompleto = esGmail.and(noEstaVacio);

long cantidadGmails = emails.stream()
    .filter(filtroCompleto)
    .count();
```

---

## 6. Conexión con Programación Reactiva

La programación funcional y los Streams son la base conceptual de la programación reactiva:

### 6.1 Similitudes Conceptuales

| Stream API | Programación Reactiva |
|------------|----------------------|
| Pipeline de operaciones | Pipeline de operadores |
| Operaciones intermedias lazy | Operadores no se ejecutan hasta suscripción |
| Operaciones terminales | Suscripciones |
| `forEach()` | `subscribe()` |
| Procesamiento de secuencias | Procesamiento de eventos asíncronos |

### 6.2 Diferencias Clave

**Streams:**
- Síncronos por defecto
- Pull-based (se consume cuando se solicita)
- Generalmente finitos
- Una sola ejecución

**Programación Reactiva (RxJava):**
- Asíncronos
- Push-based (los datos son empujados al consumidor)
- Pueden ser infinitos
- Múltiples suscriptores
- Manejo de backpressure
- Operadores de tiempo
- Manejo avanzado de errores

### 6.3 Transición Natural

```java
// Stream API
List<Integer> resultado = numeros.stream()
    .filter(n -> n % 2 == 0)
    .map(n -> n * 2)
    .collect(Collectors.toList());

// RxJava (adelanto conceptual)
Observable.fromIterable(numeros)
    .filter(n -> n % 2 == 0)
    .map(n -> n * 2)
    .subscribe(System.out::println);
```

---

## 7. Mejores Prácticas

### 7.1 Con Interfaces Funcionales

1. **Usar interfaces funcionales existentes** cuando sea posible
2. **Anotar con @FunctionalInterface** las interfaces funcionales personalizadas
3. **Preferir method references** sobre lambdas cuando mejore la legibilidad
4. **Evitar efectos secundarios** en funciones puras

```java
// ❌ Evitar
Function<Integer, Integer> conEfectoSecundario = x -> {
    System.out.println(x); // efecto secundario
    return x * 2;
};

// ✅ Preferir
Function<Integer, Integer> pura = x -> x * 2;
```

### 7.2 Con Streams

1. **Preferir streams para colecciones medianas/grandes** con lógica compleja
2. **Evitar streams para operaciones simples** donde un bucle es más claro
3. **Cuidado con streams infinitos** - siempre usar `limit()`
4. **Evitar modificar fuentes de datos** durante el procesamiento del stream
5. **Considerar streams paralelos** solo para operaciones costosas en colecciones grandes

```java
// ❌ Evitar modificar la fuente
List<String> lista = new ArrayList<>(Arrays.asList("a", "b", "c"));
lista.stream().forEach(s -> lista.add(s)); // ConcurrentModificationException

// ✅ Crear nueva colección
List<String> nuevaLista = lista.stream()
    .map(String::toUpperCase)
    .collect(Collectors.toList());
```

### 7.3 Con Métodos Default

1. **Documentar claramente** el comportamiento de métodos default
2. **Evitar lógica compleja** en métodos default
3. **Considerar compatibilidad** al agregar métodos default a interfaces públicas

---

## 8. Resumen

En este tema hemos cubierto:

✅ **Interfaces Funcionales**: La base de la programación funcional en Java
✅ **Expresiones Lambda y Method References**: Sintaxis concisa para implementar interfaces funcionales
✅ **Métodos Default**: Evolución de interfaces sin romper compatibilidad
✅ **Stream API**: Procesamiento declarativo de colecciones
✅ **Operaciones Intermedias y Terminales**: El pipeline de procesamiento
✅ **Integración**: Cómo interfaces funcionales, métodos default y streams trabajan juntos
✅ **Preparación para Programación Reactiva**: Conceptos fundamentales que se extienden en RxJava

---

## 9. Referencias de Interés

### Documentación Oficial

1. **Oracle Java Tutorials - Lambda Expressions**
   - https://docs.oracle.com/javase/tutorial/java/javaOO/lambdaexpressions.html
   - Tutorial oficial sobre expresiones lambda y su uso

2. **Oracle Java Tutorials - Stream API**
   - https://docs.oracle.com/javase/8/docs/api/java/util/stream/package-summary.html
   - Documentación completa del paquete java.util.stream

3. **Java Language Specification - Functional Interfaces**
   - https://docs.oracle.com/javase/specs/jls/se8/html/jls-9.html#jls-9.8
   - Especificación formal de interfaces funcionales

4. **Java Platform SE 8 - java.util.function Package**
   - https://docs.oracle.com/javase/8/docs/api/java/util/function/package-summary.html
   - Todas las interfaces funcionales predefinidas

### Libros Recomendados

5. **"Modern Java in Action" - Raoul-Gabriel Urma, Mario Fusco, Alan Mycroft**
   - Cobertura exhaustiva de programación funcional en Java
   - Capítulos dedicados a Streams y lambdas

6. **"Java 8 in Action: Lambdas, Streams, and Functional-style Programming"**
   - Enfoque práctico en características funcionales de Java 8+

### Artículos y Blogs

7. **Baeldung - Java 8 Streams**
   - https://www.baeldung.com/java-8-streams
   - Tutoriales prácticos y ejemplos del mundo real

8. **Baeldung - Functional Interfaces in Java 8**
   - https://www.baeldung.com/java-8-functional-interfaces
   - Guía detallada de interfaces funcionales

9. **Oracle Community - Default Methods**
   - https://docs.oracle.com/javase/tutorial/java/IandI/defaultmethods.html
   - Explicación oficial sobre métodos default

### Videos y Cursos

10. **Java Brains - Java 8 Lambda Expressions & Streams**
    - Canal de YouTube con serie completa sobre programación funcional en Java
    - Explicaciones claras con ejemplos prácticos

11. **Pluralsight - Java Fundamentals: The Java Language**
    - Curso que incluye secciones extensas sobre Streams y lambdas

### Recursos Adicionales

12. **DZone - Java 8 Stream API Cheat Sheet**
    - https://dzone.com/articles/java-8-streams-cheat-sheet
    - Referencia rápida para operaciones de Stream

13. **GitHub - Java 8 Stream Examples**
    - Repositorios con ejemplos prácticos de uso de Streams
    - Búsqueda: "java-8-stream-examples"

14. **Stack Overflow - Tags: [java-8], [java-stream], [lambda]**
    - https://stackoverflow.com/questions/tagged/java-stream
    - Problemas reales y soluciones de la comunidad

---

**Duración estimada del tema**: 2 horas (1 hora teoría + 1 hora laboratorio)
