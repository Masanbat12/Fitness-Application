package com.example.fitnessapp;

import java.util.List;

public class WorkoutSession {
    private List<Exercise> exercises;
    private boolean isLocked;
    private int unlockCost;
    private int totalDuration;

    public WorkoutSession(List<Exercise> exercises) {
        this.exercises = exercises;
        this.isLocked = true; // תמיד מתחיל נעול
        this.unlockCost = 10;
        this.totalDuration = 0;
        for (Exercise ex : exercises) {
            this.totalDuration += ex.getDurationMinutes();
        }
    }

    public boolean isLocked() { return isLocked; }
    public int getUnlockCost() { return unlockCost; }
    public List<Exercise> getExercises() { return exercises; }
    public int getTotalDuration() { return totalDuration; }

    public void setLocked(boolean locked) {
        this.isLocked = locked;
    }
}