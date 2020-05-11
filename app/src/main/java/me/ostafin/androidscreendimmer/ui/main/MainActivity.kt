package me.ostafin.androidscreendimmer.ui.main

import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.WindowManager.LayoutParams
import android.view.WindowManager.LayoutParams.*
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.activity_main.*
import me.ostafin.androidscreendimmer.R
import me.ostafin.androidscreendimmer.service.ForegroundService
import me.ostafin.androidscreendimmer.ui.base.BaseActivity
import me.ostafin.androidscreendimmer.ui.main.model.ButtonState
import me.ostafin.androidscreendimmer.ui.main.model.ButtonState.OFF
import me.ostafin.androidscreendimmer.ui.main.model.ButtonState.ON
import me.ostafin.androidscreendimmer.util.getDrawOverAppsSystemSettingsIntent


class MainActivity : BaseActivity<MainViewModel>() {

    override val viewModelType: Class<MainViewModel>
        get() = MainViewModel::class.java

    override val layoutId: Int
        get() = R.layout.activity_main

    override fun setupView() {
        super.setupView()

        createAndStoreOverlayViewIfNeeded()
        bindUi()
    }

    private fun createAndStoreOverlayViewIfNeeded() {
        if (androidScreenDimmerApp.overlayView == null) {
            androidScreenDimmerApp.overlayView = layoutInflater.inflate(R.layout.my_view, null)
        }
    }

    override fun observeViewModel() {
        viewModel.buttonStateObs
            .subscribe(::setButtonState)
            .addTo(compositeDisposable)

        viewModel.overlayVisibilityStateObs
            .subscribe(::setOverlayVisibilityState)
            .addTo(compositeDisposable)

        viewModel.lastSliderValueObs
            .subscribe { slider.progress = it }
            .addTo(compositeDisposable)

        viewModel.overlayAlphaValueObs
            .subscribe { androidScreenDimmerApp.overlayView?.alpha = it }
            .addTo(compositeDisposable)

        viewModel.openDrawOverAppSystemSettingsObs
            .subscribe { openDrawOverAppsSystemSettings() }
            .addTo(compositeDisposable)
    }

    private fun bindUi() {
        toggleButton.setOnClickListener {
            viewModel.buttonClicked()
        }

        slider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                viewModel.sliderValueChanged(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
    }

    private fun openDrawOverAppsSystemSettings() {
        val intent = getDrawOverAppsSystemSettingsIntent(this)
        startActivity(intent)
    }

    private fun setOverlayVisibilityState(isVisible: Boolean) {
        when {
            isVisible && shouldDrawOverlay() -> {
                drawOverlay()
                startService()
            }
            !isVisible && shouldRemoveOverlay() -> {
                removeOverlay()
                stopService()
            }
        }
    }

    private fun shouldDrawOverlay(): Boolean {
        return androidScreenDimmerApp.overlayView?.isAttachedToWindow == false
    }

    private fun shouldRemoveOverlay(): Boolean {
        return androidScreenDimmerApp.overlayView?.isAttachedToWindow == true
    }

    private fun drawOverlay() {
        val overlayFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            TYPE_APPLICATION_OVERLAY
        } else {
            TYPE_SYSTEM_OVERLAY
        }

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels * 1.5


        val params = LayoutParams(
            width,
            height.toInt(),
            overlayFlag,
            FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS or FLAG_NOT_FOCUSABLE or FLAG_NOT_TOUCHABLE or FLAG_LAYOUT_NO_LIMITS or FLAG_FULLSCREEN or FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.START or Gravity.BOTTOM
            y = (-height * 0.25).toInt()
        }

        windowManager.addView(androidScreenDimmerApp.overlayView, params)
    }

    private fun startService() {
        val serviceIntent = Intent(this, ForegroundService::class.java)
        serviceIntent.putExtra("inputExtra", "Foreground Service Example in Android")
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    private fun stopService() {
        val serviceIntent = Intent(this, ForegroundService::class.java)
        stopService(serviceIntent)
    }

    private fun removeOverlay() {
        windowManager.removeView(androidScreenDimmerApp.overlayView)
    }

    private fun setButtonState(buttonState: ButtonState) {
        val buttonText = when (buttonState) {
            ON -> "ON"
            OFF -> "OFF"
        }
        toggleButton.text = buttonText
    }

}
