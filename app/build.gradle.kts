plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    kotlin("kapt")
    id("com.google.dagger.hilt.android") version "2.51.1" // Correct way to apply Hilt plugin
}

android {
    namespace = "com.example.sleep_cycle"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.sleep_cycle"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "com.example.sleep_cycle.CustomTestRunner"
    }

    buildFeatures {
        dataBinding = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }
}

dependencies {
    // Hilt dependencies
    implementation(libs.runtime.livedata)
    implementation("com.google.dagger:hilt-android:2.51.1")
    implementation(libs.core.ktx)
    implementation(libs.androidx.espresso.contrib)
    kapt("com.google.dagger:hilt-android-compiler:2.51.1")
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.51.1")

    // instrumented tests
    kaptAndroidTest("com.google.dagger:hilt-compiler:2.51.1")
    // unit tests
    kaptTest("com.google.dagger:hilt-compiler:2.51.1")

    implementation(libs.androidx.datastore.preferences.v100)

    // Compose and AndroidX dependencies
    implementation("androidx.compose.compiler:compiler:1.5.0")
    val composeBom = platform("androidx.compose:compose-bom:2024.06.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)
    androidTestImplementation(libs.androidx.ui.test.junit4)


    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui.tooling.preview)
    debugImplementation(libs.androidx.ui.tooling)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.navigation.compose)

    // Core and UI dependencies
    implementation(libs.androidx.core.ktx.v1101)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.material)
    implementation(libs.material3) // Latest Material 3 version
    implementation(libs.androidx.material.icons.extended)

    // Room and Datastore
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    kapt(libs.androidx.room.compiler)
    implementation(libs.androidx.datastore.preferences.core.jvm)
    implementation(libs.androidx.localbroadcastmanager)

    // Navigation
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)

    // Work Manager
    implementation(libs.androidx.work.runtime)

    // Testing dependencies
    testImplementation(libs.junit)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.androidx.core.testing)
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.0")

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.mockito.android)
}

kapt {
    correctErrorTypes = true
}
