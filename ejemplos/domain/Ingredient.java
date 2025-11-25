package features.pizzas.models;

import common.domain.Entity;

import java.util.UUID;

public class Ingredient extends Entity {
    private String name;
    private double cost;

    protected Ingredient(UUID id, String name, double cost) {
        super(id);
        this.name = name;
        this.cost = cost;
    }

    public static Ingredient create(UUID id, String name, double cost) {
        return new Ingredient(id, name, cost);
    }

    public void update(String name, double cost) {
        this.name = name;
        this.cost = cost;
    }

    public String getName() {
        return name;
    }

    public double getCost() {
        return cost;
    }
}
