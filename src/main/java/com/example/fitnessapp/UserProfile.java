package com.example.fitnessapp;

import java.util.ArrayList;
import java.util.List;

public class UserProfile {
    private String name;
    private List<String> availableEquipment;
    private int age;
    private int heartBalance;
    private int workoutsCompleted;
    private int totalMinutesTrained;
    private int lastWorkoutMinutes;
    private double currentWeight;
    private String lastWeighInDate;
    private String lastWorkoutDateStr;
    private int dailySteps;
    private String goal;
    private long lastGoalSetDate;
    private double startWeight;

    public UserProfile(String name, List<String> availableEquipment, int age) {
        this.name = name;
        this.availableEquipment = availableEquipment != null ? availableEquipment : new ArrayList<>();
        this.age = age;
        this.heartBalance = 0;
        this.workoutsCompleted = 0;
        this.totalMinutesTrained = 0;
        this.lastWorkoutMinutes = 0;
        this.currentWeight = 0;
        this.lastWeighInDate = "";
        this.lastWorkoutDateStr = "Never";
        this.dailySteps = 0;
        this.goal = "Cut";
        this.lastGoalSetDate = 0;
        this.startWeight = 0;
    }

    // >>> הפונקציה שהייתה חסרה <<<
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    // שאר הגטרים והסטרים (Getters/Setters)
    public List<String> getAvailableEquipment() { return availableEquipment; }
    public void setAvailableEquipment(List<String> equipment) { this.availableEquipment = equipment; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public int getHeartBalance() { return heartBalance; }
    public void setHeartBalance(int heartBalance) { this.heartBalance = heartBalance; }

    public void addHearts(int amount) { this.heartBalance += amount; }

    public boolean spendHearts(int amount) {
        if (this.heartBalance >= amount) {
            this.heartBalance -= amount;
            return true;
        }
        return false;
    }

    public int getWorkoutsCompleted() { return workoutsCompleted; }
    public void setWorkoutsCompleted(int workoutsCompleted) { this.workoutsCompleted = workoutsCompleted; }

    public int getTotalMinutesTrained() { return totalMinutesTrained; }
    public void setTotalMinutesTrained(int totalMinutesTrained) { this.totalMinutesTrained = totalMinutesTrained; }

    public int getLastWorkoutMinutes() { return lastWorkoutMinutes; }
    public void setLastWorkoutMinutes(int lastWorkoutMinutes) { this.lastWorkoutMinutes = lastWorkoutMinutes; }

    public double getCurrentWeight() { return currentWeight; }
    public void setCurrentWeight(double currentWeight) { this.currentWeight = currentWeight; }

    public String getLastWeighInDate() { return lastWeighInDate; }
    public void setLastWeighInDate(String lastWeighInDate) { this.lastWeighInDate = lastWeighInDate; }

    public String getLastWorkoutDateStr() { return lastWorkoutDateStr; }
    public void setLastWorkoutDateStr(String lastWorkoutDateStr) { this.lastWorkoutDateStr = lastWorkoutDateStr; }

    public int getDailySteps() { return dailySteps; }
    public void setDailySteps(int steps) { this.dailySteps = steps; }

    public String getGoal() { return goal; }
    public void setGoal(String goal) { this.goal = goal; }

    public long getLastGoalSetDate() { return lastGoalSetDate; }
    public void setLastGoalSetDate(long date) { this.lastGoalSetDate = date; }

    public double getStartWeight() { return startWeight; }
    public void setStartWeight(double weight) { this.startWeight = weight; }

    public void logWorkout(int minutes) {
        this.workoutsCompleted++;
        this.totalMinutesTrained += minutes;
    }
}