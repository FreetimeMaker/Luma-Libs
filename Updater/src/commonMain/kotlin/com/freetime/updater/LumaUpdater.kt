package com.freetime.updater

import com.freetime.store.LumaStoreApp
import com.freetime.store.LumaStoreClient

sealed interface UpdateCheck {
    data class Available(val versionCode: Long, val versionName: String?, val downloadUrl: String?) : UpdateCheck
    data class UpToDate(val latestVersionCode: Long) : UpdateCheck
    data object PackageMismatch : UpdateCheck
    data object MissingVersionCode : UpdateCheck
}

/** Checks Android releases from the public Luma Store catalog. */
class LumaUpdater internal constructor(private val getAndroidApp: suspend (String) -> LumaStoreApp) {
    constructor() : this(LumaStoreClient().let { store -> { id -> store.getApp(id, platform = "android") } })

    /** Network and API errors propagate to the caller; they do not mean the app is up to date. */
    suspend fun check(storeId: String, packageName: String, installedVersionCode: Long): UpdateCheck {
        require(storeId.isNotBlank()) { "A Luma Store app ID is required" }
        require(packageName.isNotBlank()) { "A package name is required" }
        require(installedVersionCode > 0) { "The installed version code must be positive" }

        val release = getAndroidApp(storeId)
        if (release.packageName != packageName) return UpdateCheck.PackageMismatch
        val latest = release.versionCode?.takeIf { it > 0 } ?: return UpdateCheck.MissingVersionCode
        return if (latest > installedVersionCode) {
            UpdateCheck.Available(latest, release.versionName, release.downloadUrl)
        } else {
            UpdateCheck.UpToDate(latest)
        }
    }
}
