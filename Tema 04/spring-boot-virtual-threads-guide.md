# Spring Boot con Virtual Threads - Guía Completa

## Configuración Básica (Spring Boot 3.2+)

### 1. Habilitar Virtual Threads en `application.properties`

```properties
# Habilita Virtual Threads para todas las peticiones web
spring.threads.virtual.enabled=true
```

**Eso es todo.** Con esta línea, Spring Boot automáticamente usa Virtual Threads para manejar peticiones HTTP.

### 2. Dependencias Maven (pom.xml)

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.3.0</version> <!-- Spring Boot 3.2+ -->
</parent>

<properties>
    <java.version>21</java.version>
</properties>

<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <!-- JDBC con soporte para Virtual Threads -->
    <dependency>
        <groupId>com.zaxxer</groupId>
        <artifactId>HikariCP</artifactId>
    </dependency>
</dependencies>
```

## Arquitectura: ¿Dónde Necesitas Virtual Threads?

### ❌ NO necesitas cambiar NADA en tu código

Con `spring.threads.virtual.enabled=true`, **Spring automáticamente ejecuta cada petición HTTP en un Virtual Thread**. 

Puedes escribir código bloqueante tradicional en **todas las capas**:

```
Controller (Virtual Thread automático)
    ↓ (llamada normal)
Service (mismo Virtual Thread)
    ↓ (llamada normal)
Repository (mismo Virtual Thread)
    ↓ (llamada bloqueante JDBC)
Database
```

## Ejemplo Completo - Aplicación Real

### Estructura del Proyecto

```
src/main/java/com/example/demo/
├── DemoApplication.java
├── controller/
│   └── UserController.java
├── service/
│   └── UserService.java
├── repository/
│   └── UserRepository.java
└── model/
    └── User.java
```

### 1. Application.java

```java
package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
```

### 2. Model (User.java)

```java
package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String email;
    
    // Constructors, getters, setters
    public User() {}
    
    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }
    
    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
```

### 3. Repository (UserRepository.java)

```java
package com.example.demo.repository;

import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Métodos bloqueantes tradicionales - ¡funcionan perfecto con Virtual Threads!
    User findByEmail(String email);
}
```

### 4. Service (UserService.java)

```java
package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Service
public class UserService {
    
    private final UserRepository userRepository;
    private final HttpClient httpClient;
    
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        // HttpClient es compatible con Virtual Threads
        this.httpClient = HttpClient.newHttpClient();
    }
    
    // Método completamente bloqueante - pero eficiente con Virtual Threads
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll(); // Llamada bloqueante a DB
    }
    
    @Transactional
    public User createUser(User user) {
        return userRepository.save(user); // Llamada bloqueante a DB
    }
    
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }
    
    // Ejemplo: llamada a API externa + DB (todo bloqueante, todo eficiente)
    @Transactional
    public User getUserWithExternalData(Long id) {
        // 1. Consulta a DB (bloqueante)
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // 2. Llamada a API externa (bloqueante)
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.example.com/data/" + id))
                .GET()
                .build();
            
            // Bloqueante, pero en Virtual Thread no es problema
            HttpResponse<String> response = httpClient.send(
                request, 
                HttpResponse.BodyHandlers.ofString()
            );
            
            System.out.println("External data: " + response.body());
        } catch (Exception e) {
            System.err.println("Error calling external API: " + e.getMessage());
        }
        
        return user;
    }
    
    // Ejemplo: procesamiento pesado simulado
    public String processHeavyTask(Long userId) {
        try {
            // Simulamos operación pesada (I/O, cálculo, etc.)
            Thread.sleep(2000); // Bloqueante - ¡pero OK con Virtual Threads!
            
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            return "Processed: " + user.getName();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Task interrupted", e);
        }
    }
}
```

### 5. Controller (UserController.java)

```java
package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    // ✅ Código completamente síncrono y bloqueante
    // Spring automáticamente lo ejecuta en Virtual Thread
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }
    
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User created = userService.createUser(user);
        return ResponseEntity.ok(created);
    }
    
    // Endpoint que hace llamada externa + DB
    @GetMapping("/{id}/external")
    public ResponseEntity<User> getUserWithExternal(@PathVariable Long id) {
        User user = userService.getUserWithExternalData(id);
        return ResponseEntity.ok(user);
    }
    
    // Endpoint con operación pesada simulada
    @GetMapping("/{id}/process")
    public ResponseEntity<String> processUser(@PathVariable Long id) {
        String result = userService.processHeavyTask(id);
        return ResponseEntity.ok(result);
    }
}
```

### 6. Configuration (application.properties)

```properties
# Habilitar Virtual Threads
spring.threads.virtual.enabled=true

# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/mydb
spring.datasource.username=user
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# HikariCP (pool de conexiones compatible con Virtual Threads)
spring.datasource.hikari.maximum-pool-size=50
```

## ¿Qué Pasa Internamente?

### Sin Virtual Threads (tradicional):
```
Thread Pool (200 threads) → Petición HTTP → Bloquea thread → Espera DB/API
                             ↓
                        Thread ocupado y bloqueado
```

### Con Virtual Threads:
```
Virtual Thread (millones posibles) → Petición HTTP → "Espera" DB/API
                                     ↓
                        OS Thread liberado, Virtual Thread "aparcado"
                        ↓
                        Cuando DB responde → Virtual Thread se "despierta"
```

## Ventajas de Este Enfoque

### ✅ Código Simple
- No necesitas `CompletableFuture`, `Mono`, `Flux`, callbacks
- Escribes código bloqueante tradicional
- Stack traces legibles y debugging normal

### ✅ Sin Cambios en el Código
```java
// Antes (Platform Threads)
@GetMapping("/users")
public List<User> getUsers() {
    return userService.findAll(); // Bloqueaba un thread del pool
}

// Después (Virtual Threads) - ¡EL MISMO CÓDIGO!
@GetMapping("/users")
public List<User> getUsers() {
    return userService.findAll(); // Ahora es eficiente
}
```

### ✅ Escalabilidad Masiva
- Puedes manejar 10,000+ peticiones concurrentes
- Cada petición en su propio Virtual Thread
- No te quedas sin threads

## Casos Especiales

### 1. Si Necesitas Ejecutar Tareas en Paralelo

```java
@GetMapping("/parallel-example")
public ResponseEntity<Map<String, Object>> getParallelData() {
    try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
        
        var futureUsers = executor.submit(() -> userService.getAllUsers());
        var futureExternal = executor.submit(() -> callExternalAPI());
        var futureStats = executor.submit(() -> calculateStats());
        
        Map<String, Object> result = Map.of(
            "users", futureUsers.get(),
            "external", futureExternal.get(),
            "stats", futureStats.get()
        );
        
        return ResponseEntity.ok(result);
    } catch (Exception e) {
        return ResponseEntity.internalServerError().build();
    }
}
```

### 2. Tareas Asíncronas en Background

```java
@Service
public class BackgroundTaskService {
    
    @Async // Spring usa Virtual Threads si está habilitado
    public void processInBackground(Long userId) {
        // Tarea larga que no bloquea la respuesta HTTP
        try {
            Thread.sleep(5000);
            System.out.println("Background task completed for user: " + userId);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
```

Configuración para `@Async`:

```java
@Configuration
@EnableAsync
public class AsyncConfig {
    
    @Bean
    public Executor taskExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }
}
```

## Testing

```java
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void testGetAllUsers() throws Exception {
        mockMvc.perform(get("/api/users"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}
```

Los tests funcionan igual, no necesitas cambios.

## Migración Gradual

Si tienes código existente con WebFlux (Reactor):

```java
// Código antiguo con WebFlux
@GetMapping("/users")
public Mono<List<User>> getUsers() {
    return userRepository.findAll().collectList();
}

// Migración a Virtual Threads
@GetMapping("/users")
public List<User> getUsers() {
    return userRepository.findAll(); // JPA tradicional
}
```

## Checklist de Implementación

- [ ] Java 21+
- [ ] Spring Boot 3.2+
- [ ] `spring.threads.virtual.enabled=true` en properties
- [ ] HikariCP para pool de conexiones (viene por defecto)
- [ ] **NO cambiar** código existente - funciona automáticamente
- [ ] Usar `HttpClient` de Java (no RestTemplate antigua)

## Conclusión

Con Virtual Threads en Spring Boot:

1. **Activas una propiedad** y todo funciona automáticamente
2. **Escribes código bloqueante tradicional** (más simple)
3. **Obtienes escalabilidad** sin complejidad
4. **No necesitas WebFlux, RxJava, callbacks, CompletableFuture** para I/O

Es la forma más simple y moderna de construir APIs REST escalables en Java.
