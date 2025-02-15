// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.google.gms.google.services) apply false
    alias(libs.plugins.google.hilt.android) apply false
    id("com.google.firebase.crashlytics") version "3.0.2" apply false
    kotlin("kapt") version "1.9.0" apply false
}