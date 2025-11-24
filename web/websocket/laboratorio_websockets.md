# Laboratorio: WebSockets con Spring WebFlux

## Objetivo
Implementar una comunicación bidireccional en tiempo real usando WebSockets con Spring WebFlux en el servidor y JavaScript en el cliente.

## Requisitos previos
- Java 17+
- Spring Boot 3.x
- Conocimientos básicos de JavaScript
- Maven o Gradle

## Parte 1: Configuración del Proyecto

### Dependencias Maven

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-webflux</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-websocket</artifactId>
    </dependency>
    <dependency>
        <groupId>io.projectreactor</groupId>
        <artifactId>reactor-core</artifactId>
    </dependency>
</dependencies>
```

## Parte 2: Servidor Spring WebFlux

### 2.1 Configuración WebSocket

```java
package com.ejemplo.websocket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class WebSocketConfig {

    @Bean
    public HandlerMapping webSocketHandlerMapping(ChatWebSocketHandler handler) {
        Map<String, WebSocketHandler> map = new HashMap<>();
        map.put("/chat", handler);
        
        SimpleUrlHandlerMapping handlerMapping = new SimpleUrlHandlerMapping();
        handlerMapping.setOrder(1);
        handlerMapping.setUrlMap(map);
        return handlerMapping;
    }

    @Bean
    public WebSocketHandlerAdapter handlerAdapter() {
        return new WebSocketHandlerAdapter();
    }
}
```

### 2.2 Handler WebSocket Reactivo

```java
package com.ejemplo.websocket.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.Duration;

@Component
public class ChatWebSocketHandler implements WebSocketHandler {

    private final Sinks.Many<String> messageSink = Sinks.many().multicast().onBackpressureBuffer();

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        // Recibir mensajes del cliente
        Mono<Void> input = session.receive()
                .map(msg -> msg.getPayloadAsText())
                .doOnNext(message -> {
                    System.out.println("Mensaje recibido: " + message);
                    messageSink.tryEmitNext(message);
                })
                .then();

        // Enviar mensajes al cliente
        Flux<String> output = messageSink.asFlux()
                .map(msg -> "Echo: " + msg);

        return session.send(
                output.map(session::textMessage)
        ).and(input);
    }
}
```

### 2.3 Configuración CORS

```java
package com.ejemplo.websocket.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
@EnableWebFlux
public class CorsConfig implements WebFluxConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("*")
                .allowedHeaders("*");
    }
}
```

### 2.4 Clase Principal

```java
package com.ejemplo.websocket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WebSocketApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebSocketApplication.class, args);
    }
}
```

## Parte 3: Cliente JavaScript

### 3.1 HTML Base

```html
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>WebSocket Chat</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            max-width: 800px;
            margin: 50px auto;
            padding: 20px;
        }
        #messages {
            border: 1px solid #ccc;
            height: 400px;
            overflow-y: auto;
            padding: 10px;
            margin-bottom: 20px;
            background-color: #f9f9f9;
        }
        .message {
            padding: 5px;
            margin: 5px 0;
            border-radius: 4px;
        }
        .sent {
            background-color: #e3f2fd;
            text-align: right;
        }
        .received {
            background-color: #f1f8e9;
        }
        #inputArea {
            display: flex;
            gap: 10px;
        }
        #messageInput {
            flex: 1;
            padding: 10px;
            font-size: 16px;
        }
        button {
            padding: 10px 20px;
            font-size: 16px;
            cursor: pointer;
            background-color: #4CAF50;
            color: white;
            border: none;
            border-radius: 4px;
        }
        button:hover {
            background-color: #45a049;
        }
        button:disabled {
            background-color: #cccccc;
            cursor: not-allowed;
        }
        #status {
            padding: 10px;
            margin-bottom: 10px;
            border-radius: 4px;
            font-weight: bold;
        }
        .connected {
            background-color: #c8e6c9;
            color: #2e7d32;
        }
        .disconnected {
            background-color: #ffcdd2;
            color: #c62828;
        }
    </style>
</head>
<body>
    <h1>WebSocket Chat en Tiempo Real</h1>
    
    <div id="status" class="disconnected">Desconectado</div>
    
    <div id="messages"></div>
    
    <div id="inputArea">
        <input type="text" id="messageInput" placeholder="Escribe un mensaje..." disabled>
        <button id="sendButton" disabled>Enviar</button>
        <button id="connectButton">Conectar</button>
    </div>

    <script src="websocket-client.js"></script>
</body>
</html>
```

### 3.2 Cliente WebSocket JavaScript

```javascript
// websocket-client.js
class WebSocketClient {
    constructor() {
        this.ws = null;
        this.messagesDiv = document.getElementById('messages');
        this.messageInput = document.getElementById('messageInput');
        this.sendButton = document.getElementById('sendButton');
        this.connectButton = document.getElementById('connectButton');
        this.statusDiv = document.getElementById('status');
        
        this.setupEventListeners();
    }

    setupEventListeners() {
        this.sendButton.addEventListener('click', () => this.sendMessage());
        this.messageInput.addEventListener('keypress', (e) => {
            if (e.key === 'Enter') this.sendMessage();
        });
        this.connectButton.addEventListener('click', () => this.toggleConnection());
    }

    toggleConnection() {
        if (this.ws && this.ws.readyState === WebSocket.OPEN) {
            this.disconnect();
        } else {
            this.connect();
        }
    }

    connect() {
        const wsUrl = 'ws://localhost:8080/chat';
        this.ws = new WebSocket(wsUrl);

        this.ws.onopen = () => {
            console.log('Conectado al servidor WebSocket');
            this.updateStatus(true);
            this.addSystemMessage('Conectado al servidor');
        };

        this.ws.onmessage = (event) => {
            console.log('Mensaje recibido:', event.data);
            this.addMessage(event.data, 'received');
        };

        this.ws.onerror = (error) => {
            console.error('Error WebSocket:', error);
            this.addSystemMessage('Error de conexión');
        };

        this.ws.onclose = () => {
            console.log('Desconectado del servidor WebSocket');
            this.updateStatus(false);
            this.addSystemMessage('Desconectado del servidor');
        };
    }

    disconnect() {
        if (this.ws) {
            this.ws.close();
            this.ws = null;
        }
    }

    sendMessage() {
        const message = this.messageInput.value.trim();
        
        if (message && this.ws && this.ws.readyState === WebSocket.OPEN) {
            this.ws.send(message);
            this.addMessage(message, 'sent');
            this.messageInput.value = '';
        }
    }

    addMessage(text, type) {
        const messageDiv = document.createElement('div');
        messageDiv.className = `message ${type}`;
        messageDiv.textContent = text;
        this.messagesDiv.appendChild(messageDiv);
        this.messagesDiv.scrollTop = this.messagesDiv.scrollHeight;
    }

    addSystemMessage(text) {
        const messageDiv = document.createElement('div');
        messageDiv.className = 'message';
        messageDiv.style.fontStyle = 'italic';
        messageDiv.style.color = '#666';
        messageDiv.textContent = `[Sistema] ${text}`;
        this.messagesDiv.appendChild(messageDiv);
        this.messagesDiv.scrollTop = this.messagesDiv.scrollHeight;
    }

    updateStatus(connected) {
        if (connected) {
            this.statusDiv.textContent = 'Conectado';
            this.statusDiv.className = 'connected';
            this.messageInput.disabled = false;
            this.sendButton.disabled = false;
            this.connectButton.textContent = 'Desconectar';
        } else {
            this.statusDiv.textContent = 'Desconectado';
            this.statusDiv.className = 'disconnected';
            this.messageInput.disabled = true;
            this.sendButton.disabled = true;
            this.connectButton.textContent = 'Conectar';
        }
    }
}

// Inicializar cliente al cargar la página
document.addEventListener('DOMContentLoaded', () => {
    new WebSocketClient();
});
```

## Parte 4: Ejecución y Pruebas

### 4.1 Ejecutar el servidor

```bash
mvn spring-boot:run
```

### 4.2 Servir el cliente HTML

**Opción A: Usando http-server (Node.js)**
```bash
# Instalar si no lo tienes
npm install -g http-server

# En la carpeta donde está el index.html
http-server -p 3000
```

**Opción B: Usando Python**
```bash
# Python 3
python -m http.server 3000
```

**Opción C: Desde Spring Boot**
Coloca los archivos HTML y JS en `src/main/resources/static/` y accede a `http://localhost:8080`

### 4.3 Abrir el cliente

1. Accede a `http://localhost:3000` (si usas http-server o Python)
2. O a `http://localhost:8080` (si sirves desde Spring Boot)
3. Haz clic en "Conectar"
4. Envía mensajes y observa cómo el servidor responde con "Echo: [tu mensaje]"

**Nota sobre CORS:** Si ves errores de conexión WebSocket, asegúrate de que la configuración CORS está correctamente aplicada en el servidor Spring Boot.

### 4.4 Pruebas con múltiples clientes

Abre múltiples pestañas del navegador para simular varios clientes conectados simultáneamente.

## Ejercicios Adicionales

### TODO 1: Broadcast a todos los clientes
Modifica el `ChatWebSocketHandler` para que cuando un cliente envíe un mensaje, todos los clientes conectados lo reciban.

**Pista:**
```java
// Mantén un registro de todas las sesiones activas
private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();
```

### TODO 2: Agregar nombres de usuario
Permite que cada cliente se identifique con un nombre de usuario que se muestre junto a sus mensajes.

### TODO 3: Historial de mensajes
Implementa un almacenamiento temporal de los últimos 50 mensajes para que los nuevos clientes puedan ver el historial al conectarse.

## Preguntas de Reflexión

1. ¿Cuál es la diferencia entre WebSockets y Server-Sent Events? ¿Cuándo usarías cada uno?
2. ¿Cómo manejarías la reconexión automática si el servidor se cae temporalmente?
3. ¿Qué ventajas ofrece el uso de `Sinks` de Reactor para la comunicación entre sesiones?
4. ¿Cómo implementarías autenticación para las conexiones WebSocket?
5. ¿Qué consideraciones de seguridad debes tener en cuenta al exponer WebSockets en producción?

## Proyecto de Integración

**Sistema de Notificaciones en Tiempo Real**

Crea una aplicación que:
- Permita a usuarios suscribirse a diferentes "salas" o "canales"
- Envíe notificaciones push cuando ocurran eventos específicos
- Mantenga estadísticas en tiempo real del número de usuarios conectados
- Implemente throttling para evitar sobrecarga de mensajes
- Incluya manejo de errores y reconexión automática

**Tecnologías adicionales a considerar:**
- Redis para gestión de sesiones distribuidas
- JWT para autenticación
- STOMP para protocolo de mensajería estructurada
