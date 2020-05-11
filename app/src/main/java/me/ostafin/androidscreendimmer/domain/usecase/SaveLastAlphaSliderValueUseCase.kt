package me.ostafin.androidscreendimmer.domain.usecase

import android.util.Log
import me.ostafin.androidscreendimmer.domain.repository.SettingsRepository
import javax.inject.Inject

class SaveLastAlphaSliderValueUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {

    fun execute(newValue: Int) {
        Log.d("elo" ,"saved")
        settingsRepository.setAlphaSliderValue(newValue)
    }

}