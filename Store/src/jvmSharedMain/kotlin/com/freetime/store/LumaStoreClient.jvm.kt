package com.freetime.store

import java.io.IOException
import java.net.HttpURLConnection
import java.net.URI
import java.net.URLEncoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

// Wire contract: All-API-Node/src/v2/lumastore/store.js (public GET routes and normalizeApp).
private const val BASE_URL = "https://api.free-time.me/v2/lumastore"

actual class LumaStoreClient internal constructor(private val fetch: (String) -> String) {
    actual constructor() : this(::fetchPublicJson)

    actual suspend fun listApps(search: String?, categoryId: String?, platform: String?): List<LumaStoreApp> =
        withContext(Dispatchers.IO) {
            val rows = JSONArray(fetch("/apps" + query("search" to search, "category" to categoryId, "platform" to platform)))
            List(rows.length()) { rows.getJSONObject(it).toApp() }
        }

    actual suspend fun getApp(id: String, platform: String?): LumaStoreApp = withContext(Dispatchers.IO) {
        JSONObject(fetch(appPath(id) + query("platform" to platform))).toApp()
    }

    actual suspend fun getRating(id: String): LumaStoreRating = withContext(Dispatchers.IO) {
        val json = JSONObject(fetch(appPath(id) + "/ratings"))
        LumaStoreRating(json.getDouble("average"), json.getInt("count"))
    }
}

private fun appPath(id: String): String {
    require(id.isNotBlank() && id != "." && id != "..") { "A Luma Store app ID is required" }
    return "/apps/${encode(id)}"
}

private fun encode(value: String): String = URLEncoder.encode(value, "UTF-8").replace("+", "%20")

private fun query(vararg parameters: Pair<String, String?>): String = parameters.mapNotNull { (key, value) ->
    value?.takeIf { it.isNotBlank() }?.let { "$key=${encode(it)}" }
}.joinToString("&").let { if (it.isEmpty()) "" else "?$it" }

private fun fetchPublicJson(path: String): String {
    val connection = URI.create(BASE_URL + path).toURL().openConnection() as HttpURLConnection
    try {
        connection.instanceFollowRedirects = false
        connection.connectTimeout = 15_000
        connection.readTimeout = 20_000
        connection.setRequestProperty("Accept", "application/json")
        val status = connection.responseCode
        if (status !in 200..299) throw LumaStoreHttpException(status)
        return connection.inputStream.bufferedReader(Charsets.UTF_8).use { reader ->
            val buffer = CharArray(8192)
            val body = StringBuilder()
            while (true) {
                val count = reader.read(buffer)
                if (count == -1) break
                if (body.length + count > 16 * 1024 * 1024) throw IOException("Luma Store response is too large")
                body.append(buffer, 0, count)
            }
            body.toString()
        }
    } finally {
        connection.disconnect()
    }
}

private fun JSONObject.text(key: String): String? = (opt(key) as? String)?.takeIf { it.isNotBlank() }
private fun JSONObject.number(key: String): Long? = opt(key)?.toString()?.toLongOrNull()

private fun JSONObject.toApp() = LumaStoreApp(
    id = getString("id"),
    name = getString("name"),
    packageName = text("package_name"),
    summary = text("short_description") ?: text("summary"),
    description = text("description"),
    category = text("category"),
    iconUrl = text("icon_url"),
    versionName = text("version_name") ?: text("version"),
    versionCode = number("version_code"),
    downloadUrl = text("download_url"),
    downloadCount = number("download_count"),
)
