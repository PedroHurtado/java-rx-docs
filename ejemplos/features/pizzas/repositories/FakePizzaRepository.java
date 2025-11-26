package features.pizzas.repositories;

import features.pizzas.models.Ingredient;
import features.pizzas.models.Pizza;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class FakePizzaRepository {
    
    private final Map<UUID, Pizza> pizzas = new ConcurrentHashMap<>();
    private final FakeIngredientRepository ingredientRepository;

    public FakePizzaRepository(FakeIngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
        initializePizzas();
    }

    private void initializePizzas() {
        // Pizza Margarita
        Pizza margarita = Pizza.create(
                UUID.randomUUID(),
                "Pizza Margarita",
                "Pizza clásica italiana con tomate, mozzarella y albahaca",
                "https://example.com/margarita.jpg"
        );
        
        ingredientRepository.findAll()
                .filter(ing -> ing.getName().equals("Tomate") || 
                              ing.getName().equals("Queso Mozzarella") || 
                              ing.getName().equals("Albahaca"))
                .doOnNext(margarita::addIngredient)
                .blockLast();
        
        pizzas.put(margarita.getId(), margarita);

        // Pizza Pepperoni
        Pizza pepperoni = Pizza.create(
                UUID.randomUUID(),
                "Pizza Pepperoni",
                "Pizza con pepperoni, tomate y queso mozzarella",
                "https://example.com/pepperoni.jpg"
        );
        
        ingredientRepository.findAll()
                .filter(ing -> ing.getName().equals("Tomate") || 
                              ing.getName().equals("Queso Mozzarella") || 
                              ing.getName().equals("Pepperoni"))
                .doOnNext(pepperoni::addIngredient)
                .blockLast();
        
        pizzas.put(pepperoni.getId(), pepperoni);

        // Pizza Hawaiana
        Pizza hawaiana = Pizza.create(
                UUID.randomUUID(),
                "Pizza Hawaiana",
                "Pizza con jamón, piña, tomate y queso mozzarella",
                "https://example.com/hawaiana.jpg"
        );
        
        ingredientRepository.findAll()
                .filter(ing -> ing.getName().equals("Tomate") || 
                              ing.getName().equals("Queso Mozzarella") || 
                              ing.getName().equals("Jamón") ||
                              ing.getName().equals("Piña"))
                .doOnNext(hawaiana::addIngredient)
                .blockLast();
        
        pizzas.put(hawaiana.getId(), hawaiana);

        // Pizza Cuatro Estaciones
        Pizza cuatroEstaciones = Pizza.create(
                UUID.randomUUID(),
                "Pizza Cuatro Estaciones",
                "Pizza con champiñones, aceitunas, jamón y alcachofas",
                "https://example.com/cuatro-estaciones.jpg"
        );
        
        ingredientRepository.findAll()
                .filter(ing -> ing.getName().equals("Tomate") || 
                              ing.getName().equals("Queso Mozzarella") || 
                              ing.getName().equals("Champiñones") ||
                              ing.getName().equals("Aceitunas") ||
                              ing.getName().equals("Jamón"))
                .doOnNext(cuatroEstaciones::addIngredient)
                .blockLast();
        
        pizzas.put(cuatroEstaciones.getId(), cuatroEstaciones);
    }

    public Mono<Pizza> save(Pizza pizza) {
        return Mono.fromCallable(() -> {
            pizzas.put(pizza.getId(), pizza);
            return pizza;
        });
    }

    public Mono<Pizza> findById(UUID id) {
        return Mono.justOrEmpty(pizzas.get(id));
    }

    public Flux<Pizza> findAll() {
        return Flux.fromIterable(pizzas.values());
    }

    public Mono<Void> deleteById(UUID id) {
        return Mono.fromRunnable(() -> pizzas.remove(id));
    }

    public Mono<Boolean> existsById(UUID id) {
        return Mono.just(pizzas.containsKey(id));
    }

    public Mono<Pizza> update(UUID id, Pizza updatedPizza) {
        return findById(id)
                .flatMap(existingPizza -> {
                    existingPizza.update(
                            updatedPizza.getName(),
                            updatedPizza.getDescription(),
                            updatedPizza.getUrl()
                    );
                    return Mono.just(existingPizza);
                });
    }
}
