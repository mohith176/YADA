package com.app;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

//extension to import from web
import com.app.foodimport.FoodImporter;
import com.app.foodimport.ImportException;

public class FoodDatabase {
    private static final String DB_PATH = "foodDatabase.json";
    private List<Food> foods;
    
    public FoodDatabase() {
        this.foods = new ArrayList<>();
        
        // Try to load the database
        if (!loadDatabase()) {
            // If loading failed, add default foods
            addDefaultFoods();
            saveDatabase(); // Save immediately
        } else {
            // Clean up any duplicates that might exist in the loaded database
            removeDuplicateFoods();
        }
    }
    
    // Remove any duplicate foods (with the same name)
    private void removeDuplicateFoods() {
        Set<String> foodNames = new HashSet<>();
        List<Food> uniqueFoods = new ArrayList<>();
        
        for (Food food : foods) {
            if (!foodNames.contains(food.getName().toLowerCase())) {
                foodNames.add(food.getName().toLowerCase());
                uniqueFoods.add(food);
            }
        }
        
        // Replace the foods list with the de-duplicated list
        this.foods = uniqueFoods;
    }
    
    public boolean loadDatabase() {
        try {
            if (!Files.exists(Paths.get(DB_PATH)) || Files.size(Paths.get(DB_PATH)) == 0) {
                return false;
            }
            
            // Clear existing foods list to avoid duplicates
            foods.clear();
            
            JSONParser parser = new JSONParser();
            try (FileReader reader = new FileReader(DB_PATH)) {
                JSONArray foodArray = (JSONArray) parser.parse(reader);
                
                // First pass: Load all basic foods
                for (Object obj : foodArray) {
                    JSONObject foodJson = (JSONObject) obj;
                    String type = (String) foodJson.get("type");
                    
                    if ("basic".equals(type)) {
                        BasicFood basicFood = BasicFood.fromJSON(foodJson);
                        foods.add(basicFood);
                    }
                }
                
                // Second pass: Load composite foods (now that all basic foods are loaded)
                for (Object obj : foodArray) {
                    JSONObject foodJson = (JSONObject) obj;
                    String type = (String) foodJson.get("type");
                    
                    if ("composite".equals(type)) {
                        CompositeFood compositeFood = CompositeFood.fromJSON(foodJson, this);
                        foods.add(compositeFood);
                    }
                }
                
                return true;
            }
        } catch (IOException | ParseException e) {
            System.err.println("Error loading food database: " + e.getMessage());
            return false;
        }
    }
    
    public void saveDatabase() {
        try {
            // Remove duplicates before saving
            removeDuplicateFoods();
            
            JSONArray foodArray = new JSONArray();
            
            // Add all foods to JSON array
            for (Food food : foods) {
                JSONObject foodJson;
                if (food instanceof BasicFood) {
                    foodJson = ((BasicFood) food).toJSON();
                } else if (food instanceof CompositeFood) {
                    foodJson = ((CompositeFood) food).toJSON(this);
                } else {
                    continue; // Skip unknown food types
                }
                foodArray.add(foodJson);
            }
            
            // Write to file
            try (FileWriter file = new FileWriter(DB_PATH)) {
                file.write(foodArray.toJSONString());
                file.flush();
            }
            
            System.out.println("Food database saved successfully.");
        } catch (IOException e) {
            System.err.println("Error saving food database: " + e.getMessage());
        }
    }
    
    public void addFood(Food food) {
        // Remove any existing food with the same name
        foods.removeIf(f -> f.getName().equalsIgnoreCase(food.getName()));
        foods.add(food);
    }
    
    public List<Food> getAllFoods() {
        return foods;
    }
    
    public Food getFoodByName(String name) {
        for (Food food : foods) {
            if (food.getName().equalsIgnoreCase(name)) {
                return food;
            }
        }
        return null;
    }
    
    public List<Food> getFoodsVisibleToUser(String username) {
        return foods.stream()
                .filter(food -> food.isVisibleToUser(username))
                .collect(Collectors.toList());
    }
    
    public List<BasicFood> getBasicFoodsVisibleToUser(String username) {
        return foods.stream()
                .filter(food -> food instanceof BasicFood && food.isVisibleToUser(username))
                .map(food -> (BasicFood) food)
                .collect(Collectors.toList());
    }
    
    public List<CompositeFood> getCompositeFoodsVisibleToUser(String username) {
        return foods.stream()
                .filter(food -> food instanceof CompositeFood && food.isVisibleToUser(username))
                .map(food -> (CompositeFood) food)
                .collect(Collectors.toList());
    }
    
    public List<Food> searchFoods(String query, String username) {
        String lowercaseQuery = query.toLowerCase();
        return foods.stream()
                .filter(food -> food.isVisibleToUser(username) && 
                       (food.getName().toLowerCase().contains(lowercaseQuery) || food.matchesKeyword(lowercaseQuery)))
                .collect(Collectors.toList());
    }
    
    private void addDefaultFoods() {
        // Check if default foods already exist
        if (getFoodByName("Whole Milk") != null) {
            // Default foods already exist, don't add them again
            return;
        }
        
        // Clear foods list to ensure we're starting fresh
        foods.clear();
        
        // Add default basic foods across categories
        
        // Dairy
        foods.add(new BasicFood("Whole Milk", "milk,dairy,beverage", 150, "Dairy", "default"));
        foods.add(new BasicFood("Cheddar Cheese", "cheese,dairy", 113, "Dairy", "default"));
        foods.add(new BasicFood("Plain Yogurt", "yogurt,dairy", 100, "Dairy", "default"));
        
        // Protein
        foods.add(new BasicFood("Chicken Breast", "chicken,meat,protein", 165, "Protein", "default"));
        foods.add(new BasicFood("Eggs", "egg,protein", 78, "Protein", "default"));
        foods.add(new BasicFood("Tofu", "vegetarian,vegan,protein", 76, "Protein", "default"));
        
        // Grains
        foods.add(new BasicFood("White Bread", "bread,grain,sandwich", 75, "Grains", "default"));
        foods.add(new BasicFood("Whole Wheat Bread", "bread,grain,sandwich,whole grain", 69, "Grains", "default"));
        foods.add(new BasicFood("White Rice", "rice,grain", 206, "Grains", "default"));
        
        // Vegetables
        foods.add(new BasicFood("Broccoli", "vegetable,green", 55, "Vegetables", "default"));
        foods.add(new BasicFood("Spinach", "vegetable,green,leafy", 23, "Vegetables", "default"));
        foods.add(new BasicFood("Carrot", "vegetable,orange", 50, "Vegetables", "default"));
        
        // Fruits
        foods.add(new BasicFood("Apple", "fruit,sweet", 95, "Fruits", "default"));
        foods.add(new BasicFood("Banana", "fruit,sweet", 105, "Fruits", "default"));
        foods.add(new BasicFood("Orange", "fruit,citrus", 62, "Fruits", "default"));
        
        // Condiments
        foods.add(new BasicFood("Peanut Butter", "spread,nut", 94, "Condiments", "default"));
        foods.add(new BasicFood("Strawberry Jam", "spread,sweet,fruit", 50, "Condiments", "default"));
        foods.add(new BasicFood("Mayonnaise", "condiment,sauce", 94, "Condiments", "default"));
        
        // Snacks
        foods.add(new BasicFood("Potato Chips", "snack,salty", 150, "Snacks", "default"));
        foods.add(new BasicFood("Chocolate Bar", "snack,sweet,dessert", 210, "Snacks", "default"));
        
        // Create some default composite foods
        BasicFood wheatBread = (BasicFood) getFoodByName("Whole Wheat Bread");
        BasicFood peanutButter = (BasicFood) getFoodByName("Peanut Butter");
        BasicFood jam = (BasicFood) getFoodByName("Strawberry Jam");
        
        if (wheatBread != null && peanutButter != null) {
            CompositeFood pbSandwich = new CompositeFood("Peanut Butter Sandwich", 
                "sandwich,lunch,peanut butter", "Sandwiches", "default");
            pbSandwich.addComponent(wheatBread, 2); // 2 slices of bread
            pbSandwich.addComponent(peanutButter, 1); // 1 serving of peanut butter
            foods.add(pbSandwich);
            
            if (jam != null) {
                CompositeFood pbjSandwich = new CompositeFood("PB&J Sandwich", 
                    "sandwich,lunch,peanut butter,jelly,jam", "Sandwiches", "default");
                pbjSandwich.addComponent(wheatBread, 2); // 2 slices of bread
                pbjSandwich.addComponent(peanutButter, 1); // 1 serving of peanut butter
                pbjSandwich.addComponent(jam, 1); // Add jelly
                foods.add(pbjSandwich);
            }
        }
    }
    
    public List<String> getCategories() {
        Set<String> categories = new HashSet<>();
        
        // Get categories from basic foods
        for (Food food : foods) {
            if (food instanceof BasicFood) {
                categories.add(((BasicFood) food).getCategory());
            } else if (food instanceof CompositeFood) {
                // Include categories from composite foods too
                categories.add(((CompositeFood) food).getCategory());
            }
        }
        
        List<String> sortedCategories = new ArrayList<>(categories);
        Collections.sort(sortedCategories);
        return sortedCategories;
    }

    // Get foods by category
    public List<Food> getFoodsByCategory(String category, String username) {
        return foods.stream()
            .filter(food -> food.isVisibleToUser(username) && 
                   ((food instanceof BasicFood && category.equals(((BasicFood) food).getCategory())) ||
                    (food instanceof CompositeFood && category.equals(((CompositeFood) food).getCategory()))))
            .collect(Collectors.toList());
    }

    // Get foods created by a specific user
    public List<Food> getUserFoods(String username) {
        return foods.stream()
            .filter(food -> username.equals(food.getOwner()))
            .collect(Collectors.toList());
    }

    

    // Search for foods that match multiple keywords (either all or any)
    public List<Food> searchFoodsByMultipleKeywords(String[] keywords, boolean matchAll, String username) {
        if (keywords == null || keywords.length == 0) {
            return getFoodsVisibleToUser(username);
        }
        
        return foods.stream()
            .filter(food -> food.isVisibleToUser(username) && matchesMultipleKeywords(food, keywords, matchAll))
            .collect(Collectors.toList());
    }

    private boolean matchesMultipleKeywords(Food food, String[] keywords, boolean matchAll) {
        if (matchAll) {
            // Food must match ALL keywords
            for (String keyword : keywords) {
                if (!food.matchesKeyword(keyword)) {
                    return false;
                }
            }
            return true;
        } else {
            // Food must match ANY keyword
            for (String keyword : keywords) {
                if (food.matchesKeyword(keyword)) {
                    return true;
                }
            }
            return false;
        }
    }

    // extesion to import from web

    public List<Food> importFoodsFromExternalSource(FoodImporter importer, String source, String username) {
        try {
            List<Food> importedFoods = importer.importFoods(source);
            
            // Add owner information to all imported foods
            for (Food food : importedFoods) {
                // Modify the field directly since we can't change the constructor calls in the importer
                if (food instanceof BasicFood) {
                    ((BasicFood) food).setOwner(username);
                } else if (food instanceof CompositeFood) {
                    ((CompositeFood) food).setOwner(username);
                }
                
                // Add each food to the database
                addFood(food);
            }
            
            // Save changes to the database
            saveDatabase();
            
            return importedFoods;
        } catch (ImportException e) {
            System.err.println("Import failed: " + e.getMessage());
            return List.of(); // Return empty list on failure
        }
    }
}