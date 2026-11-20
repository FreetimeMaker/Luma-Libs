package com.freetime.store

data class LumaStoreApp(
    val id: String,
    val name: String,
    val packageName: String?,
    val summary: String?,
    val description: String?,
    val category: String?,
    val iconUrl: String?,
    val versionName: String?,
    val versionCode: Long?,
    val downloadUrl: String?,
    val downloadCount: Long?,
)

data class LumaStoreRating(val average: Double, val count: Int)

class LumaStoreHttpException(val statusCode: Int) : Exception("Luma Store API returned HTTP $statusCode")

/** Read-only access to the public Luma Store catalog. No account or API key is needed. */
expect class LumaStoreClient() {
    /** Filters by app name, category ID and/or platform (for example, `android`). */
    suspend fun listApps(search: String? = null, categoryId: String? = null, platform: String? = null): List<LumaStoreApp>

    /** Fetches details using the catalog entry's [LumaStoreApp.id], not its package name. */
    suspend fun getApp(id: String, platform: String? = null): LumaStoreApp

    suspend fun getRating(id: String): LumaStoreRating
}
