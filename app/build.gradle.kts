plugins {
    // Use the alias defined in libs.versions.toml
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    kotlin("kapt")
}

android {
    namespace = "com.example.control_inv_movil"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.control_inv_movil"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    // Habilitar ViewBinding
    buildFeatures {
        viewBinding = true
    }
}

// BLOQUE DE DEPENDENCIAS CORREGIDO
dependencies {

    implementation("com.github.bumptech.glide:glide:4.16.0")

    // --- LIBRERÍAS BÁSICAS DE ANDROIDX ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.activity.ktx)

    // --- ARQUITECTURA Y CICLO DE VIDA (ViewModel, LiveData) ---
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)

    // --- ROOM (BASE DE DATOS) ---
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.activity)
    kapt(libs.androidx.room.compiler) // "kapt" para el procesador de anotaciones

    // --- RETROFIT (COMUNICACIÓN CON SERVIDOR) ---
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    // --- COROUTINES (HILOS SECUNDARIOS) ---
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // --- LIBRERÍAS DE TESTING ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // --- RESTRICCIONES DE VERSIÓN (CONSTRAINTS) ---
    // Esto se coloca al final para asegurar la versión de las dependencias
    constraints {
        implementation(libs.androidx.activity) {
            because("Asegurar compatibilidad con compileSdk 34 y evitar conflictos")
        }
        implementation(libs.androidx.activity.ktx) {
            because("Asegurar compatibilidad con compileSdk 34 y evitar conflictos")
        }
    }
}
