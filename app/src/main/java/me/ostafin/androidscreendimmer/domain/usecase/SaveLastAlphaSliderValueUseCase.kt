package me.ostafin.androidscreendimmer.domain.usecase

import me.ostafin.androidscreendimmer.domain.repository.SettingsRepository
import javax.inject.Inject

class SaveLastAlphaSliderValueUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {

    fun execute(newValue: Int) {
        settingsRepository.setAlphaSliderValue(newValue)
    }

}