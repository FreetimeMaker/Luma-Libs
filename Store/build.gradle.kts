plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.multiplatform.library)
}

kotlin {
    androidLibrary {
        namespace = "com.freetime.store"
        compileSdk = libs.versions.compileSdk.get().toInt()
        minSdk = libs.versions.minSdk.get().toInt()
        withHostTestBuilder {}.configure {}
    }
    jvm("desktop")
    jvmToolchain(17)
    sourceSets {
        val jvmSharedMain by creating {
            dependsOn(commonMain.get())
            dependencies { implementation(libs.kotlinx.coroutines.core) }
        }
        androidMain { dependsOn(jvmSharedMain) }
        val desktopMain by getting {
            dependsOn(jvmSharedMain)
            dependencies { implementation(libs.json) }
        }
        val jvmSharedTest by creating {
            dependsOn(commonTest.get())
            dependencies {
                implementation(libs.junit)
                implementation(libs.json)
            }
        }
        getByName("androidHostTest").dependsOn(jvmSharedTest)
        val desktopTest by getting { dependsOn(jvmSharedTest) }
    }
}
