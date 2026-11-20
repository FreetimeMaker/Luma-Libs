package com.freetime.updater

import com.freetime.store.LumaStoreApp
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class LumaUpdaterTest {
    @Test
    fun findsNewerLongVersionCodeWithoutComparingVersionNames() = runBlocking {
        val updater = LumaUpdater { id ->
            assertEquals("store-entry", id)
            release(4_000_000_001L)
        }
        assertEquals(UpdateCheck.Available(4_000_000_001L, "beta", null), updater.check("store-entry", "example.app", 4_000_000_000L))
    }

    @Test
    fun equalOrOlderVersionsDoNotOfferAnUpdate() = runBlocking {
        val updater = LumaUpdater { release(42) }
        for (installed in listOf(42L, 43L)) {
            assertEquals(UpdateCheck.UpToDate(42), updater.check("store-entry", "example.app", installed))
        }
    }

    @Test
    fun distinguishesPackageMismatchAndMissingVersion() = runBlocking {
        val updater = LumaUpdater { release(null) }
        assertEquals(UpdateCheck.PackageMismatch, updater.check("store-entry", "other.app", 1))
        assertEquals(UpdateCheck.MissingVersionCode, updater.check("store-entry", "example.app", 1))
    }

    @Test
    fun errorsAreNotReportedAsUpToDate() = runBlocking {
        val updater = LumaUpdater { error("offline") }
        assertFailsWith<IllegalStateException> { updater.check("store-entry", "example.app", 1) }
        Unit
    }

    private fun release(versionCode: Long?) = LumaStoreApp(
        id = "store-entry", name = "Example", packageName = "example.app",
        summary = null, description = null, category = null, iconUrl = null,
        versionName = "beta", versionCode = versionCode, downloadUrl = null, downloadCount = null,
    )
}
