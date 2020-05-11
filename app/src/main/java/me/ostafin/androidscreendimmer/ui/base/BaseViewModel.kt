package me.ostafin.androidscreendimmer.ui.base

import androidx.annotation.CallSuper
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable

abstract class BaseViewModel : ViewModel() {

    protected val compositeDisposable = CompositeDisposable()

    var isViewModelInitialized = false
        private set

    @CallSuper
    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }

    @CallSuper
    open fun onInitialized() {
        isViewModelInitialized = true
    }

}