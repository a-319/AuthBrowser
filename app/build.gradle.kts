plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.restrictedauthbrowser"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.restrictedauthbrowser"
        minSdk = 21
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0"

        testInstrumentationRunner = "android.test.InstrumentationTestRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}

dependencies {
    implementation("org.mozilla.geckoview:geckoview:145.0.20251124145406")
    testImplementation("junit:junit:4.13.2")
}
