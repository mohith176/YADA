package com.app;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class User {
    private String UserName;
    private String Password;
    private String Gender;
    private int Height;
    private int Weight;
    private int Age;
    private String ActivityLevel;
    
    public User(String UserName, String Password, String Gender, int Height, int Weight, int Age, String ActivityLevel) {
        this.UserName = UserName;
        this.Password = Password;
        this.Gender = Gender;
        this.Height = Height;
        this.Weight = Weight;
        this.Age = Age;
        this.ActivityLevel = ActivityLevel;
    }

    /**
     * Saves this user to a JSON file database
     * @return boolean indicating success or failure
     */
    public boolean saveToDatabase() {
        String dbPath = "userDatabase.json";
        JSONParser parser = new JSONParser();
        JSONArray userList = new JSONArray();
        
        // Convert this user to JSONObject
        JSONObject userJson = new JSONObject();
        userJson.put("username", this.UserName);
        userJson.put("password", this.Password);  // Note: In a real app, you'd want to hash this
        userJson.put("gender", this.Gender);
        userJson.put("height", this.Height);
        userJson.put("weight", this.Weight);
        userJson.put("age", this.Age);
        userJson.put("activityLevel", this.ActivityLevel);
        
        try {
            // Check if file exists and isn't empty
            if (Files.exists(Paths.get(dbPath)) && Files.size(Paths.get(dbPath)) > 0) {
                // Read existing users
                try (FileReader reader = new FileReader(dbPath)) {
                    userList = (JSONArray) parser.parse(reader);
                }
                
                // Check if user already exists
                for (Object obj : userList) {
                    JSONObject user = (JSONObject) obj;
                    if (user.get("username").equals(this.UserName)) {
                        System.out.println("User already exists!");
                        return false;
                    }
                }
            }
            
            // Add new user
            userList.add(userJson);
            
            // Write back to file
            try (FileWriter file = new FileWriter(dbPath)) {
                file.write(userList.toJSONString());
                file.flush();
                return true;
            }
            
        } catch (IOException | ParseException e) {
            System.err.println("Error saving user to database: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Retrieves a user from the database by username and password
     * @param username The username to search for
     * @param password The password to validate
     * @return User object if found and password matches, null otherwise
     */
    public static User getUser(String username, String password) {
        String dbPath = "userDatabase.json";
        JSONParser parser = new JSONParser();
        
        try {
            // Check if file exists and isn't empty
            if (!Files.exists(Paths.get(dbPath)) || Files.size(Paths.get(dbPath)) == 0) {
                return null;
            }
            
            // Read existing users
            try (FileReader reader = new FileReader(dbPath)) {
                JSONArray userList = (JSONArray) parser.parse(reader);
                
                // Search for user with matching username and password
                for (Object obj : userList) {
                    JSONObject userJson = (JSONObject) obj;
                    if (userJson.get("username").equals(username) && userJson.get("password").equals(password)) {
                        // Create and return user object
                        return new User(
                            (String) userJson.get("username"),
                            (String) userJson.get("password"),
                            (String) userJson.get("gender"),
                            ((Long) userJson.get("height")).intValue(),
                            ((Long) userJson.get("weight")).intValue(),
                            ((Long) userJson.get("age")).intValue(),
                            (String) userJson.get("activityLevel")
                        );
                    }
                }
            }
        } catch (IOException | ParseException e) {
            System.err.println("Error retrieving user from database: " + e.getMessage());
        }
        
        return null;
    }

        /**
     * Returns the username of this user
     * @return String containing the username
     */
    public String getUserName() {
        return this.UserName;
    }

    // Add these methods to User.java
    public int getHeight() {
        return this.Height;
    }

    public int getWeight() {
        return this.Weight;
    }

    public int getAge() {
        return this.Age;
    }

    public String getGender() {
        return this.Gender;
    }

    public String getActivityLevel() {
        return this.ActivityLevel;
    }

    public void setHeight(int height) {
        this.Height = height;
    }

    public void setWeight(int weight) {
        this.Weight = weight;
    }

    public void setAge(int age) {
        this.Age = age;
    }

    public void setActivityLevel(String activityLevel) {
        this.ActivityLevel = activityLevel;
    }

    public boolean saveChangesToDatabase() {
        // Similar to saveToDatabase() but handles updates for existing users
        String dbPath = "userDatabase.json";
        JSONParser parser = new JSONParser();
        
        try {
            JSONArray userArray;
            
            // Check if file exists and isn't empty
            if (!Files.exists(Paths.get(dbPath)) || Files.size(Paths.get(dbPath)) == 0) {
                userArray = new JSONArray();
            } else {
                // Load existing users
                try (FileReader reader = new FileReader(dbPath)) {
                    userArray = (JSONArray) parser.parse(reader);
                    
                    // Look for existing user to update
                    for (int i = 0; i < userArray.size(); i++) {
                        JSONObject userJson = (JSONObject) userArray.get(i);
                        if (userJson.get("username").equals(this.UserName)) {
                            // Update user data
                            userJson.put("gender", this.Gender);
                            userJson.put("height", this.Height);
                            userJson.put("weight", this.Weight);
                            userJson.put("age", this.Age);
                            userJson.put("activityLevel", this.ActivityLevel);
                            
                            // Write updated array back to file
                            try (FileWriter file = new FileWriter(dbPath)) {
                                file.write(userArray.toJSONString());
                                file.flush();
                            }
                            return true;
                        }
                    }
                }
            }
            
            return false; // User not found
            
        } catch (IOException | ParseException e) {
            System.err.println("Error updating user in database: " + e.getMessage());
            return false;
        }
    }
    
}
