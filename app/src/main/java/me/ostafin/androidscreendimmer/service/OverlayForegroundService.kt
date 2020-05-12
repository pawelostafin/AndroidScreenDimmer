package me.ostafin.androidscreendimmer.service

import android.app.*
import android.app.NotificationManager.*
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.core.app.NotificationCompat
import me.ostafin.androidscreendimmer.R
import me.ostafin.androidscreendimmer.app.AndroidScreenDimmerApp
import me.ostafin.androidscreendimmer.ui.main.MainActivity
import kotlin.math.max


class OverlayForegroundService : Service() {

    private val androidScreenDimmerApp: AndroidScreenDimmerApp
        get() = application as AndroidScreenDimmerApp

    private val windowManager: WindowManager
        get() = getSystemService(Context.WINDOW_SERVICE) as WindowManager

    private val layoutInflater: LayoutInflater
        get() = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun onCreate() {
        super.onCreate()

        createAndStoreOverlayViewIfNeeded()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())
        drawOverlayIfNeeded()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()

        removeOverlayIfNeeded()
    }

    private fun createAndStoreOverlayViewIfNeeded() {
        if (androidScreenDimmerApp.overlayView == null) {
            androidScreenDimmerApp.overlayView = layoutInflater.inflate(R.layout.view_overlay, null)
        }
    }

    private fun drawOverlayIfNeeded() {
        if (shouldDrawOverlay()) {
            drawOverlay()
        }
    }

    private fun shouldDrawOverlay(): Boolean {
        return androidScreenDimmerApp.overlayView?.isAttachedToWindow == false
    }

    private fun removeOverlayIfNeeded() {
        if (shouldRemoveOverlay()) {
            removeOverlay()
        }
    }

    private fun shouldRemoveOverlay(): Boolean {
        return androidScreenDimmerApp.overlayView?.isAttachedToWindow == true
    }

    private fun createNotification(): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
        val notificationTitle = getString(R.string.notification_title)
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(notificationTitle)
            .setSmallIcon(R.drawable.notification_icon)
            .setContentIntent(pendingIntent)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = getString(R.string.notification_channel_name)
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                channelName,
                IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager!!.createNotificationChannel(serviceChannel)
        }
    }

    private fun drawOverlay() {
        val overlayType = getOverlayType()
        val screenLongerDimensionInPixels = getScreenLongerDimensionInPixels()

        val params = WindowManager.LayoutParams(
            screenLongerDimensionInPixels,
            screenLongerDimensionInPixels,
            overlayType,
            WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
                    or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                    or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                    or WindowManager.LayoutParams.FLAG_FULLSCREEN
                    or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.START or Gravity.BOTTOM
        }

        windowManager.addView(androidScreenDimmerApp.overlayView, params)
    }

    private fun getScreenLongerDimensionInPixels(): Int {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels
        return max(height, width)
    }

    private fun getOverlayType(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY
        }
    }

    private fun removeOverlay() {
        windowManager.removeView(androidScreenDimmerApp.overlayView)
    }

    companion object {
        const val CHANNEL_ID = "AndroidScreenDimmer"
        const val NOTIFICATION_ID = 1
    }
}