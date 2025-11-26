package features.pizzas.dto;

import features.pizzas.models.Pizza;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public record PizzaResponse(
        UUID id,
        String name,
        String description,
        String url,
        double price,
        Set<IngredientResponse> ingredients
) {
    public static PizzaResponse fromEntity(Pizza pizza) {
        return new PizzaResponse(
                pizza.getId(),
                pizza.getName(),
                pizza.getDescription(),
                pizza.getUrl(),
                pizza.getPrice(),
                pizza.getIngredients().stream()
                        .map(IngredientResponse::fromEntity)
                        .collect(Collectors.toSet())
        );
    }
}
