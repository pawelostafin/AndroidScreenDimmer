package me.ostafin.androidscreendimmer.app.di

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import me.ostafin.androidscreendimmer.app.AndroidScreenDimmerApp
import me.ostafin.androidscreendimmer.app.di.qualifier.ApplicationContext
import me.ostafin.androidscreendimmer.app.di.scope.ApplicationScope
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

        @Provides
        @JvmStatic
        @ApplicationScope
        fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(context)

    }

}