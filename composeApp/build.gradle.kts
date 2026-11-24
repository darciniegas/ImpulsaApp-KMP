import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.sqldelight)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    jvm("desktop") {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    sourceSets {
        val desktopMain by getting

        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)

            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.datetime)
            implementation(libs.sqldelight.runtime)
            implementation(libs.sqldelight.coroutines)
        }

        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
            implementation(libs.kotlinx.coroutines.android)
            implementation(libs.sqldelight.android.driver)
        }

        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
            implementation(libs.sqldelight.sqlite.driver)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
        }

        // Las pruebas del repositorio (SQLite real vía JDBC) viven en src/desktopTest y
        // reutilizan el driver ya declarado en desktopMain.
    }
}

android {
    namespace = "com.impulsa.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.impulsa.app"
        minSdk = 24
        targetSdk = 34
        versionCode = 2
        versionName = "2.0-kmp"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

// Clase Res generada para los recursos compartidos (logo oficial, etc.)
compose.resources {
    publicResClass = false
    packageOfResClass = "com.impulsa.app.resources"
    generateResClass = auto
}

// Base de datos compartida (SQLDelight). El esquema vive en commonMain/sqldelight.
sqldelight {
    databases {
        create("ImpulsaDatabase") {
            packageName.set("com.impulsa.app.db")
        }
    }
}

compose.desktop {
    application {
        mainClass = "com.impulsa.app.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Msi, TargetFormat.Exe)
            packageName = "Impulsa"
            packageVersion = "1.0.0"
            description = "Impulsa - gestión de ventas y comisiones"
            // Necesario para que SQLite (JDBC) funcione en el instalador empaquetado
            modules("java.sql")
            windows {
                menuGroup = "Impulsa"
                shortcut = true
            }
        }
    }
}
