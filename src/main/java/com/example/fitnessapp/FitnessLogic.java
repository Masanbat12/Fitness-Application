package com.example.fitnessapp;

import java.util.ArrayList;
import java.util.List;

public class FitnessLogic {
    private static FitnessLogic instance;
    private UserProfile currentUser;
    private WorkoutSession currentSession;

    private FitnessLogic() {
        // יצירת משתמש ברירת מחדל למניעת קריסות
        List<String> equipment = new ArrayList<>();
        equipment.add("None");
        currentUser = new UserProfile("Guest", equipment, 30);
    }

    public static FitnessLogic getInstance() {
        if (instance == null) {
            instance = new FitnessLogic();
        }
        return instance;
    }

    public UserProfile getCurrentUser() {
        if (currentUser == null) {
            List<String> equipment = new ArrayList<>();
            equipment.add("None");
            currentUser = new UserProfile("Guest", equipment, 30);
        }
        return currentUser;
    }

    public void setCurrentUser(UserProfile user) {
        this.currentUser = user;
    }

    public WorkoutSession getCurrentSession() {
        return currentSession;
    }

    public void startSession(List<Exercise> exercises) {
        this.currentSession = new WorkoutSession(exercises);
    }

    // --- פונקציה מורחבת ליצירת אימון עם ציוד ביתי ---
    public void generateNewWorkout() {
        List<Exercise> exercises = new ArrayList<>();

        // 1. חימום (תמיד)
        exercises.add(new Exercise("Jumping Jacks", "None", 1));
        exercises.add(new Exercise("High Knees", "None", 1));

        // 2. תרגילי כוח מרכזיים (גוף מלא)
        exercises.add(new Exercise("Squats", "None", 2));
        exercises.add(new Exercise("Push-ups (Any variation)", "None", 2));
        exercises.add(new Exercise("Lunges (Alternating)", "None", 2));

        // 3. תוספות לפי ציוד מיוחד (אם נבחר)
        if (currentUser != null) {
            List<String> equipment = currentUser.getAvailableEquipment();

            if (equipment.contains("Dumbbells")) {
                exercises.add(new Exercise("Dumbbell Thrusters", "Dumbbells", 3));
                exercises.add(new Exercise("Renegade Rows", "Dumbbells", 2));
            }
            if (equipment.contains("Pull-up Bar")) {
                exercises.add(new Exercise("Pull-ups / Chin-ups", "Pull-up Bar", 2));
                exercises.add(new Exercise("Hanging Knee Raise", "Pull-up Bar", 1));
            }
        }

        // 4. >>> תרגילים עם ציוד ביתי נפוץ (תיקון: מספרים שלמים בלבד) <<<

        // תיק גב (Backpack)
        exercises.add(new Exercise("Backpack Rows (Use a heavy bag)", "Backpack", 2));
        exercises.add(new Exercise("Backpack Front Squat", "Backpack", 2));

        // כיסא יציב (Chair)
        exercises.add(new Exercise("Chair Dips (Triceps)", "Stable Chair", 2)); // שונה מ-1.5 ל-2
        exercises.add(new Exercise("Step-ups onto Chair", "Stable Chair", 2));

        // בקבוקי מים (Water Bottles)
        exercises.add(new Exercise("Lateral Raises (Water Bottles)", "Water Bottles", 2)); // שונה מ-1.5 ל-2
        exercises.add(new Exercise("Bicep Curls (Water Bottles)", "Water Bottles", 2)); // שונה מ-1.5 ל-2

        // רצפה / מגבת (Floor / Towel)
        exercises.add(new Exercise("Glute Bridges", "Floor", 2));
        exercises.add(new Exercise("Superman (Back extension)", "Floor", 1));
        exercises.add(new Exercise("Mountain Climbers", "Floor", 1));

        // 5. סיום וליבה (Core)
        exercises.add(new Exercise("Plank", "None", 1)); // שונה מ-1.5 ל-1
        exercises.add(new Exercise("Russian Twists", "None", 1));

        // יצירת הסשן
        startSession(exercises);
    }
}