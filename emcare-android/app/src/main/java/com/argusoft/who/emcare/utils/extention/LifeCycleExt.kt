package com.argusoft.who.emcare.utils.extention

import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

/*
 *   observe(viewModel.yourLiveData) {
 *           updateUi(data)
 *     }
 *    observeNotNull(viewModel.anotherLiveData) {
 *          doSomething(it.someProperty)
 *          doSomethingElse(it)
 *    }
 */
inline fun <T> LifecycleOwner.observe(
    liveData: LiveData<T>,
    crossinline observer: (t: T?) -> Unit
): Observer<T> = Observer<T> {
    observer(it)
}.also { liveData.observe(this, it) }

inline fun <T : Any> LifecycleOwner.observeNotNull(
    liveData: LiveData<T>,
    crossinline observer: (t: T) -> Unit
): Observer<T> = Observer<T> {
    if (it != null) observer(it)
}.also { liveData.observe(this, it) }


inline fun <T : Any> Fragment.observeNotNull(
    liveData: LiveData<T>,
    crossinline observer: (t: T) -> Unit
): Observer<T> = Observer<T> {
    if (it != null) observer(it)
}.also { liveData.observe(this, it) }

/**
 *  private val viewModel: YourViewModel by viewModel()
 *  private val anotherViewModel by viewModel<AnotherViewModel>()
 *  private val viewModel: YourViewModel by viewModel(YourViewModelFactory)
 */
/*inline fun <reified VM : ViewModel> FragmentActivity.viewModel(factory: ViewModelProvider.Factory? = null) = lazy {
    factory?.let {
        ViewModelProvider(this, it).get(VM::class.java)
    } ?: ViewModelProvider(this).get(VM::class.java)
}*/

/**
 *  private val viewModel: YourViewModel by viewModel()
 *  private val anotherViewModel by viewModel<AnotherViewModel>()
 *  private val viewModel: YourViewModel by viewModel(YourViewModelFactory)
 */
/*inline fun <reified VM : ViewModel> Fragment.viewModel(factory: ViewModelProvider.Factory? = null) = lazy {
    factory?.let { ViewModelProvider(this, it).get(VM::class.java) } ?: ViewModelProvider(this).get(VM::class.java)
}*/

/**
 *  It's return activity scope ViewModel, make sure this ViewModel also created in Activity
 *  private val viewModel: YourViewModel by sharedViewModel()
 *  private val anotherViewModel by sharedViewModel<AnotherViewModel>()
 *  private val viewModel: YourViewModel by sharedViewModel(YourViewModelFactory)
 */
/*inline fun <reified VM : ViewModel> Fragment.sharedViewModel(factory: ViewModelProvider.Factory? = null) = lazy {
    factory?.let { ViewModelProvider(activity!!, it).get(VM::class.java) } ?: ViewModelProvider(activity!!).get(VM::class.java)
}*/

/**
 *  private val viewModel: YourViewModel by viewModel{ YourViewModel() }
 *  private val anotherViewModel by viewModel<AnotherViewModel>{ AnotherViewModel() }
 */
/*inline fun <reified VM : ViewModel> FragmentActivity.viewModel(
    noinline factory: () -> VM
) = lazy {
    ViewModelProvider(this, TypeSafeViewModelFactory(factory)).get(VM::class.java)
}*/

/**
 *  private val viewModel: YourViewModel by viewModel{ YourViewModel() }
 *  private val anotherViewModel by viewModel<AnotherViewModel>{ AnotherViewModel() }
 */
/*inline fun <reified VM : ViewModel> Fragment.viewModel(
    noinline factory: () -> VM
) = lazy {
    ViewModelProvider(this, TypeSafeViewModelFactory(factory)).get(VM::class.java)
}*/

/**
 *  private val viewModel: YourViewModel by sharedViewModel{ YourViewModel() }
 *  private val anotherViewModel by sharedViewModel<AnotherViewModel>{ AnotherViewModel() }
 */
/*inline fun <reified VM : ViewModel> Fragment.sharedViewModel(
    noinline factory: () -> VM
) = lazy {
    ViewModelProvider(activity!!, TypeSafeViewModelFactory(factory)).get(VM::class.java)
}*/

/*@PublishedApi
internal class TypeSafeViewModelFactory<VM : ViewModel>(
    private val factory: () -> VM
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>) = factory() as T
}*/


