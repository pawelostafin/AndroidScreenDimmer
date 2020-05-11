package me.ostafin.androidscreendimmer.domain.repository

import me.ostafin.androidscreendimmer.app.configuration.Configuration
import javax.inject.Inject

class SettingsRepository @Inject constructor(
    private val configuration: Configuration
) {

    fun getAlphaSliderValue(): Int {
        return configuration.alphaSliderValue
    }

    fun setAlphaSliderValue(newValue: Int) {
        configuration.alphaSliderValue = newValue
    }

}