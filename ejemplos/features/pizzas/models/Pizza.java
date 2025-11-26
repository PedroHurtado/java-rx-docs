package features.pizzas.models;

import common.domain.Entity;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Pizza extends Entity {
    private String name;
    private String description;
    private String url;
    private Set<Ingredient> ingredients;

    protected Pizza(UUID id, String name, String description, String url) {
        super(id);
        this.name = name;
        this.description = description;
        this.url = url;
        this.ingredients = new HashSet<>();
    }

    public static Pizza create(UUID id, String name, String description, String url) {
        return new Pizza(id, name, description, url);
    }

    public void update(String name, String description, String url) {
        this.name = name;
        this.description = description;
        this.url = url;
    }

    public void addIngredient(Ingredient ingredient) {
        if (ingredient != null) {
            this.ingredients.add(ingredient);
        }
    }

    public void removeIngredient(Ingredient ingredient) {
        this.ingredients.remove(ingredient);
    }

    public double getPrice() {
        double ingredientsCost = ingredients.stream()
                .mapToDouble(Ingredient::getCost)
                .sum();
        return ingredientsCost * 1.20; // +20%
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    public Set<Ingredient> getIngredients() {
        return Collections.unmodifiableSet(ingredients);
    }
}
