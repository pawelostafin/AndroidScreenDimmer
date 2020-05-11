package me.ostafin.androidscreendimmer.app.di.activity

import dagger.Module
import dagger.android.ContributesAndroidInjector
import me.ostafin.androidscreendimmer.app.di.scope.ActivityScope
import me.ostafin.androidscreendimmer.ui.main.MainActivity
import me.ostafin.androidscreendimmer.ui.main.di.MainActivityModule

@Module
abstract class ActivityBuildersModule {

    @ActivityScope
    @ContributesAndroidInjector(modules = [ActivityModule::class, MainActivityModule::class])
    internal abstract fun mainActivity(): MainActivity

}