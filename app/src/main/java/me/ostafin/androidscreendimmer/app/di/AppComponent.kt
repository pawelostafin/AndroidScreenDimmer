package me.ostafin.androidscreendimmer.app.di

import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import me.ostafin.androidscreendimmer.app.AndroidScreenDimmerApp
import me.ostafin.androidscreendimmer.app.di.activity.ActivityBuildersModule
import me.ostafin.androidscreendimmer.app.di.scope.ApplicationScope

@ApplicationScope
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        AppModule::class,
        ActivityBuildersModule::class
    ]
)
interface AppComponent : AndroidInjector<AndroidScreenDimmerApp> {

    @Component.Factory
    abstract class Factory : AndroidInjector.Factory<AndroidScreenDimmerApp>

}