package me.ostafin.androidscreendimmer.app

import android.view.View
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import me.ostafin.androidscreendimmer.app.di.DaggerAppComponent

class AndroidScreenDimmerApp : DaggerApplication() {

    var overlayView: View? = null

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.factory().create(this)
    }

}