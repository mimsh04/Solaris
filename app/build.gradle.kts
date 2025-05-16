plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    kotlin("plugin.serialization") version "2.0.21"
    alias(libs.plugins.ksp)

}

android {
    namespace = "in2000.team42"
    compileSdk = 35

    defaultConfig {
        applicationId = "in2000.team42"
        minSdk = 26
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}
val ktorVersion: String by project
dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.runtime.android)
    implementation(libs.androidx.runtime)
    implementation (libs.androidx.datastore.preferences)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Jetpack Compose integration
    implementation(libs.androidx.navigation.compose)

    // Views/Fragments integration
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)

    // Feature module support for Fragments
    implementation(libs.androidx.navigation.dynamic.features.fragment)

    // Testing Navigation
    androidTestImplementation(libs.androidx.navigation.testing)

    // JSON serialization library, works with the Kotlin serialization plugin
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.android)
    implementation(libs.maps.compose)

    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)

    implementation(libs.ktor.serialization.kotlinx.json)

    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.auth)
    implementation(libs.ktor.client.logging)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.vico.compose)
    implementation(libs.vico.compose.m3)

    implementation(libs.autofill)
    implementation(libs.discover)
    implementation(libs.place.autocomplete)
    implementation(libs.offline)
    implementation(libs.mapbox.search.android)
    implementation(libs.mapbox.search.android.ui)
    //Database
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
    //BottomSheet
    implementation(libs.core)
    implementation(libs.androidx.material.icons.core)

    // Unit tests
    androidTestImplementation(libs.mockwebserver.v4120)
    testImplementation (libs.junit)
    testImplementation (libs.mockk)
    testImplementation (libs.mapbox.sdk.geojson)
    testImplementation(kotlin("test"))
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockito.kotlin)
    testImplementation (libs.mockito.inline)
    testImplementation (libs.slf4j.api)
    testImplementation (libs.slf4j.simple)

    implementation(libs.coil.compose)
    implementation(libs.coil.network.ktor3)
}