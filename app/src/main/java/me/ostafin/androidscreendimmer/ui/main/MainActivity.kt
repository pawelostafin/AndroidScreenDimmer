package me.ostafin.androidscreendimmer.ui.main

import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import me.ostafin.androidscreendimmer.R
import me.ostafin.androidscreendimmer.service.OverlayForegroundService
import me.ostafin.androidscreendimmer.ui.base.BaseActivity
import me.ostafin.androidscreendimmer.ui.main.model.ButtonState
import me.ostafin.androidscreendimmer.ui.main.model.ButtonState.TURN_ON
import me.ostafin.androidscreendimmer.ui.main.model.ButtonState.TURN_OFF
import me.ostafin.androidscreendimmer.ui.main.model.OverlayState
import me.ostafin.androidscreendimmer.ui.main.model.OverlayState.*
import me.ostafin.androidscreendimmer.util.getDrawOverAppsSystemSettingsIntent
import me.ostafin.androidscreendimmer.util.setOnSeekBarChangeListener


class MainActivity : BaseActivity<MainViewModel>() {

    override val viewModelType: Class<MainViewModel>
        get() = MainViewModel::class.java

    override val layoutId: Int
        get() = R.layout.activity_main

    override fun setupView() {
        super.setupView()

        setupOnOffButton()
        setupSlider()
    }

    override fun observeViewModel() {
        viewModel.buttonState
            .onEach { setButtonState(it) }
            .launchIn(lifecycleScope)

        viewModel.initialSliderProgressObs
            .onEach { setInitialSliderProgress(it) }
            .launchIn(lifecycleScope)

        viewModel.overlayState
            .onEach { setOverlayState(it) }
            .launchIn(lifecycleScope)

        viewModel.openDrawOverAppSystemSettings
            .onEach { openDrawOverAppsSystemSettings() }
            .launchIn(lifecycleScope)
    }

    private fun setupOnOffButton() {
        onOffButton.setOnClickListener {
            viewModel.buttonClicked()
        }
    }

    private fun setupSlider() {
        slider.setOnSeekBarChangeListener(
            onProgressChanged = { _, progress, _ ->
                viewModel.sliderValueChanged(progress)
            }
        )
    }

    private fun setInitialSliderProgress(progress: Int) {
        slider.progress = progress
    }

    private fun openDrawOverAppsSystemSettings() {
        val intent = getDrawOverAppsSystemSettingsIntent(this)
        startActivity(intent)
    }

    private fun setOverlayState(state: OverlayState) {
        when (state) {
            is Visible -> startOverlayForegroundService(state.alphaValue)
            is Hidden -> stopOverlayForegroundServiceService()
        }
    }

    private fun startOverlayForegroundService(alphaValue: Float) {
        val serviceIntent = OverlayForegroundService.getStartIntent(this, alphaValue)
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    private fun stopOverlayForegroundServiceService() {
        val serviceIntent = OverlayForegroundService.getStopIntent(this)
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
