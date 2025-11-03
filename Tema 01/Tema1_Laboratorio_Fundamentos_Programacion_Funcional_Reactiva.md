# LABORATORIO 1: FUNDAMENTOS DE PROGRAMACIÓN FUNCIONAL Y REACTIVA

---

## Objetivos del Laboratorio

Al finalizar este laboratorio, serás capaz de:
- Crear y utilizar interfaces funcionales personalizadas y predefinidas
- Implementar expresiones lambda y referencias a métodos
- Aplicar métodos default en interfaces para extender funcionalidad
- Utilizar Stream API para procesar colecciones de forma declarativa
- Combinar operaciones intermedias y terminales en pipelines complejos
- Componer funciones utilizando métodos default de interfaces funcionales

---

## Requisitos Previos

- JDK 8 o superior instalado
- IDE (IntelliJ IDEA, Eclipse, VS Code con extensiones Java)
- Conocimientos básicos de Java
- Maven o Gradle (opcional para gestión de dependencias)

---

## Estructura del Laboratorio

El laboratorio está dividido en 5 ejercicios progresivos que cubren todos los conceptos del tema.

---

## EJERCICIO 1: Interfaces Funcionales y Lambdas

### Objetivo
Practicar la creación de interfaces funcionales personalizadas y su implementación con expresiones lambda.

### Descripción
Vamos a crear un sistema de validación de datos utilizando interfaces funcionales.

### Paso 1: Crear las interfaces funcionales

```java
package lab01.ejercicio1;

@FunctionalInterface
public interface Validador<T> {
    boolean esValido(T valor);
    
    // Método default para combinar validadores
    default Validador<T> and(Validador<T> otro) {
        return valor -> this.esValido(valor) && otro.esValido(valor);
    }
    
    default Validador<T> or(Validador<T> otro) {
        return valor -> this.esValido(valor) || otro.esValido(valor);
    }
    
    default Validador<T> negate() {
        return valor -> !this.esValido(valor);
    }
}
```

### Paso 2: Crear clase de modelo

```java
package lab01.ejercicio1;

public class Usuario {
    private String nombre;
    private String email;
    private int edad;
    
    public Usuario(String nombre, String email, int edad) {
        this.nombre = nombre;
        this.email = email;
        this.edad = edad;
    }
    
    // Getters y setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public int getEdad() { return edad; }
    public void setEdad(int edad) { this.edad = edad; }
    
    @Override
    public String toString() {
        return "Usuario{" +
                "nombre='" + nombre + '\'' +
                ", email='" + email + '\'' +
                ", edad=" + edad +
                '}';
    }
}
```

### Paso 3: Implementar validadores usando lambdas

```java
package lab01.ejercicio1;

public class ValidadorDemo {
    
    public static void main(String[] args) {
        // 1. Validadores simples con lambdas
        Validador<String> emailValidador = email -> 
            email != null && email.contains("@") && email.contains(".");
        
        Validador<String> nombreValidador = nombre -> 
            nombre != null && nombre.length() >= 3;
        
        Validador<Integer> edadValidador = edad -> 
            edad != null && edad >= 18 && edad <= 100;
        
        // 2. Probar validadores individuales
        System.out.println("=== Prueba de validadores individuales ===");
        System.out.println("Email 'user@example.com' válido: " + 
            emailValidador.esValido("user@example.com"));
        System.out.println("Email 'invalid' válido: " + 
            emailValidador.esValido("invalid"));
        
        // 3. Combinar validadores usando métodos default
        Validador<String> emailCompletoValidador = emailValidador
            .and(email -> !email.contains(" "))
            .and(email -> email.length() <= 50);
        
        System.out.println("\n=== Prueba de validador compuesto ===");
        System.out.println("Email 'user@example.com' válido: " + 
            emailCompletoValidador.esValido("user@example.com"));
        System.out.println("Email con espacios válido: " + 
            emailCompletoValidador.esValido("user @example.com"));
        
        // 4. Validar usuarios completos
        System.out.println("\n=== Validación de usuarios ===");
        Usuario usuario1 = new Usuario("Ana", "ana@example.com", 25);
        Usuario usuario2 = new Usuario("Bo", "invalid", 15);
        
        System.out.println("Usuario 1: " + usuario1);
        System.out.println("  Nombre válido: " + nombreValidador.esValido(usuario1.getNombre()));
        System.out.println("  Email válido: " + emailValidador.esValido(usuario1.getEmail()));
        System.out.println("  Edad válida: " + edadValidador.esValido(usuario1.getEdad()));
        
        System.out.println("\nUsuario 2: " + usuario2);
        System.out.println("  Nombre válido: " + nombreValidador.esValido(usuario2.getNombre()));
        System.out.println("  Email válido: " + emailValidador.esValido(usuario2.getEmail()));
        System.out.println("  Edad válida: " + edadValidador.esValido(usuario2.getEdad()));
        
        // 5. Crear un validador de usuario completo
        Validador<Usuario> usuarioValidador = usuario ->
            nombreValidador.esValido(usuario.getNombre()) &&
            emailValidador.esValido(usuario.getEmail()) &&
            edadValidador.esValido(usuario.getEdad());
        
        System.out.println("\n=== Validación completa de usuarios ===");
        System.out.println("Usuario 1 válido: " + usuarioValidador.esValido(usuario1));
        System.out.println("Usuario 2 válido: " + usuarioValidador.esValido(usuario2));
    }
}
```

### Tareas

1. **Tarea 1.1**: Crear un validador para contraseñas que verifique:
   - Longitud mínima de 8 caracteres
   - Al menos una letra mayúscula
   - Al menos un número
   - Al menos un carácter especial (@, #, $, etc.)

2. **Tarea 1.2**: Implementar un validador para números de teléfono que acepte diferentes formatos.

3. **Tarea 1.3**: Crear una clase `ValidadorCompuesto<T>` que permita registrar múltiples validadores y ejecutarlos todos.

---

## EJERCICIO 2: Interfaces Funcionales Predefinidas

### Objetivo
Dominar el uso de interfaces funcionales del paquete `java.util.function`.

### Descripción
Crear un sistema de procesamiento de productos que utilice las interfaces funcionales predefinidas.

### Paso 1: Crear clase Producto

```java
package lab01.ejercicio2;

public class Producto {
    private String id;
    private String nombre;
    private double precio;
    private String categoria;
    private int stock;
    
    public Producto(String id, String nombre, double precio, String categoria, int stock) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.categoria = categoria;
        this.stock = stock;
    }
    
    // Getters y setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }
    
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
    
    @Override
    public String toString() {
        return String.format("Producto{id='%s', nombre='%s', precio=%.2f, categoria='%s', stock=%d}",
                id, nombre, precio, categoria, stock);
    }
}
```

### Paso 2: Implementar procesamiento con interfaces funcionales

```java
package lab01.ejercicio2;

import java.util.*;
import java.util.function.*;

public class ProcesadorProductos {
    
    private List<Producto> productos;
    
    public ProcesadorProductos() {
        this.productos = new ArrayList<>();
        inicializarProductos();
    }
    
    private void inicializarProductos() {
        productos.add(new Producto("P001", "Laptop HP", 899.99, "Electrónica", 15));
        productos.add(new Producto("P002", "Mouse Logitech", 25.50, "Electrónica", 50));
        productos.add(new Producto("P003", "Teclado Mecánico", 89.99, "Electrónica", 30));
        productos.add(new Producto("P004", "Monitor Samsung", 299.99, "Electrónica", 20));
        productos.add(new Producto("P005", "Escritorio", 199.99, "Muebles", 10));
        productos.add(new Producto("P006", "Silla Ergonómica", 249.99, "Muebles", 25));
        productos.add(new Producto("P007", "Lámpara LED", 35.99, "Iluminación", 40));
    }
    
    // Método 1: Usar Predicate para filtrar
    public List<Producto> filtrar(Predicate<Producto> criterio) {
        List<Producto> resultado = new ArrayList<>();
        for (Producto p : productos) {
            if (criterio.test(p)) {
                resultado.add(p);
            }
        }
        return resultado;
    }
    
    // Método 2: Usar Function para transformar
    public <R> List<R> transformar(Function<Producto, R> transformador) {
        List<R> resultado = new ArrayList<>();
        for (Producto p : productos) {
            resultado.add(transformador.apply(p));
        }
        return resultado;
    }
    
    // Método 3: Usar Consumer para procesar
    public void procesar(Consumer<Producto> accion) {
        productos.forEach(accion);
    }
    
    // Método 4: Usar BiFunction para calcular
    public double calcularTotal(BiFunction<Double, Double, Double> operacion) {
        double total = 0.0;
        for (Producto p : productos) {
            total = operacion.apply(total, p.getPrecio());
        }
        return total;
    }
    
    // Método 5: Usar UnaryOperator para modificar
    public void aplicarDescuento(UnaryOperator<Double> calculadorDescuento) {
        productos.forEach(p -> p.setPrecio(calculadorDescuento.apply(p.getPrecio())));
    }
    
    // Método 6: Usar Supplier para generar
    public void agregarProducto(Supplier<Producto> generador) {
        productos.add(generador.get());
    }
    
    public static void main(String[] args) {
        ProcesadorProductos procesador = new ProcesadorProductos();
        
        System.out.println("=== 1. USO DE PREDICATE - FILTRADO ===");
        
        // Filtrar productos caros (precio > 100)
        Predicate<Producto> esProductoCaro = p -> p.getPrecio() > 100;
        List<Producto> productosCaros = procesador.filtrar(esProductoCaro);
        System.out.println("\nProductos con precio mayor a $100:");
        productosCaros.forEach(System.out::println);
        
        // Combinar predicados con métodos default
        Predicate<Producto> esElectronica = p -> p.getCategoria().equals("Electrónica");
        Predicate<Producto> electronicaCaros = esElectronica.and(esProductoCaro);
        List<Producto> electronicaCarosLista = procesador.filtrar(electronicaCaros);
        System.out.println("\nElectrónica cara:");
        electronicaCarosLista.forEach(System.out::println);
        
        // Predicate con OR
        Predicate<Producto> stockBajo = p -> p.getStock() < 20;
        Predicate<Producto> caroOStockBajo = esProductoCaro.or(stockBajo);
        System.out.println("\nProductos caros O con stock bajo:");
        procesador.filtrar(caroOStockBajo).forEach(System.out::println);
        
        System.out.println("\n=== 2. USO DE FUNCTION - TRANSFORMACIÓN ===");
        
        // Extraer solo los nombres
        Function<Producto, String> extraerNombre = Producto::getNombre;
        List<String> nombres = procesador.transformar(extraerNombre);
        System.out.println("\nNombres de productos:");
        nombres.forEach(System.out::println);
        
        // Crear descripciones
        Function<Producto, String> crearDescripcion = p -> 
            String.format("%s - $%.2f (%s)", p.getNombre(), p.getPrecio(), p.getCategoria());
        List<String> descripciones = procesador.transformar(crearDescripcion);
        System.out.println("\nDescripciones:");
        descripciones.forEach(System.out::println);
        
        // Composición de funciones con andThen
        Function<Producto, Double> obtenerPrecio = Producto::getPrecio;
        Function<Double, String> formatearPrecio = precio -> String.format("$%.2f", precio);
        Function<Producto, String> obtenerPrecioFormateado = obtenerPrecio.andThen(formatearPrecio);
        
        System.out.println("\nPrecios formateados:");
        procesador.transformar(obtenerPrecioFormateado).forEach(System.out::println);
        
        System.out.println("\n=== 3. USO DE CONSUMER - PROCESAMIENTO ===");
        
        // Imprimir información
        Consumer<Producto> imprimirInfo = p -> 
            System.out.println(p.getNombre() + " tiene " + p.getStock() + " unidades");
        
        System.out.println("\nInventario:");
        procesador.procesar(imprimirInfo);
        
        // Combinar consumers con andThen
        Consumer<Producto> verificarStock = p -> {
            if (p.getStock() < 20) {
                System.out.println("¡ALERTA! Stock bajo para: " + p.getNombre());
            }
        };
        
        Consumer<Producto> procesadorCompleto = imprimirInfo.andThen(verificarStock);
        System.out.println("\nProcesamiento completo:");
        procesador.procesar(procesadorCompleto);
        
        System.out.println("\n=== 4. USO DE BIFUNCTION - CÁLCULOS ===");
        
        // Calcular suma total
        BiFunction<Double, Double, Double> sumar = (a, b) -> a + b;
        double total = procesador.calcularTotal(sumar);
        System.out.printf("\nValor total del inventario: $%.2f\n", total);
        
        System.out.println("\n=== 5. USO DE UNARYOPERATOR - MODIFICACIÓN ===");
        
        System.out.println("\nPrecios antes del descuento:");
        procesador.procesar(p -> System.out.printf("%s: $%.2f\n", p.getNombre(), p.getPrecio()));
        
        // Aplicar 10% de descuento
        UnaryOperator<Double> descuento10 = precio -> precio * 0.90;
        procesador.aplicarDescuento(descuento10);
        
        System.out.println("\nPrecios después del descuento del 10%:");
        procesador.procesar(p -> System.out.printf("%s: $%.2f\n", p.getNombre(), p.getPrecio()));
        
        System.out.println("\n=== 6. USO DE SUPPLIER - GENERACIÓN ===");
        
        // Generar nuevo producto
        Supplier<Producto> generadorProducto = () -> 
            new Producto("P008", "Auriculares", 79.99, "Electrónica", 35);
        
        procesador.agregarProducto(generadorProducto);
        System.out.println("\nProducto agregado con Supplier:");
        System.out.println(procesador.productos.get(procesador.productos.size() - 1));
    }
}
```

### Tareas

1. **Tarea 2.1**: Implementar un método que use `BiPredicate<Producto, String>` para comparar productos con una categoría específica.

2. **Tarea 2.2**: Crear un método que use `BinaryOperator<Producto>` para encontrar el producto más caro.

3. **Tarea 2.3**: Implementar un método que use `ToIntFunction<Producto>` para calcular el valor total del inventario (precio * stock).

---

## EJERCICIO 3: Stream API - Operaciones Básicas

### Objetivo
Dominar las operaciones intermedias y terminales de Stream API.

### Descripción
Procesar datos de estudiantes usando operaciones de Stream.

### Paso 1: Crear clase Estudiante

```java
package lab01.ejercicio3;

public class Estudiante {
    private String nombre;
    private int edad;
    private String carrera;
    private double promedio;
    
    public Estudiante(String nombre, int edad, String carrera, double promedio) {
        this.nombre = nombre;
        this.edad = edad;
        this.carrera = carrera;
        this.promedio = promedio;
    }
    
    // Getters
    public String getNombre() { return nombre; }
    public int getEdad() { return edad; }
    public String getCarrera() { return carrera; }
    public double getPromedio() { return promedio; }
    
    @Override
    public String toString() {
        return String.format("Estudiante{nombre='%s', edad=%d, carrera='%s', promedio=%.2f}",
                nombre, edad, carrera, promedio);
    }
}
```

### Paso 2: Implementar operaciones con Streams

```java
package lab01.ejercicio3;

import java.util.*;
import java.util.stream.*;

public class StreamOperacionesBasicas {
    
    private static List<Estudiante> crearEstudiantes() {
        return Arrays.asList(
            new Estudiante("Ana García", 20, "Ingeniería", 8.5),
            new Estudiante("Luis Pérez", 22, "Medicina", 9.2),
            new Estudiante("María López", 19, "Ingeniería", 7.8),
            new Estudiante("Carlos Ruiz", 21, "Derecho", 8.9),
            new Estudiante("Elena Martín", 20, "Medicina", 9.5),
            new Estudiante("Pedro Sánchez", 23, "Ingeniería", 7.5),
            new Estudiante("Laura Torres", 22, "Derecho", 8.7),
            new Estudiante("Juan Ramírez", 19, "Medicina", 9.0),
            new Estudiante("Sofia Díaz", 21, "Ingeniería", 8.3),
            new Estudiante("Diego Flores", 20, "Derecho", 8.1)
        );
    }
    
    public static void main(String[] args) {
        List<Estudiante> estudiantes = crearEstudiantes();
        
        System.out.println("=== 1. OPERACIÓN FILTER ===");
        
        // Filtrar estudiantes de Ingeniería
        System.out.println("\nEstudiantes de Ingeniería:");
        estudiantes.stream()
            .filter(e -> e.getCarrera().equals("Ingeniería"))
            .forEach(System.out::println);
        
        // Filtrar estudiantes con promedio >= 9.0
        System.out.println("\nEstudiantes con promedio >= 9.0:");
        estudiantes.stream()
            .filter(e -> e.getPromedio() >= 9.0)
            .forEach(System.out::println);
        
        // Múltiples filtros
        System.out.println("\nEstudiantes de Medicina con promedio >= 9.0:");
        estudiantes.stream()
            .filter(e -> e.getCarrera().equals("Medicina"))
            .filter(e -> e.getPromedio() >= 9.0)
            .forEach(System.out::println);
        
        System.out.println("\n=== 2. OPERACIÓN MAP ===");
        
        // Extraer nombres
        System.out.println("\nNombres de todos los estudiantes:");
        estudiantes.stream()
            .map(Estudiante::getNombre)
            .forEach(System.out::println);
        
        // Convertir promedios a calificación
        System.out.println("\nCalificaciones (escala A-F):");
        estudiantes.stream()
            .map(e -> {
                String calificacion;
                if (e.getPromedio() >= 9.0) calificacion = "A";
                else if (e.getPromedio() >= 8.0) calificacion = "B";
                else if (e.getPromedio() >= 7.0) calificacion = "C";
                else calificacion = "D";
                return e.getNombre() + ": " + calificacion;
            })
            .forEach(System.out::println);
        
        System.out.println("\n=== 3. OPERACIÓN SORTED ===");
        
        // Ordenar por promedio (ascendente)
        System.out.println("\nOrdenados por promedio (menor a mayor):");
        estudiantes.stream()
            .sorted(Comparator.comparingDouble(Estudiante::getPromedio))
            .forEach(System.out::println);
        
        // Ordenar por promedio (descendente)
        System.out.println("\nOrdenados por promedio (mayor a menor):");
        estudiantes.stream()
            .sorted(Comparator.comparingDouble(Estudiante::getPromedio).reversed())
            .forEach(System.out::println);
        
        // Ordenar por nombre
        System.out.println("\nOrdenados alfabéticamente:");
        estudiantes.stream()
            .sorted(Comparator.comparing(Estudiante::getNombre))
            .limit(5)
            .forEach(System.out::println);
        
        System.out.println("\n=== 4. OPERACIONES DISTINCT, LIMIT, SKIP ===");
        
        // Carreras únicas
        System.out.println("\nCarreras disponibles:");
        estudiantes.stream()
            .map(Estudiante::getCarrera)
            .distinct()
            .sorted()
            .forEach(System.out::println);
        
        // Top 3 promedios
        System.out.println("\nTop 3 estudiantes:");
        estudiantes.stream()
            .sorted(Comparator.comparingDouble(Estudiante::getPromedio).reversed())
            .limit(3)
            .forEach(System.out::println);
        
        // Saltar primeros 3 y tomar siguiente 3
        System.out.println("\nEstudiantes del lugar 4 al 6:");
        estudiantes.stream()
            .sorted(Comparator.comparingDouble(Estudiante::getPromedio).reversed())
            .skip(3)
            .limit(3)
            .forEach(System.out::println);
        
        System.out.println("\n=== 5. OPERACIONES TERMINALES - COUNT, MIN, MAX ===");
        
        // Contar estudiantes por carrera
        long totalIngenieria = estudiantes.stream()
            .filter(e -> e.getCarrera().equals("Ingeniería"))
            .count();
        System.out.println("\nTotal estudiantes de Ingeniería: " + totalIngenieria);
        
        // Encontrar estudiante con mejor promedio
        Optional<Estudiante> mejorEstudiante = estudiantes.stream()
            .max(Comparator.comparingDouble(Estudiante::getPromedio));
        mejorEstudiante.ifPresent(e -> 
            System.out.println("Mejor estudiante: " + e));
        
        // Encontrar estudiante más joven
        Optional<Estudiante> masJoven = estudiantes.stream()
            .min(Comparator.comparingInt(Estudiante::getEdad));
        masJoven.ifPresent(e -> 
            System.out.println("Estudiante más joven: " + e));
        
        System.out.println("\n=== 6. OPERACIONES TERMINALES - ANYMATCH, ALLMATCH, NONEMATCH ===");
        
        // Verificar si hay algún estudiante con promedio perfecto
        boolean hayPromedioPerfecto = estudiantes.stream()
            .anyMatch(e -> e.getPromedio() == 10.0);
        System.out.println("\n¿Hay algún promedio perfecto? " + hayPromedioPerfecto);
        
        // Verificar si todos tienen promedio aprobatorio
        boolean todosAprobados = estudiantes.stream()
            .allMatch(e -> e.getPromedio() >= 7.0);
        System.out.println("¿Todos aprobados? " + todosAprobados);
        
        // Verificar que ninguno tiene promedio bajo
        boolean ningunoBajo = estudiantes.stream()
            .noneMatch(e -> e.getPromedio() < 6.0);
        System.out.println("¿Ninguno con promedio bajo? " + ningunoBajo);
        
        System.out.println("\n=== 7. OPERACIÓN REDUCE ===");
        
        // Sumar todos los promedios
        double sumaPromedios = estudiantes.stream()
            .map(Estudiante::getPromedio)
            .reduce(0.0, Double::sum);
        System.out.println("\nSuma de promedios: " + sumaPromedios);
        
        // Calcular promedio general
        double promedioGeneral = sumaPromedios / estudiantes.size();
        System.out.printf("Promedio general: %.2f\n", promedioGeneral);
        
        // Concatenar nombres
        String todosLosNombres = estudiantes.stream()
            .map(Estudiante::getNombre)
            .reduce("", (a, b) -> a.isEmpty() ? b : a + ", " + b);
        System.out.println("\nTodos los nombres: " + todosLosNombres);
        
        System.out.println("\n=== 8. COLLECT - COLECCIONES ===");
        
        // Colectar en lista
        List<String> nombresLista = estudiantes.stream()
            .map(Estudiante::getNombre)
            .collect(Collectors.toList());
        System.out.println("\nNombres en lista: " + nombresLista.size() + " elementos");
        
        // Colectar en set
        Set<String> carrerasSet = estudiantes.stream()
            .map(Estudiante::getCarrera)
            .collect(Collectors.toSet());
        System.out.println("Carreras únicas: " + carrerasSet);
        
        // Colectar en mapa
        Map<String, Double> mapaPromedios = estudiantes.stream()
            .collect(Collectors.toMap(
                Estudiante::getNombre,
                Estudiante::getPromedio
            ));
        System.out.println("\nMapa nombre -> promedio:");
        mapaPromedios.forEach((k, v) -> System.out.printf("  %s: %.2f\n", k, v));
        
        System.out.println("\n=== 9. COLLECTORS AVANZADOS ===");
        
        // Agrupar por carrera
        Map<String, List<Estudiante>> porCarrera = estudiantes.stream()
            .collect(Collectors.groupingBy(Estudiante::getCarrera));
        
        System.out.println("\nEstudiantes agrupados por carrera:");
        porCarrera.forEach((carrera, lista) -> {
            System.out.println(carrera + ": " + lista.size() + " estudiantes");
        });
        
        // Calcular promedio por carrera
        Map<String, Double> promediosPorCarrera = estudiantes.stream()
            .collect(Collectors.groupingBy(
                Estudiante::getCarrera,
                Collectors.averagingDouble(Estudiante::getPromedio)
            ));
        
        System.out.println("\nPromedio por carrera:");
        promediosPorCarrera.forEach((carrera, prom) -> 
            System.out.printf("  %s: %.2f\n", carrera, prom));
        
        // Particionar por promedio >= 8.5
        Map<Boolean, List<Estudiante>> particion = estudiantes.stream()
            .collect(Collectors.partitioningBy(e -> e.getPromedio() >= 8.5));
        
        System.out.println("\nEstudiantes con promedio >= 8.5: " + 
            particion.get(true).size());
        System.out.println("Estudiantes con promedio < 8.5: " + 
            particion.get(false).size());
        
        System.out.println("\n=== 10. PEEK PARA DEBUGGING ===");
        
        System.out.println("\nProcesamiento con peek:");
        long resultado = estudiantes.stream()
            .peek(e -> System.out.println("Procesando: " + e.getNombre()))
            .filter(e -> e.getPromedio() >= 8.5)
            .peek(e -> System.out.println("  -> Pasó filtro: " + e.getNombre()))
            .count();
        System.out.println("Total que pasaron filtro: " + resultado);
    }
}
```

### Tareas

1. **Tarea 3.1**: Crear un pipeline que:
   - Filtre estudiantes de "Ingeniería"
   - Los ordene por promedio descendente
   - Tome los 3 primeros
   - Retorne una lista de sus nombres

2. **Tarea 3.2**: Implementar un método que use `flatMap` para procesar una lista de listas de estudiantes.

3. **Tarea 3.3**: Crear estadísticas personalizadas usando `Collectors.summarizingDouble()`.

---

## EJERCICIO 4: Composición de Funciones y Métodos Default

### Objetivo
Practicar la composición de funciones usando métodos default de interfaces funcionales.

### Paso 1: Crear pipeline de procesamiento de texto

```java
package lab01.ejercicio4;

import java.util.function.*;
import java.util.*;

public class ComposicionFunciones {
    
    public static void main(String[] args) {
        
        System.out.println("=== 1. COMPOSICIÓN CON FUNCTION ===");
        
        // Funciones individuales
        Function<String, String> quitarEspacios = String::trim;
        Function<String, String> aMayusculas = String::toUpperCase;
        Function<String, String> agregarPrefijo = s -> "PROCESADO: " + s;
        
        // Composición con andThen
        Function<String, String> pipeline1 = quitarEspacios
            .andThen(aMayusculas)
            .andThen(agregarPrefijo);
        
        String texto = "   hola mundo   ";
        System.out.println("\nTexto original: '" + texto + "'");
        System.out.println("Resultado: " + pipeline1.apply(texto));
        
        // Composición con compose
        Function<String, Integer> contarLetras = String::length;
        Function<String, Integer> pipeline2 = contarLetras.compose(quitarEspacios);
        
        System.out.println("\nConteo de letras (sin espacios): " + 
            pipeline2.apply(texto));
        
        System.out.println("\n=== 2. COMPOSICIÓN CON PREDICATE ===");
        
        // Predicados individuales
        Predicate<String> noEstaVacio = s -> !s.isEmpty();
        Predicate<String> tieneNumeros = s -> s.matches(".*\\d.*");
        Predicate<String> tieneMayusculas = s -> !s.equals(s.toLowerCase());
        
        // Composición con AND
        Predicate<String> validacionCompleta = noEstaVacio
            .and(tieneNumeros)
            .and(tieneMayusculas);
        
        System.out.println("\nValidación de contraseña:");
        List<String> contraseñas = Arrays.asList(
            "Password123",
            "password",
            "PASSWORD",
            "Pass123"
        );
        
        contraseñas.forEach(pwd -> 
            System.out.println(pwd + " es válida: " + validacionCompleta.test(pwd)));
        
        // Composición con OR
        Predicate<String> tieneLongitudCorta = s -> s.length() < 5;
        Predicate<String> esDebil = noEstaVacio.negate()
            .or(tieneLongitudCorta)
            .or(tieneNumeros.negate());
        
        System.out.println("\nContraseñas débiles:");
        contraseñas.forEach(pwd -> 
            System.out.println(pwd + " es débil: " + esDebil.test(pwd)));
        
        System.out.println("\n=== 3. COMPOSICIÓN CON CONSUMER ===");
        
        // Consumers individuales
        Consumer<String> imprimir = s -> System.out.print(s + " ");
        Consumer<String> imprimirLongitud = s -> 
            System.out.print("(longitud: " + s.length() + ") ");
        Consumer<String> saltarLinea = s -> System.out.println();
        
        // Composición con andThen
        Consumer<String> procesadorCompleto = imprimir
            .andThen(imprimirLongitud)
            .andThen(saltarLinea);
        
        System.out.println("\nProcesamiento de palabras:");
        List<String> palabras = Arrays.asList("Java", "Stream", "Funcional");
        palabras.forEach(procesadorCompleto);
        
        System.out.println("\n=== 4. PIPELINE COMPLEJO DE TRANSFORMACIÓN ===");
        
        // Crear un pipeline de transformación de datos
        Function<String, String> normalizarTexto = String::trim;
        Function<String, String> capitalizarPrimeraLetra = s -> 
            s.isEmpty() ? s : Character.toUpperCase(s.charAt(0)) + s.substring(1).toLowerCase();
        Function<String, String> eliminarCaracteresEspeciales = s -> 
            s.replaceAll("[^a-zA-Z0-9\\s]", "");
        Function<String, String[]> dividirEnPalabras = s -> s.split("\\s+");
        
        // Procesador de nombres
        Function<String, String> procesadorNombres = normalizarTexto
            .andThen(capitalizarPrimeraLetra)
            .andThen(eliminarCaracteresEspeciales);
        
        System.out.println("\nProcesamiento de nombres:");
        List<String> nombresDesordenados = Arrays.asList(
            "  JUAN pérez  ",
            "maría@GARCÍA",
            "  pedro-lópez  "
        );
        
        nombresDesordenados.stream()
            .map(procesadorNombres)
            .forEach(System.out::println);
        
        System.out.println("\n=== 5. CREAR PROCESADOR CONFIGURABLE ===");
        
        // Clase para crear pipelines configurables
        ProcesadorTexto procesador = new ProcesadorTexto();
        
        procesador
            .agregarPaso(String::trim)
            .agregarPaso(String::toLowerCase)
            .agregarPaso(s -> s.replaceAll("\\s+", "_"));
        
        System.out.println("\nConversión a snake_case:");
        String entrada = "  Hola Mundo en Java  ";
        System.out.println("Entrada: '" + entrada + "'");
        System.out.println("Salida: '" + procesador.procesar(entrada) + "'");
    }
}

// Clase helper para construir pipelines
class ProcesadorTexto {
    private Function<String, String> pipeline = Function.identity();
    
    public ProcesadorTexto agregarPaso(Function<String, String> paso) {
        pipeline = pipeline.andThen(paso);
        return this;
    }
    
    public String procesar(String texto) {
        return pipeline.apply(texto);
    }
}
```

### Paso 2: Crear procesador de datos con validación

```java
package lab01.ejercicio4;

import java.util.function.*;
import java.util.*;

public class ValidadorCompuesto<T> {
    
    private List<Predicate<T>> validadores = new ArrayList<>();
    private List<String> mensajes = new ArrayList<>();
    
    public ValidadorCompuesto<T> agregarRegla(Predicate<T> validador, String mensaje) {
        validadores.add(validador);
        mensajes.add(mensaje);
        return this;
    }
    
    public ResultadoValidacion validar(T objeto) {
        List<String> errores = new ArrayList<>();
        
        for (int i = 0; i < validadores.size(); i++) {
            if (!validadores.get(i).test(objeto)) {
                errores.add(mensajes.get(i));
            }
        }
        
        return new ResultadoValidacion(errores.isEmpty(), errores);
    }
    
    public static void main(String[] args) {
        System.out.println("=== VALIDADOR COMPUESTO ===\n");
        
        // Crear validador para productos
        ValidadorCompuesto<ProductoValidacion> validador = new ValidadorCompuesto<>();
        
        validador
            .agregarRegla(
                p -> p.getNombre() != null && !p.getNombre().isEmpty(),
                "El nombre no puede estar vacío"
            )
            .agregarRegla(
                p -> p.getPrecio() > 0,
                "El precio debe ser mayor que cero"
            )
            .agregarRegla(
                p -> p.getPrecio() < 10000,
                "El precio debe ser menor que 10,000"
            )
            .agregarRegla(
                p -> p.getStock() >= 0,
                "El stock no puede ser negativo"
            )
            .agregarRegla(
                p -> p.getCategoria() != null,
                "La categoría es obligatoria"
            );
        
        // Probar con diferentes productos
        List<ProductoValidacion> productos = Arrays.asList(
            new ProductoValidacion("Laptop", 899.99, 10, "Electrónica"),
            new ProductoValidacion("", 50.0, 5, "Varios"),
            new ProductoValidacion("Monitor", -100.0, 3, "Electrónica"),
            new ProductoValidacion("Teclado", 15000.0, 20, null)
        );
        
        productos.forEach(producto -> {
            System.out.println("Validando: " + producto);
            ResultadoValidacion resultado = validador.validar(producto);
            
            if (resultado.esValido()) {
                System.out.println("  ✓ Producto válido\n");
            } else {
                System.out.println("  ✗ Errores encontrados:");
                resultado.getErrores().forEach(error -> 
                    System.out.println("    - " + error));
                System.out.println();
            }
        });
    }
}

// Clases helper
class ProductoValidacion {
    private String nombre;
    private double precio;
    private int stock;
    private String categoria;
    
    public ProductoValidacion(String nombre, double precio, int stock, String categoria) {
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
        this.categoria = categoria;
    }
    
    public String getNombre() { return nombre; }
    public double getPrecio() { return precio; }
    public int getStock() { return stock; }
    public String getCategoria() { return categoria; }
    
    @Override
    public String toString() {
        return String.format("Producto{nombre='%s', precio=%.2f, stock=%d, categoria='%s'}",
                nombre, precio, stock, categoria);
    }
}

class ResultadoValidacion {
    private boolean valido;
    private List<String> errores;
    
    public ResultadoValidacion(boolean valido, List<String> errores) {
        this.valido = valido;
        this.errores = errores;
    }
    
    public boolean esValido() { return valido; }
    public List<String> getErrores() { return errores; }
}
```

### Tareas

1. **Tarea 4.1**: Crear un pipeline de transformación que convierta cadenas a formato título (Primera Letra De Cada Palabra En Mayúscula).

2. **Tarea 4.2**: Implementar un validador de formulario que combine múltiples validadores usando `and()`, `or()` y `negate()`.

3. **Tarea 4.3**: Crear una clase `TransformadorConfigurable<T,R>` que permita construir transformaciones complejas usando `andThen()` y `compose()`.

---

## EJERCICIO 5: Proyecto Integrador - Sistema de Análisis de Ventas

### Objetivo
Integrar todos los conceptos aprendidos en un proyecto completo.

### Descripción
Crear un sistema de análisis de ventas que utilice interfaces funcionales, streams y métodos default para procesar y analizar datos.

### Paso 1: Crear modelos de datos

```java
package lab01.ejercicio5;

import java.time.LocalDate;

public class Venta {
    private String id;
    private String producto;
    private String categoria;
    private double precio;
    private int cantidad;
    private LocalDate fecha;
    private String vendedor;
    private String region;
    
    public Venta(String id, String producto, String categoria, double precio, 
                 int cantidad, LocalDate fecha, String vendedor, String region) {
        this.id = id;
        this.producto = producto;
        this.categoria = categoria;
        this.precio = precio;
        this.cantidad = cantidad;
        this.fecha = fecha;
        this.vendedor = vendedor;
        this.region = region;
    }
    
    public double getTotal() {
        return precio * cantidad;
    }
    
    // Getters
    public String getId() { return id; }
    public String getProducto() { return producto; }
    public String getCategoria() { return categoria; }
    public double getPrecio() { return precio; }
    public int getCantidad() { return cantidad; }
    public LocalDate getFecha() { return fecha; }
    public String getVendedor() { return vendedor; }
    public String getRegion() { return region; }
    
    @Override
    public String toString() {
        return String.format("Venta{id='%s', producto='%s', total=%.2f, fecha=%s}",
                id, producto, getTotal(), fecha);
    }
}
```

### Paso 2: Implementar analizador de ventas

```java
package lab01.ejercicio5;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class AnalizadorVentas {
    
    private List<Venta> ventas;
    
    public AnalizadorVentas() {
        this.ventas = generarVentasEjemplo();
    }
    
    private List<Venta> generarVentasEjemplo() {
        List<Venta> ventas = new ArrayList<>();
        String[] productos = {"Laptop", "Mouse", "Teclado", "Monitor", "Impresora"};
        String[] categorias = {"Electrónica", "Accesorios"};
        String[] vendedores = {"Juan", "María", "Pedro", "Ana"};
        String[] regiones = {"Norte", "Sur", "Este", "Oeste"};
        
        Random random = new Random(42);
        
        for (int i = 1; i <= 50; i++) {
            ventas.add(new Venta(
                "V" + String.format("%03d", i),
                productos[random.nextInt(productos.length)],
                categorias[random.nextInt(categorias.length)],
                100 + random.nextDouble() * 900,
                1 + random.nextInt(5),
                LocalDate.of(2024, 1 + random.nextInt(12), 1 + random.nextInt(28)),
                vendedores[random.nextInt(vendedores.length)],
                regiones[random.nextInt(regiones.length)]
            ));
        }
        
        return ventas;
    }
    
    // Método 1: Análisis con Predicates
    public void analizarPorCriterio(Predicate<Venta> criterio, String descripcion) {
        System.out.println("\n" + descripcion + ":");
        
        List<Venta> ventasFiltradas = ventas.stream()
            .filter(criterio)
            .collect(Collectors.toList());
        
        System.out.println("  Total ventas: " + ventasFiltradas.size());
        
        double totalVentas = ventasFiltradas.stream()
            .mapToDouble(Venta::getTotal)
            .sum();
        
        System.out.printf("  Monto total: $%.2f\n", totalVentas);
        
        double promedio = ventasFiltradas.isEmpty() ? 0 : 
            totalVentas / ventasFiltradas.size();
        System.out.printf("  Promedio por venta: $%.2f\n", promedio);
    }
    
    // Método 2: Transformación con Functions
    public <R> void mostrarTransformacion(Function<Venta, R> transformador, 
                                          String descripcion, int limite) {
        System.out.println("\n" + descripcion + ":");
        ventas.stream()
            .map(transformador)
            .distinct()
            .limit(limite)
            .forEach(System.out::println);
    }
    
    // Método 3: Agrupación avanzada
    public void analisisAvanzado() {
        System.out.println("\n=== ANÁLISIS AVANZADO ===");
        
        // Ventas por categoría
        Map<String, Long> ventasPorCategoria = ventas.stream()
            .collect(Collectors.groupingBy(
                Venta::getCategoria,
                Collectors.counting()
            ));
        
        System.out.println("\nVentas por categoría:");
        ventasPorCategoria.forEach((cat, count) -> 
            System.out.printf("  %s: %d ventas\n", cat, count));
        
        // Total por región
        Map<String, Double> totalPorRegion = ventas.stream()
            .collect(Collectors.groupingBy(
                Venta::getRegion,
                Collectors.summingDouble(Venta::getTotal)
            ));
        
        System.out.println("\nTotal de ventas por región:");
        totalPorRegion.entrySet().stream()
            .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
            .forEach(e -> System.out.printf("  %s: $%.2f\n", e.getKey(), e.getValue()));
        
        // Mejor vendedor
        Map<String, Double> totalPorVendedor = ventas.stream()
            .collect(Collectors.groupingBy(
                Venta::getVendedor,
                Collectors.summingDouble(Venta::getTotal)
            ));
        
        Optional<Map.Entry<String, Double>> mejorVendedor = totalPorVendedor.entrySet()
            .stream()
            .max(Map.Entry.comparingByValue());
        
        mejorVendedor.ifPresent(e -> 
            System.out.printf("\nMejor vendedor: %s con $%.2f\n", e.getKey(), e.getValue()));
        
        // Productos más vendidos
        Map<String, Long> productosMasVendidos = ventas.stream()
            .collect(Collectors.groupingBy(
                Venta::getProducto,
                Collectors.counting()
            ));
        
        System.out.println("\nTop 3 productos más vendidos:");
        productosMasVendidos.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(3)
            .forEach(e -> System.out.printf("  %s: %d ventas\n", e.getKey(), e.getValue()));
    }
    
    // Método 4: Análisis temporal
    public void analisisTemporal() {
        System.out.println("\n=== ANÁLISIS TEMPORAL ===");
        
        // Ventas por mes
        Map<Month, Long> ventasPorMes = ventas.stream()
            .collect(Collectors.groupingBy(
                v -> v.getFecha().getMonth(),
                Collectors.counting()
            ));
        
        System.out.println("\nVentas por mes:");
        ventasPorMes.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(e -> System.out.printf("  %s: %d ventas\n", 
                e.getKey(), e.getValue()));
        
        // Promedio de venta por mes
        Map<Month, Double> promedioMensual = ventas.stream()
            .collect(Collectors.groupingBy(
                v -> v.getFecha().getMonth(),
                Collectors.averagingDouble(Venta::getTotal)
            ));
        
        System.out.println("\nPromedio de venta por mes:");
        promedioMensual.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(e -> System.out.printf("  %s: $%.2f\n", 
                e.getKey(), e.getValue()));
    }
    
    // Método 5: Estadísticas con Collectors
    public void estadisticasGenerales() {
        System.out.println("\n=== ESTADÍSTICAS GENERALES ===");
        
        DoubleSummaryStatistics stats = ventas.stream()
            .mapToDouble(Venta::getTotal)
            .summaryStatistics();
        
        System.out.printf("\nTotal de ventas: %d\n", stats.getCount());
        System.out.printf("Suma total: $%.2f\n", stats.getSum());
        System.out.printf("Promedio: $%.2f\n", stats.getAverage());
        System.out.printf("Venta mínima: $%.2f\n", stats.getMin());
        System.out.printf("Venta máxima: $%.2f\n", stats.getMax());
        
        // Encontrar venta más alta y más baja
        Optional<Venta> ventaMasAlta = ventas.stream()
            .max(Comparator.comparingDouble(Venta::getTotal));
        
        Optional<Venta> ventaMasBaja = ventas.stream()
            .min(Comparator.comparingDouble(Venta::getTotal));
        
        ventaMasAlta.ifPresent(v -> 
            System.out.println("\nVenta más alta: " + v));
        
        ventaMasBaja.ifPresent(v -> 
            System.out.println("Venta más baja: " + v));
    }
    
    public static void main(String[] args) {
        AnalizadorVentas analizador = new AnalizadorVentas();
        
        System.out.println("=== SISTEMA DE ANÁLISIS DE VENTAS ===");
        
        // 1. Análisis con predicados
        Predicate<Venta> ventasAltas = v -> v.getTotal() > 1000;
        Predicate<Venta> ventasElectronica = v -> v.getCategoria().equals("Electrónica");
        Predicate<Venta> ventasRegionNorte = v -> v.getRegion().equals("Norte");
        
        analizador.analizarPorCriterio(ventasAltas, "Ventas mayores a $1000");
        analizador.analizarPorCriterio(
            ventasElectronica.and(ventasAltas), 
            "Electrónica con ventas altas"
        );
        analizador.analizarPorCriterio(
            ventasRegionNorte.or(v -> v.getRegion().equals("Sur")), 
            "Ventas en Norte o Sur"
        );
        
        // 2. Transformaciones
        Function<Venta, String> resumenVenta = v -> 
            String.format("%s - %s ($%.2f)", v.getProducto(), v.getVendedor(), v.getTotal());
        
        analizador.mostrarTransformacion(Venta::getProducto, 
            "Productos únicos", 10);
        analizador.mostrarTransformacion(resumenVenta, 
            "Resumen de ventas (primeras 5)", 5);
        
        // 3. Análisis avanzado
        analizador.analisisAvanzado();
        
        // 4. Análisis temporal
        analizador.analisisTemporal();
        
        // 5. Estadísticas
        analizador.estadisticasGenerales();
    }
}
```

### Tareas

1. **Tarea 5.1**: Implementar un método que identifique tendencias de ventas (crecimiento o decrecimiento) mes a mes.

2. **Tarea 5.2**: Crear un sistema de alertas que notifique cuando:
   - Un producto tiene pocas ventas
   - Un vendedor supera un objetivo
   - Una región tiene desempeño excepcional

3. **Tarea 5.3**: Implementar un generador de reportes que:
   - Use Suppliers para crear diferentes tipos de reportes
   - Use Consumers para diferentes formatos de salida (consola, archivo, etc.)
   - Use Functions para transformaciones personalizadas

4. **Tarea 5.4**: Crear un dashboard interactivo que permita al usuario:
   - Filtrar ventas por múltiples criterios combinados
   - Ordenar resultados por diferentes campos
   - Agrupar y resumir datos de forma flexible

---



## Resumen del Laboratorio

Has practicado:

✅ Creación de interfaces funcionales personalizadas
✅ Implementación de expresiones lambda
✅ Uso de interfaces funcionales predefinidas (Function, Predicate, Consumer, etc.)
✅ Composición de funciones con métodos default
✅ Operaciones de Stream API (filter, map, reduce, collect)
✅ Agrupación y agregación de datos
✅ Pipelines complejos de procesamiento
✅ Integración de todos los conceptos en un proyecto real

---



