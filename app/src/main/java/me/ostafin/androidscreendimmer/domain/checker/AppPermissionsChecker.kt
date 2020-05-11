package me.ostafin.androidscreendimmer.domain.checker

import android.content.Context
import android.os.Build
import android.provider.Settings
import me.ostafin.androidscreendimmer.app.di.qualifier.ApplicationContext
import javax.inject.Inject

class AppPermissionsChecker @Inject constructor(
    @ApplicationContext private val context: Context
) {

    val canDrawOverlays: Boolean
        get() {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Settings.canDrawOverlays(context)
            } else {
                true
            }
        }

}