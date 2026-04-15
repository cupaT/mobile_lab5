plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    implementation(project(":feature:favorites:api"))
    implementation(libs.kotlinx.coroutines.core)
}
