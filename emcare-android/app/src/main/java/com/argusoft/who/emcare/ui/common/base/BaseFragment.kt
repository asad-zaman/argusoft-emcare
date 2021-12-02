package com.argusoft.who.emcare.ui.common.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.argusoft.who.emcare.data.local.pref.Preference
import com.argusoft.who.emcare.utils.extention.hideKeyboard
import com.argusoft.who.emcare.utils.extention.onViewBinding
import javax.inject.Inject

abstract class BaseFragment<B : ViewBinding> : Fragment(), View.OnClickListener {

    @Inject
    lateinit var preference: Preference
    private var _binding: B? = null
    protected val binding
        get() = _binding
            ?: throw RuntimeException("Should only use binding after onCreateView and before onDestroyView")
    abstract fun initView()
    abstract fun initListener()
    abstract fun initObserver()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = onViewBinding(inflater, container)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initListener()
        initObserver()
    }

    override fun onClick(v: View?) {
        hideKeyboard(v)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}