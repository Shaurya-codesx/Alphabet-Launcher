# Alphabet Launcher

A highly optimized, minimal, and premium Android Launcher built entirely with Jetpack Compose and Clean Architecture. Designed to be fast, beautiful, and accessible.

[![Alphabet Launcher Demo Video](https://img.youtube.com/vi/9VlcYtM-LFw/0.jpg)](https://youtube.com/shorts/9VlcYtM-LFw?feature=share)

*Click the image above to watch the demo video on YouTube!*

## 🌟 Core Features

- **Physics-Based Alphabet Bar:** A custom-built, gesture-driven alphabet navigation bar. As you drag your thumb, the letters smoothly bulge outward using precision `cos`/`sin` trigonometric math, accompanied by a beautifully styled floating indicator bubble.
- **Left-Handed Mode:** A true accessibility feature. Toggling Left-Handed Mode (accessible via the "Launcher Settings" dummy app under 'S') instantly flips the entire UI layout. The alphabet bar snaps to the left, and the physical bulge math reverses its amplitude to naturally curve around a left thumb.
- **Favorites System:** Long-press any app to instantly add or remove it from your favorites list. Favorites are automatically persisted using `SharedPreferences` and populate the top of your screen in the default resting state.
- **Swipe-Up Search:** Need an app quickly? Swipe up anywhere on the home screen to bring up a sleek search overlay with an automatically focused keyboard. 
- **Haptic Feedback:** The alphabet bar provides tactile, subtle vibrations exactly when your thumb crosses a new letter boundary, creating a premium mechanical feel.
- **Dynamic Monet Theming:** The UI strictly follows Material 3 guidelines, adapting its accent colors (like the clock date and highlighted letters) to your system's wallpaper theme.

## 🛠️ Architecture & Implementation Details

This project was built to strict engineering standards, prioritizing a clear separation of concerns, a state-driven UI, and a fluid user experience.

### 1. Separation of Animation & UI Logic
The complex touch tracking and physics calculations for the Alphabet Bar are kept completely separate from the visual components. Instead of mixing math with UI code, all the logic for calculating the curved bulge animation is isolated inside a dedicated `AlphabetBarState` class. This ensures the Jetpack Compose UI remains clean, declarative, and focused solely on drawing what is on the screen, exactly as modern Android development intended.

### 2. Clean Architecture & Dependency Injection
Powered by **Dagger Hilt**, the codebase is organized into clear `Domain`, `Data`, and `Presentation` layers. The `AppRepository` serves as a boundary that hides the messy details of fetching apps from the Android system. By structuring it this way, the `LauncherViewModel` doesn't need to rely on Android-specific classes. This makes the core business logic—like filtering search results or managing favorites—highly cohesive, modular, and easy to test.

### 3. Smooth Performance (Zero Dropped Frames)
A common issue with custom launchers is that rendering hundreds of app icons as you scroll can cause the app to stutter or drop frames. This often happens because the app tries to convert heavy image files on the main UI thread. To guarantee a silky smooth scrolling experience, this launcher uses Kotlin Coroutines to proactively load, convert, and cache all app icons in the background immediately when the app opens. Because the images are ready to go in memory, the UI can scroll at 60 or 120 frames per second without breaking a sweat.

## 🚀 Getting Started

### Prerequisites
- Android Studio Ladybug (or newer)
- Gradle 8.11.1+
- Minimum SDK: 26 (Android 8.0)

### Installation
1. Clone the repository.
2. Open the project in Android Studio.
3. Build and run the app on a physical Android device or emulator.
4. When prompted, select "Alphabet Launcher" as your default home app.

## 🤖 AI Tools Used

In accordance with transparency guidelines, I utilized **Antigravity (Google's Agentic AI Assistant)** during the development of this project. It was primarily used as a pair-programming partner for the following tasks:
* **Trigonometric Math:** Assisting with the `cos`/`sin` mathematical equations and `graphicsLayer` modifiers required for the custom, physics-based Alphabet Bar bulge animation.
* **Performance Optimization:** Helping debug Jetpack Compose UI thread locks by moving `Drawable.toBitmap()` conversions into Coroutine background threads (`Dispatchers.IO`) for stutter-free scrolling.
* **Boilerplate & Architecture:** Assisting with generating boilerplate code for Hilt Dependency Injection and structuring the `AppRepository` interfaces to maintain Clean Architecture principles.
