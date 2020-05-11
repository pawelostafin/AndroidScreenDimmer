package me.ostafin.androidscreendimmer.ui.main

import android.graphics.PixelFormat
import android.os.Build
import android.view.WindowManager.LayoutParams
import android.view.WindowManager.LayoutParams.*
import android.widget.SeekBar
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.activity_main.*
import me.ostafin.androidscreendimmer.R
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

        createAndStoreOverlayView()
        bindUi()
    }

    private fun createAndStoreOverlayView() {
        androidScreenDimmerApp.overlayView = layoutInflater.inflate(R.layout.my_view, null)
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
        if (isVisible) {
            drawOverlay()
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

}
