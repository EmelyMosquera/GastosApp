plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.emely.gastosapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.emely.gastosapp"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner =
            "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false

            proguardFiles(
                getDefaultProguardFile(
                    "proguard-android-optimize.txt"
                ),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility =
            JavaVersion.VERSION_11

        targetCompatibility =
            JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
    }
}

dependencies {

    // Android
    implementation(
        "androidx.core:core-ktx:1.15.0"
    )

    implementation(
        "androidx.activity:activity-compose:1.9.3"
    )

    // Lifecycle y ViewModel
    implementation(
        "androidx.lifecycle:lifecycle-runtime-ktx:2.8.7"
    )

    implementation(
        "androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7"
    )

    implementation(
        "androidx.lifecycle:lifecycle-runtime-compose:2.8.7"
    )

    // Jetpack Compose
    implementation(
        platform(
            "androidx.compose:compose-bom:2024.10.01"
        )
    )

    implementation(
        "androidx.compose.material3:material3"
    )

    implementation(
        "androidx.compose.ui:ui"
    )

    implementation(
        "androidx.compose.ui:ui-graphics"
    )

    implementation(
        "androidx.compose.ui:ui-tooling-preview"
    )

    // Navegación
    implementation(
        "androidx.navigation:navigation-compose:2.8.3"
    )

    // DataStore
    implementation(
        "androidx.datastore:datastore-preferences:1.1.1"
    )

    // ROOM
    // Se actualiza desde 2.6.1 para evitar el error
    // "unexpected jvm signature V" con KSP2.
    implementation(
        "androidx.room:room-runtime:2.8.4"
    )

    implementation(
        "androidx.room:room-ktx:2.8.4"
    )

    ksp(
        "androidx.room:room-compiler:2.8.4"
    )

    // Retrofit
    implementation(
        "com.squareup.retrofit2:retrofit:2.9.0"
    )

    implementation(
        "com.squareup.retrofit2:converter-gson:2.9.0"
    )

    // Coil
    implementation(
        "io.coil-kt:coil-compose:2.6.0"
    )

    // Pruebas
    testImplementation(
        "junit:junit:4.13.2"
    )

    androidTestImplementation(
        platform(
            "androidx.compose:compose-bom:2024.10.01"
        )
    )

    androidTestImplementation(
        "androidx.compose.ui:ui-test-junit4"
    )

    androidTestImplementation(
        "androidx.espresso:espresso-core:3.6.1"
    )

    androidTestImplementation(
        "androidx.test.ext:junit:1.2.1"
    )

    debugImplementation(
        "androidx.compose.ui:ui-test-manifest"
    )

    debugImplementation(
        "androidx.compose.ui:ui-tooling"
    )
}