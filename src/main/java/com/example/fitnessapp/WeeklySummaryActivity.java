package com.example.fitnessapp;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

public class WeeklySummaryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_summary);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("My Progress 📈");
        }

        TextView tvWorkouts = findViewById(R.id.tvWorkouts);
        TextView tvMinutes = findViewById(R.id.tvMinutes);
        TextView tvHearts = findViewById(R.id.tvHearts);
        TextView tvTrend = findViewById(R.id.tvWeightTrend);
        TextView tvGoalTitle = findViewById(R.id.tvGoalTitle);
        TextView tvProteinValue = findViewById(R.id.tvProteinValue);

        // הגרף
        LineChart chart = findViewById(R.id.chartWeight);

        // טעינת הנתונים הכלליים
        DatabaseManager.getInstance().loadUserProgress((hearts, workouts, minutes, lastMin, weight, weighDate, workoutDate, steps, goal, goalDate, startWeight) -> {

            if (tvWorkouts != null) tvWorkouts.setText(String.valueOf(workouts));
            if (tvMinutes != null) tvMinutes.setText(String.valueOf(minutes));
            if (tvHearts != null) tvHearts.setText(String.valueOf(hearts));

            // חלבון ומטרה (קוד מקוצר שהיה לנו קודם)
            if (tvGoalTitle != null) tvGoalTitle.setText("Goal: " + goal);
            if (weight > 0 && tvProteinValue != null) {
                tvProteinValue.setText(String.format("%.0f g", weight * 1.6));
            }
            if (tvTrend != null) {
                if (weight > 0) tvTrend.setText(weight + " kg");
                else tvTrend.setText("--");
            }
        });

        // >>> טעינת הגרף <<<
        DatabaseManager.getInstance().loadWeightHistory(weights -> {
            if (chart == null) return;

            if (weights.isEmpty()) {
                chart.setNoDataText("No weight history yet. Update your weight!");
                chart.setNoDataTextColor(Color.WHITE);
                return;
            }

            // יצירת נקודות לגרף
            List<Entry> entries = new ArrayList<>();
            for (int i = 0; i < weights.size(); i++) {
                // ציר X = מספר המדידה (1, 2, 3...)
                // ציר Y = המשקל
                entries.add(new Entry(i + 1, weights.get(i)));
            }

            // עיצוב הקו
            LineDataSet dataSet = new LineDataSet(entries, "Weight History (kg)");
            dataSet.setColor(Color.MAGENTA); // צבע הקו
            dataSet.setLineWidth(3f);
            dataSet.setCircleColor(Color.BLUE);
            dataSet.setCircleRadius(5f);
            dataSet.setValueTextSize(10f);
            dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER); // קו מעוגל ויפה

            LineData lineData = new LineData(dataSet);
            chart.setData(lineData);

            // עיצוב כללי של הגרף
            chart.getDescription().setEnabled(false);
            chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
            chart.getAxisRight().setEnabled(false); // ביטול ציר ימני
            chart.animateX(1000); // אנימציה של שנייה

            chart.invalidate(); // רענון
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}