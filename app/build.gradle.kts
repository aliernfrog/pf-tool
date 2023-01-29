plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

val composeVersion = "1.4.0-alpha05"
val composeCompilerVersion = "1.4.0"

android {
    namespace = "com.aliernfrog.pftool"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.aliernfrog.pftool"
        minSdk = 23
        targetSdk = 33
        versionCode = 131
        versionName = "1.3.1"
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

    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.compose.ui:ui:$composeVersion")
    implementation("androidx.compose.material:material:$composeVersion")
    implementation("androidx.compose.material:material-icons-extended:$composeVersion")
    implementation("androidx.compose.material3:material3:1.1.0-alpha05")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.1")
    implementation("androidx.activity:activity-compose:1.6.1")
    implementation("com.google.accompanist:accompanist-navigation-animation:0.29.0-alpha")
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.27.0")
    implementation("com.github.aliernfrog:top-toast-compose:1.1.1")
    implementation("com.lazygeniouz:dfc:0.91")
    implementation("io.coil-kt:coil-compose:2.2.2")
}