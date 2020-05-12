package me.ostafin.androidscreendimmer.ui.main

import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import me.ostafin.androidscreendimmer.domain.usecase.CheckDrawOverlaysPermissionStateUseCase
import me.ostafin.androidscreendimmer.domain.usecase.CheckOverlayVisibilityUseCase
import me.ostafin.androidscreendimmer.domain.usecase.GetLastAlphaSliderValueUseCase
import me.ostafin.androidscreendimmer.domain.usecase.SaveLastAlphaSliderValueUseCase
import me.ostafin.androidscreendimmer.ui.base.BaseViewModel
import me.ostafin.androidscreendimmer.ui.main.model.ButtonState
import me.ostafin.androidscreendimmer.ui.main.model.ButtonState.TURN_ON
import me.ostafin.androidscreendimmer.ui.main.model.ButtonState.TURN_OFF
import me.ostafin.androidscreendimmer.util.accept
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val getLastAlphaSliderValueUseCase: GetLastAlphaSliderValueUseCase,
    private val saveLastAlphaSliderValueUseCase: SaveLastAlphaSliderValueUseCase,
    private val checkDrawOverlaysPermissionStateUseCase: CheckDrawOverlaysPermissionStateUseCase,
    private val checkOverlayVisibilityUseCase: CheckOverlayVisibilityUseCase
) : BaseViewModel() {

    private var buttonStateRelay: BehaviorRelay<ButtonState> = BehaviorRelay.create()
    var buttonStateObs: Observable<ButtonState> = buttonStateRelay

    private val overlayVisibilityStateRelay: BehaviorRelay<Boolean> = BehaviorRelay.create()
    var overlayVisibilityStateObs: Observable<Boolean> = overlayVisibilityStateRelay

    private val sliderValueChangedRelay: BehaviorRelay<Int> = BehaviorRelay.create()

    private val overlayAlphaValueRelay: BehaviorRelay<Float> = BehaviorRelay.create()
    var overlayAlphaValueObs: Observable<Float> = overlayAlphaValueRelay

    private val openDrawOverAppSystemSettingsRelay: BehaviorRelay<Unit> = BehaviorRelay.create()
    var openDrawOverAppSystemSettingsObs: Observable<Unit> = openDrawOverAppSystemSettingsRelay

    val initialSliderProgressObs: Observable<Int>
        get() = Observable.just(getLastAlphaSliderValueUseCase.execute())

    private val isDrawOverlaysPermissionGranted: Boolean
        get() = checkDrawOverlaysPermissionStateUseCase.execute()

    override fun onInitialized() {
        super.onInitialized()

        initializeButtonState()
        setupAlphaSliderValueSaving()
    }

    private fun setupAlphaSliderValueSaving() {
        sliderValueChangedRelay
            .debounce(ALPHA_VALUE_SAVE_DEBOUNCE_TIME_IN_MILLIS, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(saveLastAlphaSliderValueUseCase::execute)
            .addTo(compositeDisposable)
    }

    private fun initializeButtonState() {
        val isOverlayVisible = checkOverlayVisibilityUseCase.execute()
        val initialButtonState = if (isOverlayVisible) TURN_OFF else TURN_ON
        buttonStateRelay.accept(initialButtonState)
    }

    fun sliderValueChanged(newValue: Int) {
        val percentage = newValue / 100f
        val normalizedAlpha = (MAX_ALPHA_PERCENT - (percentage * MAX_ALPHA_PERCENT)) / 100f

        sliderValueChangedRelay.accept(newValue)
        overlayAlphaValueRelay.accept(normalizedAlpha)
    }

    fun buttonClicked() {
        if (isDrawOverlaysPermissionGranted) {
            emitNewButtonStateAndOverlayVisibilityState()
        } else {
            overlayVisibilityStateRelay.accept(false)
            openDrawOverAppSystemSettingsRelay.accept()
        }
    }

    private fun emitNewButtonStateAndOverlayVisibilityState() {
        val currentState = buttonStateRelay.value
        val newState = if (currentState == TURN_ON) TURN_OFF else TURN_ON
        buttonStateRelay.accept(newState)

        val isOverlayVisible = newState != TURN_ON
        overlayVisibilityStateRelay.accept(isOverlayVisible)
    }

    companion object {
        const val MAX_ALPHA_PERCENT = 90
        const val ALPHA_VALUE_SAVE_DEBOUNCE_TIME_IN_MILLIS = 100L
    }

}