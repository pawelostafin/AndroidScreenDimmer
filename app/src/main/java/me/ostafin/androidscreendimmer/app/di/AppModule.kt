package me.ostafin.androidscreendimmer.app.di

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.Provides
import me.ostafin.androidscreendimmer.app.AndroidScreenDimmerApp
import me.ostafin.androidscreendimmer.app.di.qualifier.ApplicationContext
import me.ostafin.androidscreendimmer.app.di.viewmodelfactory.ViewModelFactory

@Module
abstract class AppModule {

    @Binds
    abstract fun factory(viewModelFactory: ViewModelFactory): ViewModelProvider.Factory

    @Module
    companion object {

        @Provides
        @JvmStatic
        @ApplicationContext
        fun provideAppContext(application: AndroidScreenDimmerApp): Context = application.applicationContext

    }

}