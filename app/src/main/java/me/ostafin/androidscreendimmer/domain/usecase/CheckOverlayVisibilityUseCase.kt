package me.ostafin.androidscreendimmer.domain.usecase

import me.ostafin.androidscreendimmer.domain.checker.OverlayVisibilityChecker
import javax.inject.Inject

class CheckOverlayVisibilityUseCase @Inject constructor(
    private val overlayVisibilityChecker: OverlayVisibilityChecker
) {

    fun execute(): Boolean {
        return overlayVisibilityChecker.isOverlayVisible
    }

}