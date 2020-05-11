package me.ostafin.androidscreendimmer.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import me.ostafin.androidscreendimmer.ui.main.ButtonState.*
import kotlin.math.floor

class MainViewModel : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    private var buttonStateRelay: BehaviorRelay<ButtonState> = BehaviorRelay.create()
    var buttonStateObs: Observable<ButtonState> = buttonStateRelay

    private val overlayVisibilityStateRelay: BehaviorRelay<Boolean> = BehaviorRelay.create()
    var overlayVisibilityStateObs: Observable<Boolean> = overlayVisibilityStateRelay

    private val sliderValueChangedRelay: BehaviorRelay<Int> = BehaviorRelay.create()

    val lastSliderValueObs: Observable<Int>
        get() = Observable.just(sliderValueChangedRelay.value ?: 0)

    private val overlayAlphaValueRelay: BehaviorRelay<Float> = BehaviorRelay.create()
    var overlayAlphaValueObs: Observable<Float> = overlayAlphaValueRelay

    private val openDrawOverAppSystemSettingsRelay: BehaviorRelay<Unit> = BehaviorRelay.create()
    var openDrawOverAppSystemSettingsObs: Observable<Unit> = openDrawOverAppSystemSettingsRelay

    init {
        initializeButtonState()
        initializeSliderValue()
        setupOverlayVisibilityStateChange()
    }

    private fun initializeSliderValue() {
        //TODO save value in shared preferences
        sliderValueChangedRelay.accept(50)
    }

    private fun setupOverlayVisibilityStateChange() {
        buttonStateRelay
            .map { it != OFF }
            .subscribe(overlayVisibilityStateRelay)
            .addTo(compositeDisposable)
    }

    private fun initializeButtonState() {
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
        val currentState = buttonStateRelay.value
        val newState = if (currentState == OFF) ON else OFF
        buttonStateRelay.accept(newState)
    }

    fun drawOverAppsSettingsChanged() {

    }

    companion object {
        const val MAX_ALPHA_PERCENT = 90
    }

}