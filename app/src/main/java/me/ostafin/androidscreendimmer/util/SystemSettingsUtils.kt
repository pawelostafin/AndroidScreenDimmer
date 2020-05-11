package me.ostafin.androidscreendimmer.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings

fun getDrawOverAppsSystemSettingsIntent(context: Context): Intent {
    return Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${context.packageName}"))
}