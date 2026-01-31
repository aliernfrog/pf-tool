import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.parcelize)
    `maven-publish`
}

android {
    namespace = "io.github.aliernfrog.pftool_shared"
    compileSdk = 36
    buildToolsVersion = "36.1.0"

    defaultConfig {
        minSdk = 23
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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        aidl = true
        compose = true
    }
}

androidComponents {
    onVariants { variant ->
        variant.sources.res?.addGeneratedSourceDirectory(
            tasks.named<GenerateStringsTask>("generateSharedStringsTxt"),
            GenerateStringsTask::outputDir
        )
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
        optIn.add("kotlin.RequiresOptIn")
        freeCompilerArgs.add("-Xannotation-default-target=param-property")
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

    implementation(libs.aboutlibraries.core)
    implementation(libs.aboutlibraries.compose.core)
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

abstract class GenerateStringsTask : DefaultTask() {
    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun execute() {
        val enumFile = project.file("src/main/java/io/github/aliernfrog/pftool_shared/util/SharedString.kt")
        val pattern = """SharedString\("([^"]+)"\)""".toRegex(RegexOption.MULTILINE)
        val keys = enumFile.readText().let { content ->
            pattern.findAll(content).map { it.groupValues[1] }.toList()
        }
        val targetFile = outputDir.file("raw/shared_strings.txt").get().asFile
        targetFile.parentFile.mkdirs()
        targetFile.writeText(keys.joinToString("\n"))
    }
}

val generateSharedStringsTxt = tasks.register<GenerateStringsTask>("generateSharedStringsTxt") {
    outputDir.set(layout.buildDirectory.dir("generated/pftool_shared/res"))
}

tasks.named("preBuild") {
    dependsOn(tasks.named("generateSharedStringsTxt"))
}

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