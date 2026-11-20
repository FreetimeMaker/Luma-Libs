package com.freetime.store

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class LumaStoreClientTest {
    @Test
    fun listUsesServerFiltersAndParsesCatalog() = runBlocking {
        val client = LumaStoreClient { path ->
            assertEquals("/apps?search=A%26B%20Tools&category=tools&platform=android", path)
            """[{"id":"app-id","name":"Tools","package_name":"com.example.tools",
                "short_description":"Utilities","category":null,"version_code":4000000000,
                "download_url":null,"download_count":42,"future_field":true}]"""
        }
        val app = client.listApps("A&B Tools", "tools", "android").single()
        assertEquals("app-id", app.id)
        assertEquals("com.example.tools", app.packageName)
        assertEquals("Utilities", app.summary)
        assertEquals(4_000_000_000L, app.versionCode)
        assertEquals(42L, app.downloadCount)
        assertNull(app.category)
        assertNull(app.downloadUrl)
    }

    @Test
    fun detailsAndRatingsUseEncodedIds() = runBlocking {
        val client = LumaStoreClient { path ->
            when (path) {
                "/apps/a%2Fb?platform=android" -> """{"id":"a/b","name":"Example"}"""
                "/apps/a%2Fb/ratings" -> """{"average":4.5,"count":8}"""
                else -> error("Unexpected request: $path")
            }
        }
        assertEquals("Example", client.getApp("a/b", "android").name)
        assertEquals(LumaStoreRating(4.5, 8), client.getRating("a/b"))
    }

    @Test
    fun emptyCatalogOmitsUnsetFilters() = runBlocking {
        val client = LumaStoreClient { path ->
            assertEquals("/apps", path)
            "[]"
        }
        assertEquals(emptyList<LumaStoreApp>(), client.listApps(search = " "))
    }
}
