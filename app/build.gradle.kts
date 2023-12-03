plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

val composeVersion = "1.6.0-alpha07"
val material3Version = "1.2.0-alpha09"
val composeCompilerVersion = "1.5.5"

android {
    namespace = "com.aliernfrog.pftool"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.aliernfrog.pftool"
        minSdk = 23
        targetSdk = 34
        versionCode = 15000
        versionName = "1.5.0"
        vectorDrawables { useSupportLibrary = true }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = freeCompilerArgs + "-opt-in=kotlin.RequiresOptIn"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = composeCompilerVersion
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("androidx.compose.ui:ui:$composeVersion")
    implementation("androidx.compose.material:material:$composeVersion")
    implementation("androidx.compose.material:material-icons-extended:$composeVersion")
    implementation("androidx.compose.material3:material3:$material3Version")
    implementation("androidx.compose.material3:material3-window-size-class:$material3Version")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.1")
    implementation("androidx.navigation:navigation-compose:2.7.5")
    implementation("io.insert-koin:koin-androidx-compose:3.5.0")
    implementation("com.github.aliernfrog:top-toast-compose:1.3.4")
    implementation("com.lazygeniouz:dfc:1.0.8")
    implementation("io.coil-kt:coil-compose:2.5.0")
    implementation("com.github.jeziellago:compose-markdown:0.3.7")
}