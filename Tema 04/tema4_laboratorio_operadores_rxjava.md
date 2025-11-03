# Laboratorio 4: Operadores RxJava

## Objetivo

Practicar el uso de los principales operadores de RxJava mediante ejercicios progresivos que cubran transformación, filtrado, combinación y manejo de errores.

## Requisitos Previos

- Tener configurado el proyecto RxJava del laboratorio anterior
- Conocimientos de Observables y Observers

## Configuración del Proyecto

Asegúrate de tener las dependencias necesarias en tu `pom.xml`:

```xml
<dependency>
    <groupId>io.reactivex.rxjava3</groupId>
    <artifactId>rxjava</artifactId>
    <version>3.1.8</version>
</dependency>
```

## Ejercicio 1: Operadores de Transformación

### 1.1 Usando map()

Crea una clase `Ejercicio1_Map.java`:

```java
import io.reactivex.rxjava3.core.Observable;

public class Ejercicio1_Map {
    public static void main(String[] args) {
        System.out.println("=== Ejercicio 1.1: map() ===");
        
        // TODO: Crear un Observable con números del 1 al 5
        // Transformar cada número multiplicándolo por 10
        // Imprimir el resultado
        
        Observable.range(1, 5)
            .map(numero -> numero * 10)
            .subscribe(
                resultado -> System.out.println("Resultado: " + resultado),
                error -> System.err.println("Error: " + error),
                () -> System.out.println("Completado")
            );
    }
}
```

**Ejercicio adicional**: Modifica el código para convertir números a Strings con formato "Número: X".

### 1.2 Usando flatMap()

Crea una clase `Ejercicio1_FlatMap.java`:

```java
import io.reactivex.rxjava3.core.Observable;

public class Ejercicio1_FlatMap {
    public static void main(String[] args) {
        System.out.println("=== Ejercicio 1.2: flatMap() ===");
        
        // TODO: Para cada número del 1 al 3, crear un Observable 
        // que emita ese número multiplicado por 1, 2 y 3
        
        Observable.range(1, 3)
            .flatMap(numero -> 
                Observable.just(
                    numero * 1,
                    numero * 2,
                    numero * 3
                )
            )
            .subscribe(System.out::println);
    }
}
```

**Pregunta**: ¿El orden de emisión está garantizado? ¿Por qué?

### 1.3 Comparando flatMap() y concatMap()

Crea una clase `Ejercicio1_ConcatMap.java`:

```java
import io.reactivex.rxjava3.core.Observable;
import java.util.concurrent.TimeUnit;

public class Ejercicio1_ConcatMap {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Con flatMap() ===");
        
        Observable.just("A", "B", "C")
            .flatMap(letra -> 
                Observable.just(letra + "1", letra + "2")
                    .delay(100, TimeUnit.MILLISECONDS)
            )
            .blockingSubscribe(System.out::println);
        
        System.out.println("\n=== Con concatMap() ===");
        
        // TODO: Reemplazar flatMap por concatMap y observar la diferencia
        Observable.just("A", "B", "C")
            .concatMap(letra -> 
                Observable.just(letra + "1", letra + "2")
                    .delay(100, TimeUnit.MILLISECONDS)
            )
            .blockingSubscribe(System.out::println);
    }
}
```

**Ejercicio**: Documenta las diferencias observadas entre ambos operadores.

### 1.4 Usando scan()

Crea una clase `Ejercicio1_Scan.java`:

```java
import io.reactivex.rxjava3.core.Observable;

public class Ejercicio1_Scan {
    public static void main(String[] args) {
        System.out.println("=== Ejercicio 1.4: scan() ===");
        
        // TODO: Calcular el factorial de números del 1 al 5 usando scan()
        Observable.range(1, 5)
            .scan((acumulador, numero) -> acumulador * numero)
            .subscribe(factorial -> System.out.println("Factorial: " + factorial));
    }
}
```

**Desafío**: Modifica el código para calcular la suma acumulativa de números pares del 1 al 10.

## Ejercicio 2: Operadores de Filtrado

### 2.1 Usando filter()

Crea una clase `Ejercicio2_Filter.java`:

```java
import io.reactivex.rxjava3.core.Observable;

public class Ejercicio2_Filter {
    public static void main(String[] args) {
        System.out.println("=== Ejercicio 2.1: filter() ===");
        
        // TODO: Filtrar solo los números mayores que 5
        Observable.range(1, 10)
            .filter(numero -> numero > 5)
            .subscribe(System.out::println);
        
        System.out.println("\n=== Combinando filter y map ===");
        
        // TODO: Filtrar números pares y elevarlos al cuadrado
        Observable.range(1, 10)
            .filter(numero -> numero % 2 == 0)
            .map(numero -> numero * numero)
            .subscribe(resultado -> System.out.println("Cuadrado: " + resultado));
    }
}
```

### 2.2 Usando take() y skip()

Crea una clase `Ejercicio2_TakeSkip.java`:

```java
import io.reactivex.rxjava3.core.Observable;

public class Ejercicio2_TakeSkip {
    public static void main(String[] args) {
        System.out.println("=== take(3) ===");
        Observable.range(1, 10)
            .take(3)
            .subscribe(System.out::println);
        
        System.out.println("\n=== skip(5) ===");
        Observable.range(1, 10)
            .skip(5)
            .subscribe(System.out::println);
        
        System.out.println("\n=== skip(3).take(4) ===");
        // TODO: Combina skip y take para obtener elementos del 4 al 7
        Observable.range(1, 10)
            .skip(3)
            .take(4)
            .subscribe(System.out::println);
    }
}
```

### 2.3 Usando distinct()

Crea una clase `Ejercicio2_Distinct.java`:

```java
import io.reactivex.rxjava3.core.Observable;

public class Ejercicio2_Distinct {
    public static void main(String[] args) {
        System.out.println("=== distinct() ===");
        
        Observable.just(1, 2, 2, 3, 3, 3, 4, 5, 5)
            .distinct()
            .subscribe(System.out::println);
        
        System.out.println("\n=== distinctUntilChanged() ===");
        
        // TODO: Observa la diferencia con distinctUntilChanged()
        Observable.just(1, 1, 2, 2, 2, 3, 1, 1, 4, 4)
            .distinctUntilChanged()
            .subscribe(System.out::println);
    }
}
```

**Pregunta**: ¿Cuál es la diferencia entre `distinct()` y `distinctUntilChanged()`?

## Ejercicio 3: Operadores de Combinación

### 3.1 Usando merge()

Crea una clase `Ejercicio3_Merge.java`:

```java
import io.reactivex.rxjava3.core.Observable;

public class Ejercicio3_Merge {
    public static void main(String[] args) {
        System.out.println("=== Ejercicio 3.1: merge() ===");
        
        Observable<String> numeros = Observable.just("1", "2", "3");
        Observable<String> letras = Observable.just("A", "B", "C");
        
        // TODO: Combina ambos observables usando merge()
        Observable.merge(numeros, letras)
            .subscribe(System.out::println);
    }
}
```

### 3.2 Usando concat()

Crea una clase `Ejercicio3_Concat.java`:

```java
import io.reactivex.rxjava3.core.Observable;

public class Ejercicio3_Concat {
    public static void main(String[] args) {
        System.out.println("=== Ejercicio 3.2: concat() ===");
        
        Observable<String> saludos = Observable.just("Hola", "Buenos días");
        Observable<String> nombres = Observable.just("Juan", "María");
        Observable<String> despedidas = Observable.just("Adiós", "Hasta luego");
        
        // TODO: Concatena los tres observables
        Observable.concat(saludos, nombres, despedidas)
            .subscribe(System.out::println);
    }
}
```

**Diferencia clave**: ¿En qué se diferencia `concat()` de `merge()`?

### 3.3 Usando zip()

Crea una clase `Ejercicio3_Zip.java`:

```java
import io.reactivex.rxjava3.core.Observable;

public class Ejercicio3_Zip {
    public static void main(String[] args) {
        System.out.println("=== Ejercicio 3.3: zip() ===");
        
        Observable<String> nombres = Observable.just("Ana", "Luis", "Carmen");
        Observable<Integer> edades = Observable.just(25, 30, 28);
        Observable<String> ciudades = Observable.just("Madrid", "Barcelona", "Valencia");
        
        // TODO: Combina los tres observables para crear una ficha personal
        Observable.zip(nombres, edades, ciudades,
            (nombre, edad, ciudad) -> 
                nombre + " tiene " + edad + " años y vive en " + ciudad
        )
        .subscribe(System.out::println);
    }
}
```

**Ejercicio adicional**: Crea una clase `Persona` y usa `zip()` para crear objetos de esta clase.

## Ejercicio 4: Operadores de Manejo de Errores

### 4.1 Usando onErrorReturn()

Crea una clase `Ejercicio4_OnErrorReturn.java`:

```java
import io.reactivex.rxjava3.core.Observable;

public class Ejercicio4_OnErrorReturn {
    public static void main(String[] args) {
        System.out.println("=== Sin manejo de errores ===");
        
        Observable.just(10, 5, 0, 2)
            .map(numero -> 100 / numero)
            .subscribe(
                resultado -> System.out.println("Resultado: " + resultado),
                error -> System.err.println("Error: " + error.getMessage())
            );
        
        System.out.println("\n=== Con onErrorReturn() ===");
        
        // TODO: Usa onErrorReturn para devolver -1 cuando ocurra un error
        Observable.just(10, 5, 0, 2)
            .map(numero -> 100 / numero)
            .onErrorReturn(error -> -1)
            .subscribe(
                resultado -> System.out.println("Resultado: " + resultado),
                error -> System.err.println("Error: " + error.getMessage())
            );
    }
}
```

### 4.2 Usando onErrorResumeNext()

Crea una clase `Ejercicio4_OnErrorResumeNext.java`:

```java
import io.reactivex.rxjava3.core.Observable;

public class Ejercicio4_OnErrorResumeNext {
    public static void main(String[] args) {
        System.out.println("=== Con onErrorResumeNext() ===");
        
        Observable<Integer> observablePrincipal = Observable.just(10, 5, 0, 2)
            .map(numero -> 100 / numero);
        
        Observable<Integer> observableRespaldo = Observable.just(999, 888, 777);
        
        // TODO: Cuando ocurra un error, cambiar al observable de respaldo
        observablePrincipal
            .onErrorResumeNext(observableRespaldo)
            .subscribe(
                resultado -> System.out.println("Resultado: " + resultado),
                error -> System.err.println("Error: " + error.getMessage()),
                () -> System.out.println("Completado")
            );
    }
}
```

### 4.3 Usando retry()

Crea una clase `Ejercicio4_Retry.java`:

```java
import io.reactivex.rxjava3.core.Observable;
import java.util.concurrent.atomic.AtomicInteger;

public class Ejercicio4_Retry {
    private static AtomicInteger contador = new AtomicInteger(0);
    
    public static void main(String[] args) {
        System.out.println("=== Ejercicio 4.3: retry() ===");
        
        // TODO: Crear un Observable que falle las primeras 2 veces
        // y tenga éxito en el tercer intento
        Observable<String> observableConFallos = Observable.create(emitter -> {
            int intentos = contador.incrementAndGet();
            System.out.println("Intento número: " + intentos);
            
            if (intentos < 3) {
                emitter.onError(new Exception("Fallo simulado"));
            } else {
                emitter.onNext("¡Éxito!");
                emitter.onComplete();
            }
        });
        
        observableConFallos
            .retry(3)  // Reintentar hasta 3 veces
            .subscribe(
                resultado -> System.out.println("Resultado: " + resultado),
                error -> System.err.println("Error final: " + error.getMessage()),
                () -> System.out.println("Completado")
            );
    }
}
```

## Ejercicio 5: Caso Práctico Integral

Crea una clase `Ejercicio5_CasoPractico.java` que simule el procesamiento de pedidos:

```java
import io.reactivex.rxjava3.core.Observable;
import java.util.Arrays;
import java.util.List;

class Producto {
    private String nombre;
    private double precio;
    private int stock;
    
    public Producto(String nombre, double precio, int stock) {
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
    }
    
    public String getNombre() { return nombre; }
    public double getPrecio() { return precio; }
    public int getStock() { return stock; }
    
    @Override
    public String toString() {
        return nombre + " - $" + precio + " (Stock: " + stock + ")";
    }
}

class Pedido {
    private String producto;
    private int cantidad;
    private double precioTotal;
    
    public Pedido(String producto, int cantidad, double precioTotal) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioTotal = precioTotal;
    }
    
    public double getPrecioTotal() { return precioTotal; }
    
    @Override
    public String toString() {
        return "Pedido: " + producto + " x" + cantidad + " = $" + precioTotal;
    }
}

public class Ejercicio5_CasoPractico {
    public static void main(String[] args) {
        // Catálogo de productos
        List<Producto> productos = Arrays.asList(
            new Producto("Laptop", 1200.00, 5),
            new Producto("Mouse", 25.00, 50),
            new Producto("Teclado", 75.00, 30),
            new Producto("Monitor", 300.00, 10),
            new Producto("Webcam", 80.00, 0),  // Sin stock
            new Producto("Auriculares", 60.00, 20)
        );
        
        System.out.println("=== PROCESAMIENTO DE PEDIDOS ===\n");
        
        // TODO: Implementar el siguiente flujo:
        // 1. Filtrar solo productos con stock disponible
        // 2. Filtrar productos con precio entre $50 y $500
        // 3. Tomar los primeros 3 productos
        // 4. Crear un pedido de 2 unidades de cada producto
        // 5. Calcular el total de todos los pedidos
        
        Observable.fromIterable(productos)
            .filter(producto -> producto.getStock() > 0)
            .filter(producto -> producto.getPrecio() >= 50 && producto.getPrecio() <= 500)
            .take(3)
            .map(producto -> new Pedido(
                producto.getNombre(), 
                2, 
                producto.getPrecio() * 2
            ))
            .doOnNext(pedido -> System.out.println(pedido))
            .map(Pedido::getPrecioTotal)
            .reduce((total, precio) -> total + precio)
            .subscribe(
                total -> System.out.println("\nTOTAL A PAGAR: $" + total),
                error -> System.err.println("Error: " + error.getMessage())
            );
    }
}
```

**Desafíos adicionales**:

1. Aplica un descuento del 10% si el total supera los $500
2. Agrupa los productos por rango de precio (económicos < $100, medios < $200, caros >= $200)
3. Maneja el caso de productos sin stock devolviendo un mensaje personalizado

## Ejercicio 6: Debugging con doOnNext

Crea una clase `Ejercicio6_Debugging.java`:

```java
import io.reactivex.rxjava3.core.Observable;

public class Ejercicio6_Debugging {
    public static void main(String[] args) {
        System.out.println("=== Debugging con doOnNext ===\n");
        
        // TODO: Usa doOnNext para ver el flujo de datos en cada paso
        Observable.range(1, 10)
            .doOnNext(numero -> System.out.println("Emitido: " + numero))
            .filter(numero -> numero % 2 == 0)
            .doOnNext(numero -> System.out.println("Después de filtrar: " + numero))
            .map(numero -> numero * numero)
            .doOnNext(numero -> System.out.println("Después de elevar al cuadrado: " + numero))
            .take(3)
            .doOnNext(numero -> System.out.println("Después de take: " + numero))
            .subscribe(
                resultado -> System.out.println("RESULTADO FINAL: " + resultado),
                error -> System.err.println("Error: " + error),
                () -> System.out.println("\nCompletado")
            );
    }
}
```

## Ejercicios Propuestos

### Ejercicio 7: Sistema de Calificaciones

Crea un sistema que procese las calificaciones de estudiantes:

```java
class Estudiante {
    String nombre;
    List<Double> calificaciones;
    
    // Constructor, getters, etc.
}
```

**Requisitos**:
1. Filtrar estudiantes con promedio >= 7.0
2. Calcular el promedio de cada estudiante usando `reduce()`
3. Ordenar por promedio (usa `toList()` y `sorted()`)
4. Obtener los 3 mejores estudiantes

### Ejercicio 8: Procesamiento de Eventos

Simula un sistema de eventos con tiempos:

**Requisitos**:
1. Usa `interval()` para simular eventos cada 500ms
2. Aplica `map()` para agregar información al evento
3. Usa `buffer(3)` para agrupar eventos
4. Aplica `take(4)` para procesar solo 4 grupos
5. Usa `doOnNext()` para logging

### Ejercicio 9: Conversor de Temperaturas

Crea un conversor que transforme temperaturas:

**Requisitos**:
1. Observable con temperaturas en Celsius
2. Usa `map()` para convertir a Fahrenheit
3. Filtra temperaturas extremas (< 0°F o > 100°F)
4. Agrupa en rangos (frío, templado, calor)

### Ejercicio 10: Validación de Datos

Implementa un sistema de validación:

**Requisitos**:
1. Observable con emails y números de teléfono
2. Usa `filter()` con expresiones regulares para validar
3. Maneja errores de validación con `onErrorReturn()`
4. Usa `distinct()` para evitar duplicados
5. Cuenta elementos válidos con `count()`

## Preguntas de Reflexión

1. ¿Cuándo usarías `flatMap()` vs `concatMap()` vs `switchMap()`?
2. ¿Cuál es la diferencia entre `onErrorReturn()` y `onErrorResumeNext()`?
3. ¿Por qué es importante el orden de los operadores en una cadena?
4. ¿Cómo afecta `take()` al ciclo de vida del Observable?
5. ¿Qué ventajas ofrece `zip()` sobre combinar streams manualmente?

## Recursos Adicionales

- Experimenta con los operadores en RxMarbles: https://rxmarbles.com/
- Revisa la documentación oficial de cada operador
- Prueba a combinar diferentes operadores en cadenas complejas
- Utiliza `doOnNext()`, `doOnError()` y `doOnComplete()` para debugging
