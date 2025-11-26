package features.pizzas.dto;

import features.pizzas.models.Ingredient;

import java.util.UUID;

public record IngredientResponse(
        UUID id,
        String name,
        double cost
) {
    public static IngredientResponse fromEntity(Ingredient ingredient) {
        return new IngredientResponse(
                ingredient.getId(),
                ingredient.getName(),
                ingredient.getCost()
        );
    }
}
