plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
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

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(libs.hilt.android)
    implementation(libs.androidx.ui.test.junit4.android)
    implementation(libs.transport.runtime)
    androidTestImplementation(libs.hilt.android.testing)


    implementation(libs.androidx.datastore.preferences.core.jvm)
    implementation(libs.androidx.room.common)
    androidTestImplementation(project(":app"))
    androidTestImplementation(project(":app"))


    kapt(libs.hilt.android.compiler)
    kapt(libs.androidx.room.compiler)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)

    implementation(libs.androidx.core.ktx.v1101)
    implementation(libs.androidx.work.runtime)
    implementation(libs.androidx.localbroadcastmanager)
    implementation(libs.material3) // Check for the latest version
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.datastore.preferences)

    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.runtime.livedata)

    // Compose Compiler dependency
    implementation("androidx.compose.compiler:compiler:1.5.0") // Update this version if needed

    val composeBom = platform("androidx.compose:compose-bom:2024.06.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui.tooling.preview)
    debugImplementation(libs.androidx.ui.tooling)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit.v115)
    androidTestImplementation(libs.androidx.espresso.core.v350)

    testImplementation(libs.mockito.core)
    androidTestImplementation(libs.mockito.android)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.androidx.core.testing)

}

kapt {
    correctErrorTypes = true
}