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

public class USDAImporter implements FoodImporter {
    
    @Override
    public List<Food> importFoods(String source) throws ImportException {
        try {
            // Create HTTP client and send request
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(source))
                .header("API-Key", getApiKey())
                .GET()
                .build();
                
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() != 200) {
                throw new ImportException("Failed to fetch data: HTTP " + response.statusCode());
            }
            
            // Parse the XML or JSON response (USDA supports both formats)
            List<Food> importedFoods = new ArrayList<>();
            
            if (source.contains("format=json")) {
                importedFoods = parseJsonResponse(response.body());
            } else {
                importedFoods = parseXmlResponse(response.body());
            }
            
            return importedFoods;
            
        } catch (IOException | InterruptedException e) {
            throw new ImportException("Error importing from USDA: " + e.getMessage(), e);
        }
    }
    
    private List<Food> parseJsonResponse(String responseBody) throws ImportException {
        List<Food> foods = new ArrayList<>();
        
        try {
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(responseBody);
            
            JSONArray foodsArray = (JSONArray) json.get("foods");
            
            for (Object item : foodsArray) {
                JSONObject foodItem = (JSONObject) item;
                JSONObject foodDetails = (JSONObject) foodItem.get("food");
                
                String name = (String) foodDetails.get("description");
                JSONObject nutrients = (JSONObject) foodDetails.get("nutrients");
                JSONObject energy = (JSONObject) nutrients.get("energy");
                int calories = ((Long) energy.get("value")).intValue();
                
                String foodGroup = (String) foodDetails.get("foodGroup");
                
                // Generate keywords
                String keywords = name.toLowerCase() + "," + foodGroup.toLowerCase() + ",usda";
                
                BasicFood food = new BasicFood(
                    name,
                    keywords,
                    calories,
                    mapUsdaGroupToCategory(foodGroup),
                    "imported"
                );
                
                foods.add(food);
            }
            
        } catch (ParseException | ClassCastException e) {
            throw new ImportException("Error parsing USDA JSON response: " + e.getMessage(), e);
        }
        
        return foods;
    }
    
    private List<Food> parseXmlResponse(String responseBody) throws ImportException {
        // In a real implementation, this would parse XML
        // For this example, we'll throw an exception
        throw new ImportException("XML parsing not implemented for USDA importer");
    }
    
    private String mapUsdaGroupToCategory(String usdaGroup) {
        // Map USDA food groups to our application's categories
        switch (usdaGroup.toLowerCase()) {
            case "dairy and egg products":
                return "Dairy";
            case "spices and herbs":
                return "Condiments";
            case "vegetables and vegetable products":
                return "Vegetables";
            case "fruits and fruit juices":
                return "Fruits";
            case "nut and seed products":
                return "Snacks";
            case "cereal grains and pasta":
                return "Grains";
            case "beef products":
            case "pork products":
            case "poultry products":
                return "Protein";
            default:
                return "Other";
        }
    }
    
    private String getApiKey() {
        // In a real application, this would be stored securely
        // For this example, return a placeholder
        return "USDA_API_KEY_PLACEHOLDER";
    }
    
    @Override
    public String getName() {
        return "USDA Food Database Importer";
    }
    
    @Override
    public String getSourceFormatDescription() {
        return "Enter the USDA Food Data Central API URL with your query parameters";
    }
}