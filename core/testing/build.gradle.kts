plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.abn.amro.core.testing"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
}

dependencies {
    // Standard Android/Kotlin libs
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    // Using api so these are exposed to consumers of this module to be used in testing
    api(libs.junit.jupiter.api)
    api(libs.junit.jupiter.engine)
    api(libs.junit.platform.launcher)
    api(libs.kotlinx.coroutines.test)
    api(libs.mockk)
    api(libs.truth)
    api(libs.turbine)

    // Needed for Dispatchers.setMain(testDispatcher)
    implementation(libs.kotlinx.coroutines.android)
}