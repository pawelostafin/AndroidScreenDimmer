package me.ostafin.androidscreendimmer.ui.base

import androidx.annotation.CallSuper
import androidx.lifecycle.ViewModel

abstract class BaseViewModel : ViewModel() {

    var isViewModelInitialized = false
        private set

    @CallSuper
    open fun onInitialized() {
        isViewModelInitialized = true
    }

}