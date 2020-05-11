package me.ostafin.androidscreendimmer.app.di.activity

import android.app.Activity
import android.os.Bundle
import me.ostafin.androidscreendimmer.app.di.qualifier.ActivityBundle
import dagger.Module
import dagger.Provides

@Module
abstract class ActivityModule {

    @Module
    companion object {

        @JvmStatic
        @ActivityBundle
        @Provides
        fun bundle(activity: Activity): Bundle = activity.intent.extras ?: Bundle.EMPTY

    }
}
