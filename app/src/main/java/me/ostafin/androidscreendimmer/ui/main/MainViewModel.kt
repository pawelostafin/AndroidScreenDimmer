package me.ostafin.androidscreendimmer.ui.main

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import me.ostafin.androidscreendimmer.domain.usecase.CheckDrawOverlaysPermissionStateUseCase
import me.ostafin.androidscreendimmer.domain.usecase.CheckOverlayVisibilityUseCase
import me.ostafin.androidscreendimmer.domain.usecase.GetLastAlphaSliderValueUseCase
import me.ostafin.androidscreendimmer.domain.usecase.SaveLastAlphaSliderValueUseCase
import me.ostafin.androidscreendimmer.ui.base.BaseViewModel
import me.ostafin.androidscreendimmer.ui.main.model.ButtonState
import me.ostafin.androidscreendimmer.ui.main.model.ButtonState.TURN_OFF
import me.ostafin.androidscreendimmer.ui.main.model.ButtonState.TURN_ON
import me.ostafin.androidscreendimmer.ui.main.model.OverlayState
import me.ostafin.androidscreendimmer.ui.main.model.OverlayState.*
import me.ostafin.androidscreendimmer.util.flow.BehaviourFlow
import me.ostafin.androidscreendimmer.util.flow.emit
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val getLastAlphaSliderValueUseCase: GetLastAlphaSliderValueUseCase,
    private val saveLastAlphaSliderValueUseCase: SaveLastAlphaSliderValueUseCase,
    private val checkDrawOverlaysPermissionStateUseCase: CheckDrawOverlaysPermissionStateUseCase,
    private val checkOverlayVisibilityUseCase: CheckOverlayVisibilityUseCase
) : BaseViewModel() {

    private var _buttonState: BehaviourFlow<ButtonState> = BehaviourFlow()
    var buttonState: Flow<ButtonState> = _buttonState

    private val _overlayState: BehaviourFlow<OverlayState> = BehaviourFlow()
    var overlayState: Flow<OverlayState> = _overlayState

    private val _openDrawOverAppSystemSettings: BehaviourFlow<Unit> = BehaviourFlow()
    var openDrawOverAppSystemSettings: Flow<Unit> = _openDrawOverAppSystemSettings

    private val _sliderValue: BehaviourFlow<Int> = BehaviourFlow()

    val initialSliderProgressObs: Flow<Int>
        get() = flow { emit(getLastAlphaSliderValueUseCase.execute()) }

    private val isDrawOverlaysPermissionGranted: Boolean
        get() = checkDrawOverlaysPermissionStateUseCase.execute()

    override fun onInitialized() {
        super.onInitialized()

        setupAlphaSliderValueSaving()
        setupOverlayUpdateLogic()

        initializeButtonState()
        initializeOverlayState()
    }

    private fun setupAlphaSliderValueSaving() {
        _sliderValue
            .debounce(ALPHA_VALUE_SAVE_DEBOUNCE_TIME_IN_MILLIS)
            .onEach { saveLastAlphaSliderValueUseCase.execute(it) }
            .launchIn(viewModelScope)
    }

    private fun setupOverlayUpdateLogic() {
        _sliderValue
            .filter { _overlayState.valueOrNull is Visible }
            .map { calculateAlphaFromSliderValue(it) }
            .map { Visible(it) }
            .onEach { _overlayState.emit(it) }
            .launchIn(viewModelScope)
    }

    private fun initializeButtonState() {
        val isOverlayVisible = checkOverlayVisibilityUseCase.execute()
        val initialButtonState = if (isOverlayVisible) TURN_OFF else TURN_ON
        _buttonState.emit(initialButtonState)
    }

    private fun initializeOverlayState() {
        val isOverlayVisible = checkOverlayVisibilityUseCase.execute()

        if (isOverlayVisible) {
            showOverlayWithLastSavedAlphaValue()
        } else {
            hideOverlay()
        }
    }

    fun sliderValueChanged(newValue: Int) {
        _sliderValue.emit(newValue)
    }

    fun buttonClicked() {
        if (isDrawOverlaysPermissionGranted) {
            emitNewButtonStateAndOverlayState()
        } else {
            _overlayState.emit(Hidden)
            _openDrawOverAppSystemSettings.emit()
        }
    }

    private fun emitNewButtonStateAndOverlayState() {
        val currentState = _buttonState.requireValue
        val newState = if (currentState == TURN_ON) TURN_OFF else TURN_ON
        _buttonState.emit(newState)

        val shouldShowOverlay = newState != TURN_ON

        if (shouldShowOverlay) {
            showOverlayWithLastSavedAlphaValue()
        } else {
            hideOverlay()
        }
    }

    private fun showOverlayWithLastSavedAlphaValue() {
        val sliderValue = getLastAlphaSliderValueUseCase.execute()
        val alphaValue = calculateAlphaFromSliderValue(sliderValue)
        val state = Visible(alphaValue)

        _overlayState.emit(state)
    }

    private fun hideOverlay() {
        _overlayState.emit(Hidden)
    }

    private fun calculateAlphaFromSliderValue(sliderValue: Int): Float {
        val percentage = sliderValue / 100f
        return (MAX_ALPHA_PERCENT - (percentage * MAX_ALPHA_PERCENT)) / 100f
    }

    companion object {
        const val MAX_ALPHA_PERCENT = 90
        const val ALPHA_VALUE_SAVE_DEBOUNCE_TIME_IN_MILLIS = 100L
    }

}