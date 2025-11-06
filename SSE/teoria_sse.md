# Server-Sent Events (SSE)

## ¿Qué son los Server-Sent Events?

Server-Sent Events (SSE) es una tecnología web que permite a un servidor enviar actualizaciones automáticas a un cliente a través de una conexión HTTP persistente. A diferencia de las peticiones HTTP tradicionales donde el cliente siempre inicia la comunicación, con SSE el servidor puede "empujar" datos al cliente de manera unidireccional.

## Características principales

### Comunicación unidireccional
SSE establece un canal de comunicación de una sola vía: del servidor al cliente. El cliente no puede enviar datos a través de la misma conexión, solo recibirlos. Si necesitas comunicación bidireccional, deberías considerar WebSockets.

### Basado en HTTP
SSE utiliza el protocolo HTTP estándar, lo que significa que funciona sobre la infraestructura web existente sin necesidad de protocolos especiales. Esto facilita su implementación y compatibilidad con proxies, firewalls y balanceadores de carga.

### Reconexión automática
Los navegadores implementan reconexión automática cuando se pierde la conexión con el servidor. El cliente intentará reestablecer la conexión automáticamente sin necesidad de código adicional.

### Formato de texto simple
Los eventos se transmiten como texto plano con un formato específico, utilizando el tipo MIME `text/event-stream`. Cada evento puede contener:
- **data**: El contenido del mensaje
- **event**: El nombre del tipo de evento (opcional)
- **id**: Un identificador único (opcional)
- **retry**: Tiempo en milisegundos para reintentar la conexión (opcional)

### Soporte nativo en navegadores
La mayoría de los navegadores modernos soportan SSE nativamente a través de la API `EventSource`, lo que simplifica enormemente su implementación en el lado del cliente.

## Ventajas de SSE

**Simplicidad**: La implementación es más sencilla que WebSockets, especialmente cuando solo necesitas comunicación del servidor al cliente.

**Eficiencia**: Utiliza una única conexión HTTP persistente, reduciendo la sobrecarga de establecer múltiples conexiones.

**Reconexión automática**: El navegador gestiona automáticamente las reconexiones, reduciendo la complejidad del código del cliente.

**Compatible con HTTP/2**: SSE se beneficia de las mejoras de rendimiento de HTTP/2, como multiplexación de streams.

## Limitaciones

**Unidireccional**: Solo permite comunicación del servidor al cliente. Para enviar datos al servidor, necesitas peticiones HTTP adicionales.

**Límite de conexiones**: Los navegadores limitan el número de conexiones SSE simultáneas por dominio (típicamente 6).

**Solo texto**: Aunque puedes enviar JSON serializado, no soporta datos binarios nativamente como WebSockets.

## SSE vs WebSockets

| Característica | SSE | WebSockets |
|---------------|-----|------------|
| Comunicación | Unidireccional (servidor → cliente) | Bidireccional |
| Protocolo | HTTP | WS/WSS |
| Reconexión | Automática | Manual |
| Formato | Texto | Texto y binario |
| Complejidad | Baja | Media |
| Casos de uso | Notificaciones, actualizaciones en tiempo real | Chat, juegos, colaboración |

## Casos de uso comunes

### Notificaciones en tiempo real
Ideal para sistemas de notificaciones donde el servidor necesita alertar a los usuarios sobre eventos importantes como mensajes nuevos, alertas del sistema o actualizaciones de estado.

**Ejemplo**: Una aplicación de mensajería que notifica al usuario cuando recibe un nuevo mensaje sin necesidad de que el cliente esté consultando constantemente al servidor.

### Feeds de noticias y redes sociales
Perfecto para mantener actualizados los feeds de contenido sin que el usuario tenga que refrescar la página.

**Ejemplo**: Twitter o LinkedIn actualizando el timeline con nuevos posts en tiempo real mientras el usuario navega por la aplicación.

### Monitorización de sistemas
Excelente para dashboards que muestran métricas en tiempo real como uso de CPU, memoria, tráfico de red o estado de servicios.

**Ejemplo**: Un panel de control que muestra el estado de los servidores en tiempo real, alertando inmediatamente cuando algún servicio falla.

### Actualizaciones de precios y cotizaciones
Utilizado en aplicaciones financieras para mantener actualizados los precios de acciones, criptomonedas o tipos de cambio.

**Ejemplo**: Una plataforma de trading que actualiza los precios de las acciones cada segundo sin recargar la página.

### Progreso de tareas largas
Ideal para mostrar el progreso de operaciones que toman tiempo, como subidas de archivos, procesamiento de datos o generación de reportes.

**Ejemplo**: Una aplicación que procesa un archivo CSV grande y va informando al usuario del porcentaje de progreso en tiempo real.

### Logs en tiempo real
Muy útil para aplicaciones de desarrollo donde necesitas ver logs del servidor en tiempo real.

**Ejemplo**: Una herramienta de CI/CD que muestra los logs de compilación y despliegue mientras se ejecutan.

### Actualizaciones de inventario o disponibilidad
Para e-commerce o sistemas de reservas donde múltiples usuarios pueden estar viendo el mismo producto o recurso.

**Ejemplo**: Una tienda online que actualiza la disponibilidad de productos en tiempo real cuando otros usuarios realizan compras.

## Formato del evento SSE

Los mensajes SSE siguen un formato específico:

```
data: Este es el contenido del mensaje\n\n

event: customEvent\n
data: {"message": "Evento personalizado con JSON"}\n\n

id: 123\n
event: update\n
data: Primera línea\n
data: Segunda línea\n\n

retry: 5000\n\n
```

Cada campo termina con `\n` y cada evento completo termina con `\n\n` (doble salto de línea).

## Implementación con Spring WebFlux

Spring WebFlux proporciona soporte nativo para SSE a través de `Flux<ServerSentEvent>`. La programación reactiva es ideal para SSE porque permite manejar múltiples conexiones concurrentes de manera eficiente sin bloquear threads.

Los streams reactivos permiten:
- Generar eventos de manera asíncrona
- Controlar el backpressure automáticamente
- Cancelar suscripciones cuando el cliente se desconecta
- Combinar múltiples fuentes de eventos

## Consideraciones de seguridad

**Autenticación**: Aunque SSE no soporta headers personalizados directamente en el cliente, puedes usar cookies o tokens en la URL.

**CORS**: Debes configurar correctamente CORS si el cliente está en un dominio diferente al servidor.

**Rate limiting**: Implementa limitaciones de tasa para prevenir abuso de recursos del servidor.

**Validación de datos**: Siempre valida y sanitiza los datos antes de enviarlos al cliente.

## Buenas prácticas

1. **Implementa heartbeats**: Envía mensajes periódicos para mantener la conexión activa y detectar desconexiones.

2. **Maneja errores gracefully**: Proporciona mensajes de error claros y estrategias de reconexión.

3. **Usa IDs de eventos**: Esto permite al cliente reanudar desde el último evento recibido después de una reconexión.

4. **Limita la frecuencia de eventos**: No satures al cliente con demasiados eventos por segundo.

5. **Monitoriza las conexiones**: Lleva un registro de las conexiones activas y ciérralas adecuadamente cuando sea necesario.

6. **Considera la escalabilidad**: Para sistemas grandes, usa message brokers como Kafka o RabbitMQ para distribuir eventos entre múltiples instancias del servidor.

## Referencias

### Documentación oficial
- [MDN Web Docs - Server-Sent Events](https://developer.mozilla.org/es/docs/Web/API/Server-sent_events)
- [MDN Web Docs - EventSource API](https://developer.mozilla.org/es/docs/Web/API/EventSource)
- [HTML Living Standard - Server-Sent Events](https://html.spec.whatwg.org/multipage/server-sent-events.html)

### Spring Framework
- [Spring WebFlux Documentation](https://docs.spring.io/spring-framework/reference/web/webflux.html)
- [Spring WebFlux Server-Sent Events](https://docs.spring.io/spring-framework/reference/web/webflux/reactive-spring.html#webflux-codecs-sse)

### Artículos y tutoriales
- [Stream Updates with Server-Sent Events - web.dev](https://web.dev/eventsource-basics/)
- [Using Server-Sent Events - HTML5 Rocks](https://www.html5rocks.com/en/tutorials/eventsource/basics/)
- [Server-Sent Events with Spring WebFlux - Baeldung](https://www.baeldung.com/spring-server-sent-events)

### Herramientas y bibliotecas
- [EventSource Polyfill](https://github.com/Yaffle/EventSource) - Para navegadores que no soportan SSE nativamente
- [OkHttp SSE](https://square.github.io/okhttp/4.x/okhttp-sse/okhttp3.sse/) - Cliente SSE para Java/Kotlin
- [RxSSE](https://github.com/spring-attic/reactive-streams-commons) - Extensiones reactivas para SSE
