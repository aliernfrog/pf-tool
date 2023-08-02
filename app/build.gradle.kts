plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

val composeVersion = "1.6.0-alpha02"
val composeCompilerVersion = "1.5.1"

android {
    namespace = "com.aliernfrog.pftool"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.aliernfrog.pftool"
        minSdk = 23
        targetSdk = 34
        versionCode = 140
        versionName = "1.4.0"
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
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.compose.ui:ui:$composeVersion")
    implementation("androidx.compose.material:material:$composeVersion")
    implementation("androidx.compose.material:material-icons-extended:$composeVersion")
    implementation("androidx.compose.material3:material3:1.2.0-alpha04")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.activity:activity-compose:1.7.2")
    // TODO use androidx.navigation.compose
    implementation("com.google.accompanist:accompanist-navigation-animation:0.31.6-rc")
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.31.6-rc")
    implementation("com.github.aliernfrog:top-toast-compose:1.3.1")
    implementation("com.lazygeniouz:dfc:1.0.7")
    implementation("io.coil-kt:coil-compose:2.4.0")
}