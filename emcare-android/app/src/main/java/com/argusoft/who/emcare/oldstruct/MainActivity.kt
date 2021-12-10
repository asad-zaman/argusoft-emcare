package com.argusoft.who.emcare.oldstruct

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import com.argusoft.who.emcare.R
import com.argusoft.who.emcare.databinding.ActivityMainBinding
import com.google.android.fhir.sync.State
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

const val MAX_RESOURCE_COUNT = 20

class MainActivity : AppCompatActivity() {
  private lateinit var binding: ActivityMainBinding
  private lateinit var drawerToggle: ActionBarDrawerToggle
  private val TAG = javaClass.name
  private val viewModel: MainActivityViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)
    initActionBar()
    initNavigationDrawer()
    observeLastSyncTime()
    requestSyncPoll()
    viewModel.getLastSyncTime()
  }

  override fun onBackPressed() {
    if (binding.drawer.isDrawerOpen(GravityCompat.START)) {
      binding.drawer.closeDrawer(GravityCompat.START)
      return
    }
    super.onBackPressed()
  }

  fun setDrawerEnabled(enabled: Boolean) {
    val lockMode =
      if (enabled) DrawerLayout.LOCK_MODE_UNLOCKED else DrawerLayout.LOCK_MODE_LOCKED_CLOSED
    binding.drawer.setDrawerLockMode(lockMode)
    drawerToggle.isDrawerIndicatorEnabled = enabled
  }

  fun openNavigationDrawer() {
    binding.drawer.openDrawer(GravityCompat.START)
    viewModel.getLastSyncTime()
  }

  private fun initActionBar() {
    val toolbar = binding.toolbar
    setSupportActionBar(toolbar)
    toolbar.title = title
  }

  private fun initNavigationDrawer() {
    binding.navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected)
    drawerToggle = ActionBarDrawerToggle(this, binding.drawer, R.string.open, R.string.close)
    binding.drawer.addDrawerListener(drawerToggle)
    drawerToggle.syncState()
  }

  private fun onNavigationItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      R.id.menu_sync -> {
        requestSyncPoll()
        true
      }
    }
    binding.drawer.closeDrawer(GravityCompat.START)
    return false
  }

  private fun showToast(message: String) {
    Log.i(TAG, message)
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
  }

  private fun requestSyncPoll() {
    val flow = viewModel.poll()
    lifecycleScope.launch {
      flow.collect {
        when (it) {
          is State.Started -> showToast("Sync: started")
          is State.InProgress -> showToast("Sync: in progress with ${it.resourceType?.name}")
          is State.Finished -> {
            showToast("Sync: succeeded at ${it.result.timestamp}")
            viewModel.getLastSyncTime()
          }
          is State.Failed -> {
            showToast("Sync: failed at ${it.result.timestamp}")
            viewModel.getLastSyncTime()
          }
          else -> showToast("Sync: unknown state.")
        }
      }
    }
  }

  private fun observeLastSyncTime() {
    viewModel.lastSyncLiveData.observe(
      this,
      {
        binding.navigationView.getHeaderView(0).findViewById<TextView>(R.id.last_sync_tv).text = it
      }
    )
  }
}