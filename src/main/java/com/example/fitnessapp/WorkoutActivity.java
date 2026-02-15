package com.example.fitnessapp;

import android.graphics.Color; // הוספנו את זה בשביל הצבע
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class WorkoutActivity extends AppCompatActivity {

    private View layoutLocked;
    private View layoutUnlocked;
    private Button btnUnlock;
    private Button btnComplete;

    // משתנה למדידת זמן האימון הכללי
    private long startTimeMillis = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);

        layoutLocked = findViewById(R.id.layoutLocked);
        layoutUnlocked = findViewById(R.id.layoutUnlocked);
        btnUnlock = findViewById(R.id.btnUnlock);
        btnComplete = findViewById(R.id.btnCompleteWorkout);

        // מצב התחלתי
        layoutLocked.setVisibility(View.VISIBLE);
        layoutUnlocked.setVisibility(View.GONE);

        // --- כפתור פתיחה ---
        btnUnlock.setOnClickListener(v -> {
            UserProfile user = FitnessLogic.getInstance().getCurrentUser();

            if (user.spendHearts(10)) {
                startTimeMillis = System.currentTimeMillis();
                FitnessLogic.getInstance().generateNewWorkout();

                Toast.makeText(this, "Workout Started! Timer is running ⏱️", Toast.LENGTH_SHORT).show();

                layoutLocked.setVisibility(View.GONE);
                layoutUnlocked.setVisibility(View.VISIBLE);
                loadExercises();
                DatabaseManager.getInstance().saveUserProgress(user);
            } else {
                Toast.makeText(this, "Not enough hearts! Need 10.", Toast.LENGTH_SHORT).show();
            }
        });

        // --- כפתור סיום ---
        btnComplete.setOnClickListener(v -> {
            UserProfile user = FitnessLogic.getInstance().getCurrentUser();

            long endTimeMillis = System.currentTimeMillis();
            long durationMillis = endTimeMillis - startTimeMillis;
            int actualMinutes = (int) (durationMillis / (1000 * 60));

            if (actualMinutes < 1) actualMinutes = 1;

            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault());
            String currentDate = sdf.format(new java.util.Date());

            StringBuilder msg = new StringBuilder("Great Job! 🎉\n");
            msg.append("Time: ").append(actualMinutes).append(" min\n");
            msg.append("Earned: +15 Hearts ❤️\n\n");

            if (user.getLastWorkoutMinutes() > 0 && actualMinutes > user.getLastWorkoutMinutes()) {
                msg.append("🔥 New Record! Longer than last time!");
            }

            user.addHearts(15);
            user.logWorkout(actualMinutes);
            user.setLastWorkoutMinutes(actualMinutes);
            user.setLastWorkoutDateStr(currentDate);

            DatabaseManager.getInstance().saveUserProgress(user);

            new AlertDialog.Builder(this)
                    .setTitle("Workout Completed!")
                    .setMessage(msg.toString())
                    .setPositiveButton("Close", (dialog, which) -> finish())
                    .setCancelable(false)
                    .show();
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Workout Zone");
        }
    }

    private void loadExercises() {
        LinearLayout container = findViewById(R.id.exercisesContainer);
        container.removeAllViews();

        WorkoutSession session = FitnessLogic.getInstance().getCurrentSession();

        if (session != null && session.getExercises() != null) {
            for (Exercise exercise : session.getExercises()) {
                LinearLayout row = new LinearLayout(this);
                row.setOrientation(LinearLayout.HORIZONTAL);
                row.setGravity(Gravity.CENTER_VERTICAL);
                row.setBackgroundColor(0xFFFFFFFF);
                row.setPadding(24, 24, 24, 24);

                LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                rowParams.setMargins(0, 0, 0, 24);
                row.setLayoutParams(rowParams);

                CheckBox checkBox = new CheckBox(this);
                String displayText = exercise.getName();
                if (!exercise.getEquipmentNeeded().equals("None")) {
                    displayText += "\n(" + exercise.getEquipmentNeeded() + ")";
                }
                checkBox.setText(displayText);
                checkBox.setTextSize(18);

                // >>> התיקון החשוב: צבע שחור לטקסט <<<
                checkBox.setTextColor(Color.BLACK);

                LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                        0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
                checkBox.setLayoutParams(textParams);

                Button btnTimer = new Button(this);
                btnTimer.setText("⏱️ Timer");
                btnTimer.setBackgroundColor(0xFFFFD700);
                btnTimer.setTextColor(0xFF1A237E);
                btnTimer.setOnClickListener(v -> showDurationSetupDialog(exercise));

                row.addView(checkBox);
                row.addView(btnTimer);
                container.addView(row);
            }
        } else {
            Toast.makeText(this, "Error generating workout!", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDurationSetupDialog(Exercise exercise) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setPadding(50, 40, 50, 10);
        layout.setGravity(Gravity.CENTER);

        EditText etMinutes = new EditText(this);
        etMinutes.setHint("Min");
        etMinutes.setInputType(InputType.TYPE_CLASS_NUMBER);
        etMinutes.setGravity(Gravity.CENTER);
        etMinutes.setTextColor(Color.BLACK); // שיהיה קריא
        etMinutes.setText(String.valueOf(exercise.getDurationMinutes()));

        EditText etSeconds = new EditText(this);
        etSeconds.setHint("Sec");
        etSeconds.setInputType(InputType.TYPE_CLASS_NUMBER);
        etSeconds.setGravity(Gravity.CENTER);
        etSeconds.setTextColor(Color.BLACK); // שיהיה קריא
        etSeconds.setText("00");

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        params.setMargins(10, 0, 10, 0);

        layout.addView(etMinutes, params);
        layout.addView(etSeconds, params);

        new AlertDialog.Builder(this)
                .setTitle("Set Time for " + exercise.getName())
                .setMessage("Enter duration:")
                .setView(layout)
                .setPositiveButton("START", (dialog, which) -> {
                    String minStr = etMinutes.getText().toString();
                    String secStr = etSeconds.getText().toString();

                    int mins = minStr.isEmpty() ? 0 : Integer.parseInt(minStr);
                    int secs = secStr.isEmpty() ? 0 : Integer.parseInt(secStr);

                    if (mins == 0 && secs == 0) {
                        Toast.makeText(this, "Please enter time", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    long totalMillis = (mins * 60 + secs) * 1000L;
                    startCountDownTimer(exercise.getName(), totalMillis);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void startCountDownTimer(String exerciseName, long durationMillis) {
        TextView tvTimer = new TextView(this);
        tvTimer.setTextSize(45);
        tvTimer.setGravity(Gravity.CENTER);
        tvTimer.setPadding(0, 60, 0, 60);
        tvTimer.setTextColor(0xFFD32F2F);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(exerciseName)
                .setView(tvTimer)
                .setNegativeButton("Stop", (d, w) -> {})
                .setCancelable(false)
                .create();

        CountDownTimer timer = new CountDownTimer(durationMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long totalSeconds = millisUntilFinished / 1000;
                long minutes = totalSeconds / 60;
                long seconds = totalSeconds % 60;
                tvTimer.setText(String.format("%02d:%02d", minutes, seconds));
            }

            @Override
            public void onFinish() {
                tvTimer.setText("DONE! ✅");
                tvTimer.setTextColor(0xFF388E3C);
            }
        };

        dialog.setOnShowListener(d -> timer.start());
        dialog.setOnDismissListener(d -> timer.cancel());
        dialog.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}