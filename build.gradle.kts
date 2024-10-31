// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
//    id(libs.plugins.androidApplication.get().pluginId) apply false
//    id(libs.plugins.jetbrainsKotlinAndroid.get().pluginId) apply false
//    id(libs.plugins.kotlin-kapt)
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    alias(libs.plugins.kotlinKapt) apply false
    alias(libs.plugins.kotlin.compose) apply false
}