# Updater

Check for Android app updates using the public Luma Store catalog. The module uses `Store` internally and does not require an account or a Luma Store installation.

Use `implementation(project(":Updater"))` locally or `implementation("com.freetime:Updater:1.0.0")` after publication.

On Android, check the installed host app:

```kotlin
import com.freetime.updater.LumaUpdater
import com.freetime.updater.UpdateCheck
import com.freetime.updater.checkInstalledApp

suspend fun checkForUpdate(context: android.content.Context, storeId: String) {
    when (val result = LumaUpdater().checkInstalledApp(context, storeId)) {
        is UpdateCheck.Available -> println("New version: ${result.versionName ?: result.versionCode}")
        is UpdateCheck.UpToDate -> println("No newer version")
        UpdateCheck.PackageMismatch -> println("Store entry does not identify this app")
        UpdateCheck.MissingVersionCode -> println("Store entry has no usable version code")
    }
}
```

Pass the Store entry ID, not the package name. From shared or desktop JVM code, use `LumaUpdater().check(storeId, packageName, installedVersionCode)` to check an **Android** release with explicit inputs. The updater compares numeric version codes, not display version names. Network and API errors propagate to the caller.

The host controls when checks run and how results are shown. A newer release can exist without a download URL. The library does not download, verify or install packages, and it does not schedule background checks.
