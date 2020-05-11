package me.ostafin.androidscreendimmer.ui.main.di

import android.app.Activity
import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import me.ostafin.androidscreendimmer.app.di.annotation.ViewModelKey
import me.ostafin.androidscreendimmer.ui.main.MainActivity
import me.ostafin.androidscreendimmer.ui.main.MainViewModel

@Module
abstract class MainActivityModule {

    @Binds
    abstract fun activity(activity: MainActivity): Activity

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun hostDetailsViewModel(viewModel: MainViewModel): ViewModel

}