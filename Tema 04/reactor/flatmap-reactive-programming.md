# Entendiendo flatMap en Programación Reactiva

## Introducción

Uno de los conceptos más desafiantes al aprender programación reactiva con Project Reactor es comprender cuándo usar `map` versus `flatMap`. Este documento explica la diferencia de manera clara y práctica.

---

## La diferencia fundamental

### flatMap en colecciones tradicionales

Si vienes del mundo de JavaScript o de Streams de Java, conoces `flatMap` como un operador que "aplana" estructuras anidadas:

```javascript
// JavaScript
[[1, 2], [3, 4]].flatMap(x => x)  // Resultado: [1, 2, 3, 4]
```

```java
// Java Streams
List<List<Integer>> nested = List.of(List.of(1, 2), List.of(3, 4));
nested.stream()
    .flatMap(List::stream)
    .collect(Collectors.toList());  // Resultado: [1, 2, 3, 4]
```

### flatMap en Programación Reactiva (Mono/Flux)

En programación reactiva, `flatMap` tiene un propósito diferente: **desenvolver Monos/Flux anidados**.

Se utiliza cuando tu función de transformación **devuelve otro Mono o Flux** en lugar de un valor simple.

---

## Regla de oro

```java
// ✅ Usa map cuando tu función devuelve un VALOR
.map(pizza -> pizza.getName())           // Devuelve String
.map(pizza -> new PizzaDTO(pizza))       // Devuelve PizzaDTO
.map(ResponseEntity::ok)                 // Devuelve ResponseEntity

// ✅ Usa flatMap cuando tu función devuelve MONO/FLUX
.flatMap(req -> Mono.just(pizza))        // Devuelve Mono<Pizza>
.flatMap(pizzaRepository::save)          // Devuelve Mono<Pizza>
.flatMap(id -> service.findById(id))     // Devuelve Mono<Entity>
```

---

## Análisis del código ejemplo

Analicemos el siguiente código que crea una Pizza con ingredientes de forma reactiva:

```java
return request
    .flatMap(req -> {
        Pizza pizza = Pizza.create(
            UUID.randomUUID(),
            req.name(),
            req.description(),
            req.url()
        );
        
        // Agregar ingredientes si existen
        if (req.ingredientIds() != null && !req.ingredientIds().isEmpty()) {
            return ingredientRepository.findAllById(req.ingredientIds())
                .doOnNext(pizza::addIngredient)
                .then(Mono.just(pizza));
        }
        
        return Mono.just(pizza);
    })
    .flatMap(pizzaRepository::save)
    .map(pizza -> ResponseEntity
        .status(HttpStatus.CREATED)
        .body(PizzaResponse.fromEntity(pizza)));
```

### Desglose paso a paso

#### 1. `request.flatMap(req -> {...})`

**¿Por qué flatMap y no map?**

```java
// ❌ Si usáramos map:
request.map(req -> {
    // ...
    return Mono.just(pizza);  // Devuelve Mono<Pizza>
})
// Resultado: Mono<Mono<Pizza>> 😱 (estructura anidada no deseada)

// ✅ Con flatMap:
request.flatMap(req -> {
    // ...
    return Mono.just(pizza);  // Devuelve Mono<Pizza>
})
// Resultado: Mono<Pizza> ✅ (estructura correcta)
```

#### 2. Creación de la Pizza

```java
Pizza pizza = Pizza.create(
    UUID.randomUUID(),
    req.name(),
    req.description(),
    req.url()
);
```

Creamos el objeto Pizza con los datos de la petición y un ID único.

#### 3. Procesamiento de ingredientes

```java
if (req.ingredientIds() != null && !req.ingredientIds().isEmpty()) {
    return ingredientRepository.findAllById(req.ingredientIds())
        .doOnNext(pizza::addIngredient)
        .then(Mono.just(pizza));
}
```

- `findAllById()` devuelve `Flux<Ingredient>` (stream de ingredientes)
- `doOnNext()` efecto lateral: agrega cada ingrediente a la pizza
- `then()` ignora los elementos del Flux y devuelve la pizza completa

#### 4. `.flatMap(pizzaRepository::save)`

**¿Por qué flatMap aquí?**

```java
// ❌ Si usáramos map:
.map(pizzaRepository::save)
// pizzaRepository.save() devuelve Mono<Pizza>
// Resultado: Mono<Mono<Pizza>> 😱

// ✅ Con flatMap:
.flatMap(pizzaRepository::save)
// Resultado: Mono<Pizza> ✅
```

El método `save()` del repositorio reactivo devuelve `Mono<Pizza>`, por lo tanto necesitamos `flatMap` para evitar el anidamiento.

#### 5. `.map(pizza -> ResponseEntity...)`

**¿Por qué map y no flatMap?**

```java
.map(pizza -> ResponseEntity
    .status(HttpStatus.CREATED)
    .body(PizzaResponse.fromEntity(pizza)));
```

Aquí usamos `map` porque la transformación devuelve un `ResponseEntity<PizzaResponse>` (un valor simple), no un Mono o Flux.

---

## Analogía visual

```
map:      Mono<Pizza> → [Pizza] → String → Mono<String>
          "Abres la caja, transformas el contenido, vuelves a cerrar"

flatMap:  Mono<Pizza> → [Pizza] → Mono<String> → Mono<String>
          "Abres la caja, obtienes otra caja, las fusionas en una sola"
```

---

## Flujo de datos reactivo

```
Mono<PizzaRequest> 
  → flatMap → Pizza creada
  → (si hay ingredientes) → Flux<Ingredient> → agregar a Pizza
  → flatMap → guardar Pizza en BD
  → map → ResponseEntity<PizzaResponse>
```

---

## Consejos prácticos

### 1. Pregúntate: "¿Qué devuelve mi función?"

```java
// Devuelve un valor simple → map
pizza -> pizza.getName()

// Devuelve Mono/Flux → flatMap
id -> repository.findById(id)
```

### 2. Lee los tipos de retorno

```java
// Método del repositorio
Mono<Pizza> save(Pizza pizza);  // Devuelve Mono → usar flatMap

// Método de conversión
PizzaResponse fromEntity(Pizza pizza);  // Devuelve objeto → usar map
```

### 3. Al empezar, comenta tu código

```java
.flatMap(req -> {  // flatMap porque devuelvo Mono
    return Mono.just(pizza);
})
.flatMap(pizzaRepository::save)  // flatMap porque save() devuelve Mono
.map(PizzaResponse::fromEntity)  // map porque devuelve DTO simple
```

---

## Comparación: Código Reactivo vs Imperativo

### Versión Reactiva (Original)

```java
public Mono<ResponseEntity<PizzaResponse>> createPizza(Mono<PizzaRequest> request) {
    return request
        .flatMap(req -> {
            Pizza pizza = Pizza.create(
                UUID.randomUUID(),
                req.name(),
                req.description(),
                req.url()
            );
            
            // Agregar ingredientes si existen
            if (req.ingredientIds() != null && !req.ingredientIds().isEmpty()) {
                return ingredientRepository.findAllById(req.ingredientIds())
                    .doOnNext(pizza::addIngredient)
                    .then(Mono.just(pizza));
            }
            
            return Mono.just(pizza);
        })
        .flatMap(pizzaRepository::save)
        .map(pizza -> ResponseEntity
            .status(HttpStatus.CREATED)
            .body(PizzaResponse.fromEntity(pizza)));
}
```

### Versión Imperativa (Equivalente Bloqueante)

```java
public ResponseEntity<PizzaResponse> createPizza(PizzaRequest request) {
    // 1. Crear la pizza
    Pizza pizza = Pizza.create(
        UUID.randomUUID(),
        request.name(),
        request.description(),
        request.url()
    );
    
    // 2. Agregar ingredientes si existen
    if (request.ingredientIds() != null && !request.ingredientIds().isEmpty()) {
        List<Ingredient> ingredients = ingredientRepository
            .findAllById(request.ingredientIds());
        
        for (Ingredient ingredient : ingredients) {
            pizza.addIngredient(ingredient);
        }
    }
    
    // 3. Guardar en base de datos
    Pizza savedPizza = pizzaRepository.save(pizza);
    
    // 4. Crear respuesta HTTP
    PizzaResponse response = PizzaResponse.fromEntity(savedPizza);
    
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(response);
}
```

### Diferencias clave

| Aspecto | Imperativo | Reactivo |
|---------|-----------|----------|
| **Estilo** | Secuencial, paso a paso | Declarativo, cadena de operaciones |
| **Bloqueante** | Sí, espera cada operación | No, asíncrono y no bloqueante |
| **Escalabilidad** | Thread por petición | Event loop, más eficiente |
| **Legibilidad inicial** | Más natural e intuitivo | Curva de aprendizaje más pronunciada |
| **Control de flujo** | if/else, loops explícitos | Operadores reactivos |
| **Manejo de datos** | Valores directos (Pizza) | Envueltos en Mono/Flux |

---

## Conclusión

La programación reactiva requiere un cambio de mentalidad:

- **map**: transformación simple (valor → valor)
- **flatMap**: transformación que devuelve otro contenedor reactivo (valor → Mono/Flux)

Con práctica, distinguir entre `map` y `flatMap` se vuelve natural. La clave está en identificar el tipo de retorno de tu función de transformación.

---

## Recursos adicionales

- [Project Reactor Documentation](https://projectreactor.io/docs)
- [Reactive Programming with Spring Boot](https://spring.io/reactive)
- [Understanding Reactive Streams](https://www.reactive-streams.org/)

---

**Nota**: Este documento forma parte del material educativo del curso de programación reactiva con Java.
