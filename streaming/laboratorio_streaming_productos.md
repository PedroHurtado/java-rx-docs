# Laboratorio: Streaming de Productos con JSON Lines

## Objetivo

Implementarás un sistema completo de streaming de productos donde el servidor Spring Boot (sin WebFlux) envía datos en formato NDJSON y el cliente JavaScript los consume y renderiza en tiempo real conforme van llegando.

## Requisitos previos

- JDK 17 o superior
- Maven 3.6+
- IDE (IntelliJ IDEA, Eclipse o VS Code)
- Navegador moderno (Chrome, Firefox, Edge)
- Conocimientos básicos de Spring Boot y JavaScript

## Parte 1: Configuración del Proyecto Spring Boot

### Paso 1: Crear proyecto con dependencias

Crea un nuevo proyecto Spring Boot con las siguientes dependencias en `pom.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
        <relativePath/>
    </parent>
    
    <groupId>com.example</groupId>
    <artifactId>product-streaming</artifactId>
    <version>1.0.0</version>
    <name>Product Streaming Demo</name>
    
    <properties>
        <java.version>17</java.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
        </dependency>
        
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

### Paso 2: Modelo de datos

Crea la clase `Product.java`:

```java
package com.example.streaming.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String category;
    private Integer stock;
    private String imageUrl;
    private Boolean featured;
}
```

### Paso 3: Servicio de productos

Crea `ProductService.java` que simula una base de datos:

```java
package com.example.streaming.service;

import com.example.streaming.model.Product;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {
    
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        
        // Simulamos diferentes categorías de productos
        products.add(new Product(1L, "Laptop Dell XPS 13", 
            "Ultraportátil con procesador Intel i7 de 11ª generación, 16GB RAM, SSD 512GB", 
            new BigDecimal("1299.99"), "Electrónica", 15,
            "https://via.placeholder.com/300x300/0066cc/ffffff?text=Laptop+Dell", true));
            
        products.add(new Product(2L, "iPhone 14 Pro", 
            "Smartphone Apple con chip A16 Bionic, cámara 48MP, pantalla ProMotion", 
            new BigDecimal("1099.99"), "Electrónica", 25,
            "https://via.placeholder.com/300x300/000000/ffffff?text=iPhone+14", true));
            
        products.add(new Product(3L, "Samsung Galaxy Watch 5", 
            "Smartwatch con monitor de salud avanzado, GPS, resistente al agua", 
            new BigDecimal("279.99"), "Wearables", 40,
            "https://via.placeholder.com/300x300/1428a0/ffffff?text=Galaxy+Watch", false));
            
        products.add(new Product(4L, "Sony WH-1000XM5", 
            "Auriculares con cancelación de ruido líder en la industria, 30h batería", 
            new BigDecimal("399.99"), "Audio", 30,
            "https://via.placeholder.com/300x300/ff6b6b/ffffff?text=Sony+Audio", true));
            
        products.add(new Product(5L, "iPad Pro 12.9", 
            "Tablet con chip M2, pantalla Liquid Retina XDR, compatible con Apple Pencil", 
            new BigDecimal("1199.99"), "Electrónica", 12,
            "https://via.placeholder.com/300x300/5f27cd/ffffff?text=iPad+Pro", true));
            
        products.add(new Product(6L, "Logitech MX Master 3S", 
            "Mouse inalámbrico ergonómico, 8000 DPI, silencioso, recargable", 
            new BigDecimal("99.99"), "Accesorios", 50,
            "https://via.placeholder.com/300x300/48dbfb/000000?text=MX+Master", false));
            
        products.add(new Product(7L, "Mechanical Keyboard RGB", 
            "Teclado mecánico gaming con switches Cherry MX, iluminación RGB personalizable", 
            new BigDecimal("149.99"), "Accesorios", 35,
            "https://via.placeholder.com/300x300/ff9ff3/000000?text=Keyboard", false));
            
        products.add(new Product(8L, "LG UltraWide Monitor 34\"", 
            "Monitor curvo QHD 3440x1440, 144Hz, HDR10, ideal para productividad", 
            new BigDecimal("699.99"), "Monitores", 8,
            "https://via.placeholder.com/300x300/54a0ff/ffffff?text=LG+Monitor", true));
            
        products.add(new Product(9L, "Canon EOS R6 Mark II", 
            "Cámara mirrorless full-frame, 24.2MP, video 4K60, estabilización IBIS", 
            new BigDecimal("2499.99"), "Fotografía", 5,
            "https://via.placeholder.com/300x300/ee5a6f/ffffff?text=Canon+R6", true));
            
        products.add(new Product(10L, "Nintendo Switch OLED", 
            "Consola híbrida con pantalla OLED 7 pulgadas, 64GB almacenamiento", 
            new BigDecimal("349.99"), "Gaming", 20,
            "https://via.placeholder.com/300x300/e74c3c/ffffff?text=Switch", false));
            
        products.add(new Product(11L, "PS5 Digital Edition", 
            "Consola de última generación, SSD ultrarrápido, ray tracing, 4K gaming", 
            new BigDecimal("399.99"), "Gaming", 10,
            "https://via.placeholder.com/300x300/0984e3/ffffff?text=PS5", true));
            
        products.add(new Product(12L, "Kindle Paperwhite", 
            "E-reader con pantalla de 6.8 pulgadas, resistente al agua, batería semanas", 
            new BigDecimal("139.99"), "Electrónica", 45,
            "https://via.placeholder.com/300x300/2d3436/ffffff?text=Kindle", false));
            
        products.add(new Product(13L, "DJI Mini 3 Pro", 
            "Drone compacto con cámara 4K HDR, 34 min vuelo, detección obstáculos", 
            new BigDecimal("759.99"), "Fotografía", 7,
            "https://via.placeholder.com/300x300/00b894/ffffff?text=DJI+Drone", false));
            
        products.add(new Product(14L, "Google Nest Hub Max", 
            "Pantalla inteligente 10 pulgadas, cámara Nest, control domótica", 
            new BigDecimal("229.99"), "Smart Home", 22,
            "https://via.placeholder.com/300x300/fdcb6e/000000?text=Nest+Hub", false));
            
        products.add(new Product(15L, "Anker PowerCore 26800", 
            "Batería externa 26800mAh, carga rápida, 3 puertos USB, portátil", 
            new BigDecimal("65.99"), "Accesorios", 60,
            "https://via.placeholder.com/300x300/6c5ce7/ffffff?text=PowerBank", false));
            
        products.add(new Product(16L, "Bose QuietComfort Earbuds II", 
            "Auriculares true wireless, cancelación ruido personalizada, IPX4", 
            new BigDecimal("299.99"), "Audio", 28,
            "https://via.placeholder.com/300x300/a29bfe/000000?text=Bose+Buds", false));
            
        products.add(new Product(17L, "SanDisk Extreme Pro 1TB", 
            "SSD portátil, velocidad lectura 1050MB/s, resistente golpes", 
            new BigDecimal("159.99"), "Almacenamiento", 38,
            "https://via.placeholder.com/300x300/fab1a0/000000?text=SanDisk+SSD", false));
            
        products.add(new Product(18L, "Philips Hue Starter Kit", 
            "Kit iluminación inteligente, 3 bombillas LED color, puente incluido", 
            new BigDecimal("179.99"), "Smart Home", 18,
            "https://via.placeholder.com/300x300/fd79a8/000000?text=Philips+Hue", false));
            
        products.add(new Product(19L, "GoPro HERO11 Black", 
            "Cámara acción 5.3K60, estabilización HyperSmooth, sumergible 10m", 
            new BigDecimal("499.99"), "Fotografía", 14,
            "https://via.placeholder.com/300x300/00cec9/000000?text=GoPro", true));
            
        products.add(new Product(20L, "Razer DeathAdder V3 Pro", 
            "Mouse gaming inalámbrico, sensor 30000 DPI, switches ópticos", 
            new BigDecimal("149.99"), "Gaming", 32,
            "https://via.placeholder.com/300x300/00d2d3/000000?text=Razer+Mouse", false));
        
        return products;
    }
}
```

### Paso 4: Controlador con streaming NDJSON

Crea `ProductStreamController.java`:

```java
package com.example.streaming.controller;

import com.example.streaming.model.Product;
import com.example.streaming.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProductStreamController {
    
    private final ProductService productService;
    private final ObjectMapper objectMapper;
    
    /**
     * Endpoint que devuelve productos en formato NDJSON (Newline Delimited JSON)
     * Simula un delay entre productos para demostrar el streaming
     */
    @GetMapping(value = "/stream", produces = "application/x-ndjson")
    public void streamProducts(HttpServletResponse response) throws IOException {
        response.setContentType("application/x-ndjson");
        response.setCharacterEncoding("UTF-8");
        
        // Importante: deshabilitar el buffering para streaming real
        response.setBufferSize(0);
        
        PrintWriter writer = response.getWriter();
        List<Product> products = productService.getAllProducts();
        
        log.info("Iniciando streaming de {} productos", products.size());
        
        for (Product product : products) {
            try {
                // Convertir el producto a JSON
                String jsonLine = objectMapper.writeValueAsString(product);
                
                // Escribir línea JSON + newline
                writer.write(jsonLine);
                writer.write("\n");
                writer.flush(); // Flush inmediato para streaming
                
                log.debug("Producto enviado: {}", product.getName());
                
                // Simular delay de procesamiento (250ms por producto)
                Thread.sleep(250);
                
            } catch (InterruptedException e) {
                log.error("Error en el streaming", e);
                Thread.currentThread().interrupt();
                break;
            }
        }
        
        log.info("Streaming completado");
        writer.close();
    }
    
    /**
     * Endpoint tradicional que devuelve todos los productos de una vez
     * Para comparación
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Product> getAllProducts() {
        log.info("Obteniendo todos los productos (no streaming)");
        return productService.getAllProducts();
    }
}
```

### Paso 5: Configuración CORS (opcional pero recomendado)

Crea `WebConfig.java`:

```java
package com.example.streaming.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*");
    }
}
```

### Paso 6: Clase principal

```java
package com.example.streaming;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProductStreamingApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ProductStreamingApplication.class, args);
    }
}
```

### Paso 7: Configuración application.properties

```properties
server.port=8080
spring.application.name=product-streaming

# Logging
logging.level.com.example.streaming=DEBUG
logging.level.org.springframework.web=INFO

# Jackson configuration
spring.jackson.serialization.write-dates-as-timestamps=false
spring.jackson.serialization.indent-output=true
```

## Parte 2: Cliente HTML/JavaScript

### Paso 8: Crear página HTML con estilos

Crea el archivo `index.html`:

```html
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Catálogo de Productos - Streaming Demo</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            padding: 20px;
        }
        
        .container {
            max-width: 1400px;
            margin: 0 auto;
        }
        
        header {
            text-align: center;
            color: white;
            margin-bottom: 40px;
            padding: 20px;
            background: rgba(255, 255, 255, 0.1);
            border-radius: 15px;
            backdrop-filter: blur(10px);
        }
        
        h1 {
            font-size: 2.5rem;
            margin-bottom: 10px;
            text-shadow: 2px 2px 4px rgba(0,0,0,0.3);
        }
        
        .subtitle {
            font-size: 1.1rem;
            opacity: 0.9;
        }
        
        .controls {
            display: flex;
            gap: 15px;
            justify-content: center;
            margin-bottom: 30px;
            flex-wrap: wrap;
        }
        
        button {
            padding: 12px 30px;
            font-size: 1rem;
            font-weight: 600;
            border: none;
            border-radius: 25px;
            cursor: pointer;
            transition: all 0.3s ease;
            box-shadow: 0 4px 15px rgba(0,0,0,0.2);
        }
        
        button:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(0,0,0,0.3);
        }
        
        button:active {
            transform: translateY(0);
        }
        
        .btn-stream {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
        }
        
        .btn-clear {
            background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
            color: white;
        }
        
        .btn-stop {
            background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
            color: #333;
        }
        
        button:disabled {
            opacity: 0.5;
            cursor: not-allowed;
            transform: none;
        }
        
        .stats {
            display: flex;
            gap: 20px;
            justify-content: center;
            margin-bottom: 30px;
            flex-wrap: wrap;
        }
        
        .stat-card {
            background: white;
            padding: 20px 30px;
            border-radius: 15px;
            box-shadow: 0 4px 15px rgba(0,0,0,0.1);
            min-width: 150px;
            text-align: center;
        }
        
        .stat-label {
            font-size: 0.9rem;
            color: #666;
            margin-bottom: 5px;
        }
        
        .stat-value {
            font-size: 2rem;
            font-weight: bold;
            color: #667eea;
        }
        
        .loading {
            text-align: center;
            padding: 40px;
            color: white;
            font-size: 1.2rem;
        }
        
        .spinner {
            border: 4px solid rgba(255,255,255,0.3);
            border-top: 4px solid white;
            border-radius: 50%;
            width: 50px;
            height: 50px;
            animation: spin 1s linear infinite;
            margin: 20px auto;
        }
        
        @keyframes spin {
            0% { transform: rotate(0deg); }
            100% { transform: rotate(360deg); }
        }
        
        .products-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
            gap: 25px;
            margin-top: 30px;
        }
        
        .product-card {
            background: white;
            border-radius: 15px;
            overflow: hidden;
            box-shadow: 0 4px 15px rgba(0,0,0,0.1);
            transition: all 0.3s ease;
            animation: fadeIn 0.5s ease;
            position: relative;
        }
        
        .product-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 8px 25px rgba(0,0,0,0.2);
        }
        
        @keyframes fadeIn {
            from {
                opacity: 0;
                transform: translateY(20px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }
        
        .product-image {
            width: 100%;
            height: 200px;
            object-fit: cover;
            background: #f0f0f0;
        }
        
        .product-badge {
            position: absolute;
            top: 10px;
            right: 10px;
            background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
            color: white;
            padding: 5px 15px;
            border-radius: 20px;
            font-size: 0.8rem;
            font-weight: bold;
            box-shadow: 0 2px 10px rgba(0,0,0,0.2);
        }
        
        .product-info {
            padding: 20px;
        }
        
        .product-category {
            display: inline-block;
            background: #667eea;
            color: white;
            padding: 4px 12px;
            border-radius: 12px;
            font-size: 0.75rem;
            font-weight: 600;
            margin-bottom: 10px;
            text-transform: uppercase;
        }
        
        .product-name {
            font-size: 1.3rem;
            font-weight: 700;
            color: #333;
            margin-bottom: 10px;
            line-height: 1.3;
        }
        
        .product-description {
            color: #666;
            font-size: 0.9rem;
            line-height: 1.5;
            margin-bottom: 15px;
            height: 60px;
            overflow: hidden;
            text-overflow: ellipsis;
        }
        
        .product-footer {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding-top: 15px;
            border-top: 1px solid #eee;
        }
        
        .product-price {
            font-size: 1.8rem;
            font-weight: bold;
            color: #667eea;
        }
        
        .product-stock {
            font-size: 0.85rem;
            color: #666;
        }
        
        .stock-available {
            color: #10b981;
            font-weight: 600;
        }
        
        .stock-low {
            color: #f59e0b;
            font-weight: 600;
        }
        
        .error {
            background: #fee;
            color: #c33;
            padding: 20px;
            border-radius: 10px;
            margin: 20px 0;
            border-left: 4px solid #c33;
        }
        
        @media (max-width: 768px) {
            h1 {
                font-size: 1.8rem;
            }
            
            .products-grid {
                grid-template-columns: 1fr;
            }
            
            .controls {
                flex-direction: column;
            }
            
            button {
                width: 100%;
            }
        }
    </style>
</head>
<body>
    <div class="container">
        <header>
            <h1>🛍️ Catálogo de Productos</h1>
            <p class="subtitle">Demostración de Streaming con NDJSON</p>
        </header>
        
        <div class="controls">
            <button id="btnStream" class="btn-stream">
                ▶️ Iniciar Streaming
            </button>
            <button id="btnStop" class="btn-stop" disabled>
                ⏹️ Detener Streaming
            </button>
            <button id="btnClear" class="btn-clear">
                🗑️ Limpiar Catálogo
            </button>
        </div>
        
        <div class="stats">
            <div class="stat-card">
                <div class="stat-label">Productos Cargados</div>
                <div class="stat-value" id="productCount">0</div>
            </div>
            <div class="stat-card">
                <div class="stat-label">Tiempo Transcurrido</div>
                <div class="stat-value" id="elapsedTime">0s</div>
            </div>
            <div class="stat-card">
                <div class="stat-label">Estado</div>
                <div class="stat-value" id="status" style="font-size: 1rem;">Listo</div>
            </div>
        </div>
        
        <div id="loading" class="loading" style="display: none;">
            <div class="spinner"></div>
            <p>Cargando productos...</p>
        </div>
        
        <div id="error" class="error" style="display: none;"></div>
        
        <div id="productsContainer" class="products-grid"></div>
    </div>
    
    <script src="app.js"></script>
</body>
</html>
```

### Paso 9: Implementar lógica JavaScript

Crea el archivo `app.js`:

```javascript
/**
 * Clase para consumir streams NDJSON de productos
 */
class ProductStreamConsumer {
    constructor(apiUrl) {
        this.apiUrl = apiUrl;
        this.controller = null;
        this.isStreaming = false;
        this.productCount = 0;
        this.startTime = null;
        this.timerInterval = null;
    }
    
    /**
     * Inicia el streaming de productos
     */
    async start(onProduct, onComplete, onError) {
        if (this.isStreaming) {
            console.warn('Ya hay un streaming en progreso');
            return;
        }
        
        this.isStreaming = true;
        this.productCount = 0;
        this.startTime = Date.now();
        this.startTimer();
        
        try {
            this.controller = new AbortController();
            
            const response = await fetch(this.apiUrl, {
                signal: this.controller.signal,
                headers: {
                    'Accept': 'application/x-ndjson'
                }
            });
            
            if (!response.ok) {
                throw new Error(`HTTP ${response.status}: ${response.statusText}`);
            }
            
            await this.processStream(response.body, onProduct);
            
            this.isStreaming = false;
            this.stopTimer();
            onComplete?.();
            
        } catch (error) {
            this.isStreaming = false;
            this.stopTimer();
            
            if (error.name !== 'AbortError') {
                console.error('Error en streaming:', error);
                onError?.(error);
            } else {
                console.log('Streaming cancelado por el usuario');
                onComplete?.();
            }
        }
    }
    
    /**
     * Procesa el stream de datos
     */
    async processStream(stream, onProduct) {
        const reader = stream.getReader();
        const decoder = new TextDecoder();
        let buffer = '';
        
        while (true) {
            const { done, value } = await reader.read();
            
            if (done) {
                // Procesar cualquier dato restante
                if (buffer.trim()) {
                    this.processChunk(buffer, onProduct);
                }
                break;
            }
            
            // Decodificar el chunk y agregarlo al buffer
            buffer += decoder.decode(value, { stream: true });
            
            // Procesar líneas completas
            const lines = buffer.split('\n');
            buffer = lines.pop(); // Guardar la línea incompleta
            
            for (const line of lines) {
                if (line.trim()) {
                    this.processChunk(line, onProduct);
                }
            }
        }
    }
    
    /**
     * Procesa un chunk individual (línea JSON)
     */
    processChunk(chunk, onProduct) {
        try {
            const product = JSON.parse(chunk);
            this.productCount++;
            onProduct(product, this.productCount);
        } catch (e) {
            console.error('Error parseando JSON:', e, chunk);
        }
    }
    
    /**
     * Detiene el streaming
     */
    stop() {
        if (this.controller) {
            this.controller.abort();
            this.controller = null;
        }
        this.isStreaming = false;
        this.stopTimer();
    }
    
    /**
     * Inicia el temporizador
     */
    startTimer() {
        this.timerInterval = setInterval(() => {
            const elapsed = Math.floor((Date.now() - this.startTime) / 1000);
            document.getElementById('elapsedTime').textContent = `${elapsed}s`;
        }, 1000);
    }
    
    /**
     * Detiene el temporizador
     */
    stopTimer() {
        if (this.timerInterval) {
            clearInterval(this.timerInterval);
            this.timerInterval = null;
        }
    }
    
    /**
     * Obtiene el estado del streaming
     */
    getIsStreaming() {
        return this.isStreaming;
    }
    
    /**
     * Obtiene el contador de productos
     */
    getProductCount() {
        return this.productCount;
    }
}

/**
 * Clase para gestionar la UI del catálogo
 */
class ProductCatalogUI {
    constructor() {
        this.container = document.getElementById('productsContainer');
        this.loadingElement = document.getElementById('loading');
        this.errorElement = document.getElementById('error');
        this.productCountElement = document.getElementById('productCount');
        this.statusElement = document.getElementById('status');
    }
    
    /**
     * Muestra el indicador de carga
     */
    showLoading() {
        this.loadingElement.style.display = 'block';
        this.hideError();
        this.updateStatus('Cargando...', '#f59e0b');
    }
    
    /**
     * Oculta el indicador de carga
     */
    hideLoading() {
        this.loadingElement.style.display = 'none';
    }
    
    /**
     * Muestra un mensaje de error
     */
    showError(message) {
        this.errorElement.textContent = `❌ Error: ${message}`;
        this.errorElement.style.display = 'block';
        this.updateStatus('Error', '#ef4444');
    }
    
    /**
     * Oculta el mensaje de error
     */
    hideError() {
        this.errorElement.style.display = 'none';
    }
    
    /**
     * Actualiza el estado
     */
    updateStatus(text, color = '#667eea') {
        this.statusElement.textContent = text;
        this.statusElement.style.color = color;
    }
    
    /**
     * Añade un producto al catálogo
     */
    addProduct(product, index) {
        const card = this.createProductCard(product, index);
        this.container.appendChild(card);
        this.productCountElement.textContent = index;
        
        // Scroll suave hacia el nuevo producto
        setTimeout(() => {
            card.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
        }, 100);
    }
    
    /**
     * Crea una tarjeta de producto
     */
    createProductCard(product, index) {
        const card = document.createElement('div');
        card.className = 'product-card';
        card.style.animationDelay = `${(index % 4) * 0.1}s`;
        
        const stockClass = product.stock > 20 ? 'stock-available' : 'stock-low';
        const stockText = product.stock > 20 ? 'En stock' : 'Stock limitado';
        
        card.innerHTML = `
            ${product.featured ? '<div class="product-badge">⭐ Destacado</div>' : ''}
            <img src="${product.imageUrl}" alt="${product.name}" class="product-image">
            <div class="product-info">
                <span class="product-category">${product.category}</span>
                <h3 class="product-name">${product.name}</h3>
                <p class="product-description">${product.description}</p>
                <div class="product-footer">
                    <div class="product-price">$${parseFloat(product.price).toFixed(2)}</div>
                    <div class="product-stock">
                        <span class="${stockClass}">${stockText}</span><br>
                        <small>${product.stock} unidades</small>
                    </div>
                </div>
            </div>
        `;
        
        return card;
    }
    
    /**
     * Limpia todos los productos
     */
    clear() {
        this.container.innerHTML = '';
        this.productCountElement.textContent = '0';
        this.hideError();
        this.hideLoading();
        this.updateStatus('Listo', '#667eea');
        document.getElementById('elapsedTime').textContent = '0s';
    }
}

/**
 * Controlador principal de la aplicación
 */
class App {
    constructor() {
        // Configuración del API
        this.API_URL = 'http://localhost:8080/api/products/stream';
        
        // Inicializar componentes
        this.streamConsumer = new ProductStreamConsumer(this.API_URL);
        this.ui = new ProductCatalogUI();
        
        // Referencias a botones
        this.btnStream = document.getElementById('btnStream');
        this.btnStop = document.getElementById('btnStop');
        this.btnClear = document.getElementById('btnClear');
        
        // Configurar eventos
        this.setupEventListeners();
        
        console.log('✅ Aplicación inicializada');
    }
    
    /**
     * Configura los event listeners
     */
    setupEventListeners() {
        this.btnStream.addEventListener('click', () => this.startStreaming());
        this.btnStop.addEventListener('click', () => this.stopStreaming());
        this.btnClear.addEventListener('click', () => this.clearCatalog());
    }
    
    /**
     * Inicia el streaming de productos
     */
    async startStreaming() {
        console.log('🚀 Iniciando streaming...');
        
        this.ui.showLoading();
        this.btnStream.disabled = true;
        this.btnStop.disabled = false;
        this.btnClear.disabled = true;
        this.ui.updateStatus('Streaming...', '#10b981');
        
        await this.streamConsumer.start(
            // onProduct
            (product, index) => {
                console.log(`📦 Producto ${index} recibido:`, product.name);
                this.ui.addProduct(product, index);
            },
            
            // onComplete
            () => {
                console.log('✅ Streaming completado');
                this.ui.hideLoading();
                this.ui.updateStatus('Completado', '#10b981');
                this.btnStream.disabled = false;
                this.btnStop.disabled = true;
                this.btnClear.disabled = false;
            },
            
            // onError
            (error) => {
                console.error('❌ Error:', error);
                this.ui.hideLoading();
                this.ui.showError(error.message);
                this.btnStream.disabled = false;
                this.btnStop.disabled = true;
                this.btnClear.disabled = false;
            }
        );
    }
    
    /**
     * Detiene el streaming
     */
    stopStreaming() {
        console.log('⏹️ Deteniendo streaming...');
        this.streamConsumer.stop();
        this.ui.hideLoading();
        this.ui.updateStatus('Detenido', '#f59e0b');
        this.btnStream.disabled = false;
        this.btnStop.disabled = true;
        this.btnClear.disabled = false;
    }
    
    /**
     * Limpia el catálogo
     */
    clearCatalog() {
        console.log('🗑️ Limpiando catálogo...');
        this.ui.clear();
    }
}

// Inicializar la aplicación cuando el DOM esté listo
document.addEventListener('DOMContentLoaded', () => {
    new App();
});
```

## Parte 3: Ejecución y pruebas

### Paso 10: Ejecutar el servidor

```bash
# Compilar y ejecutar Spring Boot
mvn clean install
mvn spring-boot:run

# O usando el jar
mvn clean package
java -jar target/product-streaming-1.0.0.jar
```

El servidor estará disponible en `http://localhost:8080`

### Paso 11: Abrir el cliente

1. Abre el archivo `index.html` directamente en tu navegador
2. O usa un servidor HTTP simple:

```bash
# Con Python 3
python -m http.server 3000

# Con Node.js (npx)
npx http-server -p 3000

# Con PHP
php -S localhost:3000
```

Accede a `http://localhost:3000`

### Paso 12: Probar la aplicación

1. Haz clic en "Iniciar Streaming"
2. Observa cómo los productos aparecen uno por uno
3. Prueba detener el streaming con "Detener Streaming"
4. Limpia el catálogo con "Limpiar Catálogo"

## Ejercicios propuestos

### Ejercicio 1: Filtrado por categoría

Añade un selector de categorías que permita filtrar productos antes de iniciar el streaming.

**Pistas:**
- Modifica el endpoint para aceptar un parámetro `category`
- Actualiza el servicio para filtrar productos
- Añade un `<select>` en el HTML

### Ejercicio 2: Búsqueda en tiempo real

Implementa un campo de búsqueda que filtre los productos mientras se cargan.

**Pistas:**
- Añade un `<input>` de búsqueda
- Filtra productos en el cliente según el texto
- Usa `filter()` sobre el array de productos

### Ejercicio 3: Paginación del streaming

Modifica el servidor para enviar productos en páginas de 5 elementos.

**Pistas:**
- Añade parámetros `page` y `size` al endpoint
- Implementa lógica de paginación en el servicio
- Añade botones "Cargar más" en el cliente

### Ejercicio 4: Velocidad configurable

Permite al usuario ajustar la velocidad del streaming.

**Pistas:**
- Añade un `<input type="range">` para la velocidad
- Envía la velocidad como parámetro al endpoint
- Ajusta el `Thread.sleep()` según el parámetro

### Ejercicio 5: Estadísticas avanzadas

Muestra estadísticas como precio promedio, categoría más común, etc.

**Pistas:**
- Calcula estadísticas mientras llegan productos
- Actualiza un panel de estadísticas en tiempo real
- Usa `reduce()` para cálculos acumulativos

## Preguntas de reflexión

1. ¿Qué ventajas ofrece NDJSON sobre JSON tradicional para streaming?
2. ¿Por qué es importante usar `writer.flush()` después de cada línea?
3. ¿Qué sucede si no deshabilitas el buffering en `HttpServletResponse`?
4. ¿Cómo afecta el tamaño del buffer al rendimiento del streaming?
5. ¿Qué alternativas existen a NDJSON para streaming de datos?
6. ¿Cuándo usarías Server-Sent Events (SSE) en lugar de NDJSON?
7. ¿Qué problemas podrían surgir con conexiones lentas o inestables?
8. ¿Cómo implementarías retry logic en caso de error?

## Proyecto integrador

Crea un sistema de monitoreo de logs en tiempo real donde:

1. El servidor lee un archivo de log línea por línea
2. Envía cada entrada de log como NDJSON
3. El cliente muestra los logs con diferentes colores según el nivel (INFO, WARN, ERROR)
4. Implementa filtros por nivel de log
5. Añade búsqueda en tiempo real
6. Muestra estadísticas de errores por minuto

## Recursos adicionales

- [NDJSON Specification](http://ndjson.org/)
- [MDN: Streams API](https://developer.mozilla.org/es/docs/Web/API/Streams_API)
- [Spring Boot Reference: Streaming](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-ann-async.html)
- [Fetch API: ReadableStream](https://developer.mozilla.org/es/docs/Web/API/ReadableStream)

## Conclusiones

En este laboratorio has aprendido a implementar streaming HTTP usando NDJSON, una técnica que permite enviar datos incrementalmente desde el servidor al cliente. Esta aproximación es ideal para grandes volúmenes de datos, permitiendo al usuario ver resultados inmediatamente sin esperar a que se complete toda la operación.

Has trabajado con conceptos importantes como `HttpServletResponse`, `PrintWriter`, `ReadableStream`, y has comprendido la importancia del flushing y el control del buffering para lograr streaming efectivo.
