package com.app;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Food {
    protected String name;
    protected List<String> keywords;
    protected String owner; // Either "default" for default foods or username for user-created foods
    
    public Food(String name, List<String> keywords, String owner) {
        this.name = name;
        this.keywords = keywords;
        this.owner = owner;
    }
    
    public Food(String name, String keywordsString, String owner) {
        this.name = name;
        this.owner = owner;
        
        // Parse comma-separated keywords
        this.keywords = new ArrayList<>();
        if (keywordsString != null && !keywordsString.trim().isEmpty()) {
            String[] keywordArray = keywordsString.toLowerCase().split(",");
            for (String keyword : keywordArray) {
                this.keywords.add(keyword.trim());
            }
        }
    }
    
    // Abstract method to be implemented by derived classes
    public abstract int getCaloriesPerServing();
    
    // Common methods
    public String getName() {
        return name;
    }
    
    public List<String> getKeywords() {
        return keywords;
    }
    
    public String getOwner() {
        return owner;
    }
    
    public boolean matchesKeyword(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return true; // Empty keyword matches everything
        }
        
        String lowerKeyword = keyword.toLowerCase().trim();
        
        // Check if name contains the keyword
        if (name.toLowerCase().contains(lowerKeyword)) {
            return true;
        }
        
        // Check if any of the keywords match
        for (String k : keywords) {
            if (k.contains(lowerKeyword)) {
                return true;
            }
        }
        
        return false;
    }
    
    public boolean isVisibleToUser(String username) {
        return "default".equals(owner) || username.equals(owner);
    }
    
    // extesion to import from web
    public void setOwner(String owner) {
        this.owner = owner;
    }
}