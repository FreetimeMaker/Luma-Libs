package com.freetime.updater

import android.content.Context
import android.os.Build

/** Checks the installed host app, using its actual package name and version code. */
@Suppress("DEPRECATION")
suspend fun LumaUpdater.checkInstalledApp(context: Context, storeId: String): UpdateCheck {
    val packageName = context.packageName
    val info = context.packageManager.getPackageInfo(packageName, 0)
    val versionCode = if (Build.VERSION.SDK_INT >= 28) info.longVersionCode else info.versionCode.toLong()
    return check(storeId, packageName, versionCode)
}
