package com.app.foodimport;

import com.app.BasicFood;
import com.app.Food;
import com.app.FoodDatabase;

import java.util.List;

/**
 * Interface for importing food data from external sources
 */
public interface FoodImporter {
    /**
     * Import foods from an external source
     * @param source The URL or identifier for the external source
     * @return List of imported foods
     * @throws ImportException if import fails
     */
    List<Food> importFoods(String source) throws ImportException;
    
    /**
     * Returns the name of the importer (e.g., "McDonald's Website", "USDA Database")
     * @return String name of the importer
     */
    String getName();
    
    /**
     * Returns a description of the format required for the source parameter
     * @return String description
     */
    String getSourceFormatDescription();
}