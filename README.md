# Fitness-Application
FitnessApp is a comprehensive Android application built to help users stay active, track their progress, and achieve their fitness goals. It combines daily step tracking, customizable workout routines, and gamified challenges to keep users motivated.

### Logo:

<img width="180" height="180" alt="image" src="https://github.com/user-attachments/assets/856e7d0d-6a2b-46e0-b575-0e64d710cf32" />


## ✨ Key Features

* **Secure Authentication:** User login and registration using Firebase Authentication, including Email/Password and Google Sign-In integration.
* **Daily Step Tracker:** Real-time step counting utilizing the device's built-in hardware sensors (`TYPE_STEP_COUNTER`), with smart daily resets and a progress bar towards a 6,000-step goal.
* **Workout Zone:** A dedicated section for exercises. Users can spend earned "Hearts" to unlock workouts, run dedicated timers for each exercise, and log their completed sessions.
* **Daily Challenges:** Gamified tasks (e.g., "Run 1 km", "No Sugar Today") that reward users with "Hearts" upon completion.
* **Progress & Weight Tracking:** A weekly summary dashboard that tracks total workouts, trained minutes, and visualizes weight history over time using interactive charts.
* **Cloud Sync:** Seamless data storage and retrieval using Firebase Firestore, ensuring user progress is saved across sessions.

## 🛠️ Tech Stack & Libraries

* **Language:** Java
* **Environment:** Android Studio (Target SDK 35)
* **Backend & Database:** Firebase Authentication, Firebase Firestore
* **Charts:** [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart) (for weight tracking visualization)
* **Local Storage:** Android `SharedPreferences` (for offline step caching)

## 🚀 Getting Started

Follow these steps to set up the project locally on your machine.

### Prerequisites
* Android Studio (Ladybug or newer recommended)
* JDK 17
* An Android device or Emulator running API 24 (Android 7.0) or higher.

### Installation

1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/your-username/FitnessApp.git](https://github.com/your-username/FitnessApp.git)
    ```
2.  **Open the project:**
    Open Android Studio, select `File > Open`, and choose the cloned `FitnessApp` directory.
3.  **Firebase Setup:**
    * Create a new project and add an Android app with the package name `com.example.fitnessapp`.
    * Download the `google-services.json` file and place it in the `app/` directory of this project.
    * Enable **Authentication** (Email/Password & Google) and **Firestore Database** in your Firebase console.
4.  **Sync and Build:**
    * Click `Sync Project with Gradle Files` in Android Studio.
    * Build and run the application on your emulator or physical device.

      
<img width="289" height="500" alt="image" src="https://github.com/user-attachments/assets/64afab0d-3055-4970-b1f3-a7f387ec0f70" />


To function correctly, the app requests the following permissions:
* `ACTIVITY_RECOGNITION`: Required for the step counter sensor to track daily movement.
* `INTERNET`: Required for Firebase authentication and database syncing.
