package com.example.fitnessapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class OnboardingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        EditText etName = findViewById(R.id.etName);
        EditText etTime = findViewById(R.id.etTime);
        CheckBox cbDumbbells = findViewById(R.id.cbDumbbells);
        CheckBox cbChair = findViewById(R.id.cbChair);
        Button btnStart = findViewById(R.id.btnStart);

        btnStart.setOnClickListener(v -> {
            String name = etName.getText().toString();
            String timeStr = etTime.getText().toString();

            if (name.isEmpty() || timeStr.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            int time = Integer.parseInt(timeStr);
            List<String> equipment = new ArrayList<>();
            if (cbDumbbells.isChecked()) equipment.add("Dumbbells");
            if (cbChair.isChecked()) equipment.add("Chair");

            // יצירת משתמש ושמירתו ב-Singleton
            UserProfile newUser = new UserProfile(name, equipment, time);
            FitnessLogic.getInstance().setCurrentUser(newUser);

            // יצירת אימון ראשוני
            FitnessLogic.getInstance().generateNewWorkout();

            // מעבר לדשבורד
            Intent intent = new Intent(OnboardingActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // כדי שהמשתמש לא יחזור למסך ההרשמה בלחיצה על Back
        });
    }
}