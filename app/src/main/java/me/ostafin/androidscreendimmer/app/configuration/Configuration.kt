package me.ostafin.androidscreendimmer.app.configuration

import android.content.SharedPreferences
import me.ostafin.androidscreendimmer.app.di.scope.ApplicationScope
import me.ostafin.androidscreendimmer.util.int
import javax.inject.Inject

@ApplicationScope
class Configuration @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {

    var alphaSliderValue: Int
            by sharedPreferences.int(ALPHA_SLIDER_DEFAULT_VALUE, "alphaSliderValue")

    companion object {
        const val ALPHA_SLIDER_DEFAULT_VALUE = 50
    }

}