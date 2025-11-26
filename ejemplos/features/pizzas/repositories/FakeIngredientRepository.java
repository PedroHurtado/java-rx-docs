package features.pizzas.repositories;

import features.pizzas.models.Ingredient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class FakeIngredientRepository {
    
    private final Map<UUID, Ingredient> ingredients = new ConcurrentHashMap<>();

    public FakeIngredientRepository() {
        // Ingredientes iniciales
        Ingredient tomato = Ingredient.create(UUID.randomUUID(), "Tomate", 0.50);
        Ingredient cheese = Ingredient.create(UUID.randomUUID(), "Queso Mozzarella", 1.20);
        Ingredient pepperoni = Ingredient.create(UUID.randomUUID(), "Pepperoni", 2.00);
        Ingredient mushrooms = Ingredient.create(UUID.randomUUID(), "Champiñones", 0.80);
        Ingredient olives = Ingredient.create(UUID.randomUUID(), "Aceitunas", 0.60);
        Ingredient basil = Ingredient.create(UUID.randomUUID(), "Albahaca", 0.30);
        Ingredient onion = Ingredient.create(UUID.randomUUID(), "Cebolla", 0.40);
        Ingredient bacon = Ingredient.create(UUID.randomUUID(), "Bacon", 1.50);
        Ingredient pineapple = Ingredient.create(UUID.randomUUID(), "Piña", 0.70);
        Ingredient ham = Ingredient.create(UUID.randomUUID(), "Jamón", 1.30);

        ingredients.put(tomato.getId(), tomato);
        ingredients.put(cheese.getId(), cheese);
        ingredients.put(pepperoni.getId(), pepperoni);
        ingredients.put(mushrooms.getId(), mushrooms);
        ingredients.put(olives.getId(), olives);
        ingredients.put(basil.getId(), basil);
        ingredients.put(onion.getId(), onion);
        ingredients.put(bacon.getId(), bacon);
        ingredients.put(pineapple.getId(), pineapple);
        ingredients.put(ham.getId(), ham);
    }

    public Mono<Ingredient> save(Ingredient ingredient) {
        return Mono.fromCallable(() -> {
            ingredients.put(ingredient.getId(), ingredient);
            return ingredient;
        });
    }

    public Mono<Ingredient> findById(UUID id) {
        return Mono.justOrEmpty(ingredients.get(id));
    }

    public Flux<Ingredient> findAll() {
        return Flux.fromIterable(ingredients.values());
    }

    public Mono<Void> deleteById(UUID id) {
        return Mono.fromRunnable(() -> ingredients.remove(id));
    }

    public Mono<Boolean> existsById(UUID id) {
        return Mono.just(ingredients.containsKey(id));
    }

    public Flux<Ingredient> findAllById(Iterable<UUID> ids) {
        return Flux.fromIterable(ids)
                .mapNotNull(ingredients::get);
    }
}
