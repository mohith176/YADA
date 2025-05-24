package com.app;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class App 
{
    private static final Scanner scanner = new Scanner(System.in);
    private User currentUser = null;
    private FoodDatabase foodDB = new FoodDatabase();
    private DietLog dietLog;
    private String calorieCalculationMethod = "Mifflin-St Jeor"; // Default method
    
    public static void main(String[] args)
    {
        App app = new App();
        app.initialize();
        app.showMainMenu();
    }
    
    private void initialize() {
        // Load food database
        foodDB.loadDatabase();
        // Initialize diet log
        dietLog = new DietLog(foodDB);
        
        System.out.println("\n" + 
            "██╗   ██╗ █████╗ ██████╗  █████╗ \n" +
            "╚██╗ ██╔╝██╔══██╗██╔══██╗██╔══██╗\n" +
            " ╚████╔╝ ███████║██║  ██║███████║\n" +
            "  ╚██╔╝  ██╔══██║██║  ██║██╔══██║\n" +
            "   ██║   ██║  ██║██████╔╝██║  ██║\n" +
            "   ╚═╝   ╚═╝  ╚═╝╚═════╝ ╚═╝  ╚═╝\n"
        );
    }
    
    public void showMainMenu()
    {
        while(true)
        {
            printDivider();
            System.out.println("\n     *** WELCOME TO Y.A.D.A ***     \n");
            printMenu(new String[] {
                "Signup",
                "Login",
                "Exit"
            });
            
            int choice = getIntInput("Enter your choice: ");
            
            switch(choice) {
                case 1:
                    this.Signup();
                    break;
                case 2:
                    if(this.Login()) {
                        this.Home();
                    }
                    break;
                case 3:
                    System.out.println("\nSaving database before exit...");
                    foodDB.saveDatabase();
                    dietLog.saveLog();
                    System.out.println("\nThank you for using Y.A.D.A. Goodbye!\n");
                    return;
                default:
                    System.out.println("\n[!] Invalid choice. Please try again.");
                    break;
            }
        }
    }

    public void Signup()
    {
        printDivider();
        System.out.println("\n     *** USER SIGNUP ***     \n");
        
        String username = getStringInput("Enter your username: ");
        String password = getStringInput("Enter your password: ");
        
        System.out.println("\n--- Gender ---");
        printMenu(new String[] {
            "Male",
            "Female", 
            "Other"
        });
        
        int genderChoice = getIntInput("Select your gender: ");
        String gender;
        switch(genderChoice) {
            case 1:
                gender = "Male";
                break;
            case 2:
                gender = "Female";
                break;
            default:
                gender = "Other";
                break;
        }
        
        int height = getIntInput("\nEnter your height (in cm): ");
        int weight = getIntInput("Enter your weight (in kg): ");
        int age = getIntInput("Enter your age: ");
        
        System.out.println("\n--- Activity Level ---");
        printMenu(new String[] {
            "Sedentary (little or no exercise)",
            "Light (exercise 1-3 times/week)",
            "Moderate (exercise 4-5 times/week)",
            "Active (daily exercise or intense exercise 3-4 times/week)",
            "Very Active (intense exercise 6-7 times/week)"
        });
        
        int activityChoice = getIntInput("Select your activity level: ");
        String activityLevel;
        
        switch(activityChoice) {
            case 1:
                activityLevel = "Sedentary";
                break;
            case 2:
                activityLevel = "Light";
                break;
            case 3:
                activityLevel = "Moderate";
                break;
            case 4:
                activityLevel = "Active";
                break;
            case 5:
                activityLevel = "Very Active";
                break;
            default:
                activityLevel = "Moderate"; // Default value
                break;
        }
        
        // Create a new user
        User newUser = new User(username, password, gender, height, weight, age, activityLevel);
        
        // Save the user to the database
        printDivider();
        if(newUser.saveToDatabase()) {
            System.out.println("\n✅ Signup successful! Welcome " + username + "!");
        } else {
            System.out.println("\n❌ Signup failed. Please try again with a different username.");
        }
        
        // Pause before showing the menu again
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }

    public boolean Login()
    {
        printDivider();
        System.out.println("\n     *** USER LOGIN ***     \n");
        
        String username = getStringInput("Enter your username: ");
        String password = getStringInput("Enter your password: ");
        
        // Check if user exists in the database
        User user = User.getUser(username, password);
        
        printDivider();
        if(user != null) {
            System.out.println("\n✅ Login successful! Welcome back, " + username + "!");
            currentUser = user;
            return true;
        } else {
            System.out.println("\n❌ Login failed. Please check your username and password.");
            System.out.print("\nPress Enter to continue...");
            scanner.nextLine();
            return false;
        }
    }
    
    public void Home()
    {
        while(true) {
            printDivider();
            System.out.println("\n     *** HOME MENU ***     \n");
            
            printMenu(new String[] {
                "Food Database",
                "My Diet Diary",
                "Calculate Nutritional Needs",
                "My Profile",
                "Save Database",
                "Logout"
            });
            
            int choice = getIntInput("Enter your choice: ");
            
            switch(choice) {
                case 1:
                    this.FoodSection();
                    break;
                case 2:
                    this.DietDiarySection();
                    break;
                case 3:
                    calculateNutritionalNeeds();
                    break;
                case 4:
                    viewMyProfile();
                    break;
                case 5:
                    foodDB.saveDatabase();
                    dietLog.saveLog();
                    System.out.println("\n✅ Database and diet log saved successfully!");
                    break;
                case 6:
                    currentUser = null;
                    System.out.println("\n[i] Logged out successfully!");
                    System.out.print("\nPress Enter to continue...");
                    scanner.nextLine();
                    return;
                default:
                    System.out.println("\n[!] Invalid choice. Please try again.");
                    break;
            }
            
            if (choice != 6) {
                System.out.print("\nPress Enter to continue...");
                scanner.nextLine();
            }
        }
    }

    public void FoodSection()
    {
        while(true) {
            printDivider();
            System.out.println("\n     *** FOOD DATABASE ***     \n");
            
            printMenu(new String[] {
                "Browse Foods by Category",
                "Search Foods",
                "Add New Basic Food",
                "Create Composite Food",
                "View My Custom Foods",
                "Return to Home Menu"
            });
            
            int choice = getIntInput("Enter your choice: ");
            
            switch(choice) {
                case 1:
                    browseFoodsByCategory();
                    break;
                case 2:
                    searchFoods();
                    break;
                case 3:
                    addNewBasicFood();
                    break;
                case 4:
                    createCompositeFood();
                    break;
                case 5:
                    viewMyCustomFoods();
                    break;
                case 6:
                    return;
                default:
                    System.out.println("\n[!] Invalid choice. Please try again.");
                    break;
            }
            
            if (choice != 6) {
                System.out.print("\nPress Enter to continue...");
                scanner.nextLine();
            }
        }
    }

    public void DietDiarySection() {
        while(true) {
            printDivider();
            System.out.println("\n     *** DIET DIARY ***     \n");
            
            printMenu(new String[] {
                "View Today's Log",
                "View/Edit Log for a Specific Date",
                "Add Food to Today's Log",
                "Add Food to a Specific Date",
                "Undo Last Action",
                "Return to Home Menu"
            });
            
            int choice = getIntInput("Enter your choice: ");
            
            switch(choice) {
                case 1:
                    viewDailyLog(LocalDate.now());
                    break;
                case 2:
                    viewLogForDate();
                    break;
                case 3:
                    addFoodToLog(LocalDate.now());
                    break;
                case 4:
                    addFoodToSpecificDate();
                    break;
                case 5:
                    undoLastAction();
                    break;
                case 6:
                    return;
                default:
                    System.out.println("\n[!] Invalid choice. Please try again.");
                    break;
            }
            
            if (choice != 6) {
                System.out.print("\nPress Enter to continue...");
                scanner.nextLine();
            }
        }
    }
    
    private void browseFoodsByCategory() {
        printDivider();
        System.out.println("\n     *** FOOD CATEGORIES ***     \n");
        
        List<String> categories = foodDB.getCategories();
        for (int i = 0; i < categories.size(); i++) {
            System.out.println((i+1) + ". " + categories.get(i));
        }
        System.out.println();
        
        int choice = getIntInput("Select a category (0 to cancel): ");
        if (choice == 0 || choice > categories.size()) {
            return;
        }
        
        String selectedCategory = categories.get(choice-1);
        // Make sure to pass the current username to filter foods appropriately
        List<Food> foods = foodDB.getFoodsByCategory(selectedCategory, currentUser.getUserName());        
        printDivider();
        System.out.println("\n     *** " + selectedCategory.toUpperCase() + " FOODS ***     \n");
        
        if (foods.isEmpty()) {
            System.out.println("No foods found in this category.");
            return;
        }
        
        for (int i = 0; i < foods.size(); i++) {
            Food food = foods.get(i);
            System.out.println((i+1) + ". " + food.getName() + " (" + food.getCaloriesPerServing() + " calories/serving)");
        }
        
        System.out.println("\nEnter a food number to see details (0 to go back): ");
        choice = getIntInput("");
        
        if (choice > 0 && choice <= foods.size()) {
            Food selectedFood = foods.get(choice-1);
            displayFoodDetails(selectedFood);
        }
    }
    
    private void searchFoods() {
        printDivider();
        System.out.println("\n     *** SEARCH FOODS ***     \n");
        
        printMenu(new String[] {
            "Search by Single Keyword",
            "Search by Multiple Keywords (Match ANY)",
            "Search by Multiple Keywords (Match ALL)",
            "Cancel"
        });
        
        int choice = getIntInput("Choose search method: ");
        if (choice == 4) return;
        
        List<Food> results = new ArrayList<>();
        
        switch(choice) {
            case 1:
                String searchTerm = getStringInput("Enter search term: ");
                results = foodDB.searchFoods(searchTerm, currentUser.getUserName());
                displaySearchResults(searchTerm, results);
                break;
            case 2:
                String keywordsAny = getStringInput("Enter keywords separated by spaces: ");
                results = foodDB.searchFoodsByMultipleKeywords(keywordsAny.split("\\s+"), false, currentUser.getUserName());
                displaySearchResults("ANY of: " + keywordsAny, results);
                break;
            case 3:
                String keywordsAll = getStringInput("Enter keywords separated by spaces: ");
                results = foodDB.searchFoodsByMultipleKeywords(keywordsAll.split("\\s+"), true, currentUser.getUserName());
                displaySearchResults("ALL of: " + keywordsAll, results);
                break;
        }
    }
    
    private void displaySearchResults(String query, List<Food> results) {
        printDivider();
        System.out.println("\n     *** SEARCH RESULTS FOR \"" + query + "\" ***     \n");
        
        if (results.isEmpty()) {
            System.out.println("No matching foods found.");
            return;
        }
        
        for (int i = 0; i < results.size(); i++) {
            Food food = results.get(i);
            System.out.println((i+1) + ". " + food.getName() + " (" + food.getCaloriesPerServing() + " calories/serving)");
        }
        
        System.out.println("\nEnter a food number to see details (0 to go back): ");
        int choice = getIntInput("");
        
        if (choice > 0 && choice <= results.size()) {
            Food selectedFood = results.get(choice-1);
            displayFoodDetails(selectedFood);
        }
    }
    
    private void displayFoodDetails(Food food) {
        printDivider();
        System.out.println("\n     *** FOOD DETAILS ***     \n");
        
        System.out.println("Name: " + food.getName());
        System.out.println("Calories per serving: " + food.getCaloriesPerServing());
        System.out.println("Keywords: " + String.join(", ", food.getKeywords()));
        
        if (food instanceof BasicFood) {
            BasicFood basicFood = (BasicFood) food;
            System.out.println("Category: " + basicFood.getCategory());
        }
        
        if (food instanceof CompositeFood) {
            CompositeFood composite = (CompositeFood) food;
            System.out.println("Category: " + composite.getCategory());
            System.out.println("\nIngredients:");
            
            List<FoodComponent> components = composite.getComponentsList();
            for (FoodComponent component : components) {
                System.out.println("- " + component.getServings() + " serving(s) of " + 
                                   component.getFood().getName() + " (" + 
                                   (component.getFood().getCaloriesPerServing() * component.getServings()) + " calories)");
            }
        }
        
        if (food.getOwner() != null && food.getOwner().equals(currentUser.getUserName())) {
            System.out.println("\n(This is a custom food created by you)");
        }
        
        // Option to add to log
        System.out.println("\nAdd this food to log?");
        printMenu(new String[] {
            "Add to today's log",
            "Add to another date",
            "Cancel"
        });
        
        int choice = getIntInput("Enter your choice: ");
        
        if (choice == 1) {
            double servings = getDoubleInput("Enter number of servings: ");
            dietLog.addEntry(currentUser.getUserName(), LocalDate.now(), food, servings);
            System.out.println("\n✅ Added to today's food log!");
        } else if (choice == 2) {
            String dateStr = getStringInput("Enter date (YYYY-MM-DD): ");
            try {
                LocalDate selectedDate = LocalDate.parse(dateStr);
                double servings = getDoubleInput("Enter number of servings: ");
                dietLog.addEntry(currentUser.getUserName(), selectedDate, food, servings);
                System.out.println("\n✅ Added to food log for " + dateStr + "!");
            } catch (DateTimeParseException e) {
                System.out.println("\n❌ Invalid date format. Use YYYY-MM-DD.");
            }
        }
    }
    
    private void addNewBasicFood() {
        printDivider();
        System.out.println("\n     *** ADD NEW BASIC FOOD ***     \n");
        
        String name = getStringInput("Enter food name: ");
        String category = getStringInput("Enter food category: ");
        int calories = getIntInput("Enter calories per serving: ");
        
        System.out.println("\nEnter keywords (separated by commas): ");
        String keywordsInput = scanner.nextLine();
        
        // Create a BasicFood with all required parameters
        BasicFood newFood = new BasicFood(name, keywordsInput, calories, category, currentUser.getUserName());
        
        foodDB.addFood(newFood);
        
        System.out.println("\n✅ New basic food added successfully!");
    }
    
    private void createCompositeFood() {
        printDivider();
        System.out.println("\n     *** CREATE COMPOSITE FOOD ***     \n");
        
        String name = getStringInput("Enter composite food name: ");
        String category = getStringInput("Enter food category: ");
        
        System.out.println("\nEnter keywords (separated by commas): ");
        String keywordsInput = scanner.nextLine();
        
        // Create a CompositeFood with the category parameter
        CompositeFood newComposite = new CompositeFood(name, keywordsInput, category, currentUser.getUserName());        
        boolean addingIngredients = true;
        while(addingIngredients) {
            printDivider();
            System.out.println("\n     *** ADD INGREDIENT ***     \n");
            
            String searchTerm = getStringInput("Search for ingredient (or type 'done' to finish): ");
            
            if (searchTerm.equalsIgnoreCase("done")) {
                addingIngredients = false;
                continue;
            }
            
            List<Food> results = foodDB.searchFoods(searchTerm, currentUser.getUserName());
            
            if (results.isEmpty()) {
                System.out.println("No matching foods found.");
                continue;
            }
            
            for (int i = 0; i < results.size(); i++) {
                Food food = results.get(i);
                System.out.println((i+1) + ". " + food.getName() + " (" + food.getCaloriesPerServing() + " calories/serving)");
            }
            
            int choice = getIntInput("\nSelect an ingredient (0 to cancel): ");
            
            if (choice > 0 && choice <= results.size()) {
                Food selectedFood = results.get(choice-1);
                int servings = getIntInput("Enter number of servings: ");
                
                newComposite.addComponent(new FoodComponent(selectedFood, servings));
                System.out.println("\n✅ Ingredient added: " + servings + " serving(s) of " + selectedFood.getName());
                
                System.out.println("\nCurrent ingredients in " + newComposite.getName() + ":");
                List<FoodComponent> components = newComposite.getComponentsList();
                for (FoodComponent component : components) {
                    System.out.println("- " + component.getServings() + " serving(s) of " + 
                                       component.getFood().getName());
                }
                
                System.out.println("\nTotal calories so far: " + newComposite.getCaloriesPerServing());
            }
        }
        
        if (newComposite.getComponents().isEmpty()) {
            System.out.println("\n❌ Composite food must contain at least one ingredient!");
            return;
        }
        
        foodDB.addFood(newComposite);
        System.out.println("\n✅ New composite food created successfully!");
    }
    
    private void viewMyCustomFoods() {
        printDivider();
        System.out.println("\n     *** MY CUSTOM FOODS ***     \n");
        
        List<Food> customFoods = foodDB.getUserFoods(currentUser.getUserName());
        
        if (customFoods.isEmpty()) {
            System.out.println("You haven't created any custom foods yet.");
            return;
        }
        
        for (int i = 0; i < customFoods.size(); i++) {
            Food food = customFoods.get(i);
            System.out.println((i+1) + ". " + food.getName() + " (" + 
                               (food instanceof CompositeFood ? "Composite" : "Basic") + 
                               ", " + food.getCaloriesPerServing() + " calories/serving)");
        }
        
        System.out.println("\nEnter a food number to see details (0 to go back): ");
        int choice = getIntInput("");
        
        if (choice > 0 && choice <= customFoods.size()) {
            Food selectedFood = customFoods.get(choice-1);
            displayFoodDetails(selectedFood);
        }
    }

    // Diet Diary methods with enhanced date editing capabilities
    private void viewDailyLog(LocalDate date) {
        printDivider();
        System.out.println("\n     *** FOOD LOG FOR " + date.format(DateTimeFormatter.ISO_LOCAL_DATE) + " ***     \n");
        
        List<LogEntry> dayEntries = dietLog.getEntriesForDate(currentUser.getUserName(), date);
        
        if (dayEntries.isEmpty()) {
            System.out.println("No food entries for this date.");
            System.out.println("\nWould you like to add food to this date? (Y/N)");
            String response = getStringInput("");
            
            if (response.toLowerCase().startsWith("y")) {
                addFoodToLog(date);
            }
            return;
        }
        
        int totalCalories = 0;
        for (int i = 0; i < dayEntries.size(); i++) {
            LogEntry entry = dayEntries.get(i);
            Food food = entry.getFood();
            int entryCalories = entry.getTotalCalories();
            totalCalories += entryCalories;
            
            System.out.println((i+1) + ". [ID: " + entry.getEntryId() + "] " + 
                              entry.getServings() + " serving(s) of " + 
                              food.getName() + " (" + entryCalories + " calories)");
        }
        
        System.out.println("\nTotal Calories: " + totalCalories);
        
        // Calculate and show target calories and balance
        int targetCalories = calculateTargetCalories();
        int calorieBalance = totalCalories - targetCalories;
        
        System.out.println("Target Calories: " + targetCalories);
        
        if (calorieBalance < 0) {
            System.out.println("Calories Remaining: " + Math.abs(calorieBalance));
        } else if (calorieBalance > 0) {
            System.out.println("Calories Over Target: " + calorieBalance);
        } else {
            System.out.println("Calories: Exactly at target");
        }
        
        // Enhanced options for this date's log
        printMenu(new String[] {
            "Delete an entry",
            "Add a new food to this date",
            "Modify servings for an entry",
            "Detailed calorie analysis",
            "Return to diet diary menu"
        });
        
        int option = getIntInput("Choose an option: ");
        
        switch(option) {
            case 1:
                // Delete an entry
                int entryNumber = getIntInput("Enter the number of the entry to delete (0 to cancel): ");
                if (entryNumber > 0 && entryNumber <= dayEntries.size()) {
                    LogEntry entryToDelete = dayEntries.get(entryNumber - 1);
                    if (dietLog.deleteEntry(entryToDelete.getEntryId())) {
                        System.out.println("\n✅ Entry deleted successfully!");
                    } else {
                        System.out.println("\n❌ Failed to delete entry.");
                    }
                }
                // Show the updated log
                if (!dayEntries.isEmpty()) {
                    viewDailyLog(date);
                }
                break;
            
            case 2:
                // Add a new food to this date
                addFoodToLog(date);
                break;
                
            case 3:
                // Modify servings
                int entryToModify = getIntInput("Enter the number of the entry to modify (0 to cancel): ");
                if (entryToModify > 0 && entryToModify <= dayEntries.size()) {
                    LogEntry entry = dayEntries.get(entryToModify - 1);
                    Food food = entry.getFood();
                    
                    // Need to delete the old entry and add a new one with updated servings
                    System.out.println("Current servings: " + entry.getServings());
                    double newServings = getDoubleInput("Enter new number of servings: ");
                    
                    // Delete the old entry
                    if (dietLog.deleteEntry(entry.getEntryId())) {
                        // Add the new entry with updated servings
                        dietLog.addEntry(currentUser.getUserName(), date, food, newServings);
                        System.out.println("\n✅ Servings updated successfully!");
                    } else {
                        System.out.println("\n❌ Failed to update servings.");
                    }
                    
                    // Show the updated log
                    viewDailyLog(date);
                }
                break;
                
            case 4:
                // Detailed calorie analysis
                analyzeCaloriesForDate(date, targetCalories);
                break;
                
            case 5:
                // Return to menu
                return;
        }
    }

    private void viewLogForDate() {
        printDivider();
        System.out.println("\n     *** VIEW/EDIT LOG BY DATE ***     \n");
        
        List<LocalDate> datesWithEntries = dietLog.getDatesWithEntries(currentUser.getUserName());
        
        if (datesWithEntries.isEmpty()) {
            System.out.println("You don't have any food log entries yet.");
            System.out.println("Would you like to create a log entry for a specific date? (Y/N)");
            String response = getStringInput("");
            
            if (response.toLowerCase().startsWith("y")) {
                addFoodToSpecificDate();
            }
            return;
        }
        
        System.out.println("Dates with entries:");
        for (int i = 0; i < datesWithEntries.size(); i++) {
            System.out.println((i+1) + ". " + datesWithEntries.get(i).format(DateTimeFormatter.ISO_LOCAL_DATE));
        }
        
        printMenu(new String[] {
            "Select from list above",
            "Enter a specific date (YYYY-MM-DD)",
            "Cancel"
        });
        
        int choice = getIntInput("Choose an option: ");
        
        if (choice == 1) {
            int dateChoice = getIntInput("Select a date number: ");
            if (dateChoice > 0 && dateChoice <= datesWithEntries.size()) {
                LocalDate selectedDate = datesWithEntries.get(dateChoice - 1);
                viewDailyLog(selectedDate);
            }
        } else if (choice == 2) {
            String dateStr = getStringInput("Enter date (YYYY-MM-DD): ");
            try {
                LocalDate selectedDate = LocalDate.parse(dateStr);
                viewDailyLog(selectedDate);
            } catch (DateTimeParseException e) {
                System.out.println("\n❌ Invalid date format. Use YYYY-MM-DD.");
            }
        }
    }

    private void addFoodToSpecificDate() {
        printDivider();
        System.out.println("\n     *** ADD FOOD TO SPECIFIC DATE ***     \n");
        
        // Two options: select from dates with entries, or enter a new date
        printMenu(new String[] {
            "Select from dates with entries",
            "Enter a specific date (YYYY-MM-DD)",
            "Cancel"
        });
        
        int choice = getIntInput("Choose an option: ");
        
        if (choice == 1) {
            List<LocalDate> datesWithEntries = dietLog.getDatesWithEntries(currentUser.getUserName());
            
            if (datesWithEntries.isEmpty()) {
                System.out.println("You don't have any food log entries yet.");
                return;
            }
            
            System.out.println("Dates with entries:");
            for (int i = 0; i < datesWithEntries.size(); i++) {
                System.out.println((i+1) + ". " + datesWithEntries.get(i).format(DateTimeFormatter.ISO_LOCAL_DATE));
            }
            
            int dateChoice = getIntInput("Select a date number (0 to cancel): ");
            if (dateChoice <= 0 || dateChoice > datesWithEntries.size()) {
                return;
            }
            
            LocalDate selectedDate = datesWithEntries.get(dateChoice - 1);
            addFoodToLog(selectedDate);
            
        } else if (choice == 2) {
            String dateStr = getStringInput("Enter date (YYYY-MM-DD): ");
            try {
                LocalDate selectedDate = LocalDate.parse(dateStr);
                addFoodToLog(selectedDate);
            } catch (DateTimeParseException e) {
                System.out.println("\n❌ Invalid date format. Use YYYY-MM-DD.");
            }
        }
    }

    // Modified to accept a date parameter
    private void addFoodToLog(LocalDate date) {
        printDivider();
        System.out.println("\n     *** ADD FOOD TO LOG FOR " + date.format(DateTimeFormatter.ISO_LOCAL_DATE) + " ***     \n");
        
        printMenu(new String[] {
            "Browse Foods by Category",
            "Search Foods by Keyword",
            "Search Foods by Multiple Keywords (Match ANY)",
            "Search Foods by Multiple Keywords (Match ALL)",
            "Cancel"
        });
        
        int choice = getIntInput("How would you like to find a food? ");
        
        if (choice == 5) {
            return;
        }
        
        List<Food> foodOptions = new ArrayList<>();
        
        switch(choice) {
            case 1:
                // Browse by category
                List<String> categories = foodDB.getCategories();
                for (int i = 0; i < categories.size(); i++) {
                    System.out.println((i+1) + ". " + categories.get(i));
                }
                
                int categoryChoice = getIntInput("\nSelect a category (0 to cancel): ");
                if (categoryChoice <= 0 || categoryChoice > categories.size()) {
                    return;
                }
                
                String selectedCategory = categories.get(categoryChoice - 1);
                foodOptions = foodDB.getFoodsByCategory(selectedCategory, currentUser.getUserName());
                break;
                
            case 2:
                // Search by single keyword
                String keyword = getStringInput("Enter keyword to search: ");
                foodOptions = foodDB.searchFoods(keyword, currentUser.getUserName());
                break;
                
            case 3:
                // Search by multiple keywords (any)
                String keywordsAny = getStringInput("Enter keywords separated by spaces: ");
                foodOptions = foodDB.searchFoodsByMultipleKeywords(keywordsAny.split("\\s+"), false, currentUser.getUserName());
                break;
                
            case 4:
                // Search by multiple keywords (all)
                String keywordsAll = getStringInput("Enter keywords separated by spaces: ");
                foodOptions = foodDB.searchFoodsByMultipleKeywords(keywordsAll.split("\\s+"), true, currentUser.getUserName());
                break;
        }
        
        if (foodOptions.isEmpty()) {
            System.out.println("\nNo matching foods found.");
            return;
        }
        
        // Display the foods
        printDivider();
        System.out.println("\n     *** SELECT FOOD TO ADD ***     \n");
        
        for (int i = 0; i < foodOptions.size(); i++) {
            Food food = foodOptions.get(i);
            System.out.println((i+1) + ". " + food.getName() + " (" + food.getCaloriesPerServing() + " calories/serving)");
        }
        
        int foodChoice = getIntInput("\nSelect a food (0 to cancel): ");
        
        if (foodChoice <= 0 || foodChoice > foodOptions.size()) {
            return;
        }
        
        Food selectedFood = foodOptions.get(foodChoice - 1);
        double servings = getDoubleInput("Enter number of servings: ");
        
        dietLog.addEntry(currentUser.getUserName(), date, selectedFood, servings);
        
        System.out.println("\n✅ Added " + servings + " serving(s) of " + 
                        selectedFood.getName() + " to log for " + date.format(DateTimeFormatter.ISO_LOCAL_DATE) +
                        " (" + (int)(selectedFood.getCaloriesPerServing() * servings) + " calories)");
        
        // Ask if the user wants to add more food to this date
        System.out.println("\nAdd another food to this date? (Y/N)");
        String response = getStringInput("");
        
        if (response.toLowerCase().startsWith("y")) {
            addFoodToLog(date);
        }
    }

    // No changes needed for addFoodToLog() without parameters - it calls the one with a date
    private void addFoodToLog() {
        addFoodToLog(LocalDate.now());
    }

    private void undoLastAction() {
        if (dietLog.canUndo()) {
            dietLog.undo();
            System.out.println("\n✅ Last action undone successfully.");
        } else {
            System.out.println("\n❌ Nothing to undo.");
        }
    }
    
    private void calculateNutritionalNeeds() {
        printDivider();
        System.out.println("\n     *** NUTRITIONAL NEEDS CALCULATOR ***     \n");
        
        // Retrieve user profile data
        int age = currentUser.getAge();
        int weight = currentUser.getWeight();
        int height = currentUser.getHeight();
        String gender = currentUser.getGender();
        String activityLevel = currentUser.getActivityLevel();
        
        // Calculate BMR using the selected method
        double bmr = calculateBMR(weight, height, age, gender);
        
        // Apply activity factor to get TDEE (Total Daily Energy Expenditure)
        double activityFactor;
        switch (activityLevel) {
            case "Sedentary":
                activityFactor = 1.2;
                break;
            case "Light":
                activityFactor = 1.375;
                break;
            case "Moderate":
                activityFactor = 1.55;
                break;
            case "Active":
                activityFactor = 1.725;
                break;
            case "Very Active":
                activityFactor = 1.9;
                break;
            default:
                activityFactor = 1.55;
                break;
        }
        
        double tdee = bmr * activityFactor;
        
        System.out.println("Here are your estimated daily nutritional needs:");
        System.out.println("\nCalculation Method: " + calorieCalculationMethod);
        System.out.println("Basal Metabolic Rate (BMR): " + Math.round(bmr) + " calories");
        System.out.println("Total Daily Energy Expenditure (TDEE): " + Math.round(tdee) + " calories");
        
        System.out.println("\nRecommended Daily Calorie Intake:");
        System.out.println("• For weight maintenance: " + Math.round(tdee) + " calories");
        System.out.println("• For mild weight loss (0.5kg/week): " + Math.round(tdee - 500) + " calories");
        System.out.println("• For weight gain (0.5kg/week): " + Math.round(tdee + 500) + " calories");
        
        // Show macronutrient recommendations
        System.out.println("\nRecommended Macronutrient Distribution:");
        System.out.println("• Protein: " + Math.round(tdee * 0.3 / 4) + "-" + Math.round(tdee * 0.35 / 4) + "g (" + 
                          Math.round(tdee * 0.3) + "-" + Math.round(tdee * 0.35) + " calories)");
        System.out.println("• Carbohydrates: " + Math.round(tdee * 0.45 / 4) + "-" + Math.round(tdee * 0.55 / 4) + 
                          "g (" + Math.round(tdee * 0.45) + "-" + Math.round(tdee * 0.55) + " calories)");
        System.out.println("• Fats: " + Math.round(tdee * 0.2 / 9) + "-" + Math.round(tdee * 0.35 / 9) + 
                          "g (" + Math.round(tdee * 0.2) + "-" + Math.round(tdee * 0.35) + " calories)");
        
        // Option to change calculation method
        System.out.println("\nWould you like to compare with a different calculation method? (Y/N)");
        String response = getStringInput("");
        
        if (response.toLowerCase().startsWith("y")) {
            changeCalculationMethod();
            calculateNutritionalNeeds(); // Recalculate with the new method
        } else {
            // Ask which date to analyze
            System.out.println("\nWould you like to see calorie analysis for:");
            printMenu(new String[] {
                "Today",
                "Another date",
                "Return to menu"
            });
            
            int choice = getIntInput("Enter your choice: ");
            
            if (choice == 1) {
                analyzeCaloriesForDate(LocalDate.now(), (int)Math.round(tdee));
            } else if (choice == 2) {
                selectDateForAnalysis((int)Math.round(tdee));
            }
        }
    }

    // Helper method to calculate BMR using the selected method
    private double calculateBMR(int weight, int height, int age, String gender) {
        boolean isMale = gender.equalsIgnoreCase("Male");
        
        switch (calorieCalculationMethod) {
            case "Mifflin-St Jeor": // Most widely used modern formula
                if (isMale) {
                    return 10 * weight + 6.25 * height - 5 * age + 5;
                } else {
                    return 10 * weight + 6.25 * height - 5 * age - 161;
                }
                
            case "Harris-Benedict": // Classic formula
                if (isMale) {
                    return 13.397 * weight + 4.799 * height - 5.677 * age + 88.362;
                } else {
                    return 9.247 * weight + 3.098 * height - 4.330 * age + 447.593;
                }
                
            case "Katch-McArdle": // Uses lean body mass, we'll estimate
                // Estimate body fat percentage based on BMI
                double bmi = weight / Math.pow(height/100.0, 2);
                double bodyFatPercentage;
                
                if (isMale) {
                    bodyFatPercentage = (1.20 * bmi) + (0.23 * age) - 16.2;
                } else {
                    bodyFatPercentage = (1.20 * bmi) + (0.23 * age) - 5.4;
                }
                
                // Ensure reasonable range for body fat percentage
                bodyFatPercentage = Math.max(5, Math.min(bodyFatPercentage, 50));
                
                // Calculate lean body mass
                double lbm = weight * (1 - bodyFatPercentage/100);
                
                // Katch-McArdle formula
                return 370 + (21.6 * lbm);
                
            default:
                return 0; // Should never happen
        }
    }

    // Method to change the calorie calculation method
    private void changeCalculationMethod() {
        printDivider();
        System.out.println("\n     *** CHANGE CALCULATION METHOD ***     \n");
        
        printMenu(new String[] {
            "Mifflin-St Jeor (Recommended for most people)",
            "Harris-Benedict (Classic formula)",
            "Katch-McArdle (Uses lean body mass estimate)",
            "Cancel"
        });
        
        int choice = getIntInput("Select calculation method: ");
        
        switch(choice) {
            case 1:
                calorieCalculationMethod = "Mifflin-St Jeor";
                System.out.println("\n✅ Changed to Mifflin-St Jeor formula");
                break;
            case 2:
                calorieCalculationMethod = "Harris-Benedict";
                System.out.println("\n✅ Changed to Harris-Benedict formula");
                break;
            case 3:
                calorieCalculationMethod = "Katch-McArdle";
                System.out.println("\n✅ Changed to Katch-McArdle formula");
                break;
            default:
                // Keep current method
                break;
        }
    }

    // Method to select a date for calorie analysis
    private void selectDateForAnalysis(int targetCalories) {
        printDivider();
        System.out.println("\n     *** SELECT DATE FOR ANALYSIS ***     \n");
        
        List<LocalDate> datesWithEntries = dietLog.getDatesWithEntries(currentUser.getUserName());
        
        if (datesWithEntries.isEmpty()) {
            System.out.println("No food log entries found for analysis.");
            return;
        }
        
        System.out.println("Dates with food log entries:");
        for (int i = 0; i < datesWithEntries.size(); i++) {
            System.out.println((i+1) + ". " + datesWithEntries.get(i).format(DateTimeFormatter.ISO_LOCAL_DATE));
        }
        
        int choice = getIntInput("\nSelect a date (0 to cancel): ");
        
        if (choice > 0 && choice <= datesWithEntries.size()) {
            LocalDate selectedDate = datesWithEntries.get(choice - 1);
            analyzeCaloriesForDate(selectedDate, targetCalories);
        }
    }

    // Method to analyze calories for a specific date
    private void analyzeCaloriesForDate(LocalDate date, int targetCalories) {
        printDivider();
        System.out.println("\n     *** CALORIE ANALYSIS FOR " + date.format(DateTimeFormatter.ISO_LOCAL_DATE) + " ***     \n");
        
        List<LogEntry> entries = dietLog.getEntriesForDate(currentUser.getUserName(), date);
        
        if (entries.isEmpty()) {
            System.out.println("No food entries for this date.");
            return;
        }
        
        int totalCalories = dietLog.getTotalCaloriesForDate(currentUser.getUserName(), date);
        int calorieBalance = totalCalories - targetCalories;
        
        System.out.println("Target calorie intake: " + targetCalories + " calories");
        System.out.println("Total calories consumed: " + totalCalories + " calories");
        
        if (calorieBalance < 0) {
            System.out.println("Calorie balance: " + Math.abs(calorieBalance) + " calories remaining");
        } else if (calorieBalance > 0) {
            System.out.println("Calorie balance: " + calorieBalance + " calories over your target");
        } else {
            System.out.println("Calorie balance: Exactly at your target");
        }
        
        // Visual representation of calorie balance
        System.out.println("\nCalorie Progress:");
        int percent = (int)((double)totalCalories / targetCalories * 100);
        percent = Math.min(percent, 100); // Cap at 100% for visualization
        
        System.out.print("[");
        for (int i = 0; i < 20; i++) {
            if (i < (percent / 5)) {
                System.out.print("■");
            } else {
                System.out.print("□");
            }
        }
        System.out.println("] " + percent + "%");
        
        // Show detailed food entries
        System.out.println("\nFood entries for this date:");
        for (int i = 0; i < entries.size(); i++) {
            LogEntry entry = entries.get(i);
            System.out.println((i+1) + ". " + entry.getServings() + " serving(s) of " + 
                             entry.getFood().getName() + " (" + entry.getTotalCalories() + " calories)");
        }
    }

    // New method to calculate target calories based on current user data and selected method
    private int calculateTargetCalories() {
        int age = currentUser.getAge();
        int weight = currentUser.getWeight();
        int height = currentUser.getHeight();
        String gender = currentUser.getGender();
        String activityLevel = currentUser.getActivityLevel();
        
        // Calculate BMR
        double bmr = calculateBMR(weight, height, age, gender);
        
        // Apply activity factor
        double activityFactor;
        switch (activityLevel) {
            case "Sedentary":
                activityFactor = 1.2;
                break;
            case "Light":
                activityFactor = 1.375;
                break;
            case "Moderate":
                activityFactor = 1.55;
                break;
            case "Active":
                activityFactor = 1.725;
                break;
            case "Very Active":
                activityFactor = 1.9;
                break;
            default:
                activityFactor = 1.55;
                break;
        }
        
        return (int)Math.round(bmr * activityFactor);
    }
    
    private void viewMyProfile() {
        printDivider();
        System.out.println("\n     *** MY PROFILE ***     \n");
        
        System.out.println("Username: " + currentUser.getUserName());
        System.out.println("Gender: " + currentUser.getGender());
        System.out.println("Age: " + currentUser.getAge());
        System.out.println("Height: " + currentUser.getHeight() + " cm");
        System.out.println("Weight: " + currentUser.getWeight() + " kg");
        System.out.println("Activity Level: " + currentUser.getActivityLevel());
        
        // Calculate BMI
        float bmi = (float) currentUser.getWeight() / ((float) currentUser.getHeight() / 100 * (float) currentUser.getHeight() / 100);
        System.out.println("BMI: " + String.format("%.1f", bmi));
        
        // BMI Category
        String bmiCategory;
        if (bmi < 18.5) {
            bmiCategory = "Underweight";
        } else if (bmi < 25) {
            bmiCategory = "Normal weight";
        } else if (bmi < 30) {
            bmiCategory = "Overweight";
        } else {
            bmiCategory = "Obese";
        }
        System.out.println("BMI Category: " + bmiCategory);
        
        // Show current calculation method
        System.out.println("Calorie Calculation Method: " + calorieCalculationMethod);
        
        // Show daily calorie target
        int targetCalories = calculateTargetCalories();
        System.out.println("Daily Calorie Target: " + targetCalories + " calories");
        
        // Options menu
        System.out.println("\nOptions:");
        printMenu(new String[] {
            "Update Profile",
            "Change Calculation Method",
            "Return to Home Menu"
        });
        
        int choice = getIntInput("Enter your choice: ");
        
        if (choice == 1) {
            updateProfile();
        } else if (choice == 2) {
            changeCalculationMethod();
        }
    }
    
    private void updateProfile() {
        printDivider();
        System.out.println("\n     *** UPDATE PROFILE ***     \n");
        
        printMenu(new String[] {
            "Update Weight",
            "Update Height",
            "Update Age",
            "Update Activity Level",
            "Cancel"
        });
        
        int choice = getIntInput("Enter your choice: ");
        
        if (choice == 5) return;
        
        switch(choice) {
            case 1:
                int newWeight = getIntInput("Enter new weight (kg): ");
                currentUser.setWeight(newWeight);
                System.out.println("\n✅ Weight updated successfully!");
                break;
            case 2:
                int newHeight = getIntInput("Enter new height (cm): ");
                currentUser.setHeight(newHeight);
                System.out.println("\n✅ Height updated successfully!");
                break;
            case 3:
                int newAge = getIntInput("Enter new age: ");
                currentUser.setAge(newAge);
                System.out.println("\n✅ Age updated successfully!");
                break;
            case 4:
                System.out.println("\n--- Activity Level ---");
                printMenu(new String[] {
                    "Sedentary (little or no exercise)",
                    "Light (exercise 1-3 times/week)",
                    "Moderate (exercise 4-5 times/week)",
                    "Active (daily exercise or intense exercise 3-4 times/week)",
                    "Very Active (intense exercise 6-7 times/week)"
                });
                
                int activityChoice = getIntInput("Select your activity level: ");
                String newActivityLevel;
                switch(activityChoice) {
                    case 1: newActivityLevel = "Sedentary"; break;
                    case 2: newActivityLevel = "Light"; break;
                    case 3: newActivityLevel = "Moderate"; break;
                    case 4: newActivityLevel = "Active"; break;
                    case 5: newActivityLevel = "Very Active"; break;
                    default: newActivityLevel = "Moderate"; break;
                }
                currentUser.setActivityLevel(newActivityLevel);
                System.out.println("\n✅ Activity level updated successfully!");
                break;
        }
        
        currentUser.saveChangesToDatabase();
    }
    
    // Helper methods for better input handling and formatting
    private static String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
    
    private static int getIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                int value = Integer.parseInt(scanner.nextLine().trim());
                return value;
            } catch (NumberFormatException e) {
                System.out.println("[!] Please enter a valid number.");
            }
        }
    }
    
    private static double getDoubleInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                double value = Double.parseDouble(scanner.nextLine().trim());
                if (value <= 0) {
                    System.out.println("[!] Please enter a positive number.");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("[!] Please enter a valid number.");
            }
        }
    }
    
    private static void printMenu(String[] options) {
        for (int i = 0; i < options.length; i++) {
            System.out.println((i+1) + ". " + options[i]);
        }
        System.out.println();
    }
    
    private static void printDivider() {
        System.out.println("\n========================================");
    }
}