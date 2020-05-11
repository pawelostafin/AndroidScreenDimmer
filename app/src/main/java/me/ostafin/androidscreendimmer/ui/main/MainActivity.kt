package me.ostafin.androidscreendimmer.ui.main

import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.WindowManager.LayoutParams
import android.view.WindowManager.LayoutParams.*
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.activity_main.*
import me.ostafin.androidscreendimmer.R
import me.ostafin.androidscreendimmer.ui.AndroidScreenDimmerApp
import me.ostafin.androidscreendimmer.ui.main.ButtonState.OFF
import me.ostafin.androidscreendimmer.ui.main.ButtonState.ON
import me.ostafin.androidscreendimmer.util.getDrawOverAppsSystemSettingsIntent


class MainActivity : AppCompatActivity() {

    private val compositeDisposable = CompositeDisposable()

    private val androidScreenDimmerApp: AndroidScreenDimmerApp
        get() = application as AndroidScreenDimmerApp

    private val viewModel: MainViewModel by lazy {
        val factory = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ViewModelProvider(this, factory).get(MainViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createAndStoreOverlayView()
        observeViewModel()
        bindUi()
    }

    private fun createAndStoreOverlayView() {
        androidScreenDimmerApp.overlayView = layoutInflater.inflate(R.layout.my_view, null)
    }

    private fun observeViewModel() {
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
        openDrawOverAppsSystemSettings()
        val intent = getDrawOverAppsSystemSettingsIntent(this)
        startActivityForResult(intent, DRAW_OVER_SYSTEM_SETTINGS_REQUEST_CODE)
    }

    private fun setOverlayVisibilityState(isVisible: Boolean) {
        if (isVisible) {
            if (Settings.canDrawOverlays(this)) {
                drawOverlay()
            } else {
                startActivityForResult(intent, 0)
            }
        } else {
            removeOverlay()
        }
    }

    private fun drawOverlay() {
        val overlayFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            TYPE_APPLICATION_OVERLAY
        } else {
            TYPE_SYSTEM_ALERT
        }

        val params = LayoutParams(
            MATCH_PARENT,
            MATCH_PARENT,
            overlayFlag,
            FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS or FLAG_NOT_FOCUSABLE or FLAG_NOT_TOUCHABLE,
            PixelFormat.TRANSLUCENT
        )

        windowManager.addView(androidScreenDimmerApp.overlayView, params)
    }

    private fun removeOverlay() {
        androidScreenDimmerApp.overlayView?.let {
            if (it.isAttachedToWindow) {
                windowManager.removeView(it)
            }
        }
    }

    private fun setButtonState(buttonState: ButtonState) {
        val buttonText = when (buttonState) {
            ON -> "ON"
            OFF -> "OFF"
        }
        toggleButton.text = buttonText
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == DRAW_OVER_SYSTEM_SETTINGS_REQUEST_CODE) {
            viewModel.drawOverAppsSettingsChanged()
        }
    }

    companion object {
        const val DRAW_OVER_SYSTEM_SETTINGS_REQUEST_CODE = 555
    }

}
