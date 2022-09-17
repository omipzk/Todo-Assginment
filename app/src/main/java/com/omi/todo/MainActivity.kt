package com.omi.todo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.omi.todo.ui.TaskFragment
import com.omi.todo.utils.SettingsManager
import com.omi.todo.utils.UiMode
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var actionBarToggle: ActionBarDrawerToggle
    private lateinit var navView: NavigationView
    private lateinit var settingsManager: SettingsManager
    private lateinit var darkMode: SwitchCompat
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val fabTask = findViewById<FloatingActionButton>(R.id.navigateToTodoTask)

        /*fabTask.setOnClickListener { navigate ->

        }*/

        // set to the drawerLayout
        setUpDrawerNavigation()
        // set to the bottom navigation
        setBottomNavigation()

        observeUiPreferences()
        initViews()
        fabTask.setOnClickListener {
            val taskFragment = TaskFragment()
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.newsNavHostFragment, taskFragment)
            transaction.commit()
        }
    }

    private fun setBottomNavigation() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.background = null
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.newsNavHostFragment) as NavHostFragment
        val navController = navHostFragment.navController
        bottomNavigationView.setupWithNavController(navController)
    }

    private fun setUpDrawerNavigation() {
        auth = FirebaseAuth.getInstance()
        drawerLayout = findViewById(R.id.drawerLayout)
        actionBarToggle = ActionBarDrawerToggle(this, drawerLayout, 0, 0)
        drawerLayout.addDrawerListener(actionBarToggle)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        actionBarToggle.syncState()
        navView = findViewById(R.id.navView)
    }


    private fun observeUiPreferences() {
        settingsManager = SettingsManager(applicationContext)
        settingsManager.uiModeFlow.asLiveData().observe(this) { uiMode ->
            when (uiMode) {
                UiMode.LIGHT -> onLightMode()
                UiMode.DARK -> onDarkMode()
            }
        }
    }

    private fun initViews() {
        val menuItem = navView.menu.findItem(R.id.dark_mode_switch)
        darkMode = menuItem.actionView as SwitchCompat
        darkMode.setOnClickListener {
            lifecycleScope.launch {
                if (darkMode.isChecked) {
                    settingsManager.setUiMode(UiMode.DARK)
                } else {
                    settingsManager.setUiMode(UiMode.LIGHT)
                }
            }
        }

        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.logout ->
                    logout()
            }
            true
        }
    }

    private fun logout() {
        auth.signOut()
        startActivity(Intent(this, SignupActivity::class.java))
        finish()
    }

    private fun onLightMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        darkMode.isChecked = false
    }

    private fun onDarkMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        darkMode.isChecked = true
    }

    override fun onSupportNavigateUp(): Boolean {
        drawerLayout.openDrawer(navView)
        return true
    }

    override fun onBackPressed() {
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

}