# Laboratorio: Server-Sent Events con Spring WebFlux

## Introducción

En este laboratorio aprenderás a implementar comunicación en tiempo real utilizando Server-Sent Events (SSE) con Spring WebFlux. Desarrollarás un servidor reactivo que emite eventos y dos tipos de clientes: uno web basado en HTML/JavaScript con reconexión automática y otro cliente Java para consumir eventos desde aplicaciones backend.

## Requisitos previos

- JDK 17 o superior
- Maven o Gradle
- IDE (IntelliJ IDEA, Eclipse o VS Code)
- Navegador web moderno
- Conocimientos básicos de Spring Boot y programación reactiva

## Objetivos

Al finalizar este laboratorio serás capaz de:
- Implementar un servidor SSE con Spring WebFlux
- Crear endpoints que emitan eventos en tiempo real
- Desarrollar un cliente web con reconexión automática
- Consumir eventos SSE desde una aplicación Java
- Manejar diferentes tipos de eventos
- Implementar estrategias de reconexión y manejo de errores

---

## Parte 1: Configuración del proyecto

### Crear el proyecto Spring Boot

Crea un nuevo proyecto Spring Boot con las siguientes dependencias:

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-webflux</artifactId>
    </dependency>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

Si usas Gradle:

```gradle
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-webflux'
    compileOnly 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}
```

### Configuración de CORS

Crea una clase de configuración para permitir peticiones desde el cliente web:

```java
package com.example.sse.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
@EnableWebFlux
public class WebFluxConfig implements WebFluxConfigurer {
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .maxAge(3600);
    }
}
```

---

## Parte 2: Implementación del servidor SSE

### Modelo de datos

Crea las clases de modelo para los eventos:

```java
package com.example.sse.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEvent {
    private String id;
    private String type;
    private String message;
    private LocalDateTime timestamp;
    private Object data;
}
```

```java
package com.example.sse.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockPrice {
    private String symbol;
    private Double price;
    private Double change;
    private Double changePercent;
    private Long volume;
}
```

### Servicio de eventos

Crea un servicio que genere eventos de diferentes tipos:

```java
package com.example.sse.service;

import com.example.sse.model.NotificationEvent;
import com.example.sse.model.StockPrice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class EventService {
    
    private final Sinks.Many<NotificationEvent> notificationSink;
    private final Random random = new Random();
    private final AtomicInteger counter = new AtomicInteger(0);
    
    public EventService() {
        // Crear un sink multicast para compartir eventos entre múltiples suscriptores
        this.notificationSink = Sinks.many().multicast().onBackpressureBuffer();
    }
    
    /**
     * Stream simple de eventos con contador
     */
    public Flux<String> getSimpleEventStream() {
        return Flux.interval(Duration.ofSeconds(1))
                .map(sequence -> "Evento #" + sequence + " - " + LocalDateTime.now());
    }
    
    /**
     * Stream de notificaciones con diferentes tipos de eventos
     */
    public Flux<NotificationEvent> getNotificationStream() {
        return Flux.interval(Duration.ofSeconds(2))
                .map(sequence -> {
                    String[] types = {"INFO", "WARNING", "ERROR", "SUCCESS"};
                    String[] messages = {
                            "Nueva orden recibida",
                            "Actualización de inventario",
                            "Usuario conectado",
                            "Proceso completado",
                            "Alerta de sistema"
                    };
                    
                    String type = types[random.nextInt(types.length)];
                    String message = messages[random.nextInt(messages.length)];
                    
                    return NotificationEvent.builder()
                            .id(UUID.randomUUID().toString())
                            .type(type)
                            .message(message)
                            .timestamp(LocalDateTime.now())
                            .data(null)
                            .build();
                })
                .doOnNext(event -> log.info("Generando notificación: {}", event));
    }
    
    /**
     * Stream de precios de acciones simulados
     */
    public Flux<StockPrice> getStockPriceStream(String symbol) {
        double basePrice = 100.0 + random.nextDouble() * 400.0;
        
        return Flux.interval(Duration.ofMillis(1000))
                .scan(basePrice, (price, tick) -> {
                    // Simular cambios de precio aleatorios
                    double change = (random.nextDouble() - 0.5) * 5.0;
                    return Math.max(1.0, price + change);
                })
                .map(price -> {
                    double change = price - basePrice;
                    double changePercent = (change / basePrice) * 100;
                    
                    return StockPrice.builder()
                            .symbol(symbol.toUpperCase())
                            .price(Math.round(price * 100.0) / 100.0)
                            .change(Math.round(change * 100.0) / 100.0)
                            .changePercent(Math.round(changePercent * 100.0) / 100.0)
                            .volume(1000000L + random.nextInt(5000000))
                            .build();
                });
    }
    
    /**
     * Stream con heartbeat para mantener la conexión viva
     */
    public Flux<NotificationEvent> getStreamWithHeartbeat() {
        Flux<NotificationEvent> events = Flux.interval(Duration.ofSeconds(5))
                .map(i -> NotificationEvent.builder()
                        .id(UUID.randomUUID().toString())
                        .type("EVENT")
                        .message("Evento periódico #" + i)
                        .timestamp(LocalDateTime.now())
                        .build());
        
        Flux<NotificationEvent> heartbeats = Flux.interval(Duration.ofSeconds(15))
                .map(i -> NotificationEvent.builder()
                        .id("heartbeat-" + i)
                        .type("HEARTBEAT")
                        .message("ping")
                        .timestamp(LocalDateTime.now())
                        .build());
        
        return Flux.merge(events, heartbeats);
    }
    
    /**
     * Publicar una notificación manualmente
     */
    public void publishNotification(NotificationEvent notification) {
        notification.setId(UUID.randomUUID().toString());
        notification.setTimestamp(LocalDateTime.now());
        notificationSink.tryEmitNext(notification);
        log.info("Notificación publicada: {}", notification);
    }
    
    /**
     * Obtener el stream de notificaciones publicadas manualmente
     */
    public Flux<NotificationEvent> getPublishedNotifications() {
        return notificationSink.asFlux();
    }
    
    /**
     * Stream de progreso simulado
     */
    public Flux<Integer> getProgressStream() {
        return Flux.interval(Duration.ofMillis(500))
                .take(101)
                .map(Long::intValue)
                .doOnNext(progress -> log.info("Progreso: {}%", progress));
    }
}
```

### Controlador REST con endpoints SSE

```java
package com.example.sse.controller;

import com.example.sse.model.NotificationEvent;
import com.example.sse.model.StockPrice;
import com.example.sse.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.time.Duration;

@Slf4j
@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {
    
    private final EventService eventService;
    
    /**
     * Endpoint SSE simple
     */
    @GetMapping(value = "/simple", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> getSimpleEvents() {
        log.info("Cliente conectado al stream simple");
        return eventService.getSimpleEventStream()
                .doOnCancel(() -> log.info("Cliente desconectado del stream simple"));
    }
    
    /**
     * Endpoint SSE con ServerSentEvent para mayor control
     */
    @GetMapping(value = "/notifications", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<NotificationEvent>> getNotifications() {
        log.info("Cliente conectado al stream de notificaciones");
        
        return eventService.getNotificationStream()
                .map(notification -> ServerSentEvent.<NotificationEvent>builder()
                        .id(notification.getId())
                        .event(notification.getType().toLowerCase())
                        .data(notification)
                        .retry(Duration.ofSeconds(5))
                        .build())
                .doOnCancel(() -> log.info("Cliente desconectado del stream de notificaciones"));
    }
    
    /**
     * Endpoint SSE para precios de acciones
     */
    @GetMapping(value = "/stocks/{symbol}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<StockPrice>> getStockPrices(@PathVariable String symbol) {
        log.info("Cliente conectado al stream de precios para: {}", symbol);
        
        return eventService.getStockPriceStream(symbol)
                .map(price -> ServerSentEvent.<StockPrice>builder()
                        .event("stock-update")
                        .data(price)
                        .build())
                .doOnCancel(() -> log.info("Cliente desconectado del stream de precios"));
    }
    
    /**
     * Endpoint SSE con heartbeat
     */
    @GetMapping(value = "/heartbeat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<NotificationEvent>> getEventsWithHeartbeat() {
        log.info("Cliente conectado al stream con heartbeat");
        
        return eventService.getStreamWithHeartbeat()
                .map(event -> ServerSentEvent.<NotificationEvent>builder()
                        .id(event.getId())
                        .event(event.getType().toLowerCase())
                        .data(event)
                        .comment(event.getType().equals("HEARTBEAT") ? "keep-alive" : null)
                        .build())
                .doOnCancel(() -> log.info("Cliente desconectado del stream con heartbeat"));
    }
    
    /**
     * Endpoint para publicar notificaciones manualmente
     */
    @PostMapping("/publish")
    public void publishNotification(@RequestBody NotificationEvent notification) {
        log.info("Publicando notificación: {}", notification);
        eventService.publishNotification(notification);
    }
    
    /**
     * Endpoint SSE para notificaciones publicadas manualmente
     */
    @GetMapping(value = "/published", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<NotificationEvent>> getPublishedNotifications() {
        log.info("Cliente conectado al stream de notificaciones publicadas");
        
        return eventService.getPublishedNotifications()
                .map(notification -> ServerSentEvent.<NotificationEvent>builder()
                        .id(notification.getId())
                        .event("notification")
                        .data(notification)
                        .build())
                .doOnCancel(() -> log.info("Cliente desconectado del stream de notificaciones publicadas"));
    }
    
    /**
     * Endpoint SSE para progreso de tareas
     */
    @GetMapping(value = "/progress", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<Integer>> getProgress() {
        log.info("Cliente conectado al stream de progreso");
        
        return eventService.getProgressStream()
                .map(progress -> ServerSentEvent.<Integer>builder()
                        .event("progress")
                        .data(progress)
                        .build())
                .doOnComplete(() -> log.info("Stream de progreso completado"))
                .doOnCancel(() -> log.info("Cliente desconectado del stream de progreso"));
    }
}
```

### Clase principal

```java
package com.example.sse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SseApplication {
    public static void main(String[] args) {
        SpringApplication.run(SseApplication.class, args);
    }
}
```

### Configuración de la aplicación

Crea el archivo `application.yml`:

```yaml
spring:
  application:
    name: sse-server

server:
  port: 8080

logging:
  level:
    com.example.sse: DEBUG
    org.springframework.web: INFO
```

---

## Parte 3: Cliente HTML/JavaScript con reconexión

Crea un archivo `index.html` con el siguiente contenido:

```html
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>SSE Client - Demo</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            padding: 20px;
            min-height: 100vh;
        }

        .container {
            max-width: 1200px;
            margin: 0 auto;
        }

        h1 {
            color: white;
            text-align: center;
            margin-bottom: 30px;
            font-size: 2.5em;
            text-shadow: 2px 2px 4px rgba(0,0,0,0.2);
        }

        .controls {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 15px;
            margin-bottom: 30px;
        }

        button {
            padding: 12px 24px;
            font-size: 16px;
            border: none;
            border-radius: 8px;
            cursor: pointer;
            transition: all 0.3s ease;
            font-weight: 600;
            box-shadow: 0 4px 6px rgba(0,0,0,0.1);
        }

        .btn-connect {
            background-color: #4CAF50;
            color: white;
        }

        .btn-disconnect {
            background-color: #f44336;
            color: white;
        }

        .btn-clear {
            background-color: #FF9800;
            color: white;
        }

        button:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 12px rgba(0,0,0,0.15);
        }

        button:disabled {
            background-color: #cccccc;
            cursor: not-allowed;
            transform: none;
        }

        .status {
            background: white;
            padding: 15px;
            border-radius: 8px;
            margin-bottom: 20px;
            box-shadow: 0 4px 6px rgba(0,0,0,0.1);
        }

        .status-indicator {
            display: inline-block;
            width: 12px;
            height: 12px;
            border-radius: 50%;
            margin-right: 8px;
            animation: pulse 2s infinite;
        }

        .status-connected {
            background-color: #4CAF50;
        }

        .status-disconnected {
            background-color: #f44336;
        }

        .status-connecting {
            background-color: #FF9800;
        }

        @keyframes pulse {
            0%, 100% {
                opacity: 1;
            }
            50% {
                opacity: 0.5;
            }
        }

        .panels {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
            gap: 20px;
        }

        .panel {
            background: white;
            border-radius: 8px;
            padding: 20px;
            box-shadow: 0 4px 6px rgba(0,0,0,0.1);
        }

        .panel h2 {
            color: #667eea;
            margin-bottom: 15px;
            font-size: 1.5em;
            border-bottom: 2px solid #667eea;
            padding-bottom: 10px;
        }

        .events-list {
            max-height: 400px;
            overflow-y: auto;
            border: 1px solid #e0e0e0;
            border-radius: 4px;
            padding: 10px;
            background-color: #f9f9f9;
        }

        .event-item {
            padding: 10px;
            margin-bottom: 10px;
            border-radius: 4px;
            border-left: 4px solid #667eea;
            background-color: white;
            box-shadow: 0 2px 4px rgba(0,0,0,0.05);
            animation: slideIn 0.3s ease;
        }

        @keyframes slideIn {
            from {
                opacity: 0;
                transform: translateX(-20px);
            }
            to {
                opacity: 1;
                transform: translateX(0);
            }
        }

        .event-time {
            font-size: 0.85em;
            color: #666;
            margin-bottom: 5px;
        }

        .event-type {
            display: inline-block;
            padding: 3px 8px;
            border-radius: 3px;
            font-size: 0.85em;
            font-weight: bold;
            margin-bottom: 5px;
        }

        .event-type.info { background-color: #2196F3; color: white; }
        .event-type.warning { background-color: #FF9800; color: white; }
        .event-type.error { background-color: #f44336; color: white; }
        .event-type.success { background-color: #4CAF50; color: white; }
        .event-type.heartbeat { background-color: #9E9E9E; color: white; }

        .stock-card {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 20px;
            border-radius: 8px;
            margin-bottom: 10px;
        }

        .stock-symbol {
            font-size: 1.5em;
            font-weight: bold;
            margin-bottom: 10px;
        }

        .stock-price {
            font-size: 2em;
            font-weight: bold;
            margin-bottom: 5px;
        }

        .stock-change {
            font-size: 1.2em;
        }

        .stock-change.positive { color: #4CAF50; }
        .stock-change.negative { color: #f44336; }

        .progress-bar {
            width: 100%;
            height: 30px;
            background-color: #e0e0e0;
            border-radius: 15px;
            overflow: hidden;
            margin-top: 10px;
        }

        .progress-fill {
            height: 100%;
            background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
            transition: width 0.3s ease;
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-weight: bold;
        }

        .reconnect-info {
            background-color: #fff3cd;
            border: 1px solid #ffc107;
            color: #856404;
            padding: 10px;
            border-radius: 4px;
            margin-top: 10px;
            display: none;
        }

        .reconnect-info.show {
            display: block;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>🚀 Server-Sent Events Demo</h1>
        
        <div class="status">
            <span class="status-indicator status-disconnected" id="statusIndicator"></span>
            <span id="statusText">Desconectado</span>
            <span style="float: right;">Eventos recibidos: <strong id="eventCount">0</strong></span>
        </div>

        <div class="controls">
            <button class="btn-connect" id="btnConnectSimple">Conectar Simple</button>
            <button class="btn-connect" id="btnConnectNotifications">Conectar Notificaciones</button>
            <button class="btn-connect" id="btnConnectStocks">Conectar Acciones</button>
            <button class="btn-connect" id="btnConnectHeartbeat">Conectar Heartbeat</button>
            <button class="btn-connect" id="btnConnectProgress">Iniciar Progreso</button>
            <button class="btn-disconnect" id="btnDisconnect" disabled>Desconectar</button>
            <button class="btn-clear" id="btnClear">Limpiar Eventos</button>
        </div>

        <div class="reconnect-info" id="reconnectInfo">
            Intentando reconectar... Intento <span id="reconnectAttempt">0</span>
        </div>

        <div class="panels">
            <div class="panel">
                <h2>📋 Eventos Simples</h2>
                <div class="events-list" id="simpleEvents"></div>
            </div>

            <div class="panel">
                <h2>🔔 Notificaciones</h2>
                <div class="events-list" id="notificationEvents"></div>
            </div>

            <div class="panel">
                <h2>📈 Precios de Acciones</h2>
                <div id="stockPrices"></div>
            </div>

            <div class="panel">
                <h2>⏱️ Heartbeat</h2>
                <div class="events-list" id="heartbeatEvents"></div>
            </div>

            <div class="panel">
                <h2>📊 Progreso</h2>
                <div id="progressContainer">
                    <div class="progress-bar">
                        <div class="progress-fill" id="progressFill" style="width: 0%">0%</div>
                    </div>
                    <div style="margin-top: 10px; text-align: center;" id="progressStatus">
                        Listo para iniciar
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script>
        class SSEClient {
            constructor() {
                this.eventSource = null;
                this.eventCount = 0;
                this.reconnectAttempts = 0;
                this.maxReconnectAttempts = 5;
                this.reconnectDelay = 1000;
                this.currentEndpoint = null;
                this.isManualDisconnect = false;
                
                this.initializeButtons();
            }

            initializeButtons() {
                document.getElementById('btnConnectSimple').addEventListener('click', () => {
                    this.connect('/api/events/simple', 'simple');
                });

                document.getElementById('btnConnectNotifications').addEventListener('click', () => {
                    this.connect('/api/events/notifications', 'notifications');
                });

                document.getElementById('btnConnectStocks').addEventListener('click', () => {
                    this.connect('/api/events/stocks/AAPL', 'stocks');
                });

                document.getElementById('btnConnectHeartbeat').addEventListener('click', () => {
                    this.connect('/api/events/heartbeat', 'heartbeat');
                });

                document.getElementById('btnConnectProgress').addEventListener('click', () => {
                    this.connect('/api/events/progress', 'progress');
                });

                document.getElementById('btnDisconnect').addEventListener('click', () => {
                    this.disconnect();
                });

                document.getElementById('btnClear').addEventListener('click', () => {
                    this.clearEvents();
                });
            }

            connect(endpoint, type) {
                if (this.eventSource) {
                    this.disconnect();
                }

                this.isManualDisconnect = false;
                this.currentEndpoint = endpoint;
                this.currentType = type;
                this.reconnectAttempts = 0;

                this.connectToEndpoint(endpoint, type);
            }

            connectToEndpoint(endpoint, type) {
                const url = `http://localhost:8080${endpoint}`;
                console.log(`Conectando a: ${url}`);

                this.updateStatus('connecting', 'Conectando...');

                this.eventSource = new EventSource(url);

                this.eventSource.onopen = () => {
                    console.log('Conexión SSE establecida');
                    this.updateStatus('connected', 'Conectado');
                    this.reconnectAttempts = 0;
                    this.hideReconnectInfo();
                    document.getElementById('btnDisconnect').disabled = false;
                };

                this.eventSource.onmessage = (event) => {
                    console.log('Mensaje recibido:', event.data);
                    this.handleMessage(event, type);
                };

                this.eventSource.onerror = (error) => {
                    console.error('Error en SSE:', error);
                    
                    if (this.eventSource.readyState === EventSource.CLOSED) {
                        this.updateStatus('disconnected', 'Desconectado');
                        
                        if (!this.isManualDisconnect) {
                            this.attemptReconnect();
                        }
                    }
                };

                // Event listeners específicos para eventos personalizados
                if (type === 'notifications') {
                    ['info', 'warning', 'error', 'success'].forEach(eventType => {
                        this.eventSource.addEventListener(eventType, (event) => {
                            this.handleNotification(event, eventType);
                        });
                    });
                } else if (type === 'stocks') {
                    this.eventSource.addEventListener('stock-update', (event) => {
                        this.handleStockUpdate(event);
                    });
                } else if (type === 'heartbeat') {
                    this.eventSource.addEventListener('heartbeat', (event) => {
                        this.handleHeartbeat(event);
                    });
                    this.eventSource.addEventListener('event', (event) => {
                        this.handleHeartbeatEvent(event);
                    });
                } else if (type === 'progress') {
                    this.eventSource.addEventListener('progress', (event) => {
                        this.handleProgress(event);
                    });
                }
            }

            attemptReconnect() {
                if (this.reconnectAttempts >= this.maxReconnectAttempts) {
                    console.log('Máximo número de intentos de reconexión alcanzado');
                    this.updateStatus('disconnected', 'Desconectado - Reconexión fallida');
                    this.hideReconnectInfo();
                    return;
                }

                this.reconnectAttempts++;
                const delay = this.reconnectDelay * Math.pow(2, this.reconnectAttempts - 1);
                
                this.showReconnectInfo(this.reconnectAttempts);
                console.log(`Intentando reconectar en ${delay}ms (intento ${this.reconnectAttempts}/${this.maxReconnectAttempts})`);

                setTimeout(() => {
                    if (!this.isManualDisconnect && this.currentEndpoint) {
                        this.connectToEndpoint(this.currentEndpoint, this.currentType);
                    }
                }, delay);
            }

            handleMessage(event, type) {
                this.eventCount++;
                document.getElementById('eventCount').textContent = this.eventCount;

                if (type === 'simple') {
                    this.addSimpleEvent(event.data);
                }
            }

            handleNotification(event, type) {
                this.eventCount++;
                document.getElementById('eventCount').textContent = this.eventCount;

                const data = JSON.parse(event.data);
                this.addNotificationEvent(data, type);
            }

            handleStockUpdate(event) {
                this.eventCount++;
                document.getElementById('eventCount').textContent = this.eventCount;

                const data = JSON.parse(event.data);
                this.updateStockPrice(data);
            }

            handleHeartbeat(event) {
                const data = JSON.parse(event.data);
                this.addHeartbeatEvent(data, true);
            }

            handleHeartbeatEvent(event) {
                this.eventCount++;
                document.getElementById('eventCount').textContent = this.eventCount;

                const data = JSON.parse(event.data);
                this.addHeartbeatEvent(data, false);
            }

            handleProgress(event) {
                const progress = parseInt(event.data);
                this.updateProgress(progress);
            }

            addSimpleEvent(message) {
                const container = document.getElementById('simpleEvents');
                const eventDiv = document.createElement('div');
                eventDiv.className = 'event-item';
                eventDiv.innerHTML = `
                    <div class="event-time">${new Date().toLocaleTimeString()}</div>
                    <div>${message}</div>
                `;
                container.insertBefore(eventDiv, container.firstChild);
                
                // Limitar a 20 eventos
                while (container.children.length > 20) {
                    container.removeChild(container.lastChild);
                }
            }

            addNotificationEvent(data, type) {
                const container = document.getElementById('notificationEvents');
                const eventDiv = document.createElement('div');
                eventDiv.className = 'event-item';
                eventDiv.innerHTML = `
                    <div class="event-time">${new Date(data.timestamp).toLocaleString()}</div>
                    <span class="event-type ${type}">${type.toUpperCase()}</span>
                    <div style="margin-top: 5px;">${data.message}</div>
                    <div style="font-size: 0.8em; color: #999; margin-top: 5px;">ID: ${data.id}</div>
                `;
                container.insertBefore(eventDiv, container.firstChild);
                
                while (container.children.length > 20) {
                    container.removeChild(container.lastChild);
                }
            }

            updateStockPrice(data) {
                const container = document.getElementById('stockPrices');
                const isPositive = data.change >= 0;
                const changeClass = isPositive ? 'positive' : 'negative';
                const changeSymbol = isPositive ? '▲' : '▼';
                
                container.innerHTML = `
                    <div class="stock-card">
                        <div class="stock-symbol">${data.symbol}</div>
                        <div class="stock-price">$${data.price.toFixed(2)}</div>
                        <div class="stock-change ${changeClass}">
                            ${changeSymbol} ${data.change.toFixed(2)} (${data.changePercent.toFixed(2)}%)
                        </div>
                        <div style="margin-top: 10px; opacity: 0.8;">
                            Volumen: ${data.volume.toLocaleString()}
                        </div>
                        <div style="margin-top: 5px; font-size: 0.9em; opacity: 0.7;">
                            Actualizado: ${new Date().toLocaleTimeString()}
                        </div>
                    </div>
                `;
            }

            addHeartbeatEvent(data, isHeartbeat) {
                const container = document.getElementById('heartbeatEvents');
                const eventDiv = document.createElement('div');
                eventDiv.className = 'event-item';
                eventDiv.innerHTML = `
                    <div class="event-time">${new Date(data.timestamp).toLocaleString()}</div>
                    <span class="event-type ${isHeartbeat ? 'heartbeat' : 'info'}">
                        ${isHeartbeat ? 'HEARTBEAT' : 'EVENT'}
                    </span>
                    <div style="margin-top: 5px;">${data.message}</div>
                `;
                container.insertBefore(eventDiv, container.firstChild);
                
                while (container.children.length > 15) {
                    container.removeChild(container.lastChild);
                }
            }

            updateProgress(progress) {
                const fill = document.getElementById('progressFill');
                const status = document.getElementById('progressStatus');
                
                fill.style.width = `${progress}%`;
                fill.textContent = `${progress}%`;
                
                if (progress < 100) {
                    status.textContent = `Procesando... ${progress}%`;
                } else {
                    status.textContent = '✅ Completado!';
                    setTimeout(() => {
                        this.disconnect();
                    }, 2000);
                }
            }

            updateStatus(status, text) {
                const indicator = document.getElementById('statusIndicator');
                const statusText = document.getElementById('statusText');
                
                indicator.className = `status-indicator status-${status}`;
                statusText.textContent = text;
            }

            showReconnectInfo(attempt) {
                const info = document.getElementById('reconnectInfo');
                const attemptSpan = document.getElementById('reconnectAttempt');
                
                attemptSpan.textContent = `${attempt}/${this.maxReconnectAttempts}`;
                info.classList.add('show');
            }

            hideReconnectInfo() {
                const info = document.getElementById('reconnectInfo');
                info.classList.remove('show');
            }

            disconnect() {
                this.isManualDisconnect = true;
                
                if (this.eventSource) {
                    this.eventSource.close();
                    this.eventSource = null;
                }
                
                this.updateStatus('disconnected', 'Desconectado');
                this.hideReconnectInfo();
                document.getElementById('btnDisconnect').disabled = true;
                this.currentEndpoint = null;
                
                console.log('Desconectado del servidor SSE');
            }

            clearEvents() {
                document.getElementById('simpleEvents').innerHTML = '';
                document.getElementById('notificationEvents').innerHTML = '';
                document.getElementById('stockPrices').innerHTML = '';
                document.getElementById('heartbeatEvents').innerHTML = '';
                document.getElementById('progressFill').style.width = '0%';
                document.getElementById('progressFill').textContent = '0%';
                document.getElementById('progressStatus').textContent = 'Listo para iniciar';
                this.eventCount = 0;
                document.getElementById('eventCount').textContent = '0';
                
                console.log('Eventos limpiados');
            }
        }

        // Inicializar el cliente cuando se carga la página
        const client = new SSEClient();

        // Limpiar al cerrar la página
        window.addEventListener('beforeunload', () => {
            client.disconnect();
        });
    </script>
</body>
</html>
```

---

## Parte 4: Cliente Java para consumir eventos SSE

Crea un nuevo proyecto Java o un paquete adicional para el cliente:

### Dependencias adicionales para el cliente

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-webflux</artifactId>
</dependency>
<dependency>
    <groupId>io.projectreactor.netty</groupId>
    <artifactId>reactor-netty</artifactId>
</dependency>
```

### Cliente SSE con Spring WebClient

```java
package com.example.sse.client;

import com.example.sse.model.NotificationEvent;
import com.example.sse.model.StockPrice;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import java.time.Duration;

@Slf4j
public class SSEJavaClient {
    
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    
    public SSEJavaClient(String baseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.findAndRegisterModules();
    }
    
    /**
     * Conectar al stream simple de eventos
     */
    public Disposable connectToSimpleEvents() {
        log.info("Conectando al stream simple de eventos...");
        
        Flux<ServerSentEvent<String>> eventStream = webClient.get()
                .uri("/api/events/simple")
                .retrieve()
                .bodyToFlux(new ParameterizedTypeReference<ServerSentEvent<String>>() {});
        
        return eventStream
                .doOnNext(event -> {
                    log.info("Evento simple recibido: {}", event.data());
                })
                .doOnError(error -> {
                    log.error("Error en el stream: {}", error.getMessage());
                })
                .doOnComplete(() -> {
                    log.info("Stream simple completado");
                })
                .subscribe();
    }
    
    /**
     * Conectar al stream de notificaciones
     */
    public Disposable connectToNotifications() {
        log.info("Conectando al stream de notificaciones...");
        
        Flux<ServerSentEvent<NotificationEvent>> eventStream = webClient.get()
                .uri("/api/events/notifications")
                .retrieve()
                .bodyToFlux(new ParameterizedTypeReference<ServerSentEvent<NotificationEvent>>() {});
        
        return eventStream
                .doOnNext(event -> {
                    NotificationEvent notification = event.data();
                    log.info("Notificación recibida [{}]: {} - {}", 
                            event.event(), 
                            notification.getType(), 
                            notification.getMessage());
                })
                .doOnError(error -> {
                    log.error("Error en el stream de notificaciones: {}", error.getMessage());
                })
                .subscribe();
    }
    
    /**
     * Conectar al stream de precios de acciones
     */
    public Disposable connectToStockPrices(String symbol) {
        log.info("Conectando al stream de precios para: {}", symbol);
        
        Flux<ServerSentEvent<StockPrice>> eventStream = webClient.get()
                .uri("/api/events/stocks/{symbol}", symbol)
                .retrieve()
                .bodyToFlux(new ParameterizedTypeReference<ServerSentEvent<StockPrice>>() {});
        
        return eventStream
                .doOnNext(event -> {
                    StockPrice stock = event.data();
                    String trend = stock.getChange() >= 0 ? "▲" : "▼";
                    log.info("Precio de {}: ${} {} {} ({}%)", 
                            stock.getSymbol(),
                            stock.getPrice(),
                            trend,
                            stock.getChange(),
                            stock.getChangePercent());
                })
                .doOnError(error -> {
                    log.error("Error en el stream de precios: {}", error.getMessage());
                })
                .subscribe();
    }
    
    /**
     * Conectar al stream con heartbeat
     */
    public Disposable connectToHeartbeat() {
        log.info("Conectando al stream con heartbeat...");
        
        Flux<ServerSentEvent<NotificationEvent>> eventStream = webClient.get()
                .uri("/api/events/heartbeat")
                .retrieve()
                .bodyToFlux(new ParameterizedTypeReference<ServerSentEvent<NotificationEvent>>() {});
        
        return eventStream
                .filter(event -> !"HEARTBEAT".equals(event.data().getType()))
                .doOnNext(event -> {
                    NotificationEvent notification = event.data();
                    log.info("Evento recibido: {}", notification.getMessage());
                })
                .doOnError(error -> {
                    log.error("Error en el stream con heartbeat: {}", error.getMessage());
                })
                .subscribe();
    }
    
    /**
     * Conectar al stream de progreso
     */
    public Disposable connectToProgress() {
        log.info("Conectando al stream de progreso...");
        
        Flux<ServerSentEvent<Integer>> eventStream = webClient.get()
                .uri("/api/events/progress")
                .retrieve()
                .bodyToFlux(new ParameterizedTypeReference<ServerSentEvent<Integer>>() {});
        
        return eventStream
                .doOnNext(event -> {
                    Integer progress = event.data();
                    if (progress % 10 == 0) {
                        log.info("Progreso: {}%", progress);
                    }
                })
                .doOnComplete(() -> {
                    log.info("Progreso completado!");
                })
                .doOnError(error -> {
                    log.error("Error en el stream de progreso: {}", error.getMessage());
                })
                .subscribe();
    }
    
    /**
     * Conectar con estrategia de reconexión personalizada
     */
    public Disposable connectWithRetry(String uri, int maxRetries) {
        log.info("Conectando con reconexión a: {}", uri);
        
        return webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToFlux(new ParameterizedTypeReference<ServerSentEvent<String>>() {})
                .retryWhen(reactor.util.retry.Retry.backoff(maxRetries, Duration.ofSeconds(2))
                        .doBeforeRetry(signal -> {
                            log.warn("Reintentando conexión, intento: {}", signal.totalRetries() + 1);
                        }))
                .doOnNext(event -> {
                    log.info("Evento recibido: {}", event.data());
                })
                .doOnError(error -> {
                    log.error("Error después de {} intentos: {}", maxRetries, error.getMessage());
                })
                .subscribe();
    }
}
```

### Aplicación de ejemplo del cliente

```java
package com.example.sse.client;

import lombok.extern.slf4j.Slf4j;
import reactor.core.Disposable;

import java.util.Scanner;

@Slf4j
public class SSEClientApp {
    
    public static void main(String[] args) {
        SSEJavaClient client = new SSEJavaClient("http://localhost:8080");
        Scanner scanner = new Scanner(System.in);
        Disposable currentSubscription = null;
        
        log.info("=== Cliente SSE Java ===");
        log.info("Comandos disponibles:");
        log.info("1 - Conectar a eventos simples");
        log.info("2 - Conectar a notificaciones");
        log.info("3 - Conectar a precios de acciones");
        log.info("4 - Conectar con heartbeat");
        log.info("5 - Conectar a progreso");
        log.info("6 - Desconectar");
        log.info("0 - Salir");
        log.info("========================");
        
        boolean running = true;
        while (running) {
            System.out.print("\nIngresa un comando: ");
            String input = scanner.nextLine().trim();
            
            try {
                switch (input) {
                    case "1":
                        if (currentSubscription != null) {
                            currentSubscription.dispose();
                        }
                        currentSubscription = client.connectToSimpleEvents();
                        log.info("Conectado al stream simple");
                        break;
                        
                    case "2":
                        if (currentSubscription != null) {
                            currentSubscription.dispose();
                        }
                        currentSubscription = client.connectToNotifications();
                        log.info("Conectado al stream de notificaciones");
                        break;
                        
                    case "3":
                        if (currentSubscription != null) {
                            currentSubscription.dispose();
                        }
                        System.out.print("Ingresa el símbolo de la acción (ej: AAPL): ");
                        String symbol = scanner.nextLine().trim();
                        currentSubscription = client.connectToStockPrices(symbol);
                        log.info("Conectado al stream de precios");
                        break;
                        
                    case "4":
                        if (currentSubscription != null) {
                            currentSubscription.dispose();
                        }
                        currentSubscription = client.connectToHeartbeat();
                        log.info("Conectado al stream con heartbeat");
                        break;
                        
                    case "5":
                        if (currentSubscription != null) {
                            currentSubscription.dispose();
                        }
                        currentSubscription = client.connectToProgress();
                        log.info("Conectado al stream de progreso");
                        break;
                        
                    case "6":
                        if (currentSubscription != null) {
                            currentSubscription.dispose();
                            currentSubscription = null;
                            log.info("Desconectado del stream");
                        } else {
                            log.info("No hay conexión activa");
                        }
                        break;
                        
                    case "0":
                        running = false;
                        if (currentSubscription != null) {
                            currentSubscription.dispose();
                        }
                        log.info("Saliendo...");
                        break;
                        
                    default:
                        log.warn("Comando no reconocido: {}", input);
                }
            } catch (Exception e) {
                log.error("Error ejecutando comando: {}", e.getMessage());
            }
        }
        
        scanner.close();
        System.exit(0);
    }
}
```

---

## Tareas

### Ejercicio 1: Implementar el servidor básico
1. Crea el proyecto Spring Boot con las dependencias necesarias
2. Implementa los modelos de datos
3. Implementa el servicio de eventos con al menos 3 streams diferentes
4. Crea el controlador con los endpoints SSE

### Ejercicio 2: Probar con el cliente HTML
1. Ejecuta el servidor Spring Boot
2. Abre el archivo HTML en tu navegador
3. Prueba cada uno de los botones de conexión
4. Observa los eventos en tiempo real
5. Desconecta el servidor y observa la reconexión automática

### Ejercicio 3: Implementar el cliente Java
1. Crea el cliente SSE usando WebClient
2. Implementa la aplicación de consola interactiva
3. Prueba conectarte a diferentes endpoints
4. Verifica que recibes los eventos correctamente

### Ejercicio 4: Mejorar la reconexión del cliente HTML
Modifica el cliente HTML para:
- Implementar exponential backoff más sofisticado
- Almacenar el último ID de evento recibido
- Recuperar eventos perdidos después de reconectar
- Mostrar mensajes de error más descriptivos

### Ejercicio 5: Implementar un endpoint personalizado
Crea un nuevo endpoint SSE que:
- Emita eventos de log de una aplicación simulada
- Incluya diferentes niveles de severidad (DEBUG, INFO, WARN, ERROR)
- Permita filtrar por nivel de severidad mediante query params
- Incluya timestamps y mensajes contextuales

---

## Preguntas de reflexión

1. **¿Cuándo es apropiado usar SSE en lugar de polling o WebSockets?** Reflexiona sobre las ventajas y desventajas de cada enfoque según el caso de uso.

2. **¿Cómo manejarías la escalabilidad** en un sistema con miles de clientes conectados simultáneamente a streams SSE?

3. **¿Qué consideraciones de seguridad** debes tener en cuenta al implementar SSE en producción?

4. **¿Cómo implementarías autenticación** en los endpoints SSE si solo ciertos usuarios pueden acceder a ciertos streams?

5. **¿Qué estrategia usarías** para mantener consistencia de datos cuando un cliente se reconecta después de estar desconectado?

---

## Desafío adicional: Integración completa

Implementa un sistema de notificaciones completo que incluya:

1. **Backend con múltiples streams**:
   - Stream de notificaciones globales
   - Stream de notificaciones por usuario
   - Stream de métricas del sistema

2. **Persistencia**:
   - Almacena las notificaciones en una base de datos
   - Permite recuperar notificaciones perdidas

3. **Cliente web mejorado**:
   - Panel de administración para publicar notificaciones
   - Visualización de métricas en tiempo real
   - Sistema de filtrado y búsqueda de eventos históricos

4. **Monitorización**:
   - Contador de clientes conectados
   - Métricas de eventos publicados/consumidos
   - Detección de clientes inactivos

---

## Recursos adicionales

### Documentación
- [Spring WebFlux Reference](https://docs.spring.io/spring-framework/reference/web/webflux.html)
- [MDN: Using Server-Sent Events](https://developer.mozilla.org/en-US/docs/Web/API/Server-sent_events/Using_server-sent_events)
- [EventSource API](https://developer.mozilla.org/en-US/docs/Web/API/EventSource)

### Artículos recomendados
- [Server-Sent Events with Spring WebFlux](https://www.baeldung.com/spring-server-sent-events)
- [Building Real-Time Applications with SSE](https://www.toptal.com/java/server-sent-events-using-spring)
- [SSE vs WebSockets: When to use each](https://ably.com/topic/websockets-vs-sse)

### Herramientas útiles
- [Postman](https://www.postman.com/) - Para probar endpoints SSE
- [EventSource Browser Extension](https://github.com/Yaffle/EventSource) - Polyfill para navegadores antiguos
- [HTTPie](https://httpie.io/) - Cliente HTTP de línea de comandos que soporta SSE
