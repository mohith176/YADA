package com.app;

import java.time.LocalDate;
import org.json.simple.JSONObject;

public class LogEntry {
    private String username;
    private LocalDate date;
    private Food food;
    private double servings;
    private int entryId;
    private static int nextId = 1;
    
    public LogEntry(String username, LocalDate date, Food food, double servings) {
        this.username = username;
        this.date = date;
        this.food = food;
        this.servings = servings;
        this.entryId = nextId++;
    }
    
    public LogEntry(String username, LocalDate date, Food food, double servings, int entryId) {
        this.username = username;
        this.date = date;
        this.food = food;
        this.servings = servings;
        this.entryId = entryId;
        if (entryId >= nextId) {
            nextId = entryId + 1;
        }
    }
    
    public String getUsername() {
        return username;
    }
    
    public LocalDate getDate() {
        return date;
    }
    
    public Food getFood() {
        return food;
    }
    
    public double getServings() {
        return servings;
    }
    
    public int getEntryId() {
        return entryId;
    }
    
    public int getTotalCalories() {
        return (int)(food.getCaloriesPerServing() * servings);
    }
    
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("entryId", entryId);
        json.put("username", username);
        json.put("date", date.toString());
        json.put("foodName", food.getName());
        json.put("servings", servings);
        return json;
    }
}