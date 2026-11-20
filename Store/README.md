# Store

Read public Luma Store data from Android and desktop JVM, without a Store installation, account or API key. The client makes HTTPS requests to `api.free-time.me`. Android's `INTERNET` permission is supplied by the library manifest.

After publication, use `implementation("com.freetime:Store:1.0.0")` with the [Maven repository](../README.md#installation). In this repository use `implementation(project(":Store"))`.

```kotlin
import com.freetime.store.LumaStoreClient

suspend fun showStoreApps() {
    val store = LumaStoreClient()
    val apps = store.listApps(search = "Luma", platform = "android")
    val match = apps.firstOrNull() ?: return
    val details = store.getApp(match.id, platform = "android")
    val rating = store.getRating(match.id)
    println("${details.name}: ${rating.average} (${rating.count} ratings)")
}
```

Call `listApps()` without filters for the catalog, or filter by name, category ID or platform. Pass the Store `id` returned by `listApps()` to `getApp()`, not the Android package name. Missing metadata is `null`; a download URL, when available, is metadata only. The library does not download or install apps.

Calls run on an I/O dispatcher. Handle `LumaStoreHttpException` (including its `statusCode`), network failures and invalid responses in the host app. The client does not cache or retry requests.
