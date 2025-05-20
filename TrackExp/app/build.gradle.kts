// app/build.gradle.kts (Module: ExpenseTracker/app/build.gradle.kts)

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize") // For @Parcelize annotation used in data models
    // Do NOT add id("kotlin-kapt") unless you are using libraries like Dagger or older Room that require it.
    // For this project, it's not needed.
    alias(libs.plugins.androidx.navigation.safeargs.kotlin) // Apply true here
}

android {
    namespace = "com.example.expensetracker" // Ensure this matches your package structure
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.expensetracker"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false // Enable for production builds (isMinifyEnabled instead of minifyEnabled)
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            // Example: Define a base URL for debug builds if different from release
            // resValue("string", "base_url", "\"http://10.0.2.2:8080/\"")
            // For KTS, you might use:
            // buildConfigField("String", "BASE_URL_DEBUG", "\"http://10.0.2.2:8080/\"")
            // Or handle it in your Constants.kt with a BuildConfig check
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        viewBinding = true // Enable ViewBinding for XML layouts
        // compose = false // Explicitly ensure Compose is disabled if the plugin was ever added
    }
    // If you have packagingOptions, they go here
    // packagingOptions {
    //     resources {
    //         excludes += "/META-INF/{AL2.0,LGPL2.1}"
    //     }
    // }
}

dependencies {
    // Core Android libraries
    implementation(libs.androidx.core.ktx)
//    implementation(libs.androidx.material3) // Material components for XML
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)

    // Lifecycle components (ViewModel, LiveData)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx.v280) // Includes lifecycleScope

    // Navigation component for Fragments
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // SwipeRefreshLayout for pull-to-refresh
    implementation(libs.androidx.swiperefreshlayout)

    // Networking: Retrofit for API calls & Gson for JSON parsing
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor) // For logging network requests/responses
    implementation(libs.gson)

    // Kotlin Coroutines for asynchronous programming
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // Testing libraries
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.androidx.security.crypto)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    // If you are using specific testing libraries from a version catalog (libs.versions.toml)
    // they would look like:
    // testImplementation(libs.junit)
    // androidTestImplementation(libs.androidx.test.ext.junit)
    // androidTestImplementation(libs.androidx.test.espresso.core)
}

