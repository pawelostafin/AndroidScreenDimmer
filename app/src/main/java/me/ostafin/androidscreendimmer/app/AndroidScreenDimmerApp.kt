package me.ostafin.androidscreendimmer.app

import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import me.ostafin.androidscreendimmer.app.di.DaggerAppComponent

class AndroidScreenDimmerApp : DaggerApplication() {

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.factory().create(this)
    }

}