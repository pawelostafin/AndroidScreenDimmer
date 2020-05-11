package me.ostafin.androidscreendimmer.ui.main

import android.util.Log
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import me.ostafin.androidscreendimmer.domain.checker.AppPermissionsChecker
import me.ostafin.androidscreendimmer.domain.usecase.GetLastAlphaSliderValueUseCase
import me.ostafin.androidscreendimmer.domain.usecase.SaveLastAlphaSliderValueUseCase
import me.ostafin.androidscreendimmer.ui.base.BaseViewModel
import me.ostafin.androidscreendimmer.ui.main.model.ButtonState
import me.ostafin.androidscreendimmer.ui.main.model.ButtonState.*
import me.ostafin.androidscreendimmer.util.accept
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val getLastAlphaSliderValueUseCase: GetLastAlphaSliderValueUseCase,
    private val saveLastAlphaSliderValueUseCase: SaveLastAlphaSliderValueUseCase,
    private val appPermissionsChecker: AppPermissionsChecker
) : BaseViewModel() {

    private var buttonStateRelay: BehaviorRelay<ButtonState> = BehaviorRelay.create()
    var buttonStateObs: Observable<ButtonState> = buttonStateRelay

    private val overlayVisibilityStateRelay: BehaviorRelay<Boolean> = BehaviorRelay.create()
    var overlayVisibilityStateObs: Observable<Boolean> = overlayVisibilityStateRelay

    private val sliderValueChangedRelay: BehaviorRelay<Int> = BehaviorRelay.create()

    val lastSliderValueObs: Observable<Int>
        get() = Observable.just(getLastAlphaSliderValueUseCase.execute())

    private val overlayAlphaValueRelay: BehaviorRelay<Float> = BehaviorRelay.create()
    var overlayAlphaValueObs: Observable<Float> = overlayAlphaValueRelay

    private val openDrawOverAppSystemSettingsRelay: BehaviorRelay<Unit> = BehaviorRelay.create()
    var openDrawOverAppSystemSettingsObs: Observable<Unit> = openDrawOverAppSystemSettingsRelay

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
        //TODO saved settings + permissions?
        buttonStateRelay.accept(OFF)
    }

    fun sliderValueChanged(newValue: Int) {
        val percentage = newValue / 100f
        val normalizedAlpha = (MAX_ALPHA_PERCENT - (percentage * MAX_ALPHA_PERCENT)) / 100f

        Log.d("elo", "$normalizedAlpha")

        sliderValueChangedRelay.accept(newValue)
        overlayAlphaValueRelay.accept(normalizedAlpha)
    }

    fun buttonClicked() {
        if (appPermissionsChecker.canDrawOverlays) {
            val currentState = buttonStateRelay.value
            val newState = if (currentState == OFF) ON else OFF
            buttonStateRelay.accept(newState)

            val isOverlayVisible = newState != OFF
            overlayVisibilityStateRelay.accept(isOverlayVisible)
        } else {
            overlayVisibilityStateRelay.accept(false)
            openDrawOverAppSystemSettingsRelay.accept()
        }
    }

    companion object {
        const val MAX_ALPHA_PERCENT = 90
        const val ALPHA_VALUE_SAVE_DEBOUNCE_TIME_IN_MILLIS = 100L
    }

}