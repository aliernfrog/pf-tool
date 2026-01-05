import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.parcelize)
    `maven-publish`
}

android {
    namespace = "io.github.aliernfrog.pftool_shared"
    compileSdk = 36
    buildToolsVersion = "36.1.0"

    defaultConfig {
        minSdk = 21
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }

    sourceSets.getByName("main") {
        res.srcDirs(layout.buildDirectory.dir("generated/pftool_shared/res"))
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
            optIn.add("kotlin.RequiresOptIn")
            freeCompilerArgs.add("-Xannotation-default-target=param-property")
        }
    }

    buildFeatures {
        aidl = true
        compose = true
    }
}

dependencies {
    implementation(project(":shared"))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.ktx)
    implementation(libs.androidx.lifecycle.compose)
    implementation(libs.androidx.lifecycle.ktx)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)

    implementation(libs.compose.ui)
    implementation(libs.compose.material)
    implementation(libs.compose.material.icons)
    implementation(libs.compose.material3)
    implementation(libs.compose.material3.window)

    implementation(libs.aboutlibraries)
    implementation(libs.coil)
    implementation(libs.coil.okhttp)
    implementation(libs.dfc)
    implementation(libs.koin)
    implementation(libs.markdown)
    implementation(libs.shizuku.api)
    implementation(libs.shizuku.provider)
    implementation(libs.toptoast)
    implementation(libs.zoomable)

    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.tooling.preview)
}

/*TODO tasks.register("generateSharedStringsTxt") {
    val enumFile = file("src/main/java/io/github/aliernfrog/pftool_shared/util/SharedString.kt")
    inputs.file(enumFile)

    val outputDir = layout.buildDirectory.dir("generated/pftool_shared/res/raw")
    val outputFile = outputDir.map { it.file("shared_strings.txt") }
    outputs.file(outputFile)

    doLast {
        if (!enumFile.exists())
            throw GradleException("SharedString.kt file not found at: ${enumFile.path}")

        val pattern = """^\s*([A-Z_]+)\("([^"]+)"\)""".toRegex(RegexOption.MULTILINE)

        val keys = enumFile.readText().let { content ->
            pattern.findAll(content).map { it.groupValues[2] }.toList()
        }

        val targetFile = outputFile.get().asFile
        targetFile.parentFile.mkdirs()
        targetFile.writeText(keys.joinToString("\n"))
    }
}*/

afterEvaluate {
    publishing {
        publications {
            register<MavenPublication>("release") {
                from(components["release"])

                groupId = "io.github.aliernfrog"
                artifactId = "pftool-shared"
            }
        }
    }
}