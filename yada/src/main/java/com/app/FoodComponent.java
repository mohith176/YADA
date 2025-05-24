package com.app;

public class FoodComponent {
    private Food food;
    private int servings;
    
    public FoodComponent(Food food, int servings) {
        this.food = food;
        this.servings = servings;
    }
    
    public Food getFood() {
        return food;
    }
    
    public int getServings() {
        return servings;
    }
}