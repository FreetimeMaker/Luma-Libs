# Luma Libraries

Open-source Kotlin Multiplatform libraries for Luma Store developers. They run on Android (min SDK 24) and desktop JVM (Java 17). They do not require a Luma Store installation or account.

| Module                                   | Use it for                                                                      |
| ---------------------------------------- | ------------------------------------------------------------------------------- |
| [Core](Core/README.md)                   | App metadata, shared models and preferences                                     |
| [Donations](Donations/README.md)         | Material 3 donation UI, wallet links and clipboard actions                      |
| [Store](Store/README.md) | Public Luma Store catalog, app details and rating summaries |
| [Updater](Updater/README.md) | Check Android releases in Luma Store for updates |

## Installation

Add the Maven repository in `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://repo.free-time.me/maven/")
    }
}
```

Once **1.0.0** is published, add only the modules your app uses to its dependencies (for KMP, in the appropriate source set):

```kotlin
implementation("com.freetime:Core:1.0.0")
implementation("com.freetime:Donations:1.0.0")
implementation("com.freetime:Store:1.0.0")
implementation("com.freetime:Updater:1.0.0")
```

Keep Gradle Module Metadata enabled so Gradle can select the Android or desktop variant. Do not add `-android` or `-desktop` to the artifact names. The `com.freetime` group and Kotlin packages are unchanged.

`Design`, `Browser`, `FreetimeWarn` and the sample app are not part of Luma Libraries. The host app supplies its own theme, link handling and warning UI. If you use Core APIs directly, declare `Core` explicitly rather than relying on it as a transitive API dependency.

## Local development

Use the Gradle wrapper and the Android SDK configured in `gradle/libs.versions.toml`.

```sh
sh gradlew desktopTest
sh gradlew :Core:testAndroidHostTest :Donations:testAndroidHostTest :Store:testAndroidHostTest :Updater:testAndroidHostTest
python3 -m unittest discover -s .github/scripts
```

## Publishing

The default release version is `[versions].luma` in `gradle/libs.versions.toml`. To stage and validate the Maven repository locally without uploading:

```sh
sh gradlew stagePublications
python3 .github/scripts/verify_gemfury_release.py 1.0.0 --staging build/public-maven --local-only
```

Each root coordinate contains a POM, Gradle Module Metadata, common metadata and sources JARs, an Android AAR and sources JAR, and a desktop JAR and sources JAR. CI validates this layout and tags successful builds on `master`; it does not upload Maven packages. Public publishing is handled separately by [FreetimeMaker/Repoitories](https://github.com/FreetimeMaker/Repoitories).

See [CHANGELOG.md](CHANGELOG.md) for release history.
