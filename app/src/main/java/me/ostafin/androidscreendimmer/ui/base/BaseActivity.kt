package me.ostafin.androidscreendimmer.ui.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import me.ostafin.androidscreendimmer.app.AndroidScreenDimmerApp
import javax.inject.Inject

abstract class BaseActivity<VM : BaseViewModel> : AppCompatActivity(), HasAndroidInjector {

    protected abstract val viewModelType: Class<VM>

    protected abstract val layoutId: Int

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Any>

    override fun androidInjector(): AndroidInjector<Any> = fragmentInjector

    protected val viewModel: VM by lazy {
        ViewModelProvider(this, viewModelFactory).get(viewModelType)
    }

    protected val androidScreenDimmerApp: AndroidScreenDimmerApp
        get() = application as AndroidScreenDimmerApp

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(layoutId)

        setupView()
        observeViewModel()

        if (!viewModel.isViewModelInitialized) {
            viewModel.onInitialized()
        }
    }

    protected open fun setupView() {}

    protected open fun observeViewModel() {}

}