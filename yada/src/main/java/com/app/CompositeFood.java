package com.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class CompositeFood extends Food {
    private Map<Food, Integer> components; // Food component and number of servings
    private String category;
    
    public CompositeFood(String name, List<String> keywords, Map<Food, Integer> components, String owner) {
        super(name, keywords, owner);
        this.components = components;
        this.category = "Composite Foods"; // Default category
    }
    
    public CompositeFood(String name, String keywordsString, String owner) {
        super(name, keywordsString, owner);
        this.components = new HashMap<>();
        this.category = "Composite Foods"; // Default category
    }
    
    public CompositeFood(String name, String keywordsString, String category, String owner) {
        super(name, keywordsString, owner);
        this.components = new HashMap<>();
        this.category = category;
    }
    
    public void addComponent(Food food, int servings) {
        components.put(food, servings);
    }
    
    // Add support for FoodComponent class
    public void addComponent(FoodComponent component) {
        addComponent(component.getFood(), component.getServings());
    }
    
    @Override
    public int getCaloriesPerServing() {
        int totalCalories = 0;
        for (Map.Entry<Food, Integer> entry : components.entrySet()) {
            Food component = entry.getKey();
            int servings = entry.getValue();
            totalCalories += component.getCaloriesPerServing() * servings;
        }
        return totalCalories;
    }
    
    public Map<Food, Integer> getComponents() {
        return components;
    }
    
    // Get components as a list of FoodComponent objects for easier display
    public List<FoodComponent> getComponentsList() {
        List<FoodComponent> componentList = new ArrayList<>();
        for (Map.Entry<Food, Integer> entry : components.entrySet()) {
            componentList.add(new FoodComponent(entry.getKey(), entry.getValue()));
        }
        return componentList;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    // Convert to JSON for storage
    public JSONObject toJSON(FoodDatabase foodDB) {
        JSONObject json = new JSONObject();
        json.put("type", "composite");
        json.put("name", name);
        json.put("keywords", String.join(",", keywords));
        json.put("owner", owner);
        json.put("category", category);
        
        JSONArray componentsArray = new JSONArray();
        for (Map.Entry<Food, Integer> entry : components.entrySet()) {
            JSONObject componentJson = new JSONObject();
            componentJson.put("foodName", entry.getKey().getName());
            componentJson.put("servings", entry.getValue());
            componentsArray.add(componentJson);
        }
        
        json.put("components", componentsArray);
        return json;
    }
    
    // Create from JSON
    public static CompositeFood fromJSON(JSONObject json, FoodDatabase foodDB) {
        String name = (String) json.get("name");
        String keywordsString = (String) json.get("keywords");
        String owner = (String) json.get("owner");
        String category = (String) json.get("category");
        if (category == null) {
            category = "Composite Foods"; // Default if not found in JSON
        }
        
        CompositeFood compositeFood = new CompositeFood(name, keywordsString, category, owner);
        
        JSONArray componentsArray = (JSONArray) json.get("components");
        for (Object compObj : componentsArray) {
            JSONObject componentJson = (JSONObject) compObj;
            String foodName = (String) componentJson.get("foodName");
            int servings = ((Long) componentJson.get("servings")).intValue();
            
            Food component = foodDB.getFoodByName(foodName);
            if (component != null) {
                compositeFood.addComponent(component, servings);
            }
        }
        
        return compositeFood;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(" (").append(getCaloriesPerServing()).append(" calories per serving)\n");
        sb.append("Category: ").append(category).append("\n");
        sb.append("Components:\n");
        
        for (Map.Entry<Food, Integer> entry : components.entrySet()) {
            Food component = entry.getKey();
            int servings = entry.getValue();
            sb.append("  - ").append(component.getName())
              .append(" (").append(servings).append(" serving")
              .append(servings > 1 ? "s" : "").append(")\n");
        }
        
        return sb.toString();
    }
}