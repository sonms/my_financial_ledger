// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.2.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
    id("org.jetbrains.kotlin.jvm") version "1.9.23"
    id("com.google.devtools.ksp") version "1.9.23-1.0.20"
    id("com.android.library") version "8.2.0" apply false
}
buildscript {
    //ext.hilt_version = "2.52"
    dependencies {
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.52")
    }
}