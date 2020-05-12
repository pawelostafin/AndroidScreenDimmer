package me.ostafin.androidscreendimmer.ui.main

import android.content.Intent
import androidx.core.content.ContextCompat
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.activity_main.*
import me.ostafin.androidscreendimmer.R
import me.ostafin.androidscreendimmer.service.OverlayForegroundService
import me.ostafin.androidscreendimmer.ui.base.BaseActivity
import me.ostafin.androidscreendimmer.ui.main.model.ButtonState
import me.ostafin.androidscreendimmer.ui.main.model.ButtonState.TURN_ON
import me.ostafin.androidscreendimmer.ui.main.model.ButtonState.TURN_OFF
import me.ostafin.androidscreendimmer.util.getDrawOverAppsSystemSettingsIntent
import me.ostafin.androidscreendimmer.util.setOnSeekBarChangeListener


class MainActivity : BaseActivity<MainViewModel>() {

    override val viewModelType: Class<MainViewModel>
        get() = MainViewModel::class.java

    override val layoutId: Int
        get() = R.layout.activity_main

    override fun setupView() {
        super.setupView()

        onOffButton.setOnClickListener {
            viewModel.buttonClicked()
        }

        slider.setOnSeekBarChangeListener(
            onProgressChanged = { _, progress, _ ->
                viewModel.sliderValueChanged(progress)
            }
        )
    }

    override fun observeViewModel() {
        viewModel.buttonStateObs
            .subscribe(::setButtonState)
            .addTo(compositeDisposable)

        viewModel.overlayVisibilityStateObs
            .subscribe(::setOverlayVisibilityState)
            .addTo(compositeDisposable)

        viewModel.initialSliderProgressObs
            .subscribe(::setInitialSliderProgress)
            .addTo(compositeDisposable)

        viewModel.overlayAlphaValueObs
            .subscribe(::setOverlayAlphaValue)
            .addTo(compositeDisposable)

        viewModel.openDrawOverAppSystemSettingsObs
            .subscribe { openDrawOverAppsSystemSettings() }
            .addTo(compositeDisposable)
    }

    private fun setInitialSliderProgress(progress: Int) {
        slider.progress = progress
    }

    private fun setOverlayAlphaValue(newValue: Float) {
        androidScreenDimmerApp.overlayView?.alpha = newValue
    }

    private fun openDrawOverAppsSystemSettings() {
        val intent = getDrawOverAppsSystemSettingsIntent(this)
        startActivity(intent)
    }

    private fun setOverlayVisibilityState(isVisible: Boolean) {
        if (isVisible) {
            startOverlayForegroundService()
        } else {
            stopOverlayForegroundServiceService()
        }
    }

    private fun startOverlayForegroundService() {
        val serviceIntent = Intent(this, OverlayForegroundService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    private fun stopOverlayForegroundServiceService() {
        val serviceIntent = Intent(this, OverlayForegroundService::class.java)
        stopService(serviceIntent)
    }

    private fun setButtonState(buttonState: ButtonState) {
        val buttonText = when (buttonState) {
            TURN_OFF -> R.string.button_text_turn_off_dimmer
            TURN_ON -> R.string.button_text_turn_on_dimmer
        }
        onOffButton.text = getString(buttonText)
    }

}
