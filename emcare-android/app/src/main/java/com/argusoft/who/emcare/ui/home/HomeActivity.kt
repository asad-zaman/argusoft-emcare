package com.argusoft.who.emcare.ui.home

import androidx.core.view.GravityCompat
import com.argusoft.who.emcare.databinding.ActivityHomeBinding
import com.argusoft.who.emcare.ui.common.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : BaseActivity<ActivityHomeBinding>() {

    override fun initView() {
        /* val drawerLayout: DrawerLayout = binding.drawerLayout
         val navView: NavigationView = binding.navView
         val navController = findNavController(R.id.nav_host_fragment_content_main)
         // Passing each menu ID as a set of Ids because each
         // menu should be considered as top level destinations.
         appBarConfiguration = AppBarConfiguration(
             setOf(
                 R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
             ), drawerLayout
         )
         setupActionBarWithNavController(navController, appBarConfiguration)
         navView.setupWithNavController(navController)*/
    }

    fun openDrawer() {
        binding.drawerLayout.openDrawer(GravityCompat.END)
    }

    fun closeDrawer() {
        binding.drawerLayout.closeDrawer(GravityCompat.END)
    }

    override fun initListener() {
    }

    override fun initObserver() {
    }

/*    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }*/
}
