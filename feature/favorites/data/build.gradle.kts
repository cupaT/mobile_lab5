plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.lab5.feature.favorites.data"
    compileSdk = 36
    defaultConfig { minSdk = 24 }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions { jvmTarget = "11" }
}

dependencies {
    implementation(project(":core"))
    implementation(project(":feature:favorites:api"))
    implementation(project(":feature:favorites:domain"))
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.koin.core)
}
