package me.ostafin.androidscreendimmer.domain.usecase

import me.ostafin.androidscreendimmer.domain.repository.SettingsRepository
import javax.inject.Inject

class GetLastAlphaSliderValueUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {

    fun execute(): Int = settingsRepository.getAlphaSliderValue()

}