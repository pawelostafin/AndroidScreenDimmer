package me.ostafin.androidscreendimmer.domain.checker

import android.app.ActivityManager
import android.content.Context
import me.ostafin.androidscreendimmer.app.di.qualifier.ApplicationContext
import me.ostafin.androidscreendimmer.service.OverlayForegroundService
import javax.inject.Inject

class OverlayVisibilityChecker @Inject constructor(
    @ApplicationContext private val context: Context
) {

    val isOverlayVisible: Boolean
        get() {
            val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val runningServices = activityManager.getRunningServices(Int.MAX_VALUE)

            val runningOverlayService =
                runningServices.firstOrNull { it.service.className == OverlayForegroundService::class.java.name }

            return runningOverlayService != null
        }

}