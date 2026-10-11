import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    // Package name of the app (also used by R and BuildConfig classes).
    namespace = "com.farooq.diceroll"

    // Android 16 (API 36). Compile against the newest stable SDK...
    compileSdk = 36

    defaultConfig {
        applicationId = "com.farooq.diceroll"

        // Android 7.0 (API 24). Covers ~99% of active devices and keeps the app small.
        minSdk = 24

        // Play Store requires targeting a recent API level.
        targetSdk = 36

        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            // Debug builds are installed next to release builds, so nothing to change here.
        }
        release {
            // R8 removes unused code and resources -> smaller, faster app.
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )

            // Google Play signing:
            // Android Studio -> Build -> Generate Signed App Bundle takes care of this,
            // or add a signingConfig here (see README, section "Publish to Google Play").
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        // Needed for BuildConfig.VERSION_NAME, shown on the Settings screen.
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    lint {
        // Lint still runs and reports, but never blocks your first build.
        abortOnError = false
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

dependencies {
    // Kotlin / AndroidX core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)

    // Jetpack Compose (versions come from the Compose BOM)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.animation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui.tooling.preview)

    // Local storage for settings + roll history
    implementation(libs.androidx.datastore.preferences)

    // Branded splash screen
    implementation(libs.androidx.core.splashscreen)

    // Tooling (only included in debug builds)
    debugImplementation(libs.androidx.compose.ui.tooling)

    // Unit tests: run on the JVM with `./gradlew test`
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}
