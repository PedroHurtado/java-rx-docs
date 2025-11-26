package features.pizzas.controllers;

import features.pizzas.dto.IngredientResponse;
import features.pizzas.repositories.FakeIngredientRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/ingredients")
public class IngredientController {

    private final FakeIngredientRepository ingredientRepository;

    public IngredientController(FakeIngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }

    @GetMapping
    public Mono<ResponseEntity<Flux<IngredientResponse>>> getAllIngredients() {
        Flux<IngredientResponse> ingredients = ingredientRepository.findAll()
                .map(IngredientResponse::fromEntity);
        return Mono.just(ResponseEntity.ok(ingredients));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<IngredientResponse>> getIngredient(@PathVariable String id) {
        UUID ingredientId = UUID.fromString(id);
        
        return ingredientRepository.findById(ingredientId)
                .map(ingredient -> ResponseEntity.ok(IngredientResponse.fromEntity(ingredient)))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
