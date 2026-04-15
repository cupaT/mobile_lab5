plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    implementation(project(":feature:catalog:api"))
    implementation(libs.kotlinx.coroutines.core)
}
