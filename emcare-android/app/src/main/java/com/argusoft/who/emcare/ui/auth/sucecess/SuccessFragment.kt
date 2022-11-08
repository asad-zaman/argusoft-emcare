package com.argusoft.who.emcare.ui.auth.sucecess

import com.argusoft.who.emcare.databinding.FragmentSuccessBinding
import com.argusoft.who.emcare.ui.common.base.BaseFragment

class SuccessFragment : BaseFragment<FragmentSuccessBinding>() {

    override fun initView() {
        //No initialization required
    }


    override fun initListener() {
        binding.headerLayout.toolbar.setTitleAndBack()
    }

    override fun initObserver() {
        //No Observers
    }

}