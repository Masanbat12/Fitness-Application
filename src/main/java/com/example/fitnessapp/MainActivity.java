package com.example.fitnessapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private TextView tvHearts, tvWelcome, tvStepCount;
    private ProgressBar progressBarSteps;

    private SensorManager sensorManager;
    private Sensor stepSensor;
    private boolean isSensorPresent = false;
    private int stepGoal = 6000;
    private boolean goalReached = false;

    // משתנים לחישוב צעדים מדויק
    private int currentStepCount = 0;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvHearts = findViewById(R.id.tvHearts);
        tvWelcome = findViewById(R.id.tvWelcome);
        tvStepCount = findViewById(R.id.tvStepCount);
        progressBarSteps = findViewById(R.id.progressBarSteps);

        Button btnWorkout = findViewById(R.id.btnWorkout);
        Button btnChallenges = findViewById(R.id.btnChallenges);
        Button btnSummary = findViewById(R.id.btnSummary);
        Button btnLogout = findViewById(R.id.btnLogout);

        // אתחול הזיכרון המקומי
        prefs = getSharedPreferences("StepCounterPrefs", MODE_PRIVATE);

        btnWorkout.setOnClickListener(v -> startActivity(new Intent(this, WorkoutActivity.class)));
        btnChallenges.setOnClickListener(v -> startActivity(new Intent(this, ChallengesActivity.class)));
        btnSummary.setOnClickListener(v -> startActivity(new Intent(this, WeeklySummaryActivity.class)));

        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                FirebaseAuth.getInstance().signOut();
                FitnessLogic.getInstance().setCurrentUser(null);
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        }

        loadUserData();
        setupStepCounter();
    }

    private void setupStepCounter() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 100);
            }
        }

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null && sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
            stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            isSensorPresent = true;
            Toast.makeText(this, "Step Counter Active 🚶", Toast.LENGTH_SHORT).show();
        } else {
            tvStepCount.setText("No Sensor");
            isSensorPresent = false;
            Toast.makeText(this, "Sensor Not Found ❌", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserData();
        if (isSensorPresent && sensorManager != null) {
            // SENSOR_DELAY_UI - עדכון מהיר יותר למסך
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // הערה: אנחנו לא מפסיקים את הליסנר כדי שימשיך לספור, אבל חוסכים בטריה אם צריך
        // אם אתה רוצה ספירה רציפה לגמרי, אל תבצע unregister כאן (אבל זה זולל סוללה).
        // הפשרה: נשמור נתונים ביציאה.
        if (isSensorPresent && sensorManager != null) {
            // sensorManager.unregisterListener(this); // ביטלתי את זה כדי שימשיך לקלוט
        }

        // שמירת הצעדים האחרונים ל-Firebase ביציאה
        UserProfile u = FitnessLogic.getInstance().getCurrentUser();
        if (u != null && currentStepCount > 0) {
            u.setDailySteps(currentStepCount);
            DatabaseManager.getInstance().saveUserProgress(u);
        }
    }

    // >>> הלוגיקה המתוקנת והחכמה לאיפוס וספירה <<<
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            int rawTotalSteps = (int) event.values[0]; // סך הצעדים מאז שהטלפון נדלק

            // 1. בדיקת תאריך היום
            String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            String lastSavedDate = prefs.getString("lastDate", "");

            // שליפת נקודת הייחוס (כמה צעדים היו בתחילת היום?)
            int stepsAtStartOfDay = prefs.getInt("stepsAtStartOfDay", -1);

            // זיהוי ריסטארט לטלפון: אם המספר הגולמי שהתקבל פתאום קטן ממה ששמרנו, סימן שהטלפון התאפס
            // או זיהוי יום חדש: התאריך השתנה
            boolean isNewDay = !todayDate.equals(lastSavedDate);
            boolean isReboot = stepsAtStartOfDay > rawTotalSteps;
            boolean isFirstRun = stepsAtStartOfDay == -1;

            if (isNewDay || isReboot || isFirstRun) {
                // איפוס נקודת הייחוס לעכשיו
                prefs.edit().putString("lastDate", todayDate).apply();
                prefs.edit().putInt("stepsAtStartOfDay", rawTotalSteps).apply();
                stepsAtStartOfDay = rawTotalSteps;

                // ביום חדש או ריסטארט מתחילים מ-0
                currentStepCount = 0;
            } else {
                // יום רגיל: מחסירים את נקודת ההתחלה מהסך הכל
                currentStepCount = rawTotalSteps - stepsAtStartOfDay;
            }

            // הגנה נוספת ממספרים שליליים
            if (currentStepCount < 0) currentStepCount = 0;

            // עדכון התצוגה
            updateStepUI();
        }
    }

    private void updateStepUI() {
        tvStepCount.setText(currentStepCount + " / " + stepGoal);
        progressBarSteps.setProgress(currentStepCount);

        if (currentStepCount >= stepGoal && !goalReached) {
            goalReached = true;
            new AlertDialog.Builder(this)
                    .setTitle("Goal Reached! 🏆")
                    .setMessage("You crushed " + stepGoal + " steps today!")
                    .setPositiveButton("Awesome", null)
                    .show();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }

    // --- שאר הפונקציות של טעינת נתונים (ללא שינוי) ---

    private void loadUserData() {
        DatabaseManager.getInstance().loadUserProgress((hearts, workouts, minutes, lastMin, weight, weighDate, workoutDate, steps, goal, goalDate, startWeight) -> {
            try {
                UserProfile user = FitnessLogic.getInstance().getCurrentUser();
                if (user != null) {
                    user.setHeartBalance(hearts);
                    user.setWorkoutsCompleted(workouts);
                    user.setTotalMinutesTrained(minutes);
                    user.setLastWorkoutMinutes(lastMin);
                    user.setCurrentWeight(weight);
                    user.setLastWeighInDate(weighDate);
                    user.setDailySteps(steps); // טוען מהשרת למקרה שהחיישן עוד לא התעדכן
                    user.setGoal(goal);
                    user.setLastGoalSetDate(goalDate);
                    user.setStartWeight(startWeight);

                    if (tvHearts != null) tvHearts.setText(String.valueOf(hearts));

                    // מציג את הנתונים מהשרת רק אם החיישן מראה 0 (למשל בהפעלה ראשונה)
                    if (currentStepCount == 0 && steps > 0) {
                        currentStepCount = steps;
                        updateStepUI();
                    }

                    if (tvWelcome != null) {
                        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        FirebaseFirestore.getInstance().collection("users").document(uid).get()
                                .addOnSuccessListener(doc -> {
                                    if (doc.exists() && doc.getString("name") != null) {
                                        tvWelcome.setText("Hi, " + doc.getString("name") + " 👋");
                                    } else {
                                        tvWelcome.setText("Hi, Guest 👋");
                                    }
                                });
                    }
                    checkGoalReminder(user);
                    checkWeighInReminder(user);
                }
            } catch (Exception e) {
                Log.e("FitnessApp", "Error UI: " + e.getMessage());
            }
        });
    }

    private void checkGoalReminder(UserProfile user) {
        long THREE_MONTHS = 90L * 24 * 60 * 60 * 1000;
        long timeSinceLastSet = System.currentTimeMillis() - user.getLastGoalSetDate();
        if (timeSinceLastSet > THREE_MONTHS || user.getLastGoalSetDate() == 0) {
            showGoalSelectionDialog(user);
        }
    }

    private void showGoalSelectionDialog(UserProfile user) {
        String[] options = {"Cut (Weight Loss) ✂️", "Bulk (Muscle Gain) 💪"};
        new AlertDialog.Builder(this)
                .setTitle("Set Goal 🎯")
                .setCancelable(false)
                .setSingleChoiceItems(options, -1, (dialog, which) -> {
                    String selectedGoal = (which == 0) ? "Cut" : "Bulk";
                    user.setGoal(selectedGoal);
                    user.setLastGoalSetDate(System.currentTimeMillis());
                    if (user.getCurrentWeight() > 0) user.setStartWeight(user.getCurrentWeight());
                    DatabaseManager.getInstance().saveUserProgress(user);
                    dialog.dismiss();
                    Toast.makeText(this, "Goal updated!", Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    private void checkWeighInReminder(UserProfile user) {
        String lastDateStr = user.getLastWeighInDate();
        if (lastDateStr == null || lastDateStr.isEmpty()) {
            showWeightDialog(user);
            return;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Date lastDate = sdf.parse(lastDateStr);
            long diff = System.currentTimeMillis() - lastDate.getTime();
            if (diff > (7L * 24 * 60 * 60 * 1000)) {
                showWeightDialog(user);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void showWeightDialog(UserProfile user) {
        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setHint("Weight (kg)");
        new AlertDialog.Builder(this)
                .setTitle("Weekly Check-in ⚖️")
                .setView(input)
                .setPositiveButton("Save", (dialog, which) -> {
                    String text = input.getText().toString();
                    if (!text.isEmpty()) {
                        double newWeight = Double.parseDouble(text);
                        if (user.getStartWeight() == 0) user.setStartWeight(newWeight);
                        user.setCurrentWeight(newWeight);
                        user.setLastWeighInDate(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date()));
                        DatabaseManager.getInstance().saveUserProgress(user);
                        DatabaseManager.getInstance().logWeightHistory(newWeight);
                        Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Later", null).show();
    }
}