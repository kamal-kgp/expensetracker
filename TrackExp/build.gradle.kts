// build.gradle.kts (Project Root: ExpenseTracker/build.gradle.kts)
// Top-level build file where you can add configuration options common to all sub-projects/modules.

plugins {
    // Apply the Android application plugin id.
    // Use a recent stable version.
//    id("com.android.application") version "8.10.0" apply false
    alias(libs.plugins.android.application) apply false
    // Apply the Kotlin Android plugin id.
    // Ensure this version matches your Kotlin language version.
//    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    alias(libs.plugins.kotlin.android) apply false
    // KSP plugin if you were using Room with KSP, etc. Not strictly needed for this setup.
//     id("com.google.devtools.ksp") version "1.9.22-1.0.17" apply false
    // Add the Safe Args plugin definition if not already in your libs.versions.toml [plugins]
     alias(libs.plugins.androidx.navigation.safeargs.kotlin) apply false // If you define it in TOML
}


