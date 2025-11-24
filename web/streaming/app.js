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
        this.API_URL = 'http://localhost:8081/api/stream';
        
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