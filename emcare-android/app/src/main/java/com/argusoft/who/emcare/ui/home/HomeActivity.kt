package com.argusoft.who.emcare.ui.home

import android.opengl.Visibility
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.RecyclerView
import com.argusoft.who.emcare.R
import com.argusoft.who.emcare.databinding.ActivityHomeBinding
import com.argusoft.who.emcare.ui.auth.signup.SignUpViewModel
import com.argusoft.who.emcare.ui.common.base.BaseActivity
import com.argusoft.who.emcare.ui.common.model.SidepaneItem
import com.argusoft.who.emcare.ui.home.settings.SettingsViewModel
import com.argusoft.who.emcare.utils.avatar.AvatarGenerator
import com.argusoft.who.emcare.utils.extention.alertDialog
import com.argusoft.who.emcare.utils.extention.convertToMap
import com.argusoft.who.emcare.utils.extention.observeNotNull
import com.argusoft.who.emcare.utils.extention.whenSuccess
import com.argusoft.who.emcare.utils.localization.LocaleHelper
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : BaseActivity<ActivityHomeBinding>() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navHostFragment: NavHostFragment
    private val homeViewModel: HomeViewModel by viewModels()
//    private val signUpViewModel: SignUpViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()
    lateinit var sidepaneAdapter: SidepaneAdapter

    override fun initView() {
//        signUpViewModel.getLocationsAndRoles()
        settingsViewModel.getLanguageByCode(LocaleHelper.load(this).language)
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController = navHostFragment.navController
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
//        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        //Setting name & Facility name in drawer view
        if(preference.getLoggedInUser() != null){
            binding.navView.menu.getItem(0).title = preference.getLoggedInUser()?.firstName
            binding.navView.menu.getItem(1).title = preference.getLoggedInUser()?.facility?.get(0)?.facilityName
        }
        binding.navView.menu.getItem(2).title = "Logout"

        sidepaneAdapter = SidepaneAdapter(onClickListener = this, navHostFragment = navHostFragment)
        setupSidepane()
        homeViewModel.loadLibraries()
    }

    fun openDrawer() {
        binding.drawerLayout.openDrawer(GravityCompat.END)
    }

    fun closeDrawer() {
        binding.drawerLayout.closeDrawer(GravityCompat.END)
    }

    fun setupSidepane() {
        binding.sidepaneRecyclerView.adapter = sidepaneAdapter
    }

    override fun initListener() {
        binding.sidepaneConstraintLayout.setOnClickListener {
            closeSidepane()
        }
        
        binding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.action_logout -> {
                    closeDrawer()
                    alertDialog {
                        setMessage(R.string.msg_logout)
                        setPositiveButton(R.string.button_yes) { _, _ ->
                            logout()
                        }
                        setNegativeButton(R.string.button_no) { _, _ -> }
                    }.show()
                }
//                R.id.action_settings -> {
//                    closeDrawer()
//                    navHostFragment.navController.navigate(R.id.action_global_settingsFragment)
//                }
            }
            return@setNavigationItemSelectedListener true
        }

    }

    fun toggleSidepane() {
        binding.sidepaneConstraintLayout.visibility = if(binding.sidepaneConstraintLayout.visibility == View.GONE) View.VISIBLE else View.GONE
        binding.sidepaneRecyclerView.visibility = if(binding.sidepaneRecyclerView.visibility == View.GONE) View.VISIBLE else View.GONE
    }

    fun closeSidepane() {
        binding.sidepaneConstraintLayout.visibility = View.GONE
        binding.sidepaneRecyclerView.visibility = View.GONE
    }

    override fun initObserver() {
//        observeNotNull(settingsViewModel.languageApiState) {
//            it.whenSuccess {
//                it.languageData?.convertToMap()?.apply {
//                    binding.navView.menu.getItem(0).setTitle(getOrElse("Edit_Profile") { getString(R.string.menu_edit_profile) } )
//                    binding.navView.menu.getItem(1).setTitle(getOrElse("Settings") { getString(R.string.menu_settings) } )
//                    binding.navView.menu.getItem(2).setTitle(getOrElse("Logout") { getString(R.string.menu_logout) } )
//                }
//            }
//        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = navHostFragment.navController
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
