package com.example.fitnessapp;

import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseManager {

    private static DatabaseManager instance;
    private FirebaseFirestore db;

    private DatabaseManager() { db = FirebaseFirestore.getInstance(); }

    public static DatabaseManager getInstance() {
        if (instance == null) instance = new DatabaseManager();
        return instance;
    }

    private String getCurrentUserId() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return (user != null) ? user.getUid() : null;
    }

    public void saveUserProgress(UserProfile user) {
        String uid = getCurrentUserId();
        if (uid == null || user == null) return;

        Map<String, Object> data = new HashMap<>();
        data.put("hearts", user.getHeartBalance());
        data.put("workouts", user.getWorkoutsCompleted());
        data.put("minutes", user.getTotalMinutesTrained());
        data.put("lastWorkoutMinutes", user.getLastWorkoutMinutes());
        data.put("currentWeight", user.getCurrentWeight());
        data.put("lastWeighInDate", user.getLastWeighInDate());
        data.put("lastWorkoutDateStr", user.getLastWorkoutDateStr());
        data.put("dailySteps", user.getDailySteps());
        data.put("goal", user.getGoal());
        data.put("lastGoalSetDate", user.getLastGoalSetDate());
        data.put("startWeight", user.getStartWeight());

        db.collection("users").document(uid)
                .set(data, SetOptions.merge())
                .addOnSuccessListener(aVoid -> Log.d("DB", "Saved user stats"));
    }

    // >>> פונקציה חדשה: הוספת נקודת ציון להיסטוריית המשקל <<<
    public void logWeightHistory(double weight) {
        String uid = getCurrentUserId();
        if (uid == null) return;

        Map<String, Object> entry = new HashMap<>();
        entry.put("weight", weight);
        entry.put("timestamp", System.currentTimeMillis());

        // שמירה בתת-אוסף (Sub-collection) כדי ליצור רשימה
        db.collection("users").document(uid).collection("weight_history")
                .add(entry);
    }

    // >>> פונקציה חדשה: טעינת ההיסטוריה לגרף <<<
    public void loadWeightHistory(final OnHistoryLoadedListener listener) {
        String uid = getCurrentUserId();
        if (uid == null) return;

        db.collection("users").document(uid).collection("weight_history")
                .orderBy("timestamp", Query.Direction.ASCENDING) // מסודר לפי זמן
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Float> weights = new ArrayList<>();
                    // המרה לרשימה פשוטה של משקלים
                    for (com.google.firebase.firestore.DocumentSnapshot doc : queryDocumentSnapshots) {
                        Double w = doc.getDouble("weight");
                        if (w != null) weights.add(w.floatValue());
                    }
                    listener.onHistoryLoaded(weights);
                });
    }

    public void loadUserProgress(final OnDataLoadedListener listener) {
        String uid = getCurrentUserId();
        if (uid == null) return;

        db.collection("users").document(uid).get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        long hearts = document.getLong("hearts") != null ? document.getLong("hearts") : 0;
                        long workouts = document.getLong("workouts") != null ? document.getLong("workouts") : 0;
                        long minutes = document.getLong("minutes") != null ? document.getLong("minutes") : 0;
                        long lastMin = document.getLong("lastWorkoutMinutes") != null ? document.getLong("lastWorkoutMinutes") : 0;
                        long steps = document.getLong("dailySteps") != null ? document.getLong("dailySteps") : 0;
                        long goalDate = document.getLong("lastGoalSetDate") != null ? document.getLong("lastGoalSetDate") : 0;

                        double curWeight = document.getDouble("currentWeight") != null ? document.getDouble("currentWeight") : 0.0;
                        double startWeight = document.getDouble("startWeight") != null ? document.getDouble("startWeight") : 0.0;

                        String weighDate = document.getString("lastWeighInDate") != null ? document.getString("lastWeighInDate") : "";
                        String workoutDate = document.getString("lastWorkoutDateStr") != null ? document.getString("lastWorkoutDateStr") : "Never";
                        String goal = document.getString("goal") != null ? document.getString("goal") : "Cut";

                        listener.onLoaded((int)hearts, (int)workouts, (int)minutes, (int)lastMin, curWeight, weighDate, workoutDate, (int)steps, goal, goalDate, startWeight);
                    } else {
                        listener.onLoaded(0, 0, 0, 0, 0.0, "", "Never", 0, "Cut", 0, 0.0);
                    }
                });
    }

    public interface OnDataLoadedListener {
        void onLoaded(int hearts, int workouts, int minutes, int lastMin, double weight, String weighDate, String workoutDate, int steps, String goal, long goalDate, double startWeight);
    }

    // ממשק חדש לגרף
    public interface OnHistoryLoadedListener {
        void onHistoryLoaded(List<Float> weights);
    }

    // הוסף את זה בתוך המחלקה DatabaseManager
    public void saveUserNameOnly(String name) {
        String uid = getCurrentUserId();
        if (uid == null) return;

        Map<String, Object> data = new HashMap<>();
        data.put("name", name); // שמירת השם

        // שימוש ב-SetOptions.merge() מבטיח שאם יש כבר נתונים, הם לא יימחקו
        db.collection("users").document(uid)
                .set(data, SetOptions.merge())
                .addOnSuccessListener(aVoid -> Log.d("DB", "Name saved: " + name));
    }
}