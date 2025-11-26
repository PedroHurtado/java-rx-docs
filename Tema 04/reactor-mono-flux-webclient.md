# Mono y Flux (Project Reactor)

## Conceptos Básicos

**Flux**: Publisher reactivo que emite de **0 a N elementos**. Representa un stream de datos asíncrono que puede emitir múltiples valores a lo largo del tiempo.

**Mono**: Publisher reactivo que emite de **0 a 1 elemento**. Útil para operaciones que devuelven un único resultado o vacío (equivalente a `Optional` o `CompletableFuture`).

---

## ⚠️ Métodos BLOQUEANTES

Estos métodos **rompen la reactividad** y nunca deben usarse en código productivo reactivo:

### Mono
- **`block()`** - Bloquea el thread actual hasta obtener el resultado o timeout
- **`block(Duration timeout)`** - Igual que block() pero con timeout configurable

### Flux
- **`blockFirst()`** - Bloquea hasta obtener el primer elemento del stream
- **`blockFirst(Duration timeout)`** - Bloquea hasta el primer elemento con timeout
- **`blockLast()`** - Bloquea hasta obtener el último elemento del stream
- **`blockLast(Duration timeout)`** - Bloquea hasta el último elemento con timeout
- **`toIterable()`** - Convierte el Flux a una colección bloqueando
- **`toStream()`** - Convierte el Flux a un Stream de Java bloqueando

### ⚠️ Advertencia
**NUNCA uses estos métodos en código reactivo real**. Solo son apropiados para:
- Tests unitarios
- Debugging
- Código legacy que necesita interoperar con código bloqueante

Usar estos métodos destruye el modelo asíncrono no-bloqueante y anula los beneficios de la programación reactiva.

---

## WebClient en Spring Reactive Web

### ¿Qué es WebClient?

**WebClient** es el cliente HTTP reactivo de Spring Framework que permite realizar peticiones HTTP de forma completamente no-bloqueante.

### Características Principales

✅ **Reemplaza a RestTemplate** - RestTemplate es bloqueante, WebClient es reactivo  
✅ **Devuelve Mono/Flux** - Mantiene la cadena reactiva sin interrupciones  
✅ **Non-blocking I/O** - Permite llamadas HTTP asíncronas end-to-end  
✅ **Integración perfecta** - Se integra nativamente con WebFlux controllers  
✅ **Alta concurrencia** - Maneja miles de peticiones con pocos threads  

### Papel en Spring Reactive Web

WebClient es **fundamental** en arquitecturas reactivas porque:

1. **Mantiene el flujo reactivo completo** - Desde el controller hasta llamadas a servicios externos
2. **Evita bloqueos de threads** - Las peticiones HTTP no bloquean el event loop
3. **Permite composición reactiva** - Se puede combinar con otros Mono/Flux usando operadores
4. **Backpressure** - Respeta el control de flujo reactivo

### Ejemplo Práctico

```java
@Service
public class UserService {
    
    private final WebClient webClient;
    
    public UserService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
            .baseUrl("https://api.example.com")
            .build();
    }
    
    // Obtener un usuario (Mono)
    public Mono<User> getUser(String id) {
        return webClient.get()
            .uri("/api/users/{id}", id)
            .retrieve()
            .bodyToMono(User.class); // Devuelve Mono<User>
    }
    
    // Obtener lista de usuarios (Flux)
    public Flux<User> getAllUsers() {
        return webClient.get()
            .uri("/api/users")
            .retrieve()
            .bodyToFlux(User.class); // Devuelve Flux<User>
    }
    
    // Composición reactiva
    public Mono<UserProfile> getUserWithProfile(String id) {
        return webClient.get()
            .uri("/api/users/{id}", id)
            .retrieve()
            .bodyToMono(User.class)
            .flatMap(user -> 
                webClient.get()
                    .uri("/api/profiles/{profileId}", user.getProfileId())
                    .retrieve()
                    .bodyToMono(Profile.class)
                    .map(profile -> new UserProfile(user, profile))
            );
    }
}
```

### Comparación: RestTemplate vs WebClient

| Aspecto | RestTemplate | WebClient |
|---------|--------------|-----------|
| Modelo | Bloqueante/Síncrono | No-bloqueante/Asíncrono |
| Tipo de retorno | Objetos directos | Mono/Flux |
| Threads | Un thread por request | Pocos threads, muchos requests |
| Estado | Maintenance mode | Recomendado/Activo |
| Uso con WebFlux | ❌ Incompatible | ✅ Nativo |

---

## Resumen Clave

- **Mono y Flux** son los pilares de Project Reactor para programación reactiva
- **Métodos bloqueantes** como `block()`, `blockFirst()`, etc. destruyen la reactividad
- **WebClient** es el cliente HTTP que mantiene todo el flujo reactivo sin bloqueos
- La combinación de **WebFlux + WebClient** permite crear aplicaciones completamente reactivas con alta concurrencia y eficiencia
