package me.ostafin.androidscreendimmer.domain.checker

import android.content.Context
import me.ostafin.androidscreendimmer.app.AndroidScreenDimmerApp
import me.ostafin.androidscreendimmer.app.di.qualifier.ApplicationContext
import javax.inject.Inject

class OverlayVisibilityChecker @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val androidScreenDimmerApp: AndroidScreenDimmerApp
        get() = context.applicationContext as AndroidScreenDimmerApp

    val isOverlayVisible: Boolean
        get() = androidScreenDimmerApp.overlayView?.isAttachedToWindow ?: false

}