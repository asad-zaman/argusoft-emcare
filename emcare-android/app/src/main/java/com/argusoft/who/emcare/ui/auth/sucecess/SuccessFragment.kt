package com.argusoft.who.emcare.ui.auth.sucecess

import android.view.View
import com.argusoft.who.emcare.databinding.FragmentSuccessBinding
import com.argusoft.who.emcare.ui.common.base.BaseFragment

class SuccessFragment : BaseFragment<FragmentSuccessBinding>() {

    override fun initView() {

    }


    override fun initListener() {
        binding.headerLayout.toolbar.onClickListener()
    }

    override fun initObserver() {
    }

    override fun onClick(view: View?) {
        super.onClick(view)
        when (view?.id) {

        }
    }
}