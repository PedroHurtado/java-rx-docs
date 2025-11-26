package features.pizzas.controllers;

import features.pizzas.dto.PizzaResponse;
import features.pizzas.models.Pizza;
import features.pizzas.repositories.FakeIngredientRepository;
import features.pizzas.repositories.FakePizzaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/pizzas")
public class PizzaController {

    private final FakePizzaRepository pizzaRepository;
    private final FakeIngredientRepository ingredientRepository;

    public PizzaController(FakePizzaRepository pizzaRepository, FakeIngredientRepository ingredientRepository) {
        this.pizzaRepository = pizzaRepository;
        this.ingredientRepository = ingredientRepository;
    }

    @PostMapping
    public Mono<ResponseEntity<PizzaResponse>> createPizza(@RequestBody Mono<PizzaCreateRequest> request) {
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

    @PutMapping("/{id}")
    public Mono<ResponseEntity<PizzaResponse>> updatePizza(
            @PathVariable String id,
            @RequestBody Mono<PizzaUpdateRequest> request) {
        
        UUID pizzaId = UUID.fromString(id);
        
        return pizzaRepository.findById(pizzaId)
                .flatMap(existingPizza -> 
                    request.flatMap(req -> {
                        existingPizza.update(req.name(), req.description(), req.url());
                        
                        // Actualizar ingredientes si se proporcionan
                        if (req.ingredientIds() != null) {
                            // Limpiar ingredientes actuales
                            existingPizza.getIngredients().forEach(existingPizza::removeIngredient);
                            
                            // Agregar nuevos ingredientes
                            return ingredientRepository.findAllById(req.ingredientIds())
                                    .doOnNext(existingPizza::addIngredient)
                                    .then(pizzaRepository.save(existingPizza));
                        }
                        
                        return pizzaRepository.save(existingPizza);
                    })
                )
                .map(pizza -> ResponseEntity.ok(PizzaResponse.fromEntity(pizza)))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deletePizza(@PathVariable String id) {
        UUID pizzaId = UUID.fromString(id);
        
        return pizzaRepository.existsById(pizzaId)
                .flatMap(exists -> {
                    if (exists) {
                        return pizzaRepository.deleteById(pizzaId)
                                .then(Mono.just(ResponseEntity.noContent().<Void>build()));
                    }
                    return Mono.just(ResponseEntity.notFound().<Void>build());
                });
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<PizzaResponse>> getPizza(@PathVariable String id) {
        UUID pizzaId = UUID.fromString(id);
        
        return pizzaRepository.findById(pizzaId)
                .map(pizza -> ResponseEntity.ok(PizzaResponse.fromEntity(pizza)))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping
    public Mono<ResponseEntity<Flux<PizzaResponse>>> getAllPizzas() {
        Flux<PizzaResponse> pizzas = pizzaRepository.findAll()
                .map(PizzaResponse::fromEntity);
        return Mono.just(ResponseEntity.ok(pizzas));
    }

    // DTOs Records
    public record PizzaCreateRequest(
            String name,
            String description,
            String url,
            List<UUID> ingredientIds
    ) {}

    public record PizzaUpdateRequest(
            String name,
            String description,
            String url,
            List<UUID> ingredientIds
    ) {}
}
