package com.example.fitnessapp;

public class Exercise {
    private String name;
    private String equipmentNeeded;
    private int durationMinutes;

    public Exercise(String name, String equipmentNeeded, int durationMinutes) {
        this.name = name;
        this.equipmentNeeded = equipmentNeeded;
        this.durationMinutes = durationMinutes;
    }

    public String getName() { return name; }
    public String getEquipmentNeeded() { return equipmentNeeded; }
    public int getDurationMinutes() { return durationMinutes; }
}