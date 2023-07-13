package com.argusoft.who.emcare.ui.home

import android.view.View
import androidx.activity.viewModels
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.argusoft.who.emcare.R
import com.argusoft.who.emcare.databinding.ActivityHomeBinding
import com.argusoft.who.emcare.ui.common.IS_LOAD_LIBRARIES
import com.argusoft.who.emcare.ui.common.base.BaseActivity
import com.argusoft.who.emcare.utils.extention.alertDialog
import com.argusoft.who.emcare.utils.extention.dismissProgressDialog
import com.argusoft.who.emcare.utils.extention.observeNotNull
import com.argusoft.who.emcare.utils.extention.whenSuccess
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : BaseActivity<ActivityHomeBinding>() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navHostFragment: NavHostFragment
    private val homeViewModel: HomeViewModel by viewModels()
//    private val signUpViewModel: SignUpViewModel by viewModels()
    lateinit var sidepaneAdapter: SidepaneAdapter

    override fun initView() {
//        signUpViewModel.getLocationsAndRoles()

        if(!intent.hasExtra(IS_LOAD_LIBRARIES) ||
            (intent.hasExtra(IS_LOAD_LIBRARIES) && intent.getBooleanExtra(IS_LOAD_LIBRARIES, false))){
            homeViewModel.loadLibraries(true)
        }
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
        binding.navView.menu.getItem(2).title = "About"
        binding.navView.menu.getItem(3).title = "Change Theme"
                                                                                        binding.navView.menu.getItem(4).title = "Logout"
        sidepaneAdapter = SidepaneAdapter(onClickListener = this, navHostFragment = navHostFragment)
        setupSidepane()
    }

    fun openDrawer() {
        binding.drawerLayout.openDrawer(GravityCompat.END)
    }

    fun closeDrawer() {
        binding.drawerLayout.closeDrawer(GravityCompat.END)
    }

    fun setupSidepane(isPreviousConsultation: Boolean = false) {
        if(isPreviousConsultation) {
            sidepaneAdapter = SidepaneAdapter(onClickListener = this, navHostFragment = navHostFragment,isPreviousConsultation = true)
        }
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
                    homeViewModel.checkUnsycnedResources()
                }
                R.id.action_about -> {
                    closeDrawer()
                    navHostFragment.navController.navigate(R.id.action_global_aboutFragment)
                }
                R.id.action_change_theme -> {
                    closeDrawer()
                    navHostFragment.navController.navigate(R.id.action_global_changeThemeFragment)
                }
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
        observeNotNull(homeViewModel.unsyncedResourcesCount) {
            it.whenSuccess {count ->
                if(count > 0){
                    alertDialog {
                        setMessage("There is un-synced data and we request you to sync before logging out!")
                        setPositiveButton(R.string.button_logout_anyway) { _, _ ->
                            logout()
                        }
                        setNegativeButton(R.string.button_cancel) { _, _ -> }
                    }.show()
                } else {
                    alertDialog {
                        setMessage(R.string.msg_logout)
                        setPositiveButton(R.string.button_yes) { _, _ ->
                            logout()
                        }
                        setNegativeButton(R.string.button_no) { _, _ -> }
                    }.show()
                }
            }
        }
        observeNotNull(homeViewModel.librariesLoaded) {
            it.whenSuccess {
                dismissProgressDialog()
            }
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = navHostFragment.navController
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
