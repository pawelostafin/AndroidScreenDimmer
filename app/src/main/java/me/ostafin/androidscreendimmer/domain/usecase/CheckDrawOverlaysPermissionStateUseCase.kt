package me.ostafin.androidscreendimmer.domain.usecase

import me.ostafin.androidscreendimmer.domain.checker.AppPermissionsChecker
import javax.inject.Inject

class CheckDrawOverlaysPermissionStateUseCase @Inject constructor(
    private val appPermissionsChecker: AppPermissionsChecker
) {

    fun execute(): Boolean {
        return appPermissionsChecker.canDrawOverlays
    }

}