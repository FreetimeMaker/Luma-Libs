plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.multiplatform.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    androidLibrary {
        namespace = "com.freetime.donations"
        compileSdk = libs.versions.compileSdk.get().toInt()
        minSdk = libs.versions.minSdk.get().toInt()
        withHostTestBuilder {}.configure {}
    }
    jvm("desktop")
    jvmToolchain(17)
    sourceSets {
        commonMain.dependencies {
            api(compose.runtime)
            api(compose.ui)
            implementation(compose.foundation)
            implementation(libs.compose.material3)
        }
        commonTest.dependencies { implementation(kotlin("test")) }
    }
}
