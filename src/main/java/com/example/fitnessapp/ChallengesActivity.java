package com.example.fitnessapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ChallengesActivity extends AppCompatActivity {

    private TextView tvHearts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenges);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Challenges 🏆");
        }

        tvHearts = findViewById(R.id.tvHeartsBalance);
        updateHeartsDisplay();

        // הגדרת הכפתורים לאתגרים
        setupChallenge(R.id.btnChallenge1, "Run 1 km 🏃", 20);
        setupChallenge(R.id.btnChallenge2, "50 Push-ups 💪", 30);
        setupChallenge(R.id.btnChallenge3, "No Sugar Today 🍬", 50);
        setupChallenge(R.id.btnChallenge4, "Sleep 8 Hours 😴", 40);
    }

    private void updateHeartsDisplay() {
        UserProfile user = FitnessLogic.getInstance().getCurrentUser();
        if (tvHearts != null && user != null) {
            tvHearts.setText("Your Hearts: " + user.getHeartBalance() + " ❤️");
        }
    }

    private void setupChallenge(int buttonId, String title, int reward) {
        Button btn = findViewById(buttonId);
        btn.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle(title)
                    .setMessage("Did you complete this challenge?\nReward: " + reward + " Hearts")
                    .setPositiveButton("Yes! I did it", (dialog, which) -> {
                        completeChallenge(reward);
                    })
                    .setNegativeButton("Not yet", null)
                    .show();
        });
    }

    private void completeChallenge(int reward) {
        UserProfile user = FitnessLogic.getInstance().getCurrentUser();
        if (user != null) {
            user.addHearts(reward);

            // שמירה ב-Firebase
            DatabaseManager.getInstance().saveUserProgress(user);

            updateHeartsDisplay();
            Toast.makeText(this, "Challenge Complete! +" + reward + " Hearts", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}