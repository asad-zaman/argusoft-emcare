package com.argusoft.who.emcare.ui.splash

import android.annotation.SuppressLint
import com.argusoft.who.emcare.databinding.ActivitySplashBinding
import com.argusoft.who.emcare.ui.common.base.BaseActivity
import com.argusoft.who.emcare.oldstruct.MainActivity

import android.content.Intent
import com.argusoft.who.emcare.ui.auth.AuthenticationActivity
import com.argusoft.who.emcare.ui.home.HomeActivity
import dagger.hilt.android.AndroidEntryPoint

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : BaseActivity<ActivitySplashBinding>() {

    override fun initView() {
        if (preference.isLogin())
            startActivity(Intent(this, HomeActivity::class.java))
        else
            startActivity(Intent(this, AuthenticationActivity::class.java))
        finish()
    }

    override fun initListener() {
    }

    override fun initObserver() {
    }
}