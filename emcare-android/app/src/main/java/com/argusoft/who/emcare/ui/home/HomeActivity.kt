package com.argusoft.who.emcare.ui.home

import android.content.Intent
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.argusoft.who.emcare.R
import com.argusoft.who.emcare.databinding.ActivityHomeBinding
import com.argusoft.who.emcare.ui.auth.AuthenticationActivity
import com.argusoft.who.emcare.ui.common.base.BaseActivity
import com.argusoft.who.emcare.utils.extention.alertDialog
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : BaseActivity<ActivityHomeBinding>() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navHostFragment: NavHostFragment
    override fun initView() {
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

        //Setting name & email in drawer view
        if (preference.getLoggedInUser() != null) {
            val headerView = binding.navView.getHeaderView(0)
            headerView.findViewById<TextView>(R.id.nameTextView).text = preference.getLoggedInUser()?.userName
            headerView.findViewById<TextView>(R.id.emailTextView).text = preference.getLoggedInUser()?.email
        }
    }

    fun openDrawer() {
        binding.drawerLayout.openDrawer(GravityCompat.END)
    }

    fun closeDrawer() {
        binding.drawerLayout.closeDrawer(GravityCompat.END)
    }

    override fun initListener() {
        binding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.action_logout -> {
                    closeDrawer()
                    alertDialog {
                        setMessage(R.string.msg_logout)
                        setPositiveButton(R.string.button_yes) { _, _ ->
                            preference.clear()
                            startActivity(Intent(this@HomeActivity, AuthenticationActivity::class.java))
                            finish()
                        }
                        setNegativeButton(R.string.button_no) { _, _ -> }
                    }.show()
                }
                R.id.action_settings -> {
                    closeDrawer()
                    navHostFragment.navController.navigate(R.id.action_global_settingsFragment)
                }
            }
            return@setNavigationItemSelectedListener true
        }
    }

    override fun initObserver() {
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = navHostFragment.navController
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
