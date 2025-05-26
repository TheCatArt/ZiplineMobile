// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}


fun dependencyResolutionManagement(function: () -> Unit) {

    
}
