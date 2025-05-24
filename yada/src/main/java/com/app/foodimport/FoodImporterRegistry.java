package com.app.foodimport;

import java.util.ArrayList;
import java.util.List;

public class FoodImporterRegistry {
    private static final List<FoodImporter> importers = new ArrayList<>();
    
    static {
        // Register default importers
        importers.add(new McdonaldsImporter());
        importers.add(new USDAImporter());
        // Additional importers can be registered here
        
    }
    
    /**
     * Get all registered importers
     * @return List of importers
     */
    public static List<FoodImporter> getImporters() {
        return new ArrayList<>(importers);
    }
    
    /**
     * Register a new importer
     * @param importer The importer to register
     */
    public static void registerImporter(FoodImporter importer) {
        importers.add(importer);
    }
    
    /**
     * Get an importer by index
     * @param index The index of the importer
     * @return The importer
     */
    public static FoodImporter getImporter(int index) {
        if (index >= 0 && index < importers.size()) {
            return importers.get(index);
        }
        return null;
    }
}