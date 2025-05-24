package com.app;

import java.util.List;
import org.json.simple.JSONObject;

public class BasicFood extends Food {
    private int caloriesPerServing;
    private String category; // Food category (e.g., Dairy, Meat, Vegetables)
    
    public BasicFood(String name, List<String> keywords, int caloriesPerServing, String category, String owner) {
        super(name, keywords, owner);
        this.caloriesPerServing = caloriesPerServing;
        this.category = category;
    }
    
    public BasicFood(String name, String keywordsString, int caloriesPerServing, String category, String owner) {
        super(name, keywordsString, owner);
        this.caloriesPerServing = caloriesPerServing;
        this.category = category;
    }
    
    @Override
    public int getCaloriesPerServing() {
        return caloriesPerServing;
    }
    
    public String getCategory() {
        return category;
    }
    
    // Convert to JSON for storage
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("type", "basic");
        json.put("name", name);
        json.put("keywords", String.join(",", keywords));
        json.put("calories", caloriesPerServing);
        json.put("category", category);
        json.put("owner", owner);
        return json;
    }
    
    // Create from JSON
    public static BasicFood fromJSON(JSONObject json) {
        String name = (String) json.get("name");
        String keywordsString = (String) json.get("keywords");
        int calories = ((Long) json.get("calories")).intValue();
        String category = (String) json.get("category");
        String owner = (String) json.get("owner");
        
        return new BasicFood(name, keywordsString, calories, category, owner);
    }
    
    @Override
    public String toString() {
        return name + " (" + category + "): " + caloriesPerServing + " calories per serving";
    }
}