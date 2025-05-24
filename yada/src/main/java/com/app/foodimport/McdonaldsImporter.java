package com.app.foodimport;

import com.app.BasicFood;
import com.app.Food;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class McdonaldsImporter implements FoodImporter {
    
    @Override
    public List<Food> importFoods(String source) throws ImportException {
        // In a real implementation, this would fetch data from McDonald's API
        // For this example, we'll simulate a response
        
        try {
            // Create HTTP client and send request
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(source))
                .GET()
                .build();
                
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() != 200) {
                throw new ImportException("Failed to fetch data: HTTP " + response.statusCode());
            }
            
            // Parse the JSON response
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(response.body());
            
            // Extract the menu items
            JSONArray menuItems = (JSONArray) json.get("menu");
            
            List<Food> importedFoods = new ArrayList<>();
            
            // Process each menu item
            for (Object item : menuItems) {
                JSONObject foodItem = (JSONObject) item;
                String name = (String) foodItem.get("name");
                String category = (String) foodItem.get("category");
                int calories = ((Long) foodItem.get("calories")).intValue();
                
                // Generate keywords from name and category
                String keywords = name.toLowerCase() + "," + category.toLowerCase() + ",mcdonalds,fast food";
                
                // Create a BasicFood object
                BasicFood food = new BasicFood(
                    name,
                    keywords,
                    calories,
                    "Fast Food",
                    "imported"
                );
                
                importedFoods.add(food);
            }
            
            return importedFoods;
            
        } catch (IOException | InterruptedException | ParseException e) {
            throw new ImportException("Error importing from McDonald's: " + e.getMessage(), e);
        }
    }
    
    @Override
    public String getName() {
        return "McDonald's Menu Importer";
    }
    
    @Override
    public String getSourceFormatDescription() {
        return "Enter the McDonald's API URL (e.g., https://api.mcdonalds.com/menu)";
    }
}